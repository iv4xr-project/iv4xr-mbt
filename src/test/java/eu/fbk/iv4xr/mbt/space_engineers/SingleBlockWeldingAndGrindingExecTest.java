package eu.fbk.iv4xr.mbt.space_engineers;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.assertNotNull;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import environments.SeAgentState;
import environments.SeEnvironment;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

import eu.fbk.iv4xr.mbt.efsm.spaceEngineering.SingleBlockWeldingAndGrinding.seActions;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.SpaceEngineersUtils;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;

import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;

import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import spaceEngineers.controller.ContextControllerWrapper;

import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;
import spaceEngineers.controller.SpaceEngineersTestContext;
import spaceEngineers.iv4xr.goal.GoalBuilder;
import spaceEngineers.iv4xr.goal.TacticLib;
import spaceEngineers.model.DefinitionId;
import spaceEngineers.model.ToolbarLocation;

public class SingleBlockWeldingAndGrindingExecTest {

	// Variables definition
	Long longSleepTime = 1000l;
	Long shortSleepTime = 500l;
	Long randomSeed = 812244l;
	
	//String worldId = "amaze";
	String worldId = "simple-place-grind-torch";
	String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;
	
	
	
	DefinitionId blockType;
	SpaceEngineersTestContext context;
	ToolbarLocation blockLocation;
	DefinitionId welder;
	ToolbarLocation welderLocation;
	DefinitionId grinder;
	ToolbarLocation grinderLocation;
	ContextControllerWrapper controllerWrapper;
	SeEnvironment theEnv;
	TestDataCollector dataCollector;
	SeAgentState myAgentState;
	TestAgent testAgent;
	SpaceEngineers se;
	
	SpaceEngineersJavaProxyBuilder proxyBuilder;
	Map<String, ToolbarLocation> blockTypeToToolbarLocation;
	
	private void initSE() {
		blockType = DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock");
		context = new SpaceEngineersTestContext();
		blockLocation = new ToolbarLocation(1, 0);
		welder = DefinitionId.Companion.physicalGun("Welder2Item");
		welderLocation = new ToolbarLocation(2, 0);
		grinder = DefinitionId.Companion.physicalGun("AngleGrinder2Item");
		grinderLocation = new ToolbarLocation(3, 0);
		
		blockTypeToToolbarLocation = context.getBlockTypeToToolbarLocation();
		blockTypeToToolbarLocation.put(blockType.getType(), blockLocation);
		proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		
	}
	
	private void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void setToolbar() {
		controllerWrapper.getItems().setToolbarItem(blockType, blockLocation);
		// Setup welder in the toolbar.new
		controllerWrapper.getItems().setToolbarItem(welder, welderLocation);
		// Setup grinder in the toolbar.
		controllerWrapper.getItems().setToolbarItem(grinder, grinderLocation);
	}
	
	private void startSE () {
		//SpaceEngineers se = proxyBuilder.localhost(agentId);
		se = proxyBuilder.localhost(agentId);
		controllerWrapper = new ContextControllerWrapper(se, context);
		sleep(longSleepTime);
		theEnv = SpaceEngineersUtils.createSeEnvWithSimpleMap(controllerWrapper);
		// theEnv = new SeEnvironment(worldId, controllerWrapper);
		dataCollector = new TestDataCollector();
		myAgentState = new SeAgentState(agentId);
		testAgent = new TestAgent(agentId, "some role name, else nothing");
		testAgent.attachState(myAgentState);
		testAgent.attachEnvironment(theEnv);
		testAgent.setTestDataCollector(dataCollector);
		
		
		// We load the scenario.

		theEnv.loadWorld();	

		// needed for slow pc
		sleep(longSleepTime);
		// Setup block in the toolbar.
		

	    // We observe for new blocks once, so that current blocks are not going to be
		// considered "new".
		//theEnv.observeForNewBlocks();
		//sleep(longSleepTime);
	}
	
	private void resetSE() {
		blockType = null;
		context  = null;
		blockLocation = null;
		welder = null;
		welderLocation = null;
		grinder = null;
		grinderLocation = null;
		controllerWrapper = null;
		theEnv = null;
		dataCollector = null;
		myAgentState = null;
		testAgent = null;
	}
	
