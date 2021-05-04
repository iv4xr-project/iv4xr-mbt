package eu.fbk.iv4xr.mbt.execution.labrecruits;

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
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.ProgressStatus;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;
import world.BeliefState;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
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
public class LabRecruitsTestSuiteExecutor {

	// LabRecruits basic settings
	private String labRecruitesExeRootDir;
	private String levelFileName;
	private String levelFolder;
	private String agentName;

	// number of cycle the execution a transition can take
	private Integer maxCyclePerGoal = 200;

	// basic reporting for test case status
	private LabRecruitsTestSuiteReporter testReporter;

	public LabRecruitsTestSuiteExecutor(String labRecruitesExeRootDir, String levelPath, String agentName, Integer maxCyclePerGoal) {
		this.labRecruitesExeRootDir = labRecruitesExeRootDir;
		// split level path	
		this.levelFileName = Paths.get(levelPath).getFileName().toString();
		this.levelFolder = Paths.get(levelPath).getParent().toString();
		this.agentName = agentName;
		this.maxCyclePerGoal = maxCyclePerGoal;
		testReporter = new LabRecruitsTestSuiteReporter();
	}

	public void setMaxCycle(Integer max) {
		this.maxCyclePerGoal = max;
	}

	public LabRecruitsTestSuiteReporter getReport() {
		return testReporter;
	}
	
	
	/**
	 * We assume a valid solution
	 * 
	 * @param solution
	 * @return
	 * @throws InterruptedException
	 */
	public void executeTestSuite(SuiteChromosome solution)
			throws InterruptedException {

		// open the server
		LabRecruitsTestServer testServer = new LabRecruitsTestServer(false,
				Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));

		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			Boolean testResult = executeTestCase(testcase);
			if (!testResult) {
				//testReporter.put(testcase, false);
			} else {
				//testReporter.put(testcase, true);
			}
		}

		// close the server
		if (testServer != null)
			testServer.close();

	}

	// run a test case
	public Boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException {

		LinkedList<LabRecruitsTestCaseReporter> goalReporter = new LinkedList<LabRecruitsTestCaseReporter>();
		
		System.out.println("Executing: " + testcase.toString());

		// set the configuration of the server
		LabRecruitsConfig lrCfg = new LabRecruitsConfig(levelFileName, levelFolder);

		// start LabRecruits environment
		LabRecruitsEnvironment labRecruitsEnvironment = new LabRecruitsEnvironment(lrCfg);

		// create the agent and attach the goal structure
		LabRecruitsTestAgent testAgent = new LabRecruitsTestAgent(agentName).attachState(new BeliefState())
				.attachEnvironment(labRecruitsEnvironment);
		// define the data collector and attach it to the agent
		var dataCollector = new TestDataCollector();
		testAgent.setTestDataCollector(dataCollector);

		// convert test case to a list of goal structure
		// each goal structure represent a transition
		List<GoalStructure> goals = convertTestCaseToGoalStructure(testAgent, testcase);

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
					String err = "Verdict " + testAgent.getTestDataCollector().getLastFailVerdict().toString() + " failed";
					System.err.println(err);
					LabRecruitsTestCaseReporter goalRep = new LabRecruitsTestCaseReporter();
					goalRep.addReport(g, err, testcase.getPath().getTransitionAt(i), getGoalStatus(g));
					goalReporter.add(goalRep);
					testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE);
					return false;
				}
				Thread.sleep(20);
				nCycle++;
				if (nCycle > maxCyclePerGoal) {
					String err = "The goal cannot be satisfied in " + maxCyclePerGoal + " cycles";
					System.err.println(err);
					LabRecruitsTestCaseReporter goalRep = new LabRecruitsTestCaseReporter();
					goalRep.addReport(g, err, testcase.getPath().getTransitionAt(i), getGoalStatus(g));
					goalReporter.add(goalRep);
					testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE);
					return false;
				}
			}
			LabRecruitsTestCaseReporter goalRep = new LabRecruitsTestCaseReporter();
			goalRep.addReport(g, "Pass", testcase.getPath().getTransitionAt(i), getGoalStatus(g));
			goalReporter.add(goalRep);
			if (!g.getStatus().success()) {
				status = "FAIL";
			}
		}
	
		if (status == "SUCCESS") {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.TRUE);
			return true;
		}else {
			testReporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE);
			return false;
		}
		
		
	}

	// sample code for translating EFSM test case into a goal structure
	private List<GoalStructure> convertTestCaseToGoalStructure(TestAgent agent, AbstractTestSequence tc) {
		Path path = tc.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		for (EFSMTransition t : listTransitions) {
			GoalStructure transitionGoals = convertEFMSTransitionToGoal(agent, t);
			subGoals.add(transitionGoals);
		}
		return subGoals;
		// GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0]));
		// return testingTask;
	}

	private GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t) {
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		// start refreshing the origin state
		// subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getSrc())));
		// look at src and tgt state to understand the type of transition
		if (t.getSrc().equals(t.getTgt())) {
			// if self loop we are pressing a button
			subGoals.add(GoalLib.entityInteracted(t.getTgt().getId()));
		} else if (oppositeDoorSides(t.getSrc(), t.getTgt())) {
			// to optimize
			String doorName = convertDoorSideToDoorName(t.getTgt().getId());
			subGoals.add(GoalLib.entityStateRefreshed(doorName));
			subGoals.add(GoalLib.entityInvariantChecked(agent, doorName, doorName+"should be open", (WorldEntity e) -> e.getBooleanProperty("isOpen"))) ;
		} else {
			subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getTgt())));
		}
		if (subGoals.size() == 1) {
			return subGoals.get(0);
		}else {
			return SEQ(subGoals.toArray(new GoalStructure[0]));
		}
	}

	// convert a state to a string
	// for buttons it is simply the name
	// for doors, that are written as d_X_p or d_X_, we have to write doorX
	// and we use convertDoorSideToDoorName
	private String convertStateToString(EFSMState s) {
		if (isDoor(s)) {
			return convertDoorSideToDoorName(s.getId());
		} else {
			return s.getId();
		}
	}

	// take a string in the form d1p/d1m and return door1
	private String convertDoorSideToDoorName(String doorSide) {
		String tmp = doorSide.substring(1, doorSide.length() - 1);
		return "door" + tmp;
	}
	
	// check if a state is a door looking at the first character
	private Boolean isDoor(EFSMState s) {
		String name = s.getId();
		String b = name.substring(0, 1);
		if (name.substring(0, 1).equals("b")) {
			return false;
		} else {
			return true;
		}
	}

	// check if 2 states represent the opposite sites of a door
	private Boolean oppositeDoorSides(EFSMState s, EFSMState t) {
		if (!isDoor(s) || !isDoor(t)) {
			// one of the state is not a door
			return false;
		} else if (convertDoorSideToDoorName(s.toString()).equals(convertDoorSideToDoorName(t.toString()))
				&& s.toString() != t.toString()) {
			return true;
		} else {
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
