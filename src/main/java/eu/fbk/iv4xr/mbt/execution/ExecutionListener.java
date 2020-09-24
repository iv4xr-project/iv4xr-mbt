/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public interface ExecutionListener<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> {
	
	public void executionStarted(TestExecutor<State, Parameter, Context, Trans> testExecutor);
	
	public void executionFinished(TestExecutor<State, Parameter, Context, Trans> testExecutor);
	
	public void transitionStarted (TestExecutor<State, Parameter, Context, Trans> testExecutor, Trans t, Parameter p);
	
	public void transitionFinished (TestExecutor<State, Parameter, Context, Trans> testExecutor, Trans t, Parameter p, Set<Parameter> o, boolean success);

	public ExecutionTrace<State, Parameter, Context, Trans> getExecutionTrace();
}
