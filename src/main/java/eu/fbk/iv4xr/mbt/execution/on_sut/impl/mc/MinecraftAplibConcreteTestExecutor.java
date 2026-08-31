package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.concretization.impl.AplibConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftAplibConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibTestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.minecraftlib.MinecraftAgent;
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

	public MinecraftAplibConcreteTestExecutor(EFSM model, String mineflayerTestURL, String serverURL, String levelPath, String agent,
			int x, int y, int z) throws IOException {

		this.model = model;
		this.environment = new MinecraftEnv(mineflayerTestURL);
		this.state = new MinecraftState();
		this.testAgent = new MinecraftAgent(agent, serverURL);

		this.dataCollector = new TestDataCollector();

		testAgent.attachState(state);
		testAgent.attachEnvironment(environment);
		testAgent.setTestDataCollector(dataCollector);
		
		


		this.testConcretizer = new MinecraftAplibConcretizer(testAgent, model);
		
		String levelString = Files.readString(Paths.get(levelPath), StandardCharsets.UTF_8);

		environment.buildLevel(testAgent.getId(), levelString, x, y, z);

		this.reporter = new TestSuiteExecutionReport();
	}

	@Override
	public boolean executeTestSuite(SuiteChromosome solution) throws InterruptedException {
		boolean result = true;
		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			if (i != 0) {				
				environment.resetAgent(testAgent.getId());
			}
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

		AplibConcreteTestCase concreteTestCase = (AplibConcreteTestCase) testConcretizer.concretizeTestCase(testcase);
		List<GoalStructure> goals = concreteTestCase.getGoalStructures();

		// iterate over goals
		//
		for (int i = 0; i < goals.size(); i++) {
			// for (GoalStructure g : goals) {
			GoalStructure g = goals.get(i);
			EFSMTransition transition = testcase.getPath().getTransitionAt(i);

			if (g == null) {
				System.err.println("Skipping " + transition);

				continue;
			}

			testAgent.setGoal(g);
			System.err.println("Testing " + transition);

			// try to execute the test case
			while (g.getStatus().inProgress()) {
				testAgent.update();
				if (dataCollector.getNumberOfFailVerdictsSeen() > 0) {
					// stop the time
					long finalTime = System.currentTimeMillis();
					long timeDuration = finalTime - initialTime;

					String err = "Failed verdict " + dataCollector.getLastFailVerdict().toString();
					System.err.println(err);
					AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
					goalRep.addReport(g, err, transition, getGoalStatus(g));
					goalReporter.add(goalRep);
					reporter.addTestCaseReport(testcase, goalReporter, Boolean.FALSE, timeDuration);
					return false;
				}
				Thread.sleep(20);
			}

			AplibTestCaseExecutionReport goalRep = new AplibTestCaseExecutionReport();
			goalRep.addReport(g, "Pass", transition, getGoalStatus(g));

			goalReporter.add(goalRep);

			// Not sure this matters since we exit early anyways
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
		} else {
			String out = "";
			for (GoalStructure g : goal.getSubgoals()) {
				out = out + getGoalStatus(g) + "; ";
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
