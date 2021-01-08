/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				if (path.contains(transition)) {
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
	public String toString() {
		return transition == null? "" : (transition.getSrc() + " --> " + transition.getTgt());
	}

}
