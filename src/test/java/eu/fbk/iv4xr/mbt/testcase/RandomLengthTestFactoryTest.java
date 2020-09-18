/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.*;

import org.junit.Test;

//import de.upb.testify.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.MBTProperties;

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
		MBTProperties.SUT_EFSM = "buttons_doors_1";
		LabRecruitsEFSMFactory efsmFactory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull (testFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#RandomLengthTestFactory(de.upb.testify.efsm.EFSM, int)}.
	 */
	@Test
	public void testRandomLengthTestFactoryEFSMInt() {
		MBTProperties.SUT_EFSM = "buttons_doors_1";
		LabRecruitsEFSMFactory efsmFactory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
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
		MBTProperties.SUT_EFSM = "buttons_doors_1";
		LabRecruitsEFSMFactory efsmFactory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull(testFactory);
		Testcase testcase = testFactory.getTestcase();
		assertNotNull(testcase);
		System.out.println(((AbstractTestSequence) testcase).toDot());
		
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory#getTestcase()}.
	 */
	@Test
	public void testGetTestcaseFromRandomModel() {
		MBTProperties.SUT_EFSM = "random_default";
		LabRecruitsEFSMFactory efsmFactory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull(testFactory);
		Testcase testcase = testFactory.getTestcase();
		assertNotNull(testcase);
		System.out.println(((AbstractTestSequence) testcase).toDot());
		
	}
	
}
