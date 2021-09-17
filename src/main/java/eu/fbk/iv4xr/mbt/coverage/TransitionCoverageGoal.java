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
public class TransitionCoverageGoal<
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
	protected static final Logger logger = LoggerFactory.getLogger(TransitionCoverageGoal.class);

	private Transition transition;
	
	/**
	 * 
	 */
	public TransitionCoverageGoal(Transition trans) {
		transition = trans;
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
			ExecutionTrace executionTrace = executionResult.getExectionTrace();
			for (Object transition : executionTrace.getCoveredTransitions()) {
				EFSMTransition coveredTransition = (EFSMTransition)transition;
				CoverageGoal goal = new TransitionCoverageGoal(coveredTransition);
//				individual.addFitness(goal, 0d);
				updateIndividual(goal, individual, 0d);
			}
		}
	}

}
