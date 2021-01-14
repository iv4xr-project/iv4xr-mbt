/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.utils.Randomness;

/**
 * @author kifetew
 *
 */
class EFSMPathTest {

	EFSM model;
	RandomLengthTestFactory testFactory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		long seed = 1234;
		Randomness.getInstance(seed);
		EFSMFactory factory = EFSMFactory.getInstance();
		assertNotNull(factory);
		model = factory.getEFSM();
		assertNotNull(model);
		testFactory = new RandomLengthTestFactory<>(model);
		assertNotNull(testFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm.EFSMPath#isConnected()}.
	 */
	@Test
	void testIsConnected1() {
		for (int i = 0; i < 5000; i++) {
			Testcase testcase = testFactory.getTestcase();
			EFSMPath path = ((AbstractTestSequence)testcase).getPath();
			assertTrue(path.isConnected());
		}
	}

	
	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm.EFSMPath#isConnected()}.
	 */
	@Test
	void testIsConnected2() {
		Testcase testcase = testFactory.getTestcase();
		EFSMPath path = ((AbstractTestSequence)testcase).getPath();
		path.getModfiableTransitions().remove(1);
		assertFalse(path.isConnected());
	}
	
}
