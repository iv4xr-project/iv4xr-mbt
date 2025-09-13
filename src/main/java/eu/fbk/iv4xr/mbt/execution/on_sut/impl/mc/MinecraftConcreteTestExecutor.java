package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import eu.fbk.iv4xr.mbt.concretization.GenericTestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.ConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * This class transform a test suite generated from an EFMS model of a
 * LabRecruits level into an aplib GoalStructure and run it on LabRecruits. The
 * EFSM model is made only of buttons and doors.
 * 
 * @author Davide Prandi
 *
 */
public class MinecraftConcreteTestExecutor implements ConcreteTestExecutor {

	// LabRecruits basic settings
	private String mineflayerTestDir;
	private String levelPath;

	private TestSuiteExecutionReport testReporter;
	// number of cycle the execution a transition can take
	private int maxCyclePerGoal = 200;

	private GenericTestConcretizer testConcretizer;

	public MinecraftConcreteTestExecutor(){ }
	
	public MinecraftConcreteTestExecutor(String mineflayerTestDir, String levelPath, String mcServerAddress) {
		this.mineflayerTestDir = mineflayerTestDir;
		// split level path	
		this.levelPath = levelPath;


		this.testReporter = new TestSuiteExecutionReport();
		
		// set the configuration of the server

		// define the data collector and attach it to the agent
		var dataCollector = new TestDataCollector();

		this.testConcretizer = new MinecraftTestConcretizer();
	}

	public void setMaxCyclePerGoal(int max) {
		this.maxCyclePerGoal = max;
	}

	public int getMaxCylcePerGoal() {
		return maxCyclePerGoal;
	}
	
	public TestSuiteExecutionReport getReport(){
		return testReporter;
	}
	
	
	/**
	 * We assume a valid solution
	 * 
	 * @param solution
	 * @return
	 * @throws InterruptedException
	 */
	public boolean executeTestSuite(SuiteChromosome solution)
			throws InterruptedException {

//		// open the server
//		LabRecruitsTestServer testServer = new LabRecruitsTestServer(false,
//				Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));

		boolean testSuiteResult = true;
		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			Boolean testResult = executeTestCase(testcase);
			
		}

		return testSuiteResult;
	}

	// run a test case
	public boolean executeTestCase(AbstractTestSequence testcase) {		
		// convert test case to a list of goal structure
		// each goal structure represent a transition
		testConcretizer.concretizeTestCase(testcase);
		
		String status = "SUCCESS";
		// iterate over goals
		// 

		/*
		if (status == "SUCCESS") {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.TRUE, timeDuration);
			return true;
		}else {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE, timeDuration);
			return false;
		}
		*/
		return true;
	}
}
