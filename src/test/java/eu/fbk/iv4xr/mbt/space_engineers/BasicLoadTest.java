package eu.fbk.iv4xr.mbt.space_engineers;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import environments.SeAgentState;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.SpaceEngineersUtils;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import nl.uu.cs.aplib.mainConcepts.Environment;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;
import spaceEngineers.controller.SpaceEngineersTestContext;
import spaceEngineers.iv4xr.goal.GoalBuilder;
import spaceEngineers.iv4xr.goal.TacticLib;
import spaceEngineers.model.DefinitionId;
import spaceEngineers.model.ToolbarLocation;

public class BasicLoadTest {

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	// Need SE ready
	public void loadBasicLevel() {

		String worldId = "simple-place-grind-torch";
		String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;

		/////////////////////////////
		// Create SE control wrapper

		// Prepare toolbar content
		var blockType = DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock");
		var context = new SpaceEngineersTestContext();
		var blockLocation = new ToolbarLocation(1, 0);
		var welder = DefinitionId.Companion.physicalGun("Welder2Item");
		var welderLocation = new ToolbarLocation(2, 0);
		var grinder = DefinitionId.Companion.physicalGun("AngleGrinder2Item");
		var grinderLocation = new ToolbarLocation(3, 0);

		// Create toolbar
		Map<String, ToolbarLocation> blockTypeToToolbarLocation = context.getBlockTypeToToolbarLocation();
		blockTypeToToolbarLocation.put(blockType.getType(), blockLocation);

		// create proxy builder
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		var controllerWrapper = new ContextControllerWrapper(proxyBuilder.localhost(agentId), context);

		// get the SE environment
		var theEnv = SpaceEngineersUtils.createSeEnvWithSimpleMap(controllerWrapper);

		// Attache state and agent to the environment
		var dataCollector = new TestDataCollector();
		var myAgentState = new SeAgentState(agentId);
		var testAgent = new TestAgent(agentId, "some role name, else nothing");
		testAgent.attachState(myAgentState);
		testAgent.attachEnvironment(theEnv);
		testAgent.setTestDataCollector(dataCollector);

		// We load the scenario.
		theEnv.loadWorld();
		sleep(10000);

		///////////////////////
		// create goals
		
		var goals = new GoalBuilder();
		var tactics = new TacticLib();

		// Setup block in the toolbar.
		controllerWrapper.getItems().setToolbarItem(blockType, blockLocation);
		// Setup welder in the toolbar.new
		controllerWrapper.getItems().setToolbarItem(welder, welderLocation);
		// Setup grinder in the toolbar.
		controllerWrapper.getItems().setToolbarItem(grinder, grinderLocation);

		// We observe for new blocks once, so that current blocks are not going to be
		// considered "new".
		theEnv.observeForNewBlocks();
		sleep(1000);

		GoalStructure testingTask = SEQ(
//        		goals.agentAtPosition(
//        				new Vec3(532.7066f, -45.193184f, -24.395466f), 
//        				0.05f, 
//        				tactics.doNothing()),
//				goals.agentDistanceFromPosition(new Vec3(532.7066f, -45.193184f, -24.395466f), 16f, 0.1f,
//						tactics.moveForward()),
				goals.blockOfTypeExists(blockType.getType(), tactics.buildBlock(blockType.getType())),
				goals.lastBuiltBlockIntegrityIsBelow(0.8,
						SEQ(tactics.equip(grinderLocation), tactics.sleep(500), tactics.startUsingTool())),
				goals.alwaysSolved(SEQ(tactics.endUsingTool(), tactics.sleep(500))),
				goals.lastBuiltBlockIntegrityIsAbove(0.9,
						SEQ(tactics.equip(welderLocation), tactics.sleep(500), tactics.startUsingTool())),
				goals.alwaysSolved(SEQ(tactics.endUsingTool(), tactics.sleep(500))));

		testAgent.setGoal(testingTask);

		////////////////////////////////
		// Run the agent and update in the loop.
		var i = 0;
		while (testingTask.getStatus().inProgress() && i <= 100) {
			sleep(200);
			testAgent.update();
//			System.out.println(i + " " + myAgentState.getAgentId() + " " + 
//			myAgentState.worldmodel().position.toString() + " " +
//			testingTask.showGoalStructureStatus());			
			System.out.println(i + " " + myAgentState.getAgentId() + " " + myAgentState.worldmodel().position.toString()
					+ " " + testingTask.showGoalStructureStatus());
			i++;

			Environment env = testAgent.env();

			System.out.println();
		}

		// exit to main menu
		theEnv.getController().getSpaceEngineers().getSession().exitToMainMenu();
		
		// close the environment
		theEnv.close();

		
	}
}
