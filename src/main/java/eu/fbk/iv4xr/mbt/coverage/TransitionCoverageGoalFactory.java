/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;

/**
 * @author kifetew
 *
 */
public class TransitionCoverageGoalFactory<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends Transition<State, Parameter, Context>> implements CoverageGoalFactory<TransitionCoverageGoal<State, Parameter, Context, Trans>> {

	List<TransitionCoverageGoal<State, Parameter, Context, Trans>> coverageGoals = new ArrayList<TransitionCoverageGoal<State, Parameter, Context, Trans>>();

	/**
	 * 
	 */
	public TransitionCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM<State, Parameter, Context, 
		Trans> model = AlgorithmFactory.getModel();
		Set<Trans> transitions = model.getTransitons();
		if (transitions == null || transitions.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No transitions.");
		}
		for (Trans transition : transitions) {
			TransitionCoverageGoal<State, Parameter, Context, Trans> goal = new TransitionCoverageGoal<State, Parameter, Context, Trans>(transition);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<TransitionCoverageGoal<State, Parameter, Context, Trans>> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}



}
