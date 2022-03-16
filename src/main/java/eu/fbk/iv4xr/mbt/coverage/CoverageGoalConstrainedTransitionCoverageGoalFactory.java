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
public class CoverageGoalConstrainedTransitionCoverageGoalFactory
		implements CoverageGoalFactory {

	List<CoverageGoalConstrainedTransitionCoverageGoal> coverageGoals = new ArrayList<CoverageGoalConstrainedTransitionCoverageGoal>();
	CoverageGoal constrainingGoal;
	/**
	 * 
	 */
	public CoverageGoalConstrainedTransitionCoverageGoalFactory(CoverageGoal constrainingGoal) {
		this.constrainingGoal = constrainingGoal;
		// build the list of coverage goals
		EFSM model = EFSMFactory.getInstance().getEFSM();
		Set<EFSMTransition> transitions = model.getTransitons();
		if (transitions == null || transitions.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No transitions.");
		}
		for (EFSMTransition transition : transitions) {
			CoverageGoalConstrainedTransitionCoverageGoal goal = new CoverageGoalConstrainedTransitionCoverageGoal( transition, constrainingGoal);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<CoverageGoalConstrainedTransitionCoverageGoal> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}



}
