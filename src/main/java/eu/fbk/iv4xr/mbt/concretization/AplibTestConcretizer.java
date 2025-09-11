/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.concretization;

import java.util.LinkedList;
import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.impl.AplibConcreteTestCase;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
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
		
		// get the EFSM model and reset it
		EFSMFactory modelFactory = EFSMFactory.getInstance();
		EFSM model = modelFactory.getEFSM();
		model.reset();
		
		for (EFSMTransition t : tc) {
			// execute the transition
			model.transition(t);
			
			GoalStructure transitionGoals = convertEFMSTransitionToGoal(testAgent, t, model);
			subGoals.add(transitionGoals);
		}
		return subGoals;
	}
	
	/**
	 * The EFSM model is updated after applying the transition t, hence the model context can be used to 
	 * access the current state of the model and the latest values of the variables, taking into account the effects of the transition.
	 * Use the method {@code model.getConfiguration()} to get the model context.
	 * 
	 * NB: the model can always be accessed via {@code EFSMFactory.getInstance().getEFSM()}. However here we are passing the model 
	 * explicitly to avoid implicit dependencies which are hard to trace and eventually debug.
	 */
	public abstract GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t, EFSM model);
	

}
