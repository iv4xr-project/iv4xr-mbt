/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionListener;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew, prandi
 *
 * Represents a sequence of K transitions, where K >= 2
 * K is defined in MBTProperties by parameter k_transition_size
 */
public class KTransitionCoverageGoal<
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
	private static final long serialVersionUID = -6341416548064403719L;
	
	// It is conveniet to use a path for a kTransition goal
	private EFSMPath kTransition;
	
	/**
	 * 
	 */
	public KTransitionCoverageGoal(EFSMPath t) {
		this.kTransition = t;
	}

	

	@Override
	public double getFitness(Chromosome individual) {
		double fitness = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionListner = 
					new EFSMTestExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition>(testcase, this);
//			testExecutor.reset();
			EFSMTestExecutor.getInstance().addListner(executionListner);
			ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
			// get trace from the listener
			ExecutionTrace trace = executionListner.getExecutionTrace();
			
			// add trace to result
			executionResult.setExectionTrace(trace);
			
			if (MBTProperties.SANITY_CHECK_FITNESS) {
				fitness = Randomness.nextDouble();
			}else {
				// feasibility fitness is the same of transition coverage
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
