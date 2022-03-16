/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.evosuite.utils.Randomness;
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



/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactory implements TestFactory {
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestFactory.class);
	
	private int maxLength = MBTProperties.MAX_PATH_LENGTH;
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
		int randomLength = Randomness.nextInt(maxLength) + 1;
		EFSMConfiguration initialConfiguration = model.getInitialConfiguration();
		EFSMState currentState = (EFSMState)initialConfiguration.getState();
		
		
		List<EFSMTransition> transitions = new LinkedList<EFSMTransition>();
		int len = 0;
		
		// loop until random length reached or current state has not outgoing transitions (finalInParameter?)
		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
			Set<EFSMTransition> outgoingTransitions = model.transitionsOutOf(currentState);
			
			// pick one transition at random and add it to path
			EFSMTransition transition = (EFSMTransition) Randomness.choice(outgoingTransitions);
			transitions.add(transition);
			
			// take the state at the end of the chosen transition, and repeat
			currentState = transition.getTgt();
			
			// until maxLength is reached or final state is reached
			len++;
		}
		//model.reset();
		
		// build the test case
		Testcase testcase = new AbstractTestSequence();
		Path path = new Path (transitions);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.isConnected();
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

}
