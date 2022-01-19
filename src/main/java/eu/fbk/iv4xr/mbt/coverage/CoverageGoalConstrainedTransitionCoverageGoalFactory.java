/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public class CoverageGoalConstrainedTransitionCoverageGoalFactory<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		implements CoverageGoalFactory<CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> {

	List<CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> coverageGoals = new ArrayList<CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>>();
	CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal;
	/**
	 * 
	 */
	public CoverageGoalConstrainedTransitionCoverageGoalFactory(CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal) {
		this.constrainingGoal = constrainingGoal;
		// build the list of coverage goals
		EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = EFSMFactory.getInstance().getEFSM();
		Set<EFSMTransition> transitions = model.getTransitons();
		if (transitions == null || transitions.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No transitions.");
		}
		for (EFSMTransition transition : transitions) {
			CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> goal = 
						new CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>((Transition) transition, constrainingGoal);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<CoverageGoalConstrainedTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}



}
