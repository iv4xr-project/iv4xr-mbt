/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution.on_sut.impl;

import java.nio.file.Paths;

import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.impl.SpaceEngineersTestConcretizer;
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 */
public class SpaceEngineersConcreteTestExecutor implements ConcreteTestExecutor {

	private String executableRootDir;
	private String levelFileName;
	private String levelFolder;
	private String agentName;

	// number of cycle the execution a transition can take
	private int maxCyclePerGoal = 200;

	private TestConcretizer testConcretizer;
	
	// basic reporting for test case status
	private TestSuiteExecutionReport testReporter;
	
	/**
	 * 
	 */
	public SpaceEngineersConcreteTestExecutor(String execRootDir, String levelPath, String agentName, int maxCyclePerGoal) {
		this.testConcretizer = new SpaceEngineersTestConcretizer();
		this.executableRootDir = execRootDir;
		// split level path	
		this.levelFileName = Paths.get(levelPath).getFileName().toString();
		this.levelFolder = Paths.get(levelPath).getParent().toString();
		this.agentName = agentName;
		this.maxCyclePerGoal = maxCyclePerGoal;
		testReporter = new TestSuiteExecutionReport();
	}

	@Override
	public boolean executeTestSuite(SuiteChromosome testSuite) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TestSuiteExecutionReport getReport() {
		return testReporter;
	}

	@Override
	public void setMaxCyclePerGoal(int max) {
		maxCyclePerGoal = max;
	}

	@Override
	public int getMaxCylcePerGoal() {
		return maxCyclePerGoal;
	}

}
