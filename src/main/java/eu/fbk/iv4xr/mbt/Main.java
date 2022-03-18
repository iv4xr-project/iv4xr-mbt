/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.evosuite.ga.FitnessFunction;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitMutationManager;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestExecutionHelper;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteReporter;
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
		
		CoverageTracker coverageTracker = generationStrategy.getCoverageTracker();

		if (!line.hasOption("silent_mode")) {
			// write tests to disk
			writeTests (solution, coverageTracker.getCoverageMap());
		}
		
		// write model on disk
		writeModel(line);
		
		
		// write statistics to disk
		writeStatistics (coverageTracker.getStatistics(), coverageTracker.getStatisticsHeader(),MBTProperties.STATISTICS_FILE());
		logger.info(coverageTracker.getStatistics());
		
		// print final coverage
		System.out.println();
		System.out.println("Final coverage: " + coverageTracker.getCoverage()*100 + "%");
		
		// if enabled, print uncovered goals
		if (org.evosuite.Properties.PRINT_MISSED_GOALS) {
			List<FitnessFunction<MBTChromosome>> uncoveredGoals = coverageTracker.getUncoveredGoals();
			printUncoveredGoals(uncoveredGoals);
		}
	}
	
	/**
	 * print out uncovered goals
	 */
	private void printUncoveredGoals(List<FitnessFunction<MBTChromosome>> uncoveredGoals) {
		System.out.println();
		for (FitnessFunction<MBTChromosome> uncoveredGoal : uncoveredGoals) {
			System.out.println(" - Uncovered goal: " + uncoveredGoal.toString());
		}
	}
	
	/**
	 * run mutation analysis
	 */
	private void runMutationAnalysis (CommandLine line) {
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
		
		// set parameters in MBTProperties and Properties
		setGlobalProperties (line);
		
		// load csv level
		File sutExecutableFile = new File(sutExecutable+".csv");
		String originalLevel = "";
		try {
			originalLevel = FileUtils.readFileToString(sutExecutableFile,Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// execute on wild type scenario
		LabRecruitsTestExecutionHelper executor = new LabRecruitsTestExecutionHelper(sutExecutableDir, sutExecutable, agentName, testsDir, maxCycles);
		executor.execute();
		
		// create mutations
		LabRecruitMutationManager mutManager = new LabRecruitMutationManager(originalLevel);
		List<String> mutatedSut = mutManager.getMutations();
		
		/*
		 *  select only tests that pass on the wild type scenario
		 */
		List<File> passedTests = new LinkedList<File>();
		// get the report of the executions
		LabRecruitsTestSuiteReporter executionReport = executor.getExecutionReport();
		// if test case pass save the location of the file
		for(AbstractTestSequence testCase : executionReport.getTestCases()) {
			if (executionReport.getTestCaseStatus(testCase)) {
				passedTests.add(executor.getTestCaseFile(testCase));
			}
		}
		
		/*
		 * prepare folder with passed test to test mutations
		 */
		
		// create folders
		String run_id = String.valueOf(System.currentTimeMillis());
		String outFolder = MBTProperties.MUTATION_ANALYSIS_FOLDER()+File.separator+run_id;
		File mutFolder = new File (outFolder);
		if (!mutFolder.exists()) {
			mutFolder.mkdirs();
		}//else {
			//try {
			///	FileUtils.deleteDirectory(mutFolder);
			//}catch (IOException e) {
				// TODO: handle exception
			//	e.printStackTrace();
			//}
			
		//}
		String mutTestPath = outFolder+File.separator+"tests";
		File mutTestFolder = new File (mutTestPath);
		if (!mutTestFolder.exists()) {
			mutTestFolder.mkdirs();
		}
		
		try {
			// copy pass tests in mutTestFolder
			for(File testFile : passedTests) {
				FileUtils.copyFileToDirectory(testFile, mutTestFolder);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	
		/*
		 * Run tests on each mutations
		 */
		Integer n_mutation_run = 0;
		Integer n_mutation_killed = 0;
		for (int i = 0; i < Math.min(mutatedSut.size(), MBTProperties.MAX_NUMBER_MUTATIONS); i++) {
			String sut = mutatedSut.get(i);
			// save sut
			String sutPath = outFolder+File.separator+"mutated_sut_"+i;
			File sutFile = new File(sutPath+".csv");
			try {
				FileUtils.writeStringToFile(sutFile, sut, Charset.defaultCharset(), false);
			}catch(IOException e) {
				e.printStackTrace();
			}
			// exec sut
			LabRecruitsTestExecutionHelper mutExecutor = new LabRecruitsTestExecutionHelper(sutExecutableDir, sutPath, agentName, mutTestPath, maxCycles);
			boolean testSuiteResult = mutExecutor.execute();
			
			
			// save debug information
			String statPath = outFolder+File.separator+"stat_mutated_sut_"+i+".csv";
			String debugHeader = mutExecutor.getDebugHeader();
			String debugData = mutExecutor.getDebutTableTable();
			
			try {
				FileUtils.writeStringToFile(new File(statPath), debugHeader+debugData, Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			n_mutation_run ++;
			if (!testSuiteResult) {
				n_mutation_killed ++;
			}
		}
		
		/*
		 * Save statistics about mutation analysis
		 */
		
		String mutStatHeader = "run_id,  n_tests, n_mutants, n_killed_mutants, wild_type_sut, test_folder\n";
		
		String mutStat =  run_id+ ",";
		mutStat = mutStat + passedTests.size() + ",";
		mutStat = mutStat + n_mutation_run + ",";
		mutStat = mutStat + n_mutation_killed + ",";
		mutStat = mutStat + sutExecutableFile + ",";
		if (passedTests.size() > 0 ) {
			mutStat = mutStat + passedTests.get(0).getParent() + "\n";	
		}else {
			mutStat = mutStat + "\n";
		}
		
		
		
		writeStatistics(mutStat, mutStatHeader, MBTProperties.MUTATION_STATISTIC_FILE());
		
	}
	
	/**
	 * write search statistics to statistics folder defined in MBTProperties
	 * If there exists one, update it. Otherwise, new file should be created.
	 * @param statistics
	 * @param statisticsHeader
	 */
	private void writeStatistics(String statistics, String statisticsHeader, String fileName) {
		// make sure stats folder exists
		File statsFolder = new File (MBTProperties.STATISTICS_DIR_NAME);
		if (!statsFolder.exists()) {
			statsFolder.mkdirs();
		}
		
		//File statsFile = new File (MBTProperties.STATISTICS_FILE);
		File statsFile = new File (fileName);
		
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
	private void writeTests(SuiteChromosome solution, Map<FitnessFunction<MBTChromosome>, MBTChromosome> coverageMap) {
		// make sure tests folder exists
		String testFolder = MBTProperties.TESTS_DIR() + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + MBTProperties.SessionId;
		File testsFolder = new File (testFolder);
		testsFolder.mkdirs();
		
		int count = 1;
		for (MBTChromosome testCase : solution.getTestChromosomes()) {
			String dotFileName = testFolder + File.separator + "test_" + count + ".dot";
			String txtFileName = testFolder + File.separator + "test_" + count + ".txt";
			String serFileName = testFolder + File.separator + "test_" + count + ".ser";
			File dotFile = new File (dotFileName);
			File txtFile = new File (txtFileName);
			
			// get the list of goals covered by this individual
			String coveredGoals = getGoveredGoalsAsComment (coverageMap, testCase);
			try {
				String testAsDot = ((AbstractTestSequence)testCase.getTestcase()).toDot();
				String testAsText = testCase.getTestcase().toString();
				FileUtils.writeStringToFile(dotFile, coveredGoals + testAsDot, Charset.defaultCharset());
				FileUtils.writeStringToFile(txtFile, coveredGoals + testAsText, Charset.defaultCharset());
				TestSerializationUtils.saveTestSequence((AbstractTestSequence) testCase.getTestcase(), serFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		
	}

	/**
	 * format the list of covered goals as commented strings to be prepended to each test (chromosome)
	 * @param set
	 * @return
	 */
	private String getGoveredGoalsAsComment(Map<FitnessFunction<MBTChromosome>, MBTChromosome> coverageMap,
			MBTChromosome testCase) {
		// first get the set of all covered goals by the given chromosome
		LinkedHashSet<FitnessFunction<MBTChromosome>> coveredGoals = new LinkedHashSet<>();
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() != null && entry.getValue().equals(testCase)) {
				coveredGoals.add(entry.getKey());
			}
		}
		StringBuffer buffer = new StringBuffer();
		String commentChar = "#";
		
		buffer.append(commentChar + " Total number of goals covered by this test: " + coveredGoals.size() + System.lineSeparator());
		for (FitnessFunction<MBTChromosome> goal : coveredGoals) {
			buffer.append(commentChar + " " + goal.toString() + System.lineSeparator());
		}
		buffer.append(System.lineSeparator());
		
		return buffer.toString();
	}

	/**
	 * Save EFSM model
	 */
	public void writeModel(CommandLine line) {
		String modelFolderName = MBTProperties.TESTS_DIR() + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + MBTProperties.SessionId + File.separator + "Model";
		File modelFolder = new File (modelFolderName);
		modelFolder.mkdirs();
		
		
			
		
		String modelFileName = modelFolderName + File.separator + "EFSM_model.ser";
		String levelFileName = modelFolderName + File.separator + "LabRecruits_level.csv";
		String modelDotFileName = modelFolderName + File.separator + "EFSM_model.dot";
		String modelFeaturesFileName = modelFolderName + File.separator + "EFSM_features.csv";
		
		File csvFile = new File (levelFileName);
		File dotFile = new File (modelDotFileName);
		File featureFile = new File (modelFeaturesFileName);
		
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		try {
			if (!line.hasOption("silent_mode")) {
				TestSerializationUtils.saveEFSM(efsm, modelFileName);
				FileUtils.writeStringToFile(dotFile, efsm.getDotString(), Charset.defaultCharset());
			}
			FileUtils.writeStringToFile(featureFile, efsm.getEfsmSummaryFeatures(), Charset.defaultCharset());
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
		
		Option mutationAnalysis = Option.builder("mutation_analysis")
				.argName("mutation_analysis")
				.type(String.class)
				.desc("execute mutation analysis on the actual system under test." 
						+ " Use -Dmax_number mutations=X to run on at most X mutions."+
						" (Deafault "+MBTProperties.MAX_NUMBER_MUTATIONS+")")
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
				.desc("search based test generation strategy, provide algorithm as -Dalgorithm=<AlgorithmName>")
				.build();
		
		Option tamer = Option.builder("planning")
				.argName("planning")
				.type(String.class)
				.desc("planning based test generation strategy")
				.build();
		
		Option silent = Option.builder("silent_mode")
				.argName("silent_mode")
				.type(String.class)
				.desc("save only execution statistics. Model and tests are not dumpped on disc")
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
		options.addOption(mutationAnalysis);
		options.addOption(executableDir);
		options.addOption(sutExecutable);
		options.addOption(testsDir);
		options.addOption(agentName);
		options.addOption(maxCycles);
		options.addOption(mosa);
		options.addOption(random);
		options.addOption(tamer);
		options.addOption(silent);
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
			
			// save stats
			writeStatistics(executor.getStatsTable() , executor.getStatHeader(), MBTProperties.EXECUTIONSTATISTICS_FILE() );
			
			// save debug data
			writeStatistics(executor.getDebutTableTable(), executor.getDebugHeader(), MBTProperties.EXECUTIONDEBUG_FILE());
			
			
			
			// save execution data for debug
			//String executorDebug = executor.getDebutTableTable();
			//String eexecutorDebugFileName = testsDir+"/execution_statistics.csv";
			//File debugFile = new File(eexecutorDebugFileName);
			//try {

			//	FileUtils.writeStringToFile(debugFile, executorDebug, Charset.defaultCharset());
				
			//} catch (IOException e) {
			//	e.printStackTrace();
			//}
			
			
			
			
		}else if (line.hasOption("mutation_analysis")) {			
			runMutationAnalysis(line);
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
				//LoggingUtils.getEvoLogger().error("Invalid value for property " + propertyName+": "+propertyValue+". Exception "+e.getMessage(),e);
				System.err.println("Invalid value for property " + propertyName + ": " + propertyValue);
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
		try {
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
		}catch(Exception e) {
			logger.error("Error when generating/executing tests for: " + MBTProperties.SUT_EFSM
					+ " with seed " + Randomness.getSeed()+". LR_Seed : " + MBTProperties.LR_seed, e);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException ex) {
//			}
			System.exit(1);
		}finally {
			System.exit(0);
		}
	}

}
