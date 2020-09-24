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
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;

import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;




//import eu.fbk.se.labrecruits.LabRecruitsContext;
//import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoalFactory<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> implements CoverageGoalFactory<StateCoverageGoal<State, Parameter, Context, Trans>> {

	List<StateCoverageGoal<State, Parameter, Context, Trans>> coverageGoals = new ArrayList<StateCoverageGoal<State, Parameter, Context, Trans>>();
	
	/**
	 * 
	 */
	public StateCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM<State, Parameter, Context, 
		Transition<State, Parameter, Context>> model = AlgorithmFactory.getModel();
		Set<State> states = model.getStates();
		if (states == null || states.isEmpty()) {
			throw new RuntimeException("Something wrong with the model: " + MBTProperties.SUT_EFSM + ". No states.");
		}
		for (State state : states) {
			StateCoverageGoal goal = new StateCoverageGoal(state);
			coverageGoals.add(goal);
		}
	}

	@Override
	public List<StateCoverageGoal<State, Parameter, Context, Trans>> getCoverageGoals() {
		return coverageGoals;
	}

//	@Override
//	public double getFitness(Chromosome suite) {
//		// compute the overall fitness of the testsuite chromosome
//		return 0;
//	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
