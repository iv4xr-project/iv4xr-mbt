/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public interface ExecutionListener {
	
	public void executionStarted(TestExecutor testExecutor);
	
	public void executionFinished(TestExecutor testExecutor, boolean success);
	
	public void transitionStarted (TestExecutor testExecutor, EFSMTransition t);
	
	public void transitionFinished (TestExecutor testExecutor, EFSMTransition t, boolean success);

	public ExecutionTrace getExecutionTrace();
}
