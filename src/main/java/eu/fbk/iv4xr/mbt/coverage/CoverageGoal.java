/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
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
		
	public abstract double getFitness(Chromosome test);
	
	public boolean isMaximizationFunction() {
		return false;
	}
	
	public double getShortestDistanceToTarget(Path path, EFSMState target) {
		double shortestDistance = Double.MAX_VALUE;
		for (Object s : path.getStates()) {
			State source = (State)s;
			double d = EFSMFactory.getInstance().getEFSM().getShortestPathDistance(source, target);
			if (d < shortestDistance) {
				shortestDistance = d;
			}
		}
		return shortestDistance;
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
}
