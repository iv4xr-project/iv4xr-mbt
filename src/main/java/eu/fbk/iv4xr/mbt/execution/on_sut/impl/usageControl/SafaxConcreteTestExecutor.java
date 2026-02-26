package eu.fbk.iv4xr.mbt.execution.on_sut.impl.usageControl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.impl.SafaxConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.SafaxTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

public class SafaxConcreteTestExecutor  implements ConcreteTestExecutor  {

	private static Set<String> ignoreEvents = new HashSet<String>(
			Arrays.asList("SESSION_CREATED", "ATTRIBUTE_UPDATED", "SESSION_STOPPED", "SKIP"));
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static String safax_input_folder  = "input_safax";
	private static String safax_output_folder  = "output_safax";
	
	private static String safax_outfile_prexif = "output_for_";
	private static String safax_outfile_valid_suffix = ".json";
	private static String safax_outfile_error_suffix = "_FAILED.json";
	
	protected EFSM model;
	private ArrayNode jsonTestCases;
	
	private JsonNode configurationNode;
	
	private TestConcretizer testConcretizer;
	private TestSuiteExecutionReport executionReporter;
	private HashMap<String, AbstractTestSequence> testCaseMap = new HashMap<>();
	
	private Path testsInputFolder;
	private Path testsOutputFolder;
		
	private Map<AbstractTestSequence, File> testToFileMap;
	
	public SafaxConcreteTestExecutor(EFSM model, String testsDir, Map testToFileMap) {
		
		// store EFSM model
		this.model = model;
		
		this.testToFileMap = testToFileMap;
		
		this.jsonTestCases = objectMapper.createArrayNode();
		
		// check if exit and load SAFAX configuration
		if (!loadSafaxConfiguration(MBTProperties.SAFAX_CFG)) {
			throw new RuntimeException("Error loading SAFAX configuration file "+MBTProperties.SAFAX_CFG);
		}
	
		// create input and output folder for running safax client
		if (!createSafaxFolders(testsDir)) {
			throw new RuntimeException("Error creating input and output foler in "+testsDir);
		}
		
		// abstract test concretizer
		testConcretizer = new SafaxTestConcretizer(model);
		
		// test execution reported
		executionReporter = new TestSuiteExecutionReport();
		
		
		
	}
	
	/**
	 * Loads the SAFAX configuration file. 
	 * Returns true if successful, false otherwise.
	 */
	private boolean loadSafaxConfiguration(String pathToSafaxJson) {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	        // Load the file into a JsonNode (The "proper data structure" for generic JSON)
	        configurationNode = mapper.readTree(new File(pathToSafaxJson));
	        
	        // Return true if the node was actually read and is not null
	        return configurationNode != null && !configurationNode.isMissingNode();
	    } catch (IOException e) {
	        // Parsing failed or file not found
	    	e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Create subfolders safax_input_folder and safax_output_folder. If they already exists, they are removed.
	 * @param folderPath
	 * @return true if successful, false otherwise.
	 */
	private boolean createSafaxFolders(String folderPath) {
	    try {
	        Path root = Paths.get(folderPath);
	        testsInputFolder = root.resolve(safax_input_folder);
	        testsOutputFolder = root.resolve(safax_output_folder);
	        
	        Path[] targets = { testsInputFolder,  testsOutputFolder};

	        // clean target folders
	        for (Path target : targets) {
	            // If folder exists, delete it recursively
	            if (Files.exists(target)) {
	                try (Stream<Path> walk = Files.walk(target)) {
	                    walk.sorted(Comparator.reverseOrder()) // Sort to delete files before directories
	                        .map(Path::toFile)
	                        .forEach(File::delete);
	                }
	            }
	            // Create the directory (including any missing parents)
	            Files.createDirectories(target);	            
	        }
	        
	        return true;
	    } catch (IOException | RuntimeException e) {
	        // Return false if permission issues or IO errors occur
	    	e.printStackTrace();
	        return false;
	    }
	}
	
	@Override
	/**
	 * To execute a test suite, three steps are required
	 * 1. generate a concrete json test case
	 * 2. call the safax executor to actually execute generated json
	 * 3. parse output generate by the safax executor to check oracles
	 */
	public boolean executeTestSuite(SuiteChromosome testSuite) throws InterruptedException {
		
		/*
		 * Generate a concrete json test case
		 */

		for (int i = 0; i < testSuite.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) testSuite.getTestChromosome(i).getTestcase();
			if (! executeTestCase(testcase)) {
				throw new RuntimeException("Error in converting abstract test case "+testcase.getPath()+" to json");
			}
		}

		/*
		 * Call the safax executor to actually execute generated json
		 */
		if (!executeSafaxClient()) {
			throw new RuntimeException("Error in running SAFAX client");
		}
		
		/*
		 * Collect outputs and compare with test case transitions
		 */		
		return loadSafaxClientOutput();
	
		
	}

