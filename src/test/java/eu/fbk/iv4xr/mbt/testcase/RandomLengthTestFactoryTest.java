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
		LabRecruitsEFSMFactory efsmFactory = new LabRecruitsEFSMFactory();
		assertNotNull(efsmFactory);
		String scenarioId = "buttons_doors_1";
		EFSM efsm = efsmFactory.getEFSM(scenarioId);
		assertNotNull (efsm);
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull (testFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#RandomLengthTestFactory(de.upb.testify.efsm.EFSM, int)}.
	 */
	@Test
	public void testRandomLengthTestFactoryEFSMInt() {
		LabRecruitsEFSMFactory efsmFactory = new LabRecruitsEFSMFactory();
		assertNotNull(efsmFactory);
		String scenarioId = "buttons_doors_1";
		EFSM efsm = efsmFactory.getEFSM(scenarioId);
		assertNotNull (efsm);
		int maxLength = 20;
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm, maxLength);
		assertNotNull (testFactory);
		Testcase t = testFactory.getTestcase();
		assertTrue (t.getLength() <= maxLength);
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
		System.out.println(((AbstractTestSequence) testcase).toDot());
		
	}

}
