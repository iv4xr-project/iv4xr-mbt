package eu.fbk.iv4xr.mbt.execution.on_sut;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;

/**
 * A class that loads tests from disk and executs them on a given LabRecruits binary
 * @author kifetew
 *
 */
public abstract class TestExecutionHelper {

	protected ConcreteTestExecutor testExecutor;
	protected SuiteChromosome testSuite;
	
	protected String debugHeader = "run_id,testCase,testCaseStatus,transition,transitionReponse,transitionGoal(s),"+
			"transitionGoalStatus\n" ;
	
	protected String statHeader = "run_id,folder,n_test,n_test_passed,time,maxCyclePerGoal\n";
	
	// save the map between the file and the test case
	protected LinkedHashMap<AbstractTestSequence, File > testToFileMap;
	
	// test folder
	protected String testsFolder;
	
	// id of the run
	protected String run_id;
	
//	public TestExecutionHelper(String lrExecutableDir, String lrLevelPath, String agentName, String testsDir, Integer maxCyclePerGoal) {
//		testExecutor = new LabRecruitsTestSuiteExecutor(lrExecutableDir, lrLevelPath, agentName, maxCyclePerGoal);
//		testToFileMap = new LinkedHashMap<AbstractTestSequence,File>();
//		testSuite = parseTests (testsDir);
//		testsFolder = testsDir;
//		
//	}
	
	public File getTestCaseFile(AbstractTestSequence testSequence) {
		if (testToFileMap.containsKey(testSequence)) {
			return testToFileMap.get(testSequence);
		}else {
			throw new RuntimeException(testSequence.toString()+" not present");
		}
	}
	
	
	// setter and getter
	public String getStatHeader() {
		return statHeader;
	}
	
	public String getDebugHeader() {
		return debugHeader;
	}

	/**
	 * Load serialized tests into a SuiteChromosome object
	 * @param testsDir
	 * @return
	 */
	protected  SuiteChromosome parseTests(String testsDir) {
		SuiteChromosome suite = new SuiteChromosome();
		try {
			WildcardFileFilter fileFilter = WildcardFileFilter.builder().setWildcards("*.ser").get();
			Collection<File> files = FileUtils.listFiles(new File(testsDir), fileFilter, null);
			for (File file : files) {
				AbstractTestSequence test = TestSerializationUtils.loadTestSequence(file.getAbsolutePath());
				MBTChromosome chromosome = new MBTChromosome();
				chromosome.setTestcase(test);
				suite.addTest(chromosome ); 
				// save file to test map
				testToFileMap.put(test,file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suite;
	}

	public boolean execute() {
		boolean success = true;
		try {
			this.run_id = String.valueOf(System.currentTimeMillis());
			success = testExecutor.executeTestSuite(testSuite);
		} catch (InterruptedException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	


	/*
	 * Create statistics table
	 */
	public String getDebutTableTable(){
		TestSuiteExecutionReport testStuiteReporter = testExecutor.getReport();

		String statsTable = "";
		
		// iterate over test cases
		for(AbstractTestSequence testCase : testToFileMap.keySet()) {
	
			// to fix
			String fileName = testToFileMap.get(testCase).getName();
			String testStatus = testStuiteReporter.getTestCaseStatus(testCase).toString();
			// get data for each transition in the test case
			List<TestCaseExecutionReport> caseReport = testStuiteReporter.getTestCaseReport(testCase);
			for(TestCaseExecutionReport rep : caseReport   ) {
				String transition = rep.getTransition().toString();
				String transitionResponse = rep.getResponse();
				String transitionGoal = "";
				String transitionGoalStatus = getGoalStatus(rep.getGoal());
				//String transitionGoalResponse = rep.getResponse();
				
				String tbLine = run_id + "," +
								fileName + "," + 
								testStatus + "," +
								transition + "," +
								transitionResponse  + "," +
								transitionGoal  + "," +
								transitionGoalStatus + "\n";// + ", " +
								//transitionGoalResponse + "\n";
				statsTable = statsTable + tbLine;
			}
			
		}
		return statsTable;
	}
	
	public String getStatsTable() {

		TestSuiteExecutionReport testStuiteReporter = testExecutor.getReport();

		String id = run_id;
		String folder = testsFolder;
		
		Long time = testStuiteReporter.getTestSuiteTime();
		Integer n_cases = 0;
		Integer n_passed_cases = 0;
		
		
		// iterate over test cases
		for(AbstractTestSequence testCase : testToFileMap.keySet()) {
			n_cases ++;
			if (testStuiteReporter.getTestCaseStatus(testCase)) {
				n_passed_cases ++;
			}
		}
		
		String statsTable = id+","+folder+","+String.valueOf(n_cases)+","+
				String.valueOf(n_passed_cases)+","+String.valueOf(time)+", "+
				String.valueOf(testExecutor.getMaxCylcePerGoal())+"\n";
		return(statsTable);
	}
	
	// covert the goal status of a goal structure to a string
	protected String getGoalStatus(GoalStructure goal) {
		if (goal instanceof PrimitiveGoal) {
			return goal.getStatus().toString();
		}else {
			String out = "";
			for(GoalStructure g : goal.getSubgoals()) {
				out = out + getGoalStatus(g) +"; ";
			}
			return out;
		}
	}
	
	public TestSuiteExecutionReport getExecutionReport() {
		
		return testExecutor.getReport();
	}
	
	
	//public static void main(String[] args) {
		
//		String level_file = "/Users/kifetew/workspace/projects/iv4xr/MBT/iv4xr-mbt/mbt-files/tests/labrecruits.random_simple/MOSA/1619821255958/Model/LabRecruits_level";
//		String agentName = "Agent1";
//		String execDir = "/Users/kifetew/workspace/projects/iv4xr/MBT/iv4xr-mbt/";
//		String testsDir = "/Users/kifetew/workspace/projects/iv4xr/MBT/iv4xr-mbt/mbt-files/tests/labrecruits.random_simple/MOSA/1619821255958/";
//		Integer maxCyclePerGoal = 500;
		//SuiteChromosome suite = parseTests(testsDir);
//		System.out.println(suite.size());
//		for (MBTChromosome t : suite.getTestChromosomes()) {
//			System.out.println(t.toString());
//		}
//		LabRecruitsTestExecutionHelper helper = new LabRecruitsTestExecutionHelper(execDir, level_file, agentName, testsDir, maxCyclePerGoal);
	//	helper.execute();
	//}

}
