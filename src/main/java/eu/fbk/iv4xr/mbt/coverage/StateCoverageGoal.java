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
					new EFSMTestExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
			testExecutor.addListner(executionListner);
			ExecutionResult executionResult = testExecutor.executeTestcase(testcase);
			// get trace from the listner
			ExecutionTrace trace = executionListner.getExecutionTrace();
			
			Path path = testcase.getPath();
			if (executionResult.isSuccess()) {
				if (path.getStates().contains(state)) {
					fitness = 0;
				}else {
					fitness = 1;
				}
			}else {
				fitness = 100; //infeasible path
			}
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

}
