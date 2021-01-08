/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.Configuration;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.utils.Randomness;
//import eu.fbk.se.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.utils.Randomness;


/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactory<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> implements TestFactory {
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestFactory.class);
	
	private int maxLength = 100;
	EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = null;
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm) {
		model = efsm;
	}

	
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm, int max) {
		model = efsm;
		maxLength = max;
	}
	
	@Override
	public Testcase getTestcase() {
		int randomLength = Randomness.nextInt(maxLength) + 1;
		EFSMConfiguration<State, Context> initialConfiguration = model.getInitialConfiguration();
		State currentState = (State)initialConfiguration.getState();
		
		
		Collection<Transition> transitions = new LinkedList<Transition>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (finalInParameter?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			Set<Transition> outgoingTransitions = model.transitionsOutOf(currentState);
			
			// pick one transition at random and add it to path
			Transition transition = Randomness.choice(outgoingTransitions);
			transitions.add(transition);
			
			// take the state at the end of the chosen transition, and repeat
			currentState = transition.getTgt();
			
			// until maxLength is reached or final state is reached
			len++;
		}
		model.reset();
		
		// build the test case
		Testcase testcase = new AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition>(model);
		Path path = new Path (transitions);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

}
