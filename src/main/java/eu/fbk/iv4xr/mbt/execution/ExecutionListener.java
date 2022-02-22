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
public interface ExecutionListener {
	
	public void executionStarted(TestExecutor testExecutor);
	
	public void executionFinished(TestExecutor testExecutor, boolean success);
	
	public void transitionStarted (TestExecutor testExecutor, EFSMTransition t);
	
	public void transitionFinished (TestExecutor testExecutor, EFSMTransition t, boolean success);

	public ExecutionTrace getExecutionTrace();
}
