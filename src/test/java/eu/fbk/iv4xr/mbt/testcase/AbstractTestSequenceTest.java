/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.evosuite.utils.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSM;

/**
 * @author kifetew
 *
 */
class AbstractTestSequenceTest {

	
	RandomLengthTestFactory testFactory;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		EFSMFactory efsmFactory = EFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		testFactory = new RandomLengthTestFactory(efsm);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence#crossOver(eu.fbk.iv4xr.mbt.testcase.Testcase, int, int)}.
	 */
	@Test
	void testCrossOver() {
		Testcase testcase1 = (AbstractTestSequence)testFactory.getTestcase();
		Testcase testcase2 = (AbstractTestSequence)testFactory.getTestcase();
		testcase1.crossOver(testcase2, Randomness.nextInt(testcase1.getLength()), Randomness.nextInt(testcase2.getLength()));
	}

}
