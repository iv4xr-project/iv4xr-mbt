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
import eu.fbk.iv4xr.mbt.efsm4j.Configuration;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.utils.Randomness;


/**
 * @author kifetew
 *
 */
public class RandomParameterLengthTestFactory<
	State extends EFSMState, 
	Parameter extends EFSMParameter, 
	Context extends IEFSMContext<Context>, 
	Trans extends Transition<State, Parameter, Context>> implements TestFactory {
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomParameterLengthTestFactory.class);
	
	private int maxLength = MBTProperties.MAX_PATH_LENGTH;
	EFSM<State, Parameter, Context, Trans> model = null;
	/**
	 * 
	 */
	public RandomParameterLengthTestFactory(EFSM<State, Parameter, Context, Trans> efsm) {
		model = efsm;
	}

	
	/**
	 * 
	 */
	public RandomParameterLengthTestFactory(EFSM<State, Parameter, Context, Trans> efsm, int max) {
		model = efsm;
		maxLength = max;
	}
	
	@Override
	public Testcase getTestcase() {
		int randomLength = Randomness.nextInt(maxLength) + 1;
		
		model.reset();
		
		Configuration<State, Context> initialConfiguration = model.getInitialConfiguration();
		State currentState = (State)initialConfiguration.getState();
		
		
		Collection<Trans> transitions = new LinkedList<Trans>();
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (final?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			
			// first generate a random input parameter
			Parameter input = model.getRandom();
			
			// get set of "valid" transitions from the current state, with the given input
			Set<Trans> outgoingTransitions = model.transitionsOutOf(currentState, input);
			
			int attempt = 0;
			while (outgoingTransitions.isEmpty() && attempt < 5) {
				input = model.getRandom();
				outgoingTransitions = model.transitionsOutOf(currentState, input);
			}
			
			// if no transition could be found, end the path here
			if (outgoingTransitions.isEmpty()) {
				logger.warn("Could not find outgoing transitions at this point after: " + attempt + " attempts");
				break;
			}
			
			// pick one transition at random and add it to path
			Trans transition = Randomness.choice(outgoingTransitions);
			transitions.add(transition);
			parameters.add(input);
			
			// apply the current transition/input on the model
			model.transition(input, transition);
			
			// take the state at the end of the chosen transition, and repeat
			currentState = model.getConfiguration().getState(); // transition.getTgt();
			
			// until maxLength is reached or final state is reached
			len++;
		}
		model.reset();
		
		// build the test case
		Testcase testcase = new AbstractTestSequence<EFSMState, EFSMParameter, Context, Transition<EFSMState, EFSMParameter, Context>>();
		Path path = new Path (transitions, parameters);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

}
