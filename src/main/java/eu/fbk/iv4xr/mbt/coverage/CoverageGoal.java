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
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public abstract class CoverageGoal extends FitnessFunction<Chromosome> {
		
	protected double W_BD = 1;
	protected double W_AL = 1;
	
	public abstract double getFitness(Chromosome test);
	
	public boolean isMaximizationFunction() {
		return false;
	}
	
	public double getShortestDistanceToTarget(Path path, EFSMState target) {
		double shortestDistance = Double.MAX_VALUE;
		for (Object s : path.getStates()) {
			EFSMState source = (EFSMState)s;
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
	
	protected abstract void updateCollateralCoverage (Chromosome individual, ExecutionResult executionResult);
	
	@Override
	public abstract String toString();
	
}
