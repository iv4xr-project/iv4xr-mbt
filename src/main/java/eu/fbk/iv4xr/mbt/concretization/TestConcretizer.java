/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization;

import java.util.LinkedList;
import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * @author kifetew
 *
 * This interface represents the concretization of an abstract test case to the corresponding concrete test case
 * Each SUT must provide an implementation for this interface so as to enable execution of tests on the actual SUT
 *
 */
public abstract class TestConcretizer {

	/*
	 * Model independent
	 * The concretizer does not consider the model but uses only a list of transitions. This means that
	 * during test execution it is not possible to test the actual values of the internal variables. 
	 */
	
	public List<GoalStructure> concretizeTestCase(TestAgent testAgent, AbstractTestSequence testCase) {
		Path path = testCase.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		return convertTestCaseToGoalStructure(testAgent,listTransitions) ;
	}

	// translating a test-case represented as a sequence of EFSM-transitions to a list of goal-structures:
	public  List<GoalStructure> convertTestCaseToGoalStructure(TestAgent agent, List<EFSMTransition>  tc ) {
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		for (EFSMTransition t : tc) {
			GoalStructure transitionGoals = convertEFMSTransitionToGoal(agent, t);
			subGoals.add(transitionGoals);
		}
		return subGoals;
		// GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0]));
		// return testingTask;
	}
	
	/*
	 * Model dependent
	 * During the concretization the abstract test case is executed on the model. This implies that the variables
	 * of the model can be used to check their current values against the SUT values
	 */
	
	public List<GoalStructure> concretizeTestCase(TestAgent testAgent, AbstractTestSequence testCase, EFSM model) {
		Path path = testCase.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		return convertTestCaseToGoalStructure(testAgent,listTransitions) ;
	}
			
	public  List<GoalStructure> convertTestCaseToGoalStructure(TestAgent agent, List<EFSMTransition>  tc, EFSM model) {
		// The list of goals to output
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>();
			
		for (EFSMTransition t : tc) {
			// Execute the transition on the model
			model.transition(t);
			// Get the goal structure corresponding to the current transition and the actual model state
			GoalStructure transitionGoals = convertEFMSTransitionToGoal(agent, t, model);
			subGoals.add(transitionGoals);
		}
		return subGoals;
		// GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0]));
		// return testingTask;
	}
	
	
	
	public abstract GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t);
	
	public abstract GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t, EFSM model);
	
	
}
