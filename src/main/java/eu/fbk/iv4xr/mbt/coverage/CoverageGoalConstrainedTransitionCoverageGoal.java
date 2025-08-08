/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.Collections;

import org.evosuite.ga.Chromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class CoverageGoalConstrainedTransitionCoverageGoal extends CoverageGoal {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 4660857042886223346L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(CoverageGoalConstrainedTransitionCoverageGoal.class);

	private EFSMTransition transition;
	private CoverageGoal constrainingGoal;

	private final double GOAL_CONSTRAINT_PENALITY = 100;
	
	/**
	 * 
	 */
	public CoverageGoalConstrainedTransitionCoverageGoal(EFSMTransition trans, 
			CoverageGoal constrainingGoal) {
		transition = trans;
		this.constrainingGoal = constrainingGoal;
	}

	@Override
	public double getFitness(Chromosome test, ExecutionResult executionResult) {
		double fitness = -1;
		if (test instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)test;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionTrace trace = executionResult.getExecutionTrace();
			
			// trivial case
			if (executionResult.isSuccess() && testContainsGoal(testcase)) {
				fitness = 0d;
			}else {
				double feasibilityFitness = W_AL * trace.getPathApproachLevel() + W_BD * trace.getPathBranchDistance();
				double targetFitness = W_AL * computeTargetApproachLevel(testcase, executionResult, testContainsGoal(testcase)) + 
						W_BD * computeTargetBranchDistance(testcase, executionResult, testContainsGoal(testcase));
				fitness = feasibilityFitness + targetFitness;
			}
			
			// does the individual respect the coverageGoal constraint? if no apply penality
			if (!respectsCoverageGoalConstraint(testcase)) {
				fitness += GOAL_CONSTRAINT_PENALITY;
				executionResult.setSuccess(false);
				testcase.setValid(false);
			}
			updateCollateralCoverage(test, executionResult);
		}
		test.setChanged(false);
		updateIndividual(test, fitness);
		return fitness;
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
		if (! (obj instanceof CoverageGoalConstrainedTransitionCoverageGoal)) {
			return false;
		}
		
		CoverageGoalConstrainedTransitionCoverageGoal other = (CoverageGoalConstrainedTransitionCoverageGoal)obj;
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
//				CoverageGoal goal = new CoverageGoalConstrainedTransitionCoverageGoal(coveredTransition, constrainingGoal);
				updateIndividual(individual, 0d);
			}
		}
	}

	
	/**
	 * checks whether or not a testcase respects the constraint imposed by the coverageGoal, 
	 * behavior of constraint is defined by {@link MBTProperties.GoalConstraintOnTestFactory}
	 * @param testcase
	 * @return
	 */
	boolean respectsCoverageGoalConstraint (AbstractTestSequence testcase) {
		boolean result = false;
		switch (MBTProperties.GOAL_CONSTRAINT_ON_TEST_FACTORY) {
		case ENDS_WITH_STATE:
			if (constrainingGoal instanceof StateCoverageGoal) {
				EFSMState goal = ((StateCoverageGoal)constrainingGoal).getState();
				if (testcase.getPath().getTgt().equals(goal)) {
					int frequency = Collections.frequency(testcase.getPath().getStates(), goal);
					if (frequency == 1 ) {
						result = true;
					}				
				}
			}
			break;
		default:
			result = false;
		}
		return result;
	}
}
