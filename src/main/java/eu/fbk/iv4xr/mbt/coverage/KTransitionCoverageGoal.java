/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;

/**
 * @author kifetew, prandi
 *
 * Represents a sequence of K transitions, where K >= 2
 * K is defined in MBTProperties by parameter k_transition_size
 */
public class KTransitionCoverageGoal extends CoverageGoal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6341416548064403719L;
	
	// It is convenient to use a path for a kTransition goal
	private EFSMPath kTransition;
	
	/**
	 * 
	 */
	public KTransitionCoverageGoal(EFSMPath t) {
		this.kTransition = t;
	}

	public EFSMPath getKTransition() {
		return kTransition;
	}
	
	
	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	protected void updateCollateralCoverage(Chromosome individual, ExecutionResult executionResult) {
		// collateral coverage only if the individual is valid
		if (executionResult.isSuccess()) {	
			// get the solution path
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			Path path = testcase.getPath();
			// the path size should be at list K_TRANSITION_SIZE to include a goal
			if (path.getLength() >=  MBTProperties.K_TRANSITION_SIZE) {
				for (int i = 0; i < path.getLength() - MBTProperties.K_TRANSITION_SIZE+1; i++) {
					EFSMPath subPath = path.subPath(i, i + MBTProperties.K_TRANSITION_SIZE);
					KTransitionCoverageGoal goal = new KTransitionCoverageGoal(subPath);
					updateIndividual(goal, individual, 0d);
				}
			}
		}
		
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kTransition == null) ? 0 : kTransition.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof KTransitionCoverageGoal))
			return false;
		KTransitionCoverageGoal other = (KTransitionCoverageGoal) obj;
		if (kTransition == null) {
			if (other.kTransition != null)
				return false;
		} else if (!kTransition.equals(other.kTransition))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return kTransition == null? "" : kTransition.toString();
	}

}
