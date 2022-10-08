/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutor 
		extends TestExecutor {
	
	private static EFSMTestExecutor instance = null;
	
	public static EFSMTestExecutor getInstance() {
		if (instance == null) {
			instance = new EFSMTestExecutor();
		}
		return instance;
	}

	private EFSMTestExecutor() {
		this.efsm = EFSMFactory.getInstance().getEFSM();
	}

	// TODO only for debugging purposes, to be disabled
	public void setEFSM (EFSM efsm) {
		this.efsm = efsm;
	}
	
	public void resetEFSM() {
		this.efsm = EFSMFactory.getInstance().getEFSM();
	}
	
	
	@Override
	public ExecutionResult executeTestcase(Testcase testcase) {
		notifyExecutionStarted();
		ExecutionResult result = new ExecutionResult();
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		if (!tc.getPath().isConnected()) {
			throw new RuntimeException("Path not connected: " + testcase.toString());
		}
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		boolean success = applyTransitions(tc.getPath().getTransitions());
		//populate the result here...
		result.setSuccess(success);
		testcase.setValid(success);

		notifyExecutionFinished(success);
		reset();
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		return result;
	}

	private boolean applyTransitions(List<EFSMTransition> transitions) {
		boolean success = true;
		for (EFSMTransition t : transitions) {
			notifyTransitionStarted(t);
			Set<EFSMParameter> output = efsm.transition(t);
			if (output == null) {
				success = false;
			}	
			notifyTransitionFinished(t, success);
			if (!success) {
				break;
			}
		}
		return success;
	}

	private void notifyExecutionFinished(boolean success) {
		for (ExecutionListener listner: listners) {
			listner.executionFinished(this, success);
		}
		
	}

	private void notifyExecutionStarted() {
		for (ExecutionListener listner: listners) {
			listner.executionStarted(this);
		}
		
	}
	
	private void notifyTransitionStarted(EFSMTransition t) {
		for (ExecutionListener listner: listners) {
			listner.transitionStarted(this, t);
		}
		
	}
	
	private void notifyTransitionFinished(EFSMTransition t, boolean success) {
		for (ExecutionListener listner: listners) {
			listner.transitionFinished(this, t, success);
		}
		
	}

	@Override
	public ExecutionResult executeTestSuite(List<Testcase> testSuite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reset() {
		efsm.reset();
		return true;
	}

}
