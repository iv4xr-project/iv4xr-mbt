/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutor<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends TestExecutor<State, Parameter, Context, Trans> {
	
	
	/**
	 * 
	 */
	public EFSMTestExecutor(EFSM<State, Parameter, Context, Trans> efsm) {
		this.efsm = efsm;
		efsm.reset();
	}

	public EFSMTestExecutor() {
		this.efsm = AlgorithmFactory.getModel();
	}

	@Override
	public ExecutionResult executeTestcase(Testcase testcase) {
		notifyExecutionStarted();
		ExecutionResult result = new ExecutionResult();
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		boolean success = applyTransitions(tc.getPath().getTransitions(), tc.getPath().getParameterValues());
		//populate the result here...
		result.setSuccess(success);
		testcase.setValid(success);
		notifyExecutionFinished();
		return result;
	}

	private boolean applyTransitions(List<Trans> transitions, List<Parameter> parameters) {
		boolean success = true;
		for (int i = 0; i < transitions.size(); i++) {
			Trans t = transitions.get(i);
			Parameter p = parameters.get(i);
			notifyTransitionStarted(t, p);
			Set<Parameter> output = efsm.transition(p, t);
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
		for (ExecutionListener<State, Parameter, Context, Trans> listner: listners) {
			listner.executionStarted(this);
		}
		
	}

	private void notifyExecutionStarted() {
		for (ExecutionListener<State, Parameter, Context, Trans> listner: listners) {
			listner.executionFinished(this);
		}
		
	}
	
	private void notifyTransitionStarted(Trans t, Parameter p) {
		for (ExecutionListener<State, Parameter, Context, Trans> listner: listners) {
			listner.transitionStarted(this, t, p);
		}
		
	}
	
	private void notifyTransitionFinished(Trans t, Parameter p, Set<Parameter> o, boolean success) {
		for (ExecutionListener<State, Parameter, Context, Trans> listner: listners) {
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
