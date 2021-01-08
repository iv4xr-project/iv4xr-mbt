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
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
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
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		implements CoverageGoalFactory<StateCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> {

	List<StateCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> coverageGoals = 
			new ArrayList<StateCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>>();
	
	/**
	 * 
	 */
	public StateCoverageGoalFactory() {
		// build the list of coverage goals
		EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = AlgorithmFactory.getModel();
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
	public List<StateCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getCoverageGoals() {
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
