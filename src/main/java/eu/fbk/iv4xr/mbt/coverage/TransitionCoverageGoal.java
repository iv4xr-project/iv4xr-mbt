/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;

/**
 * @author kifetew
 *
 */
public class TransitionCoverageGoal extends CoverageGoal {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 4660857042886223346L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(TransitionCoverageGoal.class);

	private EFSMTransition transition;
	
	/**
	 * 
	 */
	public TransitionCoverageGoal(EFSMTransition trans) {
		transition = trans;
	}

	@Override
	public String toString() {
		return transition == null? "" : (transition.getSrc() + " --> " + transition.getTgt());
	}

	/**
	 * @return the transition
	 */
	public EFSMTransition getTransition() {
		return transition;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (! (obj instanceof TransitionCoverageGoal)) {
			return false;
		}
		
		TransitionCoverageGoal other = (TransitionCoverageGoal)obj;
		return transition.equals(other.getTransition());
		
	}

	@Override
	public int hashCode() {
		return transition.hashCode();
	}

	@Override
	protected void updateCollateralCoverage(Chromosome individual, ExecutionResult executionResult) {
		// collateral coverage only if the individual is valid
		if (executionResult.isSuccess()) {
			ExecutionTrace executionTrace = executionResult.getExecutionTrace();
			for (Object transition : executionTrace.getCoveredTransitions()) {
				EFSMTransition coveredTransition = (EFSMTransition)transition;
				CoverageGoal goal = new TransitionCoverageGoal(coveredTransition);
//				individual.addFitness(goal, 0d);
				updateIndividual(individual, 0d);
			}
		}
	}

}
