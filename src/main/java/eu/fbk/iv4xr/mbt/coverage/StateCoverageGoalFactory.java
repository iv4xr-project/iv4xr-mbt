/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.ga.Chromosome;

import de.upb.testify.efsm.EFSM;
import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
import eu.fbk.se.labrecruits.LabRecruitsContext;
import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoalFactory implements CoverageGoalFactory<StateCoverageGoal> {

	List<StateCoverageGoal> coverageGoals = new ArrayList<StateCoverageGoal>();
	
	/**
	 * 
	 */
	public StateCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM<LabRecruitsState, String, LabRecruitsContext, 
		Transition<LabRecruitsState, String, LabRecruitsContext>> model = AlgorithmFactory.getModel();
		Set<LabRecruitsState> states = model.getStates();
		if (states == null || states.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No states.");
		}
		for (LabRecruitsState state : states) {
			StateCoverageGoal goal = new StateCoverageGoal(state);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<StateCoverageGoal> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public double getFitness(Chromosome suite) {
		// compute the overall fitness of the testsuite chromosome
		return 0;
	}

	

}
