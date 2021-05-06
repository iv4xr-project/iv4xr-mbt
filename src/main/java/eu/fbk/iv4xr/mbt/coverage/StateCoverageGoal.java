/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.Properties;

//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.archive.Archive;
//import org.evosuite.ga.FitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionListener;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoal<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> extends 
		CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8816426341946761190L;

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(StateCoverageGoal.class);
	
	private EFSMState state;
	
	/**
	 * 
	 */
	public StateCoverageGoal(State s) {
		state = s;
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
			// get trace from the listner
			ExecutionTrace trace = executionListner.getExecutionTrace();
			
			// add trace to result
			executionResult.setExectionTrace(trace);
			
			// calculate feasibility fitness
			double feasibilityFitness = -1;
			if (executionResult.isSuccess()) {
				feasibilityFitness = 0d;
			}else {
				feasibilityFitness = W_AL * trace.getPathApproachLevel() + W_BD * trace.getPathBranchDistance();
			}
			
			// calculate coverage target fitness
			double targetFitness = -1;
			
			if (executionResult.isSuccess()) {
				if (trace.isCurrentGoalCovered()) {
					targetFitness = 0d;
				}else {
					targetFitness = W_AL * trace.getTargetApproachLevel() + W_BD * trace.getTargetBranchDistance();
				}
			}else { // if path not valid
				//FIXME for now, simply take feasibilityFitness
				targetFitness = feasibilityFitness;
			}
			
			// calculate the fitness as a linear combination of the two fitnesses
			fitness = feasibilityFitness + targetFitness;
			EFSMTestExecutor.getInstance().removeListner(executionListner);
			//updateCollateralCoverage(individual, executionResult);
		}
		individual.setChanged(false);
		updateIndividual(this, individual, fitness);
		return fitness;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}


	@Override
	public String toString() {
		return state == null?"":state.toString();
	}

	/**
	 * @return the state
	 */
	public EFSMState getState() {
		return state;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (! (obj instanceof StateCoverageGoal)) {
			return false;
		}
		
		StateCoverageGoal other = (StateCoverageGoal)obj;
		return state.equals(other.getState());
		
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}

	@Override
	protected void updateCollateralCoverage(Chromosome individual, ExecutionResult executionResult) {
		// collateral coverage only if the individual is valid
		if (executionResult.isSuccess()) {
			ExecutionTrace executionTrace = executionResult.getExectionTrace();
			for (Object state : executionTrace.getCoveredStates()) {
				EFSMState coveredState = (EFSMState)state;
				CoverageGoal goal = new StateCoverageGoal(coveredState);
				individual.addFitness(goal, 0d);
			}
		}
		
	}

}
