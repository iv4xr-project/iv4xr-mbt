package eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.tactics;

import static eu.iv4xr.framework.Iv4xrEDSL.testgoal;
import static nl.uu.cs.aplib.AplibEDSL.ABORT;
import static nl.uu.cs.aplib.AplibEDSL.FIRSTof;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static nl.uu.cs.aplib.AplibEDSL.goal;

import java.util.function.Function;
import java.util.function.Predicate;

import environments.SeAgentState;
import environments.SeEnvironment;
import eu.iv4xr.framework.mainConcepts.WorldModel;
import eu.iv4xr.framework.mainConcepts.ObservationEvent.VerdictEvent;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestGoal;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.Goal;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.controller.extensions.ObserverExtensionsKt;
import spaceEngineers.iv4xr.goal.TacticLib;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.labrecruits.LabRecruitsController;
import spaceEngineers.labrecruits.LabRecruitsCoroutinesFreeController;
import spaceEngineers.model.Observation;
import spaceEngineers.model.TerminalBlock;
import spaceEngineers.model.Vec3F;
import world.BeliefState;


/**
 * This class provides a set of goals to interact with Space Engineers game. 
 * Goals allow to to manipulate blocks  and navigate Lab Recruits like maze in Space Engineers game
 * 
 * @author Davide Prandi
 *
 */
public class SpaceEngineersGoalLib {
	
	static float LARGE_BLOCK_CUBE_SIDE_SIZE = 2.5f;
	static float MAX_ALLOWED_DISTANCE_MULTIPLAYER = 2f;
	static float MAXIMUM_BUTTON_REACH_DISTANCE = 3.5f;
	static float DISTANCE_TOLLERANGE = 1.2f;
			
	public static GoalStructure doNothing() {
		Goal goal = new Goal("Nothing to do")
				  .toSolve((SeAgentState belief) -> {
					 return true ;
                  });
		
		var g = goal.withTactic( FIRSTof( SpaceEngineersTacticLib.doNothing(), ABORT())).lift();
		
		
		return g;
	}

	
	/**
	 * Check if the distance between the button and the agent is less than
	 * LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
	 * 
	 * @param buttonId
	 * @return
	 */
	public static GoalStructure buttonInCloseRange(String buttonId) {

		// define the goal
		Goal g = goal(String.format("Button is in interaction distance: [%s]", buttonId));
		
		// predicate that check if the distance between the agent position and
		// the button position is less than 
		// LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
		Predicate<SeAgentState> checkDistance = seState -> {
			// get agent position
			Vec3 tmpPosition = seState.worldmodel.position;	
			Vec3F agentPosition = new Vec3F(tmpPosition.x, tmpPosition.y, tmpPosition.z);
			
			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, MAXIMUM_BUTTON_REACH_DISTANCE);

			// get button position
			Vec3F buttonPosition = lrController.buttonBlockById(buttonId).getPosition();
			
			// check if distance is less than LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
			return agentPosition.distanceTo(buttonPosition) < LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER;
			
		};

		// add predicate to goal
		g.toSolve(checkDistance);
		
		// add tactic
		return g.withTactic(
				FIRSTof(
						SpaceEngineersTacticLib.navigateToButton(buttonId),
						ABORT())).lift();

	}
	
	/**
	 * Check if the distance between the door and the agent is less than
	 * LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
	 * 
	 * @param doorId
	 * @return
	 */
	public static GoalStructure doorInCloseRange(String doorId) {

		// define the goal
		Goal g = goal(String.format("Door is in interaction distance: [%s]", doorId));
		
		// predicate that check if the distance between the agent position and
		// the door position is less than 
		// LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
		Predicate<SeAgentState> checkDistance = seState -> {
			// get agent position
			Vec3 tmpPosition = seState.worldmodel.position;	
			Vec3F agentPosition = new Vec3F(tmpPosition.x, tmpPosition.y, tmpPosition.z);
			
			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, MAXIMUM_BUTTON_REACH_DISTANCE);
			
			// get door position
			Vec3F doorPosition = lrController.buttonBlockById(doorId).getPosition();
			
			// check if distance is less than LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER
			return agentPosition.distanceTo(doorPosition) < LARGE_BLOCK_CUBE_SIDE_SIZE * MAX_ALLOWED_DISTANCE_MULTIPLAYER;
			
		};

		// add predicate to goal
		g.toSolve(checkDistance);
		
		// add tactics
		return g.withTactic(
				FIRSTof(
						SpaceEngineersTacticLib.navigateToDoor(doorId),
						ABORT())).lift();

	}
	
	/**
	 * Interact with button buttonId
	 * @param buttonId
	 * @return
	 */
	public static GoalStructure buttonIsInteracted(String buttonId) {
		
		Goal press = goal(String.format("Press button: [%s]", buttonId));
		
		Predicate<SeAgentState> interact = seState -> {
			// get agent position
			Vec3 tmpPosition = seState.worldmodel.position;	
			Vec3F agentPosition = new Vec3F(tmpPosition.x, tmpPosition.y, tmpPosition.z);
			
			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, MAXIMUM_BUTTON_REACH_DISTANCE);

			// get button position
			Vec3F buttonPosition = lrController.buttonBlockById(buttonId).getPosition();
			
			// check if distance is less than MAXIMUM_BUTTON_REACH_DISTANCE
			return agentPosition.distanceTo(buttonPosition) < MAXIMUM_BUTTON_REACH_DISTANCE;
		};

		press.toSolve(interact);
		press.withTactic(FIRSTof(
						SpaceEngineersTacticLib.pressButtton(buttonId),
						ABORT()));
		
		return press.lift();
	}
	
	
	public static GoalStructure doorIsOpen(String doorId, TestAgent agent) {
		
//		Goal open = goal(String.format("Door is open: [%s]", doorId));
//		<SeAgentState> nothing = seState -> {
//			return true;
//			// get SE LabRecruits controller
//			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
//			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
//			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
//			LabRecruitsController lrController = new LabRecruitsController(seExt, MAXIMUM_BUTTON_REACH_DISTANCE);
//
//			LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
//			
//			
//			// check if distance is less than MAXIMUM_BUTTON_REACH_DISTANCE
//			return lrControllerFree.doorBlockById(doorId).getOpen();
//		};

//		open.toSolve(nothing);
//		open.withTactic( FIRSTof( SpaceEngineersTacticLib.doNothing(), ABORT()));
	
		TestGoal g = testgoal("Evaluate if door " + doorId + " is open",agent);
		Predicate<SeAgentState> nothing = seState -> true;
		g.toSolve(nothing);
		Function<SeAgentState, VerdictEvent> oracle = seState -> {
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
				ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, MAXIMUM_BUTTON_REACH_DISTANCE);         
			LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
			if (lrControllerFree.doorBlockById(doorId).getOpen())
			   return new VerdictEvent("Inv-check", " door " + doorId + " is open", true) ;
			else {
			   return new VerdictEvent("Inv-check" , " door " + doorId + " is not open", false) ;
		    }
		};
		g.invariant(oracle);
		g.withTactic(SpaceEngineersTacticLib.doNothing());
		
        return SEQ(g.lift());

		
	}
}

