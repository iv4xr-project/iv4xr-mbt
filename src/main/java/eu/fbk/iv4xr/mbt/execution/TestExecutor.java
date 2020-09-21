/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;

import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public abstract class TestExecutor {

	/**
	 * 
	 */
	public TestExecutor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Execute the given test case (sequence of transitions) on the model
	 * @param testcase
	 * @return execution trace
	 */
	abstract ExecutionResult executeTestcase (Testcase testcase);
	
	/**
	 * Execute the given test suite (set of test cases) on the model
	 * @param testSuite
	 * @return execution trace
	 */
	abstract ExecutionResult executeTestSuite (List<Testcase> testSuite);
	
	/**
	 * reset the model to the initial state
	 * @return
	 */
	abstract boolean reset ();
	
}
