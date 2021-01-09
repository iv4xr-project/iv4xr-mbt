/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutionListener<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		implements ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {

	private ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionTrace = null;
	
	/**
	 * 
	 */
	public EFSMTestExecutionListener() {
		executionTrace = new ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
	}

	@Override
	public void executionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor) {
		
	}

	@Override
	public void executionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor) {
		
	}


	@Override
	public void transitionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t) {
		
	}

	@Override
	public void transitionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean success) {
		if (success) {
			executionTrace.coveredTransitions.add(t);
		}
		
	}

	/**
	 * @return the executionTrace
	 */
	@Override
	public ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> getExecutionTrace() {
		return executionTrace;
	}
	

}
