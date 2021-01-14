/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import org.evosuite.ga.Chromosome;
//import org.evosuite.ga.FitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
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
		testExecutor = new EFSMTestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
	}

	@Override
	public double getFitness(Chromosome individual) {
		double fitness = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionListner = 
					new EFSMTestExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition>(testcase, this);
			testExecutor.addListner(executionListner);
			ExecutionResult executionResult = testExecutor.executeTestcase(testcase);
			// get trace from the listner
			ExecutionTrace trace = executionListner.getExecutionTrace();
			
			// calculate feasibility fitness
			double feasibilityFitness = -1;
			if (executionResult.isSuccess()) {
				feasibilityFitness = 0d;
			}else {
				feasibilityFitness = trace.getPathApproachLevel() + trace.getPathBranchDistance();
			}
			
			// calculate coverage target fitness
			double targetFitness = -1;
			
			//FIXME currently, path.getStates() returns correct set only if the path is valid,
			// otherwise, it returns only the source of each transition, hence some vertices may not be in the set
			if (executionResult.isSuccess()) {
				if (testcase.getPath().getStates().contains(state)) {
					if (trace.isCurrentGoalCovered()) {
						targetFitness = 0d;
					}else {
						// target in path, but not covered => path is not feasible?
						// TODO check this
						targetFitness = trace.getTargetApproachLevel() + trace.getTargetBranchDistance();
					}
				}else { // if target not in path, calculate shortest path to target
					//TODO calculate shortest path to target
					targetFitness = getShortestDistanceToTarget (testcase.getPath(), state);
				}
			}else { // if path not valid
				//FIXME for now, simply take feasibilityFitness
				targetFitness = feasibilityFitness;
			}
			
			// calculate the fitness as a linear combination of the two fitnesses
			fitness = feasibilityFitness + targetFitness;
			if (!testcase.isValid() && fitness == 0d) {
				logger.debug("Goal: {}", state);
				logger.debug("ERROR: {}", testcase);
			}
//			logger.debug("Target: {} Fitness: {}", state.toString(), fitness);
//			logger.debug(chromosome.getTestcase().toString());
			testExecutor.removeListner(executionListner);
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

}
