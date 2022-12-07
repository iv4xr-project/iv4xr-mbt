/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.LinkedList;
import java.util.List;

import agents.tactics.GoalLib;
import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;
import world.BeliefState;

/**
 * @author kifetew, prandi
 *
 */
public class LabRecruitsTestConcretizer implements TestConcretizer {

	static float THRESHOLD_DISTANCE_TO_GOALFLAG = 0.5f ;

	/**
	 * 
	 */
	public LabRecruitsTestConcretizer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<GoalStructure> concretizeTestCase(TestAgent testAgent, AbstractTestSequence testCase) {
		Path path = testCase.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		return convertTestCaseToGoalStructure(testAgent,listTransitions) ;
	}

	
	// translating a test-case represented as a sequence of EFSM-transitions to a list of goal-structures:
	private List<GoalStructure> convertTestCaseToGoalStructure(TestAgent agent, List<EFSMTransition>  tc) {
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		for (EFSMTransition t : tc) {
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
			//subGoals.add(GoalLib.entityInteracted(t.getTgt().getId()));
			subGoals.add(GoalLib.entityInteracted(convertStateToString(t.getTgt())));
			
		} else if (oppositeDoorSides(t.getSrc(), t.getTgt())) {
			// to optimize
			//String doorName = convertDoorSideToDoorName(t.getTgt().getId());
			String doorName = convertStateToString(t.getTgt());
			subGoals.add(GoalLib.entityStateRefreshed(doorName));
			subGoals.add(GoalLib.entityInvariantChecked(agent, doorName, doorName+"should be open", (WorldEntity e) -> e.getBooleanProperty("isOpen"))) ;
		} else {
			GoalStructure G = GoalLib.entityStateRefreshed(convertStateToString(t.getTgt())) ;
			if (LabRecruitsRandomEFSM.getStateType(t.getTgt()).equals(LabRecruitsRandomEFSM.StateType.GoalFlag)) {
				// target is a goal-flag:
				String goalFlagId = convertStateToString(t.getTgt()) ;
				G = SEQ(GoalLib.atBGF(goalFlagId, THRESHOLD_DISTANCE_TO_GOALFLAG, true),
						GoalLib.invariantChecked(agent, 
							"The agent should be near " + goalFlagId, 
							(BeliefState S) -> {
								var gf = S.worldmodel().getElement(goalFlagId) ;
								return Vec3.dist(S.worldmodel().getFloorPosition(), gf.getFloorPosition()) <= THRESHOLD_DISTANCE_TO_GOALFLAG ;
							})
						) ;
				//G = SEQ(G,
				//		GoalLib.entityInCloseRange(convertStateToString(t.getTgt())));
			}
			subGoals.add(G) ;
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
