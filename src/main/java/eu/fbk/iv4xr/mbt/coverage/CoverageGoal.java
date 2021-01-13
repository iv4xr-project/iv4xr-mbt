/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.execution.TestExecutor;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
import eu.fbk.iv4xr.mbt.testcase.Path;

/**
 * @author kifetew
 *
 */
public abstract class CoverageGoal<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		extends FitnessFunction<Chromosome> {
		
	protected TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor;
	
	public abstract double getFitness(Chromosome test);
	
	public boolean isMaximizationFunction() {
		return false;
	}
	
	protected double getShortestDistanceToTarget(Path path, EFSMState target) {
		double shortestDistance = Double.MAX_VALUE;
		for (Object s : path.getStates()) {
			State source = (State)s;
			double d = AlgorithmFactory.getModel().getShortestPathDistance(source, target);
			if (d < shortestDistance) {
				shortestDistance = d;
			}
		}
		return shortestDistance;
	}
}
