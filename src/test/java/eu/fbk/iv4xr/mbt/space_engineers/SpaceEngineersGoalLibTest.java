package eu.fbk.iv4xr.mbt.space_engineers;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import environments.SeAgentState;
import environments.SeEnvironment;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.concretization.impl.SpaceEngineersTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.tactics.SpaceEngineersGoalLib;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;

import spaceEngineers.controller.SpaceEngineersTestContext;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.Arrays;
import java.util.List;

public class SpaceEngineersGoalLibTest {

	Long longSleepTime = 2000l;
	Long shortSleepTime = 500l;
	String worldId = "LR_random_medium";
	String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;

	private void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void execTestingTask(GoalStructure testingTask, TestAgent testAgent) {

		testAgent.setGoal(testingTask);
		SeAgentState state = (SeAgentState) testAgent.state();

		var i = 0;
		while (testingTask.getStatus().inProgress() && i <= 100) {
			sleep(shortSleepTime);
			testAgent.update();
			System.out
					.println("Cycle " + i + ": " + testAgent.getId() + " @ " + state.worldmodel().position.toString());
			System.out.println(testingTask.showGoalStructureStatus());
			
			i++;
			// System.out.println();
		}
	}

	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	public void doNothingTest() {

		// load map
		SpaceEngineersTestContext context = new SpaceEngineersTestContext();
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		SpaceEngineers se = proxyBuilder.localhost(agentId);
		ContextControllerWrapper controllerWrapper = new ContextControllerWrapper(se, context);
		SeEnvironment theEnv = new SeEnvironment(worldId, controllerWrapper);
		theEnv.loadWorld();

		// wait the map is loaded
		sleep(longSleepTime);

		// add the agent
		var dataCollector = new TestDataCollector();
		var myAgentState = new SeAgentState(agentId);
		var testAgent = new TestAgent(agentId, "Executor of Nothing");
		testAgent.attachState(myAgentState);
		testAgent.attachEnvironment(theEnv);
		testAgent.setTestDataCollector(dataCollector);

		// add goal
		GoalStructure doNothing = SpaceEngineersGoalLib.doNothing();

		// execute
		execTestingTask(doNothing, testAgent);

		// close connection
		theEnv.close();

	}

	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	public void navigationAndInteractTest() {

		// load map
		SpaceEngineersTestContext context = new SpaceEngineersTestContext();
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		SpaceEngineers se = proxyBuilder.localhost(agentId);
		ContextControllerWrapper controllerWrapper = new ContextControllerWrapper(se, context);
		SeEnvironment theEnv = new SeEnvironment(worldId, controllerWrapper);
		theEnv.loadWorld();

		// wait the map is loaded
		sleep(longSleepTime);

		// add the agent
		var dataCollector = new TestDataCollector();
		var myAgentState = new SeAgentState(agentId);
		var testAgent = new TestAgent(agentId, "Navigator");
		testAgent.attachState(myAgentState);
		testAgent.attachEnvironment(theEnv);
		testAgent.setTestDataCollector(dataCollector);

//		b0-{toggle[TOGGLE]; }->b0
//		b0-{explore[EXPLORE]; }->d2p
//		d2p-{explore[EXPLORE]; }->d2m
//		d2m-{explore[EXPLORE]; }->b3
//		b3-{explore[EXPLORE]; }->b4
//		b4-{explore[EXPLORE]; }->d2m
//		d2m-{explore[EXPLORE]; }->b4
//		b4-{explore[EXPLORE]; }->d0p
//		d0p-{explore[EXPLORE]; }->b4
//		b4-{toggle[TOGGLE]; }->b4
//		b4-{toggle[TOGGLE]; }->b4
//		b4-{explore[EXPLORE]; }->b3
//		b3-{toggle[TOGGLE]; }->b3
//		b3-{explore[EXPLORE]; }->d1m
//		d1m-{explore[EXPLORE]; }->d1p
//		d1p-{explore[EXPLORE]; }->b7

		// add goal
		GoalStructure navigate = SEQ(
				// b0-{toggle[TOGGLE]; }->b0
				SpaceEngineersGoalLib.buttonInCloseRange("b0"), SpaceEngineersGoalLib.buttonIsInteracted("b0"),
				// b0-{explore[EXPLORE]; }->d2p
				SpaceEngineersGoalLib.doorInCloseRange("door2"),
				// d2p-{explore[EXPLORE]; }->d2m
				SpaceEngineersGoalLib.doorIsOpen("door2",testAgent),
				// d2m-{explore[EXPLORE]; }->b3
				SpaceEngineersGoalLib.buttonInCloseRange("b3"),
				// b3-{explore[EXPLORE]; }->b4
				SpaceEngineersGoalLib.buttonInCloseRange("b4"),
				// b4-{explore[EXPLORE]; }->d2m
				SpaceEngineersGoalLib.doorInCloseRange("door2"),
				// d2m-{explore[EXPLORE]; }->b4
				SpaceEngineersGoalLib.buttonInCloseRange("b4"),
				// b4-{explore[EXPLORE]; }->d0p
				SpaceEngineersGoalLib.doorInCloseRange("door0"),
				// d0p-{explore[EXPLORE]; }->b4
				SpaceEngineersGoalLib.buttonInCloseRange("b4"),
				// b4-{toggle[TOGGLE]; }->b4
				SpaceEngineersGoalLib.buttonInCloseRange("b4"), SpaceEngineersGoalLib.buttonIsInteracted("b4"),
				// b4-{toggle[TOGGLE]; }->b4
				SpaceEngineersGoalLib.buttonInCloseRange("b4"), SpaceEngineersGoalLib.buttonIsInteracted("b4"),
				// b4-{explore[EXPLORE]; }->b3
				SpaceEngineersGoalLib.buttonInCloseRange("b3"),
				// b3-{toggle[TOGGLE]; }->b3
				SpaceEngineersGoalLib.buttonInCloseRange("b3"), SpaceEngineersGoalLib.buttonIsInteracted("b3"),
				// b3-{explore[EXPLORE]; }->d1m
				SpaceEngineersGoalLib.doorInCloseRange("door1"),
				// d1m-{explore[EXPLORE]; }->d1p
				SpaceEngineersGoalLib.doorIsOpen("door1",testAgent),
				// d1p-{explore[EXPLORE]; }->b7
				SpaceEngineersGoalLib.buttonInCloseRange("b7")

		);

		// execute
		execTestingTask(navigate, testAgent);

		// close connection
		theEnv.close();
	}

