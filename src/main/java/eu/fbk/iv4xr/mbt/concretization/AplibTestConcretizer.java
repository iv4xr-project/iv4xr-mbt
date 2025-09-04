/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization;

import java.util.LinkedList;
import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * @author kifetew
 *
 * This abstract class represents the concretization of an abstract test case to the corresponding concrete test case for eventual execution using the Aplib library
 * Each SUT must provide an implementation for this interface so as to enable execution of tests on the actual SUT
 *
 */
public abstract class AplibTestConcretizer implements TestConcretizer {

	private TestAgent testAgent;
	public AplibTestConcretizer(TestAgent testAgent) {
		this.testAgent = testAgent;
	}
	
	public ConcreteTestCase concretizeTestCase(AbstractTestSequence testCase) {
		Path path = testCase.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		List<GoalStructure> goals = convertTestCaseToGoalStructure(listTransitions);
		AplibConcreteTestCase concreteTestCase = new AplibConcreteTestCase();
		concreteTestCase.setGoalStructures(goals);
		return concreteTestCase ;
	}

	
	// translating a test-case represented as a sequence of EFSM-transitions to a list of goal-structures:
	public List<GoalStructure> convertTestCaseToGoalStructure(List<EFSMTransition>  tc) {
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		for (EFSMTransition t : tc) {
			GoalStructure transitionGoals = convertEFMSTransitionToGoal(testAgent, t);
			subGoals.add(transitionGoals);
		}
		return subGoals;
		// GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0]));
		// return testingTask;
	}
	
	public abstract GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t);
	

}
