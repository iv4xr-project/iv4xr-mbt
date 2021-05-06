/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestExecutionHelper;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.fbk.iv4xr.mbt.strategy.CoverageTracker;
import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.PlanningBasedStrategy;
import eu.fbk.iv4xr.mbt.strategy.RandomTestStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

/**
 * @author kifetew
 *
 * Main entry point to the MBT
 */
public class Main {

	protected static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
	 * 
	 */
	public Main() {
	}

	
	private void runTestGeneration (CommandLine line) {
		
		// determine the test generation strategy
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		if (line.hasOption("random")) {
			generationStrategy = new RandomTestStrategy<MBTChromosome>();
		}
		
		if (line.hasOption("planning")) {
			generationStrategy = new PlanningBasedStrategy<MBTChromosome>();
		}
		
		// set parameters in MBTProperties and Properties
		setGlobalProperties (line);
		
		
		MBTProperties.SessionId = "" + System.currentTimeMillis();
		
		SuiteChromosome solution = generationStrategy.generateTests();
		
		// write tests to disk
		writeTests (solution);
		
		// write model on disk
		writeModel();
		
		// write statistics to disk
		CoverageTracker coverageTracker = generationStrategy.getCoverageTracker();
		writeStatistics (coverageTracker.getStatistics(), coverageTracker.getStatisticsHeader());
		System.out.println(coverageTracker.getStatistics());
		
	}
	
