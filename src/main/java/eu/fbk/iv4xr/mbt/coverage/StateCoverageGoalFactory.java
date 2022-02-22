/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import org.evosuite.ga.Chromosome;

//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
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
public class StateCoverageGoalFactory implements CoverageGoalFactory {

	List<StateCoverageGoal> coverageGoals = new ArrayList<StateCoverageGoal>();
	
	/**
	 * 
	 */
	public StateCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM model = EFSMFactory.getInstance().getEFSM();
		Set<EFSMState> states = model.getStates();
		if (states == null || states.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No states.");
		}
		for (EFSMState state : states) {
			StateCoverageGoal goal = new StateCoverageGoal(state);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<StateCoverageGoal> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
