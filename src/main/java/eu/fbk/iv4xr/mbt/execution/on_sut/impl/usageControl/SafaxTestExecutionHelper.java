package eu.fbk.iv4xr.mbt.execution.on_sut.impl.usageControl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import eu.fbk.iv4xr.mbt.execution.on_sut.AplibConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibTestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestExecutionHelper;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

public class SafaxTestExecutionHelper extends TestExecutionHelper {

	
	public SafaxTestExecutionHelper(String testsDir) {
		model = parseModel(testsDir);
		testToFileMap = new LinkedHashMap<AbstractTestSequence, File>();
		testSuite = parseTests(testsDir);
		testsFolder = testsDir;
		
		testExecutor = new SafaxConcreteTestExecutor(model,testsFolder, testToFileMap);
	}
	
	@Override
	public String getDebugTableTable() {
			
		TestSuiteExecutionReport testStuiteReporter = testExecutor.getReport();

		String statsTable = "";
		
		// iterate over test cases
		for(AbstractTestSequence testCase : testToFileMap.keySet()) {
			String fileName = testToFileMap.get(testCase).getName();
			String testStatus = testStuiteReporter.getTestCaseStatus(testCase).toString();
			// get data for each transition in the test case
			List<TestCaseExecutionReport> caseReport = testStuiteReporter.getTestCaseReport(testCase);
			
			for(TestCaseExecutionReport rep : caseReport   ) {
				String transition = rep.getTransition().toString();
				String transitionResponse = rep.getResponse();
				String transitionGoal = "";
				String transitionGoalStatus = "";
				
				String tbLine = run_id + "," +
								fileName + "," + 
								testStatus + "," +
								transition + "," +
								transitionResponse  + "," +
								transitionGoal  + "," +
								transitionGoalStatus + "\n";// + ", " +
				statsTable = statsTable + tbLine;
			}
		}
			
			
		return statsTable;
	}

	@Override
	public String getStatsTable() {
				
		TestSuiteExecutionReport executionReport = testExecutor.getReport();
		
		String id = run_id;
		String folder = testsFolder;
		
		Integer numberOfPassedTestCases = executionReport.getNumberOfPassedTestCases();
		Integer numberOfTestCases = executionReport.getNumberOfTestCases();
		
		String executionData = id+","+folder+","+String.valueOf(numberOfTestCases)+","+
				String.valueOf(numberOfPassedTestCases)+","+"-1"+"\n";
		
		
		return executionData;
		
		
	}

}
