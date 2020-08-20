/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.upb.testify.efsm.Configuration;
import de.upb.testify.efsm.EFSM;
import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.utils.Randomness;
import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactory implements TestFactory {
	private int maxLength = 100;
	EFSM model = null;
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM efsm) {
		model = efsm;
	}

	
	/**
	 * 
	 */
	public RandomLengthTestFactory(EFSM efsm, int max) {
		model = efsm;
		maxLength = max;
	}
	
	@Override
	public Testcase getTestcase() {
		Testcase testcase = new AbstractTestSequence();
		int randomLength = Randomness.nextInt(maxLength);
		Configuration initialConfiguration = model.getInitialConfiguration();
		LabRecruitsState currentState = (LabRecruitsState) initialConfiguration.getState();
		
		Collection<Transition> transitions = new HashSet<Transition>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (final?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			Set<Transition> outgoingTransitions = model.transitionsOutOf(currentState);
			
			// pick one transition at random and add it to path
			Transition transition = Randomness.choice(outgoingTransitions);
			transitions.add(transition);
			
			// take the state at the end of the chosen transition, and repeat
			currentState = (LabRecruitsState) transition.getTgt();
			// until maxLength is reached or final state is reached
			len++;
		}
		
		Path path = new Path (transitions);
		((AbstractTestSequence)testcase).setPath(path);
		return testcase;
	}

}
