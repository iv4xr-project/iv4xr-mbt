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

	abstract ExecutionResult executeTestcase (Testcase testcase);
	
	abstract ExecutionResult executeTestSuite (List<Testcase> testSuite);
	
}