	/**
	 * write search statistics to statistics folder defined in MBTProperties
	 * If there exists one, update it. Otherwise, new file should be created.
	 * @param statistics
	 * @param statisticsHeader
	 */
	private void writeStatistics(String statistics, String statisticsHeader) {
		// make sure stats folder exists
		File statsFolder = new File (MBTProperties.STATISTICS_DIR);
		if (!statsFolder.exists()) {
			statsFolder.mkdirs();
		}
		
		File statsFile = new File (MBTProperties.STATISTICS_FILE);
		boolean exists = false;
		if (statsFile.exists()) {
			exists = true;
		}
		
		try {
			FileUtils.writeStringToFile(statsFile, (exists?statistics:statisticsHeader + statistics), Charset.defaultCharset(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Creates a new folder (currenttime) in the default TESTS folder,
	 * writes each test in a separate file (for now in .dot and .txt formats)
	 * @param solution
	 */
	private void writeTests(SuiteChromosome solution) {
		// make sure tests folder exists
		String testFolder = MBTProperties.TESTS_DIR + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + MBTProperties.SessionId;
		File testsFolder = new File (testFolder);
		testsFolder.mkdirs();
		
		int count = 1;
		for (MBTChromosome testCase : solution.getTestChromosomes()) {
			String dotFileName = testFolder + File.separator + "test_" + count + ".dot";
			String txtFileName = testFolder + File.separator + "test_" + count + ".txt";
			String serFileName = testFolder + File.separator + "test_" + count + ".ser";
			File dotFile = new File (dotFileName);
			File txtFile = new File (txtFileName);
			try {
				FileUtils.writeStringToFile(dotFile, ((AbstractTestSequence)testCase.getTestcase()).toDot(), Charset.defaultCharset());
				FileUtils.writeStringToFile(txtFile, testCase.getTestcase().toString(), Charset.defaultCharset());
				TestSerializationUtils.saveTestSequence((AbstractTestSequence) testCase.getTestcase(), serFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		
	}

	/**
	 * Save EFSM model
	 */
	public void writeModel() {
		String modelFolderName = MBTProperties.TESTS_DIR + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + MBTProperties.SessionId + File.separator + "Model";
		File modelFolder = new File (modelFolderName);
		modelFolder.mkdirs();
		
		String modelFileName = modelFolderName + File.separator + "EFSM_model.ser";
		String levelFileName = modelFolderName + File.separator + "LabRecruits_level.csv";
		String modelDotFileName = modelFolderName + File.separator + "EFSM_model.dot";
		
		File csvFile = new File (levelFileName);
		File dotFile = new File (modelDotFileName);
		
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		try {
			
			TestSerializationUtils.saveEFSM(efsm, modelFileName);
			FileUtils.writeStringToFile(dotFile, efsm.getDotString(), Charset.defaultCharset());
			// if csv is available
			if (efsm.getEFSMString() != "") {
				FileUtils.writeStringToFile(csvFile, efsm.getEFSMString(), Charset.defaultCharset());		
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void executeForDebug(AbstractTestSequence testcase) {
//		TestExecutor executor = new EFSMTestExecutor<>();
		ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
		if (!executionResult.isSuccess()) {
			System.err.println("INVALID: " + testcase.toDot());
		}
	}

	/**
	 * TODO should no longer be necessary, options can now be passed as commandline arguments -Doption=value
	 */
	private void setProperties () {
//		MBTProperties.SEARCH_BUDGET = 500;
//		MBTProperties.LR_mean_buttons = 1;
//		MBTProperties.LR_n_buttons = 20;
//		MBTProperties.LR_n_doors = 10 ;
//		MBTProperties.SUT_EFSM = "labrecruits.random_default";
//		MBTProperties.LR_seed = 370327;
		
		
		MBTProperties.LR_seed = 325439;
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_buttons = 40;
		MBTProperties.LR_n_doors = 28;
	}
	

	
	/**
	 * TODO add proper optios here
	 * @return
	 */
	public static Options getCommandLineOptions() {
		Options options = new Options();

		Option help = new Option("help", "print this message");

		Option execOnSut = Option.builder("exec_on_sut")
				.argName("exec_on_sut")
				.type(String.class)
				.desc("execute tests on the actual system under test")
				.build();
		
		Option executableDir = new Option("sut_exec_dir", "sut_exec_dir", true, "Path to the SUT executable");
		executableDir.setArgs(1);
		
		Option sutExecutable = new Option("sut_executable", "sut_executable", true, "Path to the SUT executable, .csv file in case of LabRecruites");
		sutExecutable.setArgs(1);
		
		Option agentName = new Option("agent_name", "agent_name", true, "Name of the agent in the level, defaults to 'Agent1'");
		agentName.setArgs(1);
		
		Option testsDir = new Option("tests_dir", "tests_dir", true, "Path to the tests to be executed");
		testsDir.setArgs(1);
		
		Option maxCycles = new Option("max_cycles", "max_cycles", true, "Maximum number of cycles for executing a goal");
		maxCycles.setArgs(1);
		
		
		Option random = Option.builder("random")
				.argName("random")
				.type(String.class)
				.desc("random test generation strategy")
				.build();
		
		Option mosa = Option.builder("sbt")
				.argName("sbt")
				.type(String.class)
				.desc("Search based test generation strategy, provide algorithm as -Dalgorithm=<AlgorithmName>")
				.build();
		
		Option tamer = Option.builder("planning")
				.argName("planning")
				.type(String.class)
				.desc("planning based test generation strategy")
				.build();
		
		Option property   = Option.builder("D")
				.numberOfArgs(2)
				.argName("property=value")
				.valueSeparator('=')
				.required(false)
				.optionalArg(false)
				.type(String.class)
				.desc("use value for given property")
				.build();

		
		options.addOption(help);
		options.addOption(execOnSut);
		options.addOption(executableDir);
		options.addOption(sutExecutable);
		options.addOption(testsDir);
		options.addOption(agentName);
		options.addOption(maxCycles);
		options.addOption(mosa);
		options.addOption(random);
		options.addOption(tamer);
		options.addOption(property);
		return options;
	}
	
	public CommandLine parseCommandLine(String[] args, Options options) {
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Failed to parse commandline arguments.");
		}
		return line;
	}
	
	/**
	 * execute the actual operation, either test generation or execution
	 * @param line the commandline arguments entered by the user
	 * @param options the options available
	 */
	private void execute (CommandLine line, Options options) {
		//-exec_on_sut -sut_exec_dir=./gym/Linux -sut_executable=./mbt-files/tests/a.csv -agent_name=agent2 -tests_dir=./mbt-files/tests/a/
		/*
		 * -exec_on_sut: enable execution on LabRecruits
		 * -sut_exec_dir: folder containing gym
		 * -sut_executable: path to LabRecruits level without .csv extension
		 * -agent_name: name of the agent
		 * -tests_dir: folder containing the serialized test cases
		 */
		if (line.hasOption("exec_on_sut")) {
			String sutExecutableDir = "";
			String sutExecutable = "";
			String testsDir = "";
			String agentName = "";
			Integer maxCycles = 200;
			if (line.hasOption("sut_exec_dir")) {
				sutExecutableDir = line.getOptionValue("sut_exec_dir");
			}else {
				System.out.println("exec_on_sut option needs sut_exec_dir parameter");
			}
			
			if (line.hasOption("sut_executable")) {
				sutExecutable = line.getOptionValue("sut_executable");
			}
			
			if (line.hasOption("tests_dir")) {
				testsDir = line.getOptionValue("tests_dir");
			}else {
				System.out.println("exec_on_sut option needs tests_dir parameter");
			}
			
			if (line.hasOption("agent_name")) {
				agentName = line.getOptionValue("agent_name", "Agent1");
			}else {
				System.out.println("exec_on_sut option needs agent_name parameter, but not provided, using default: agent1");
			}
			
			if (line.hasOption("max_cycles")) {
				maxCycles = Integer.parseInt(line.getOptionValue("max_cycles", "200"));
			}else {
				System.out.println("exec_on_sut option needs max_cycles parameter, but not provided, using default: 200");
			}
			
			LabRecruitsTestExecutionHelper executor = new LabRecruitsTestExecutionHelper(sutExecutableDir, sutExecutable, agentName, testsDir, maxCycles);
			executor.execute();
		}else {
			
			runTestGeneration(line);
		}
		
	}


	/**
	 * Method adapted from the EvoSuite project
	 * Read commandline arguments and update the global properties classes (both MBT and EvoSuite)
	 * @param line
	 */
	private void setGlobalProperties(CommandLine line) {
		Properties properties = line.getOptionProperties("D");
		Set<String> propertyNames = new HashSet<>(MBTProperties.getParameters());

        for (String propertyName : properties.stringPropertyNames()) {

            if (!propertyNames.contains(propertyName)) {
				LoggingUtils.getEvoLogger().error("* Unknown property: " + propertyName);
//				throw new Error("Unknown property: " + propertyName);
			}

            String propertyValue = properties.getProperty(propertyName);

            try {
				MBTProperties.getInstance().setValue(propertyName, propertyValue);
				
			} catch (Exception e) {
				LoggingUtils.getEvoLogger().error("Invalid value for property " + propertyName+": "+propertyValue+". Exception "+e.getMessage(),e);
			}
            try {
            	//Do this also for Evosuite global properties, if they exsits
            	org.evosuite.Properties.getInstance().setValue(propertyName, propertyValue);
            }catch (Exception e) {
            	System.err.println("Unable to set Evosuite global property: " + propertyName);
            }
		}
		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		Options options = getCommandLineOptions();
		CommandLine line = main.parseCommandLine(args, options);
		logger.info("Performing requested operation ...");
		if (line == null || line.hasOption("help") || line.getOptions().length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("MBT", options);
		}else {
			main.execute(line, options);
		}
		logger.info("Requested operation completed.");
		System.exit(0);
	}

}