	@Override
	public boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException {
		
		// get the json basic structure
		SafaxConcreteTestCase concreteTestCase = (SafaxConcreteTestCase) testConcretizer.concretizeTestCase(testcase);
		
		// create the json file name from the ser file name
		File testFile = testToFileMap.get(testcase);
		
		String testFileName = testFile.getName();
		
		// store tin memory the test case
		// String caseName = "test_" + (jsonTestCases.size() + 1);
		String caseName = FilenameUtils.removeExtension(testFileName);
		ObjectNode jsonTestcase = concreteTestCase.getJsonTestCase(configurationNode);
		jsonTestCases.add(jsonTestcase);

		testCaseMap.put(caseName, testcase);
		
		// save to the disk
		Path testFilePath = testsInputFolder.resolve(caseName + ".json");
		try {
			objectMapper.writeValue(testFilePath.toFile(), jsonTestcase);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Execute SAFAX client
	 * @return
	 */
	private boolean executeSafaxClient() {
		
		// Check if Docker is installed
        if (!isDockerInstalled()) {
            System.err.println("Error: Docker is not installed on this system.");
            return false;
        }
		
        //  Check if SAFAX_EXECUTOR folder  contains a Dockerfile
        File dockerFile = new File(MBTProperties.SAFAX_EXECUTOR, "Dockerfile");
        if (!dockerFile.exists() || !dockerFile.isFile()) {
            System.err.println("Error: SAFAX_EXECUTOR folder does not contain a Dockerfile.");
            return false;
        }
                
        try {
        	
        	
        	String[] command = {"docker", "run", "--rm",  "-i",  
                    "-v", String.format("%s:/app/input", Paths.get(testsInputFolder.toString()).toAbsolutePath()),
                    "-v", String.format("%s:/app/output", Paths.get(testsOutputFolder.toString()).toAbsolutePath()),
                    "-e", "NODE_ENV=dev",
                    "ucon-client", "input"
                    };
        			
        	System.err.println(String.join(" ", command));
        	
        	// build docker command
            ProcessBuilder pb = new ProcessBuilder(command);
            
            // start test execution            
            pb.redirectErrorStream(true); 
            
            Process process = pb.start();
            int exitCode = -1;
            
            process.getInputStream().transferTo(System.out); 
            
			exitCode = process.waitFor();
           
            System.err.println(exitCode	);
            
        } catch (IOException | InterruptedException e) {
        	System.err.println("Error running SAFAX client.");
            e.printStackTrace();
            return false;
        } 
        
		return true;
	}
	
	/**
	 * SAFAX client run within Docker. The method check if Docker is installed by running 
	 * docker --version
	 * @return 
	 */
    private boolean isDockerInstalled() {
        try {
            Process process = new ProcessBuilder("docker", "--version").start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
	
    /**
     * Parse the output of the safax client
     */
    private boolean loadSafaxClientOutput() {
    	
    	boolean returnStatus = true; 
    	
    	// iterate over testCaseMap keys
    	for(String inputFile: testCaseMap.keySet()) {    	
    		
    		boolean testStatus = true;
    		
    		// create a test reporter to store report execution
    		LinkedList<TestCaseExecutionReport> testReporter = new LinkedList<TestCaseExecutionReport>();
    		
    		// check if corresponding output file exists
    		Path correctOutPath = Paths.get(testsOutputFolder.toString(), safax_outfile_prexif + inputFile + safax_outfile_valid_suffix);    		
    		if (Files.exists(correctOutPath)) {
    			// the correct output file has been generated    			
    			Object outSafax = null;
    			try {
    				outSafax = objectMapper.readValue(correctOutPath.toFile(), Object.class );
    			} catch (Exception e) {
    				System.err.println("Error parsing SAFAX output.");
    	            e.printStackTrace();
    	            returnStatus = false;    				
				}    			
    			
    			// get responses from the output file
    			List<String> safaxResponses = parseSafaxClientOutput((HashMap)outSafax);
    			// compare with the expected output
    			AbstractTestSequence abstractTestSequence = testCaseMap.get(inputFile);
    			
    			int responseIndex = 0;
    			for(EFSMTransition transition :  abstractTestSequence.getPath().getTransitions()) {
    				String expectedResponse = transition.getOutParameter().getParameter().getVariable("action").getValue().toString();
    				
    				// check if the event should be ignored
    				if (! ignoreEvents.contains(expectedResponse)) {
    					
    					TestCaseExecutionReport stepReport = new TestCaseExecutionReport();
    					
    					// check if the event observed correspond to the expected
    					if (safaxResponses.get(responseIndex).equalsIgnoreCase(expectedResponse)) {
    						stepReport.addReport(expectedResponse, transition);    						
    					}else {
    						returnStatus = false;
    						testStatus = false;
    						stepReport.addReport("Expect "+ expectedResponse + " but observe "+ safaxResponses.get(responseIndex), transition);    						
    					}
    					
    					testReporter.add(stepReport);    					
    					responseIndex += 1;
    				}    				
    			}
    			executionReporter.addTestCaseReport(abstractTestSequence, testReporter, testStatus, (long)-1);
    		} else {
    			// out file not produced    			
    			returnStatus = false;
				testStatus = false;
    			TestCaseExecutionReport stepReport = new TestCaseExecutionReport();
    			stepReport.addReport("Fail to create out file", new EFSMTransition());
    			testReporter.add(stepReport); 
    			executionReporter.addTestCaseReport(new AbstractTestSequence(), testReporter, testStatus, (long)-1);
    		}
    	}
    	
    	return returnStatus;
    }
    
    /**
     * Collect only UCON events relevant for assessing if test pass or not
     * @param outSafax
     * @return
     */
    private List<String> parseSafaxClientOutput(HashMap outSafax){
    	
    	String eventTypeString = "eventType";
    			    	
    	List<String> outList =  new ArrayList<>();
    	
    	List<HashMap> uconEvents = (List)outSafax.get("uconEvents");

    	for(HashMap event : uconEvents) {
    		
    		if (event.keySet().contains(eventTypeString) ) {
    			
    			String eventVal = (String) event.get(eventTypeString);
    			
    			if (! ignoreEvents.contains(eventVal)) {
    				outList.add(eventVal);	
    			}
    			
    		}
    		
    	}
    	
    	return outList;
    	
    }
    
    
    
	@Override
	public TestSuiteExecutionReport getReport() {		
		return executionReporter;
	}

}
