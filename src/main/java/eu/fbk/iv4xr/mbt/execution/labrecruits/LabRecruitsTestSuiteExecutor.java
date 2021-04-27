package eu.fbk.iv4xr.mbt.execution.labrecruits;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.fail;

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
	private String agentName;

	// number of cycle the execution a transition can take
	private Integer maxCyclePerGoal = 200;

	// basic reporting for test case status
	private LinkedHashMap<AbstractTestSequence, Boolean> testReporter;

	public LabRecruitsTestSuiteExecutor(String labRecruitesExeRootDir, String levelFileName, String agentName) {
		this.labRecruitesExeRootDir = labRecruitesExeRootDir;
		this.levelFileName = levelFileName;
		this.agentName = agentName;
	}

	public void setMaxCycle(Integer max) {
		this.maxCyclePerGoal = max;
	}

	/**
	 * We assume a valid solution
	 * 
	 * @param solution
	 * @return
	 * @throws InterruptedException
	 */
	public LinkedHashMap<AbstractTestSequence, Boolean> executeTestSuite(SuiteChromosome solution)
			throws InterruptedException {

		// initialize test reporter
		testReporter = new LinkedHashMap<AbstractTestSequence, Boolean>();

		// open the server
		LabRecruitsTestServer testServer = testServer = new LabRecruitsTestServer(false,
				Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));

		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			Boolean testResult = executeTestCase(testcase);
			if (!testResult) {
				testReporter.put(testcase, false);
			} else {
				testReporter.put(testcase, true);
			}
		}

		// close the server
		if (testServer != null)
			testServer.close();

		return testReporter;
	}

	// run a test case
	public Boolean executeTestCase(AbstractTestSequence testcase) {

		System.out.println("Executing: " + testcase.toString());

		// set the configuration of the server
		LabRecruitsConfig lrCfg = new LabRecruitsConfig(levelFileName);

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

		for (GoalStructure g : goals) {
			testAgent.setGoal(g);

			// try to execute the test case
			int nCycle = 0;
			while (g.getStatus().inProgress()) {
				testAgent.update();
				if (testAgent.getTestDataCollector().getNumberOfFailVerdictsSeen() > 0) {
					System.err.println(
							"Verdict " + testAgent.getTestDataCollector().getLastFailVerdict().toString() + " failed");
					return false;
				}
				// Thread.sleep(5);
				nCycle++;
				if (nCycle > maxCyclePerGoal) {
					System.err.println("The goal cannot be satisfied in " + maxCyclePerGoal + " cycles");
					return false;
				}
			}
		}
		return true;
	}

	// sample code for translating EFSM test case into a goal structure
	private List<GoalStructure> convertTestCaseToGoalStructure(TestAgent agent, AbstractTestSequence tc) {
		Path path = tc.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		for (EFSMTransition t : listTransitions) {
			LinkedList<GoalStructure> transitionGoals = convertEFMSTransitionToGoal(agent, t);
			for (GoalStructure g : transitionGoals) {
				subGoals.add(g);
			}
		}
		return subGoals;
		// GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0]));
		// return testingTask;
	}

	private LinkedList<GoalStructure> convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t) {
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
			// subGoals.add(GoalLib.entityInvariantChecked(agent, doorName, doorName+"
			// should be open", (WorldEntity e) -> e.getBooleanProperty("isOpen"))) ;
		} else {
			subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getTgt())));
		}
		return subGoals;
	}

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

	private Boolean isDoor(EFSMState s) {
		String name = s.getId();
		String b = name.substring(0, 1);
		if (name.substring(0, 1).equals("b")) {
			return false;
		} else {
			return true;
		}
	}

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
}
