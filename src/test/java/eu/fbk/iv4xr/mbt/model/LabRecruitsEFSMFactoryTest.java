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
		LabRecruitsEFSMFactory factory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(factory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory#getEFSM(java.lang.String)}.
	 */
	@Test
	public void testGetEFSM() {
		MBTProperties.SUT_EFSM = "buttons_doors_1";
		LabRecruitsEFSMFactory factory = LabRecruitsEFSMFactory.getInstance(true);
		assertNotNull(factory);
		EFSM<LabRecruitsState, String, LabRecruitsContext, 
		Transition<LabRecruitsState, String, LabRecruitsContext>> efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		Set<LabRecruitsState> states = efsm.getStates();
		assertNotNull (states);
		assertTrue (!states.isEmpty());
		assertTrue(11 == states.size());
		
		Set<Transition<LabRecruitsState, String, LabRecruitsContext>> transitions = efsm.getTransitons();
		assertNotNull (transitions);
		assertTrue (!transitions.isEmpty());
		assertTrue(31 == transitions.size());
	}

	@Test
	public void testGetRandomEFSM1() {
		MBTProperties.SUT_EFSM = "random_default";
		LabRecruitsEFSMFactory factory = LabRecruitsEFSMFactory.getInstance(true);
		assertNotNull(factory);
		EFSM<LabRecruitsState, String, LabRecruitsContext, 
		Transition<LabRecruitsState, String, LabRecruitsContext>> efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		Set<LabRecruitsState> states = efsm.getStates();
		assertNotNull (states);
		assertTrue (!states.isEmpty());
		assertTrue(13 == states.size());
		
		Set<Transition<LabRecruitsState, String, LabRecruitsContext>> transitions = efsm.getTransitons();
		assertNotNull (transitions);
		assertTrue (!transitions.isEmpty());
		assertTrue(61 == transitions.size());
	}
	
}
