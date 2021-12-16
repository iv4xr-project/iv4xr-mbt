/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
//import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
//import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
//import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;


import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionListener;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class CoverageGoalConstrainedTransitionCoverageGoal<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		extends CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 4660857042886223346L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(CoverageGoalConstrainedTransitionCoverageGoal.class);

	private Transition transition;
	private CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal;

	private final double GOAL_CONSTRAINT_PENALITY = 100;
	
	/**
	 * 
	 */
	public CoverageGoalConstrainedTransitionCoverageGoal(Transition trans, 
			CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal) {
		transition = trans;
		this.constrainingGoal = constrainingGoal;
	}

	@Override
	public double getFitness(Chromosome individual) {
		double fitness = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionListner = 
						new EFSMTestExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition>(testcase, this);
			EFSMTestExecutor.getInstance().addListner(executionListner);
			ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
			// get trace from the listner
			ExecutionTrace trace = executionListner.getExecutionTrace();

			// add trace to result
			executionResult.setExectionTrace(trace);
			
			if (MBTProperties.SANITY_CHECK_FITNESS) {
				fitness = Randomness.nextDouble();
			}else {
				// overall fintess is simply the sum of both fitnesses
				double feasibilityFitness = W_AL * trace.getPathApproachLevel() + W_BD * trace.getPathBranchDistance();
				double targetFitness = W_AL * trace.getTargetApproachLevel() + W_BD * trace.getTargetBranchDistance();
				fitness = feasibilityFitness + targetFitness;
				
				// does the individual respect the coverageGoal constraint? if no apply penality
				if (!respectsCoverageGoalConstraint(testcase)) {
					fitness += GOAL_CONSTRAINT_PENALITY;
				}
			}
			
			EFSMTestExecutor.getInstance().removeListner(executionListner);
			updateCollateralCoverage(individual, executionResult);
			logger.debug("Individual ({}): {} \nFitness: {}", executionResult.isSuccess(), individual.toString(), fitness);
		}
		individual.setChanged(false);
		updateIndividual(this, individual, fitness);
		return fitness;
	}
	
	@Override
	public String toString() {
		return transition == null? "" : (transition.getSrc() + " --> " + transition.getTgt());
	}

	/**
	 * @return the transition
	 */
	public Transition getTransition() {
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
			ExecutionTrace executionTrace = executionResult.getExectionTrace();
			for (Object transition : executionTrace.getCoveredTransitions()) {
				EFSMTransition coveredTransition = (EFSMTransition)transition;
				CoverageGoal goal = new CoverageGoalConstrainedTransitionCoverageGoal(coveredTransition, constrainingGoal);
				updateIndividual(goal, individual, 0d);
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
		case ENDS_WITH:
			if (constrainingGoal instanceof StateCoverageGoal) {
				EFSMState goal = ((StateCoverageGoal)constrainingGoal).getState();
				if (testcase.getPath().getTgt().equals(goal)) {
					result = true;
				}
			}
			break;
		default:
			result = false;
		}
		return result;
	}
}
