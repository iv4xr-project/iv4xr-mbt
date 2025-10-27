package eu.fbk.iv4xr.mbt.execution.on_sut.impl.lr;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import agents.LabRecruitsTestAgent;
import agents.tactics.GoalLib;
import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import game.LabRecruitsTestServer;
import game.Platform;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.ProgressStatus;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;
import world.BeliefState;
import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.impl.AplibConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.LabRecruitsTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibTestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * This class transform a test suite generated from an EFMS model of a
 * LabRecruits level into an aplib GoalStructure and run it on LabRecruits. The
 * EFSM model is made only of buttons and doors.
 * 
 * @author Davide Prandi
 *
 */
public class LabRecruitsConcreteTestExecutor implements AplibConcreteTestExecutor {

	// LabRecruits basic settings
	private String labRecruitesExeRootDir;
	private String levelFileName;
	private String levelFolder;
	private String agentName;
	protected EFSM model;

	private LabRecruitsTestAgent testAgent;
	private LabRecruitsEnvironment labRecruitsEnvironment;
	
	// number of cycle the execution a transition can take
	private int maxCyclePerGoal = 200;

	private AplibTestConcretizer testConcretizer;
	
	// basic reporting for test case status
	private TestSuiteExecutionReport testReporter;
	
	private LabRecruitsTestServer testServer;

	public LabRecruitsConcreteTestExecutor(){ }
	
	public LabRecruitsConcreteTestExecutor(EFSM model, String labRecruitesExeRootDir, String levelPath, String agentName, int maxCyclePerGoal) {
		this.model = model;
		this.labRecruitesExeRootDir = labRecruitesExeRootDir;
		// split level path	
		this.levelFileName = Paths.get(levelPath).getFileName().toString();
		this.levelFolder = Paths.get(levelPath).getParent().toString();
		this.agentName = agentName;
		this.maxCyclePerGoal = maxCyclePerGoal;
		testReporter = new TestSuiteExecutionReport();

		// open the server
		testServer = new LabRecruitsTestServer(false, Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));
		
		// set the configuration of the server
		LabRecruitsConfig lrCfg = new LabRecruitsConfig(levelFileName, levelFolder);
		
		// start LabRecruits environment
		labRecruitsEnvironment = new LabRecruitsEnvironment(lrCfg);
		
		// create the agent and attach the goal structure
		testAgent = new LabRecruitsTestAgent(agentName).attachState(new BeliefState())
				.attachEnvironment(labRecruitsEnvironment);
		// define the data collector and attach it to the agent
		var dataCollector = new TestDataCollector();
		testAgent.setTestDataCollector(dataCollector);
		
		this.testConcretizer = new LabRecruitsTestConcretizer(testAgent, model);
	}

	public void setMaxCyclePerGoal(int max) {
		this.maxCyclePerGoal = max;
	}

	public int getMaxCylcePerGoal() {
		return maxCyclePerGoal;
	}
	
	public TestSuiteExecutionReport getReport() {
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
			if (!testResult) {
				testSuiteResult = false;
			} 
		}

		// close the server
		if (testServer != null)
			testServer.close();

		return testSuiteResult;
	}

	// run a test case
	public boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException {

		// Start registering the time of the test suite execution
		long initialTime = System.currentTimeMillis();
		
		LinkedList<TestCaseExecutionReport> goalReporter = new LinkedList<TestCaseExecutionReport>();
		
		System.out.println("Executing: " + testcase.toString());


		// convert test case to a list of goal structure
		// each goal structure represent a transition
		AplibConcreteTestCase concreteTestCase = (AplibConcreteTestCase) testConcretizer.concretizeTestCase(testcase);
		List<GoalStructure> goals = concreteTestCase.getGoalStructures();

		// press play in Unity
		if (!labRecruitsEnvironment.startSimulation()) {
			System.err.println("Unity refuses to start the Simulation!");
			return false;
		}
		
		String status = "SUCCESS";
		// iterate over goals
		// 
		for (int i = 0; i < goals.size(); i++) {
		//for (GoalStructure g : goals) {
			GoalStructure g = goals.get(i);
			testAgent.setGoal(g);
			System.err.println("Testing "+testcase.getPath().getTransitionAt(i).toString());

		
			// try to execute the test case
			int nCycle = 0;
			while (g.getStatus().inProgress()) {
				testAgent.update();
				if (testAgent.getTestDataCollector().getNumberOfFailVerdictsSeen() > 0) {
					// stop the time
					long finalTime = System.currentTimeMillis();
					long timeDuration = finalTime - initialTime;
					
					String err = "Verdict " + testAgent.getTestDataCollector().getLastFailVerdict().toString() + " failed";
					System.err.println(err);
					AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
					goalRep.addReport(g, err, testcase.getPath().getTransitionAt(i), getGoalStatus(g));
					goalReporter.add(goalRep);
					testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE, timeDuration);
					return false;
				}
				Thread.sleep(20);
				nCycle++;
				if (nCycle > maxCyclePerGoal) {
					// stop the time
					long finalTime = System.currentTimeMillis();
					long timeDuration = finalTime - initialTime;
					
					String err = "The goal cannot be satisfied in " + maxCyclePerGoal + " cycles";
					System.err.println(err);
					AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
					goalRep.addReport(g, err, testcase.getPath().getTransitionAt(i), getGoalStatus(g));
					goalReporter.add(goalRep);
					testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE, timeDuration);
					return false;
				}
			}
			AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
			goalRep.addReport(g, "Pass", testcase.getPath().getTransitionAt(i), getGoalStatus(g));
			goalReporter.add(goalRep);
			if (!g.getStatus().success()) {
				status = "FAIL";
			}
		}
		
		// stop the time
		long finalTime = System.currentTimeMillis();
		long timeDuration = finalTime - initialTime;
		
		if (status == "SUCCESS") {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.TRUE, timeDuration);
			return true;
		}else {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE, timeDuration);
			return false;
		}
		
		
	}

	// covert the goal status of a goal structure to a string
	private String getGoalStatus(GoalStructure goal) {
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
	
	

}
