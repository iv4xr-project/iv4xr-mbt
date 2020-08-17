/**
 * 
 */
package eu.fbk.iv4xr.mbt.model;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import de.upb.testify.efsm.EFSM;

/**
 * @author kifetew
 *
 */
public class LabRecruitsEFSMFactoryTest {

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory#LabRecruitsEFSMFactory()}.
	 */
	@Test
	public void testLabRecruitsEFSMFactory() {
		LabRecruitsEFSMFactory factory = new LabRecruitsEFSMFactory();
		assertNotNull(factory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory#getEFSM(java.lang.String)}.
	 */
	@Test
	public void testGetEFSM() {
		LabRecruitsEFSMFactory factory = new LabRecruitsEFSMFactory();
		assertNotNull(factory);
		String scenarioId = "buttons_doors_1";
		EFSM efsm = factory.getEFSM(scenarioId);
		assertNotNull (efsm);
		
		Set states = efsm.getStates();
		assertNotNull (states);
		assertTrue (!states.isEmpty());
		assertTrue(11 == states.size());
		
		Set transitions = efsm.getTransitons();
		assertNotNull (transitions);
		assertTrue (!transitions.isEmpty());
		assertTrue(31 == transitions.size());
	}

}
