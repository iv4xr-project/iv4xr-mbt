/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

//import de.upb.testify.efsm.Configuration;
//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.utils.Randomness;
//import eu.fbk.se.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.efsm4j.*;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;


/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactory<State, Parameter, Context extends IEFSMContext<Context>, Trans extends Transition<State, Parameter, Context>> implements TestFactory {
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
		Testcase testcase = new AbstractTestSequence();
		int randomLength = Randomness.nextInt(maxLength) + 1;
		Configuration initialConfiguration = model.getInitialConfiguration();
		//LabRecruitsState currentState = (LabRecruitsState) initialConfiguration.getState();
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
			if (Randomness.nextDouble() < 0.1) {
				parameters.add((Parameter) "");
			}else {
				parameters.add((Parameter) ((EFSMState)transition.getTgt()).getId());
			}
			
			// take the state at the end of the chosen transition, and repeat
			//currentState = (LabRecruitsState) transition.getTgt();
			currentState = transition.getTgt();
			//// until maxLength is reached or final state is reached
			len++;
		}
		
		Path path = new Path (transitions, parameters);
		((AbstractTestSequence)testcase).setPath(path);
		return testcase;
	}

}