	private List<MBTChromosome> getTestCases() {
		
		MBTProperties.SUT_EFSM = "se.weld_and_grind";
		MBTProperties.RANDOM_SEED = 38743l;
		//MBTProperties.MODELCRITERION[0] = ModelCriterion.KTRANSITION;
		
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		
		EFSMTestExecutor.getInstance().resetEFSM();
		
		SearchBasedStrategy sbStrategy = new SearchBasedStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests();
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		return testChromosomes;
		
	}
	
	
	
	public List<GoalStructure> testCaseToGoal(MBTChromosome testCase ){
		
		LinkedList<GoalStructure> outList = new LinkedList<>();
		
		AbstractTestSequence abstractTestSequence = (AbstractTestSequence) testCase.getTestcase();
		Path path = abstractTestSequence.getPath();
				
		double energy =  100;
		double newEnergy;
		var goals = new GoalBuilder();
		var tactics = new TacticLib();
		
		GoalStructure testingTask = goals.alwaysSolved(
				tactics.sleep(shortSleepTime));
		
		for(EFSMTransition t : path.getTransitions()) {
			
			if (t.getSrc().getId() == "block_exists" && t.getTgt().getId() == "block_exists") {
				
				seActions value = (seActions)t.getOutParameter().getParameter().getVariable("action").getValue();
				switch (value) {
				case grind_10:
					newEnergy = Double.min(1, Double.max(0, (energy-10)/100));
					GoalStructure testingTask_grind_10 = SEQ(
							goals.lastBuiltBlockIntegrityIsBelow(newEnergy,
									SEQ(tactics.equip(grinderLocation),
											tactics.sleep(shortSleepTime),
											tactics.startUsingTool())),
							goals.alwaysSolved(SEQ(tactics.endUsingTool(),
												   tactics.sleep(shortSleepTime))));
					outList.add(testingTask_grind_10);
					//testingTask = SEQ(testingTask,testingTask_grind_10);
					
//					outList.add(goals.lastBuiltBlockIntegrityIsBelow(newEnergy,
//							SEQ(tactics.equip(grinderLocation),
//							tactics.sleep(shortSleepTime),
//							tactics.startUsingTool())));
//					outList.add(goals.alwaysSolved(SEQ(tactics.endUsingTool(), 
//						    tactics.sleep(shortSleepTime))));
							
					energy = energy - 10;
					break;
				case grind_20:
					newEnergy = Double.min(1, Double.max(0, (energy-20)/100));
					GoalStructure testingTask_grind_20 = SEQ(
							goals.lastBuiltBlockIntegrityIsBelow(newEnergy,
									SEQ(tactics.equip(grinderLocation),
											tactics.sleep(shortSleepTime),
											tactics.startUsingTool())),
							goals.alwaysSolved(SEQ(tactics.endUsingTool(), 
													tactics.sleep(shortSleepTime))));
					outList.add(testingTask_grind_20);
					//testingTask = SEQ(testingTask,testingTask_grind_20);
//					outList.add(goals.lastBuiltBlockIntegrityIsBelow(newEnergy,
//							SEQ(tactics.equip(grinderLocation),
//							tactics.sleep(shortSleepTime),
//							tactics.startUsingTool())));
//					outList.add(goals.alwaysSolved(SEQ(tactics.endUsingTool(), 
//						    tactics.sleep(shortSleepTime))));
					energy = energy - 20;
					break;
				case weld_10:
					newEnergy = Double.min(1, Double.max(0, (energy+10)/100));
					GoalStructure testingTask_weld_10 = SEQ(
						goals.lastBuiltBlockIntegrityIsAbove(newEnergy,
								SEQ(tactics.equip(welderLocation),
									tactics.sleep(shortSleepTime),
									tactics.startUsingTool())),
						goals.alwaysSolved(SEQ(tactics.endUsingTool(),
												tactics.sleep(shortSleepTime))));
					outList.add(testingTask_weld_10);
					//testingTask = SEQ(testingTask,testingTask_weld_10);
//					outList.add(goals.lastBuiltBlockIntegrityIsAbove(newEnergy,
//							SEQ(tactics.equip(welderLocation),
//							tactics.sleep(shortSleepTime),
//							tactics.startUsingTool())));
//					outList.add(goals.alwaysSolved(SEQ(tactics.endUsingTool(), 
//						    tactics.sleep(shortSleepTime))));
					energy = energy + 10;					
					break;
				case weld_20:
					newEnergy = Double.min(1, Double.max(0, (energy+20)/100));
					GoalStructure testingTask_weld_20 = SEQ(
						goals.lastBuiltBlockIntegrityIsAbove(newEnergy,
								SEQ(tactics.equip(welderLocation),
									tactics.sleep(shortSleepTime),
									tactics.startUsingTool())),
						goals.alwaysSolved(SEQ(tactics.endUsingTool(),
												tactics.sleep(shortSleepTime))));
					outList.add(testingTask_weld_20);
//					outList.add(goals.lastBuiltBlockIntegrityIsAbove(newEnergy,
//							SEQ(tactics.equip(welderLocation),
//							tactics.sleep(shortSleepTime),
//							tactics.startUsingTool())));
//					outList.add(goals.alwaysSolved(SEQ(tactics.endUsingTool(), 
//						    tactics.sleep(shortSleepTime))));
					//testingTask = SEQ(testingTask,testingTask_weld_20);
					energy = energy + 20;	
					break;
				default:
					
					break;
				} 
			}else if (t.getSrc().getId() == "block_exists" && t.getTgt().getId() == "block_not_exists") {
				//GoalStructure testingTask_destroy = goals.alwaysSolved(
				//		tactics.sleep(shortSleepTime));
				//outList.add(testingTask_destroy);
				
			}else if (t.getSrc().getId() == "block_not_exists" && t.getTgt().getId() == "block_exists") {
				GoalStructure testingTask_build = goals.blockOfTypeExists(
        				blockType.getType(), 
        				tactics.buildBlock(blockType.getType()));
				outList.add(testingTask_build);
				//testingTask = SEQ(testingTask,testingTask_build);
			}
			
		}
		//outList.add(goals.alwaysSolved(tactics.sleep(longSleepTime)));
		
		//return testingTask;
		//GoalStructure testingTask = SEQ(outList.toArray(new GoalStructure[0]));
		//return testingTask;
		return outList;
	}
	
	
	private void execTestingTask(GoalStructure testingTask) {
		
		testAgent.setGoal(testingTask);
		var i = 0;
		while (testingTask.getStatus().inProgress() && i <= 100) {
			sleep(shortSleepTime);
			testAgent.update();
//			System.out.println(i + " " + myAgentState.getAgentId() + " " + 
//			myAgentState.worldmodel().position.toString() + " " +
//			testingTask.showGoalStructureStatus());			
			i++;
			//System.out.println();
		}
	}
	
	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	public void WeldAndGrindTest() {
		
		// create test case
		List<MBTChromosome> testCases = getTestCases();
		
		// init SE variables
		initSE();
		startSE();
		setToolbar();
		
		for(MBTChromosome tc  : testCases) {
			System.out.println(System.lineSeparator()+"Abstract test case"+System.lineSeparator()+ tc.toString()+System.lineSeparator());
			
			theEnv.observeForNewBlocks();
			
			
 			List<GoalStructure> testCaseGoals = testCaseToGoal(tc);
			//GoalStructure testingTask = testCaseToGoal(tc);
			sleep(longSleepTime);
			
			//execTestingTask(testingTask);
			
			for(GoalStructure testingTask : testCaseGoals) {
				execTestingTask(testingTask);
				//sleep(shortSleepTime);
			
			}
			
			sleep(longSleepTime);
			
			se.getSession().exitToMainMenu();
			
			sleep(longSleepTime);
			
			theEnv.loadWorld();
			
			sleep(longSleepTime);
			setToolbar();
			sleep(longSleepTime);
			//sleep(10000);
			//resetSE();
			
//			SpaceEngineers se = proxyBuilder.localhost(agentId);
//			sleep(20000);
//			try {
//				se.getSession().exitToMainMenu();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//			sleep(10000);
			//Session session = se.getSession();
			//session.exitGame();
			//System.out.println();
			//resetSE();
//			sleep(1000);
//			try {
//				initSE();
//				startSE();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
			//
			//sleep(1000);
		}
		
		
		se.getSession().exitToMainMenu();
		theEnv.close();
		System.out.println();
		
	}
	
}
