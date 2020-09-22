/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutionListener<
State,
Parameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> implements ExecutionListener<State, Parameter, Context, Trans> {

	private ExecutionTrace<State, Parameter, Context, Trans> executionTrace = null;
	
	/**
	 * 
	 */
	public EFSMTestExecutionListener() {
		executionTrace = new ExecutionTrace<State, Parameter, Context, Trans>();
	}

	@Override
	public void executionStarted(TestExecutor<State, Parameter, Context, Trans> testExecutor) {
		
	}

	@Override
	public void executionFinished(TestExecutor<State, Parameter, Context, Trans> testExecutor) {
		
	}


	@Override
	public void transitionStarted(TestExecutor<State, Parameter, Context, Trans> testExecutor, Trans t, Parameter p) {
		
	}

	@Override
	public void transitionFinished(TestExecutor<State, Parameter, Context, Trans> testExecutor, Trans t, Parameter p, Set<Parameter> o, boolean success) {
		if (success) {
			executionTrace.coveredTransitions.add(t);
		}
		
	}

	/**
	 * @return the executionTrace
	 */
	@Override
	public ExecutionTrace<State, Parameter, Context, Trans> getExecutionTrace() {
		return executionTrace;
	}
	

}
