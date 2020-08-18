/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upb.testify.efsm.EFSM;
import eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory;

/**
 * @author kifetew
 *
 */
public class RandomLengthTestFactoryTest {

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#RandomLengthTestFactory(de.upb.testify.efsm.EFSM)}.
	 */
	@Test
	public void testRandomLengthTestFactoryEFSM() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#RandomLengthTestFactory(de.upb.testify.efsm.EFSM, int)}.
	 */
	@Test
	public void testRandomLengthTestFactoryEFSMInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#getTestcase()}.
	 */
	@Test
	public void testGetTestcase() {
		LabRecruitsEFSMFactory factory = new LabRecruitsEFSMFactory();
		assertNotNull(factory);
		String scenarioId = "buttons_doors_1";
		EFSM efsm = factory.getEFSM(scenarioId);
		assertNotNull (efsm);
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull(testFactory);
		Testcase testcase = testFactory.getTestcase();
		assertNotNull(testcase);
		
	}

}