	private List<MBTChromosome> getTestCases() {

		MBTProperties.SUT_EFSM = "labrecruits.random_medium";
		MBTProperties.RANDOM_SEED = 38743l;
		// MBTProperties.MODELCRITERION[0] = ModelCriterion.KTRANSITION;

		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull(efsm);

		EFSMTestExecutor.getInstance().resetEFSM();

		SearchBasedStrategy sbStrategy = new SearchBasedStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests();
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		return testChromosomes;

	}

	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	public void transitionToGoalTest() {

		// create test suite
		List<MBTChromosome> testCases = getTestCases();
		
		
		// load map
		SpaceEngineersTestContext context = new SpaceEngineersTestContext();
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		SpaceEngineers se = proxyBuilder.localhost(agentId);
		ContextControllerWrapper controllerWrapper = new ContextControllerWrapper(se, context);
		SeEnvironment theEnv = new SeEnvironment(worldId, controllerWrapper);
		theEnv.loadWorld();

		
		// wait the map is loaded
		sleep(longSleepTime);
		
		SpaceEngineersTestConcretizer concretizer = new SpaceEngineersTestConcretizer();
		
		for(MBTChromosome t : testCases) {
			System.out.println();
			System.out.println("Executing test case");
			System.out.println(t.toString());
			theEnv.loadWorld();
			sleep(longSleepTime);
			var dataCollector = new TestDataCollector();
			var myAgentState = new SeAgentState(agentId);
			var testAgent = new TestAgent(agentId, "Navigator");
			testAgent.attachState(myAgentState);
			testAgent.attachEnvironment(theEnv);
			testAgent.setTestDataCollector(dataCollector);
			List<GoalStructure> concretizeTestCase = concretizer.concretizeTestCase(testAgent, (AbstractTestSequence)t.getTestcase() );
			for(GoalStructure g: concretizeTestCase) {
				execTestingTask(g, testAgent);
			}
			se.getSession().exitToMainMenu();
			//theEnv.close();
			sleep(longSleepTime);
		}
		
		
		
		
	}
}
