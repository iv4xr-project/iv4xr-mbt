package eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.tactics;

import static nl.uu.cs.aplib.AplibEDSL.action;

import java.util.function.Function;

import environments.SeAgentState;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.Action;
import nl.uu.cs.aplib.mainConcepts.Tactic;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.labrecruits.LabRecruitsController;
import spaceEngineers.labrecruits.LabRecruitsCoroutinesFreeController;
import spaceEngineers.model.Vec3F;

/**
 * This class implement tactics for Space Engineers following the same approach
 * used for Lab Recruits.
 * 
 * @author Davide Prandi
 *
 */
public class SpaceEngineersTacticLib {

	public static Tactic doNothing() {
		Tactic observe = action("Observe").do1((SeAgentState seState) -> {
			return seState;
		}).lift();
		return observe;
	}

	public static Tactic navigateToButton(String buttonId) {
		Tactic goTo = action("Observe").do1((SeAgentState seState) -> {

			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, SpaceEngineersGoalLib.MAXIMUM_BUTTON_REACH_DISTANCE);
			LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
			
			lrControllerFree.goToButton(buttonId);
			return seState;
		}).lift();
		
		return goTo;
	}
	
	public static Tactic navigateToDoor(String doorId) {
		Tactic goTo = action("Observe").do1((SeAgentState seState) -> {

			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, SpaceEngineersGoalLib.MAXIMUM_BUTTON_REACH_DISTANCE);
			LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
			
			lrControllerFree.goToDoor(doorId);
			return seState;
		}).lift();
		
		return goTo;
	}
	
	
	public static Tactic pressButtton(String buttonId) {
		Tactic press = action("Press").do1((SeAgentState seState) -> {

			// get SE LabRecruits controller
			SpaceEngineers spaceEngineers = seState.getSeEnv().getController().getSpaceEngineers();
			Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
			ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(spaceEngineers, pathFinder);		
			LabRecruitsController lrController = new LabRecruitsController(seExt, SpaceEngineersGoalLib.MAXIMUM_BUTTON_REACH_DISTANCE);
			LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
			
			lrControllerFree.pressButton(buttonId);
			return seState;
		}).lift();
		
		return press;
	}

}
