/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution.on_sut;

import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 * This interface defines methods for the execution of test cases against the SUT
 * Could invoke a {@code TestConcretizer} to concretize test cases before executing them
 * After execution is finished, report can be retrieved
 */
public interface ConcreteTestExecutor {
	public boolean executeTestSuite(SuiteChromosome testSuite) throws InterruptedException;
	
	public boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException;
	
	public TestSuiteExecutionReport getReport();
	
	public void setMaxCyclePerGoal(int max);

	public int getMaxCylcePerGoal();
}
