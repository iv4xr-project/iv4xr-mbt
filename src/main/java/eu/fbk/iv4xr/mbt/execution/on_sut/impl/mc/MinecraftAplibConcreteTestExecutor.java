package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.util.LinkedList;
import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.impl.AplibConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftAplibConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibTestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.minecraftlib.MinecraftEnv;
import eu.fbk.iv4xr.minecraftlib.MinecraftState;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;

/**
 * This class transforms a test suite generated from an EFMS model
 * 
 * @author agusmeroli
 *
 */
public class MinecraftAplibConcreteTestExecutor implements AplibConcreteTestExecutor {
	protected EFSM model;
	private int maxCycle;

	private MinecraftEnv environment;

	private TestAgent testAgent;
	private MinecraftState state;

	private AplibTestConcretizer testConcretizer;
	private TestDataCollector dataCollector;

	private TestSuiteExecutionReport reporter;
	protected int failures = 0;

	public MinecraftAplibConcreteTestExecutor(EFSM model, String mineflayerTestURL, String levelPath,
			String agent, int x, int y, int z) {

		this.model = model;
		this.environment = new MinecraftEnv(mineflayerTestURL);
		this.state = new MinecraftState();
		this.testAgent = new TestAgent(agent, "tester");

		dataCollector = new TestDataCollector();

		testAgent.attachState(state);
		testAgent.attachEnvironment(environment);
		testAgent.setTestDataCollector(dataCollector);
		

		this.testConcretizer = new MinecraftAplibConcretizer(testAgent, model);

		environment.buildLevel(levelPath, x, y, z);	


		this.reporter = new TestSuiteExecutionReport();
	}

	@Override
	public boolean executeTestSuite(SuiteChromosome solution) throws InterruptedException {
		boolean result = true;
		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			result &= executeTestCase(testcase);
		}

		return result;
	}

	// run a test case
	public boolean executeTestCase(AbstractTestSequence testcase) throws InterruptedException {
		// false if at least one goal fails
		boolean status = true;

		// Start registering the time of the test suite execution
		long initialTime = System.currentTimeMillis();
		
		LinkedList<TestCaseExecutionReport> goalReporter = new LinkedList<TestCaseExecutionReport>();
		
		System.out.println("Executing: " + testcase.toString());


		AplibConcreteTestCase concreteTestCase = (AplibConcreteTestCase)testConcretizer
				.concretizeTestCase(testcase);
		List<GoalStructure> goals = concreteTestCase.getGoalStructures();

		// iterate over goals
		// 
		for (int i = 0; i < goals.size(); i++) {
		//for (GoalStructure g : goals) {
			GoalStructure g = goals.get(i);
			testAgent.setGoal(g);
			System.err.println("Testing "+testcase.getPath().getTransitionAt(i).toString());

			// try to execute the test case
			int nCycle = 0;
			while (g.getStatus().inProgress()) {
				testAgent.update();

				Thread.sleep(20);
				nCycle++;
			}

			AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
			goalRep.addReport(g, "Pass", testcase.getPath().getTransitionAt(i), getGoalStatus(g));
			goalReporter.add(goalRep);

			status &= !g.getStatus().success();
		}
		
		// stop the time
		long finalTime = System.currentTimeMillis();
		long timeDuration = finalTime - initialTime;
		

		reporter.addTestCaseReport(testcase, goalReporter, status, timeDuration);
		return status;

	}
	
	
	// covert the goal status of a goal structure to a string
	private String getGoalStatus(GoalStructure goal) {
		if (goal instanceof PrimitiveGoal) {
			return goal.getStatus().toString();
		}else {
			String out = "";
			for(GoalStructure g : goal.getSubgoals()) {
				out = out + getGoalStatus(g) +"; ";
			}
			return out;
		}
	}

	@Override
	public TestSuiteExecutionReport getReport() {
		return reporter;
	}

	@Override
	public void setMaxCyclePerGoal(int max) {
		this.maxCycle = max;
	}

	@Override
	public int getMaxCylcePerGoal() {
		return maxCycle;
	}
}
