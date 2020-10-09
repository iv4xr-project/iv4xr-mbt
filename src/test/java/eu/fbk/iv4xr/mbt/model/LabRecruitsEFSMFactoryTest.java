/**
 * 
 */
package eu.fbk.iv4xr.mbt.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.*;
import eu.fbk.iv4xr.mbt.MBTProperties;
//import eu.fbk.se.labrecruits.LabRecruitsContext;
//import eu.fbk.se.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.*;

/**
 * @author kifetew
 *
 */
public class LabRecruitsEFSMFactoryTest {

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory#LabRecruitsEFSMFactory()}.
	 */
	@Test
	public void testLabRecruitsEFSMFactory() {
		EFSMFactory factory = EFSMFactory.getInstance();
		assertNotNull(factory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory#getEFSM(java.lang.String)}.
	 */
	@Test
	public void testGetEFSM() {
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		EFSMFactory factory = EFSMFactory.getInstance(true);
		assertNotNull(factory);
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		Set<LabRecruitsState> states = efsm.getStates();
		assertNotNull (states);
		assertTrue (!states.isEmpty());
		assertTrue(11 == states.size());
		
		Set<Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> transitions = efsm.getTransitons();
		assertNotNull (transitions);
		assertTrue (!transitions.isEmpty());
		assertTrue(31 == transitions.size());
	}

	@Test
	public void testGetRandomEFSM1() {
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		EFSMFactory factory = EFSMFactory.getInstance(true);
		assertNotNull(factory);
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		Set<LabRecruitsState> states = efsm.getStates();
		assertNotNull (states);
		assertTrue (!states.isEmpty());
		assertTrue(11 == states.size());
		
		Set<Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> transitions = efsm.getTransitons();
		assertNotNull (transitions);
		assertTrue (!transitions.isEmpty());
		assertTrue(43 == transitions.size());
	}
	
}
