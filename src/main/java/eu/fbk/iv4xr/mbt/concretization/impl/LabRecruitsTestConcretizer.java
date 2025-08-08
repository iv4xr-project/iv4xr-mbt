/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.LinkedList;

import agents.tactics.GoalLib;
import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import world.BeliefState;

/**
 * @author kifetew, prandi
 *
 */
public class LabRecruitsTestConcretizer extends TestConcretizer {

	static float THRESHOLD_DISTANCE_TO_GOALFLAG = 0.5f;

	/**
	 * 
	 */
	public LabRecruitsTestConcretizer() {
		super();
	}

	// No model
	public GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t) {
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		EFSMState src = t.getSrc();
		EFSMState tgt = t.getTgt();
		String targetId = convertStateToString(tgt);

		// start refreshing the origin state
		// subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getSrc())));
		// look at src and tgt state to understand the type of transition
		if (isScreen(tgt)) {

			// if it's a screen check the color of the screen matches the one in the
			// "inParameter" variable
			// "inParameter" should be set to a Var<String> named with the id of the
			// colorScreen and the value the hex representation of the color to be expected
			Object color = t.getOutParameter().getParameter().getVariable(targetId).getValue();
			//subGoals.add(GoalLib.entityStateRefreshed(targetId));
			subGoals.add(GoalLib.entityInvariantChecked(agent, targetId, targetId + " should be " + color,
					(WorldEntity e) -> {
						//System.out.println(e.getStringProperty("color"));
						return e.getStringProperty("color").equals(color);
					}));
		} else if (src.equals(tgt)) {
			// if self loop we are pressing a button
			// subGoals.add(GoalLib.entityInteracted(targetId));
			subGoals.add(GoalLib.entityInteracted(targetId));
		} else if (oppositeDoorSides(src, tgt)) {
			// to optimize
			subGoals.add(GoalLib.entityStateRefreshed(targetId));
			subGoals.add(GoalLib.entityInvariantChecked(agent, targetId, targetId + "should be open",
					(WorldEntity e) -> e.getBooleanProperty("isOpen")));
		} else if (isGoalFlag(tgt)) {
			// target is a goal-flag:
			GoalStructure G = GoalLib.entityStateRefreshed(targetId);
			G = SEQ(GoalLib.atBGF(targetId, THRESHOLD_DISTANCE_TO_GOALFLAG, true),
					GoalLib.invariantChecked(agent,
							"The agent should be near " + targetId,
							(BeliefState S) -> {
								var gf = S.worldmodel().getElement(targetId);
								return Vec3.dist(S.worldmodel().getFloorPosition(),
										gf.getFloorPosition()) <= THRESHOLD_DISTANCE_TO_GOALFLAG;
							}));
			// G = SEQ(G,
			// GoalLib.entityInCloseRange(convertStateToString(t.getTgt())));
			subGoals.add(G);
		} else {
			subGoals.add(GoalLib.entityStateRefreshed(targetId));
		}

		if (subGoals.size() == 1) {
			return subGoals.get(0);
		} else {
			return SEQ(subGoals.toArray(new GoalStructure[0]));
		}
	}

	public GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t, EFSM model) {
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		EFSMState src = t.getSrc();
		EFSMState tgt = t.getTgt();
		String targetId = convertStateToString(tgt);

		// start refreshing the origin state
		// subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getSrc())));
		// look at src and tgt state to understand the type of transition
		if (isScreen(tgt)) {

			// if it's a screen check the color of the screen matches the one in the
			// "inParameter" variable
			// "inParameter" should be set to a Var<String> named with the id of the
			// colorScreen and the value the hex representation of the color to be expected
			Object color = t.getOutParameter().getParameter().getVariable(targetId).getValue();
			//subGoals.add(GoalLib.entityStateRefreshed(targetId));
			subGoals.add(GoalLib.entityInvariantChecked(agent, targetId, targetId + " should be " + color,
					(WorldEntity e) -> {
						//System.out.println(e.getStringProperty("color"));
						return e.getStringProperty("color").equals(color);
					}));
		} else if (src.equals(tgt)) {
			// if self loop we are pressing a button
			// subGoals.add(GoalLib.entityInteracted(targetId));
			subGoals.add(GoalLib.entityInteracted(targetId));
		} else if (oppositeDoorSides(src, tgt)) {
			// to optimize
			subGoals.add(GoalLib.entityStateRefreshed(targetId));
			subGoals.add(GoalLib.entityInvariantChecked(agent, targetId, targetId + "should be open",
					(WorldEntity e) -> e.getBooleanProperty("isOpen")));
		} else if (isGoalFlag(tgt)) {
			// target is a goal-flag:
			GoalStructure G = GoalLib.entityStateRefreshed(targetId);
			G = SEQ(GoalLib.atBGF(targetId, THRESHOLD_DISTANCE_TO_GOALFLAG, true),
					GoalLib.invariantChecked(agent,
							"The agent should be near " + targetId,
							(BeliefState S) -> {
								var gf = S.worldmodel().getElement(targetId);
								return Vec3.dist(S.worldmodel().getFloorPosition(),
										gf.getFloorPosition()) <= THRESHOLD_DISTANCE_TO_GOALFLAG;
							}));
			// G = SEQ(G,
			// GoalLib.entityInCloseRange(convertStateToString(t.getTgt())));
			subGoals.add(G);
		} else {
			subGoals.add(GoalLib.entityStateRefreshed(targetId));
		}

		if (subGoals.size() == 1) {
			return subGoals.get(0);
		} else {
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
		return s.getId().startsWith("d");
	}

	// check if a state is a goalFlag at the first character
	private Boolean isGoalFlag(EFSMState s) {
		return s.getId().startsWith("g");
	}

	// check if a state is a button or a colorButton
	private Boolean isButton(EFSMState s) {
		String name = s.getId();
		return name.startsWith("b") | name.startsWith("cb");
	}

	private Boolean isScreen(EFSMState s) {		
		return s.getId().startsWith("cs");
	}

	// check if 2 states represent the opposite sites of a door
	private Boolean oppositeDoorSides(EFSMState s, EFSMState t) {
		if (!isDoor(s) || !isDoor(t)) {
			// one of the state is not a door
			return false;
		}
		if (s.getId() == t.getId()) {
			// if they are the same thet cannot be the different sides of the same door
			return false;
		}
		return convertDoorSideToDoorName(s.getId()).equals(convertDoorSideToDoorName(t.getId()));
	}
}
