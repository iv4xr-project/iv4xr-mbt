/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
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
import eu.fbk.iv4xr.mbt.utils.Randomness;


/**
 * @author kifetew
 *
 */
public class RandomParameterLengthTestFactory<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> implements TestFactory {
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomParameterLengthTestFactory.class);
	
	private int maxLength = MBTProperties.MAX_PATH_LENGTH;
	EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = null;
	/**
	 * 
	 */
	public RandomParameterLengthTestFactory(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm) {
		model = efsm;
	}

	
	/**
	 * 
	 */
	public RandomParameterLengthTestFactory(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm, int max) {
		model = efsm;
		maxLength = max;
	}
	
	@Override
	public Testcase getTestcase() {
		int randomLength = Randomness.nextInt(maxLength) + 1;
		
		model.reset();
		
		EFSMConfiguration<State, Context> initialConfiguration = model.getInitialConfiguration();
		State currentState = (State)initialConfiguration.getState();
		
		
		Collection<Transition> transitions = new LinkedList<Transition>();
//		Collection<InParameter> parameters = new LinkedList<InParameter>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (final?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			
			// get set of "valid" transitions from the current state
			Set<EFSMTransition> outgoingTransitions = model.transitionsOutOf(currentState);
			
//			int attempt = 0;
//			while (outgoingTransitions.isEmpty() && attempt < 5) {
//				input = model.getRandomInput();
//				outgoingTransitions = model.transitionsOutOf(currentState, input);
//			}
			
			// if no transition could be found, end the path here
			if (outgoingTransitions.isEmpty()) {
				logger.warn("Could not find outgoing transitions at this point");
				break;
			}
			
			// pick one transition at random and add it to path
			Transition transition = (Transition) Randomness.choice(outgoingTransitions);
			transitions.add(transition);
//			parameters.add(input);
			
			// apply the current transition/input on the model
			model.transition(transition);
			
			// take the state at the end of the chosen transition, and repeat
			currentState = model.getConfiguration().getState(); // transition.getTgt();
			
			// until maxLength is reached or final state is reached
			len++;
		}
		model.reset();
		
		// build the test case
		Testcase testcase = new AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
		Path path = new Path (transitions);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

}
