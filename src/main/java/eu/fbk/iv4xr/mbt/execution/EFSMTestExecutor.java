/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;

import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutor<
State,
Parameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends TestExecutor {

	EFSM<State, Parameter, Context, Trans> efsm;
	
	/**
	 * 
	 */
	public EFSMTestExecutor(EFSM<State, Parameter, Context, Trans> efsm) {
		this.efsm = efsm;
		efsm.reset();
	}

	@Override
	ExecutionResult executeTestcase(Testcase testcase) {
		ExecutionResult result = new ExecutionResult();
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		boolean success = efsm.applyTransitions(tc.getPath().getTransitions(), tc.getPath().getParameterValues());
		//populate the result here...
		result.setSuccess(success);
		return result;
	}

	@Override
	ExecutionResult executeTestSuite(List<Testcase> testSuite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean reset() {
		efsm.reset();
		return true;
	}

}
