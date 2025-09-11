/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.ConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.GenericTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;

/**
 * 
 */
public class MinecraftTestConcretizer extends GenericTestConcretizer {

	@Override
	/**
	 * FIXME this is only a placeholder code!
	 */
	public ConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {
		Path path = abstractTestCase.getPath();
		List<EFSMTransition> transitions = path.getTransitions();
		
		// get the EFSM model and reset it
		EFSMFactory modelFactory = EFSMFactory.getInstance(true);
		EFSM model = modelFactory.getEFSM();
		
		ConcreteTestCase concreteTestCase = new MinecraftConcreteTestCase();
		for (EFSMTransition transition : transitions) {
			// execute the transition
			model.transition(transition);
			
			// translate the transition to the desired concrete content ..
			// and add it to the concrete test case
			
			
		}
		
		return concreteTestCase;
	}

}
