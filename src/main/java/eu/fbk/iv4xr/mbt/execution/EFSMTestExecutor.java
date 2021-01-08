/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;



import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
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
		
		
	/**
	 * 
	 */
	public EFSMTestExecutor(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm) {
		this.efsm = efsm;
		reset();
	}

	public EFSMTestExecutor() {
		this.efsm = AlgorithmFactory.getModel();
		reset();
	}

	@Override
	public ExecutionResult executeTestcase(Testcase testcase) {
		notifyExecutionStarted();
		reset();
		ExecutionResult result = new ExecutionResult();
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		boolean success = applyTransitions(tc.getPath().getTransitions(), tc.getPath().getParameterValues());
		//populate the result here...
		result.setSuccess(success);
		testcase.setValid(success);
		notifyExecutionFinished();
		reset();
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		return result;
	}

	private boolean applyTransitions(List<Transition> transitions, List<InParameter> parameters) {
		boolean success = true;
		for (int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			InParameter p = parameters.get(i);
			notifyTransitionStarted(t, p);
			Set<OutParameter> output = efsm.transition(p, t);
			if (output == null) {
				success = false;
			}	
			notifyTransitionFinished(t, p, output, success);
			if (!success) {
				break;
			}
		}
		return success;
	}

	private void notifyExecutionFinished() {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.executionStarted(this);
		}
		
	}

	private void notifyExecutionStarted() {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.executionFinished(this);
		}
		
	}
	
	private void notifyTransitionStarted(Transition t, InParameter p) {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.transitionStarted(this, t, p);
		}
		
	}
	
	private void notifyTransitionFinished(Transition t, InParameter p, Set<OutParameter> o, boolean success) {
		for (ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> listner: listners) {
			listner.transitionFinished(this, t, p, o, success);
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
