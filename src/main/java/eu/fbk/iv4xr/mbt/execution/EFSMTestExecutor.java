/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutor<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		extends TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {
	
	private static EFSMTestExecutor instance = null;
	
	private EFSM clone;
	
	public static EFSMTestExecutor getInstance() {
		if (instance == null) {
			instance = new EFSMTestExecutor();
		}
		return instance;
	}
	
//	/**
//	 * 
//	 */
//	private EFSMTestExecutor(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm) {
//		this.efsm = efsm;
//	}

	private EFSMTestExecutor() {
		this.efsm = EFSMFactory.getInstance().getEFSM();
		this.clone = SerializationUtils.clone(this.efsm);
	}

	// TODO only for debugging purposes, to be disabled
	public void setEFSM (EFSM efsm) {
		this.efsm = efsm;
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
		
		// TODO DEBUG
//		if (success) {
//			Map<Transition, Integer> evenCounts = tc.getPath().selfTransitionCounts();
//			if (!evenCounts.isEmpty()) {
//				System.out.println("check individual");
//				System.out.println(testcase);
//				System.out.println(evenCounts.toString());
//			}
//		}
		
		notifyExecutionFinished(success);
		reset();
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		return result;
	}

	private boolean applyTransitions(List<Transition> transitions) {
		boolean success = true;
		for (Transition t : transitions) {
			notifyTransitionStarted(t);
			Set<OutParameter> output = efsm.transition(t);
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
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.executionFinished(this, success);
		}
		
	}

	private void notifyExecutionStarted() {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.executionStarted(this);
		}
		
	}
	
	private void notifyTransitionStarted(Transition t) {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.transitionStarted(this, t);
		}
		
	}
	
	private void notifyTransitionFinished(Transition t, boolean success) {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
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
		
		// This also works, but is slower
		//efsm = SerializationUtils.clone(this.clone);
		return true;
	}

}
