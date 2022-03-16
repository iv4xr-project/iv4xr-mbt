/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public class TransitionCoverageGoalFactory implements CoverageGoalFactory {

	List<TransitionCoverageGoal> coverageGoals = new ArrayList<TransitionCoverageGoal>();

	/**
	 * 
	 */
	public TransitionCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM model = EFSMFactory.getInstance().getEFSM();
		Set<EFSMTransition> transitions = model.getTransitons();
		if (transitions == null || transitions.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No transitions.");
		}
		for (EFSMTransition transition : transitions) {
			TransitionCoverageGoal goal = 
						new TransitionCoverageGoal (transition);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<TransitionCoverageGoal> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}



}
