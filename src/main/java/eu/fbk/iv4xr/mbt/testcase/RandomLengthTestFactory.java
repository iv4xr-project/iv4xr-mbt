/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm4j.Configuration;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.utils.Randomness;
//import eu.fbk.se.labrecruits.LabRecruitsState;


/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactory<
	State extends EFSMState, 
	Parameter extends EFSMParameter, 
	Context extends IEFSMContext<Context>, 
	Trans extends Transition<State, Parameter, Context>> implements TestFactory {
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestFactory.class);
	
	private int maxLength = 100;
	EFSM<State, Parameter, Context, Trans> model = null;
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM<State, Parameter, Context, Trans> efsm) {
		model = efsm;
	}

	
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM<State, Parameter, Context, Trans> efsm, int max) {
		model = efsm;
		maxLength = max;
	}
	
	@Override
	public Testcase getTestcase() {
		int randomLength = Randomness.nextInt(maxLength) + 1;
		Configuration<State, Context> initialConfiguration = model.getInitialConfiguration();
		State currentState = (State)initialConfiguration.getState();
		
		
		Collection<Trans> transitions = new LinkedList<Trans>();
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (final?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			Set<Trans> outgoingTransitions = model.transitionsOutOf(currentState);
			
			// pick one transition at random and add it to path
			Trans transition = Randomness.choice(outgoingTransitions);
			transitions.add(transition);
			
			// pick random parameter values for the transition
			parameters.add(model.getRandom());
						
			// take the state at the end of the chosen transition, and repeat
			currentState = transition.getTgt();
			
			// until maxLength is reached or final state is reached
			len++;
		}
		model.reset();
		
		// build the test case
		Testcase testcase = new AbstractTestSequence<State, Parameter, Context, Trans>(model);
		Path path = new Path (transitions, parameters);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

}
