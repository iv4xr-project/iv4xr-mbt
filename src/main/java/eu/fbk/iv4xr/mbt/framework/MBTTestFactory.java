/**
 * 
 */
package eu.fbk.iv4xr.mbt.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.fbk.iv4xr.mbt.Main;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * @author kifetew
 *
 */
public class MBTTestFactory implements ITestFactory {

	TestAgent testAgent;
	Main mbt;
	
	SuiteChromosome testSuite;
	List<GoalStructure> goals;
	LabRecruitsTestSuiteExecutor testConcretizer;
	Iterator<GoalStructure> testIterator;
	
	public MBTTestFactory() {
		mbt = new Main();
		testSuite = mbt.runTestGeneration();
		goals = new ArrayList<GoalStructure>();
		testConcretizer = new LabRecruitsTestSuiteExecutor();
		for (MBTChromosome mbtTest : testSuite.getTestChromosomes()) {
			goals.addAll(testConcretizer.convertTestCaseToGoalStructure(testAgent, (AbstractTestSequence) mbtTest.getTestcase()));
		}
		testIterator = goals.iterator();
	}
	
	@Override
	public void attachAgent(TestAgent agent) {
		this.testAgent = agent;
	}

	@Override
	public TestAgent getAgent() {
		return testAgent;
	}

	@Override
	public void reset() {
		testSuite = null;
		goals = null;
	}

	@Override
	public GoalStructure nextGoal() {
		if (testIterator.hasNext()) {
			return testIterator.next();
		}else {
			return null;
		}
	}

	@Override
	public GoalStructure execute(GoalStructure G, boolean reset) {
		// the executor interface needs to be generalized in MBT
//		return testExecutor.executeTestCase(G);
		return null;
	}
	

	
	/**
	 * Possible usage scenario: setup the factory, iterate over the tests generated, execute them, collect results
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// setup the test agent ..
		TestAgent agent = new TestAgent();
		
		// create the MBT test factory, with parameters ...
		MBTTestFactory factory = new MBTTestFactory();
		
		// attach the agent to the factory
		factory.attachAgent(agent);
		
		// iterate over the tests generated
		List<TestDataCollector> results = new ArrayList<>();
		GoalStructure goal;
		do {
			goal = factory.nextGoal();
			if (goal != null) {
				factory.execute(goal, false);
				TestDataCollector result = factory.getTestResults();
				results.add(result);
			}
		}while (goal != null);

	}


}
