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
public interface ExecutionListener<State extends EFSMState,
		InParameter extends EFSMParameter,
		OutParameter extends EFSMParameter,
		Context extends EFSMContext,
		Operation extends EFSMOperation,
		Guard extends EFSMGuard,
		Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> {
	
	public void executionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor);
	
	public void executionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, boolean success);
	
	public void transitionStarted (TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t);
	
	public void transitionFinished (TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean success);

	public ExecutionTrace getExecutionTrace();
}
