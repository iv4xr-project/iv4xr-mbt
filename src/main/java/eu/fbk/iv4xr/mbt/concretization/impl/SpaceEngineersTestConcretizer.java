/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.LinkedList;

import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.tactics.SpaceEngineersGoalLib;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * 
 * @author Davide Prandi
 *
 */
public class SpaceEngineersTestConcretizer extends AplibTestConcretizer {
	
	public SpaceEngineersTestConcretizer(TestAgent testAgent) {
		super(testAgent);
	}

	public GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t, EFSM model) {
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		// start refreshing the origin state
		// subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getSrc())));
		// look at src and tgt state to understand the type of transition
		if (t.getSrc().equals(t.getTgt())) {
			// if self loop we are pressing a button
			String buttonName = convertStateToString(t.getTgt());
			GoalStructure g = SEQ(
					SpaceEngineersGoalLib.buttonInCloseRange(buttonName),
					SpaceEngineersGoalLib.buttonIsInteracted(buttonName)
					);
			subGoals.add(g);

		} else if (oppositeDoorSides(t.getSrc(), t.getTgt())) {
			String doorName = convertStateToString(t.getTgt());
			subGoals.add(SpaceEngineersGoalLib.doorIsOpen(doorName, agent));
		} else {
			// if the target is a door
			if (isDoor(t.getTgt())) {
				subGoals.add(SpaceEngineersGoalLib.doorInCloseRange(convertStateToString(t.getTgt())));
			}else {
				subGoals.add(SpaceEngineersGoalLib.buttonInCloseRange(convertStateToString(t.getTgt())));
			}
		}
		if (subGoals.size() == 1) {
			return subGoals.get(0);
		}else {
			return SEQ(subGoals.toArray(new GoalStructure[0]));
		}
	}


	
	// convert a state to a string
	// for buttons it is simply the name
	// for doors, that are written as dXp or dXm, we have to write doorX
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
		if (name.substring(0, 1).equals("d")) {
			return true;
		} else {
			return false;
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
	

	
}
