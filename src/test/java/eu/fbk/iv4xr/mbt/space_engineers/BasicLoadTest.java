package eu.fbk.iv4xr.mbt.space_engineers;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.Map;

import org.junit.jupiter.api.Test;

import environments.SeAgentState;
import environments.SeEnvironment;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import eu.iv4xr.framework.spatial.Vec3;
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

	@Test
	public void loadBasicLevel() {

		String worldId = "simple-place-grind-torch-with-tools";
		String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;

		var blockType = DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock");

		var context = new SpaceEngineersTestContext();
		var blockLocation = new ToolbarLocation(1, 0);
		var welder = DefinitionId.Companion.physicalGun("Welder2Item");
		var welderLocation = new ToolbarLocation(2, 0);
		var grinder = DefinitionId.Companion.physicalGun("AngleGrinder2Item");
		var grinderLocation = new ToolbarLocation(3, 0);

		Map<String, ToolbarLocation> blockTypeToToolbarLocation = context.getBlockTypeToToolbarLocation();
		blockTypeToToolbarLocation.put(blockType.getType(), blockLocation);

		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();

		var controllerWrapper = new ContextControllerWrapper(proxyBuilder.localhost(agentId), context);

		var theEnv = new SeEnvironment(worldId, controllerWrapper);
		theEnv.loadWorld();

		var dataCollector = new TestDataCollector();

		var myAgentState = new SeAgentState(agentId);

		var testAgent = new TestAgent(agentId, "some role name, else nothing");
		testAgent.attachState(myAgentState);
		testAgent.attachEnvironment(theEnv);
		testAgent.setTestDataCollector(dataCollector);

		var goals = new GoalBuilder();
		var tactics = new TacticLib();

		GoalStructure testingTask = SEQ(
        		goals.agentAtPosition(
        				new Vec3(532.7066f, -45.193184f, -24.395466f), 
        				0.05f, 
        				tactics.doNothing()),
				//goals.blockOfTypeExists(blockType.getType(), tactics.buildBlock(blockType.getType())),
				goals.agentDistanceFromPosition(new Vec3(532.7066f, -45.193184f, -23.946253f), 16f, 0.1f,
						tactics.moveForward()));

		testAgent.setGoal(testingTask);

		// We load the scenario.
		theEnv.loadWorld();
		// Setup block in the toolbar.
		controllerWrapper.getItems().setToolbarItem(blockType, blockLocation);
		// Setup welder in the toolbar.new
		controllerWrapper.getItems().setToolbarItem(welder, welderLocation);
		// Setup grinder in the toolbar.
		controllerWrapper.getItems().setToolbarItem(grinder, grinderLocation);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// We observe for new blocks once, so that current blocks are not going to be
		// considered "new".
		theEnv.observeForNewBlocks();

		// Run the agent and update in the loop.
		var i = 0;
		while (testingTask.getStatus().inProgress() && i <= 1500) {
			testAgent.update();
			System.out.println(i + " " + myAgentState.getAgentId());
			i++;
		}
//        
//        // Print results.
//        testingTask.printGoalStructureStatus();
//        List<GoalStructure> subgoals = testingTask.getSubgoals();

//        for(GoalStructure gs : subgoals){
//        	assertTrue(gs.getStatus().success());
//        }

	}
}
