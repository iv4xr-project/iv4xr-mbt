/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.utils.Randomness;

/**
 * @author kifetew
 *
 */
class EFSMTestExecutorTest {

	EFSM efsm;
	
	EFSMTestExecutor executor;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		long seed = 1234;
		Randomness.getInstance(seed);
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		EFSMFactory factory = EFSMFactory.getInstance(true);
		assertNotNull(factory);
		efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		executor = new EFSMTestExecutor(efsm);
		assertNotNull (executor);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor#executeTestcase(eu.fbk.iv4xr.mbt.testcase.Testcase)}.
	 */
	@Test
	void testExecuteTestcase() {
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull(testFactory);
		for (int i = 0; i < 10; i++) {
			Testcase testcase = testFactory.getTestcase();
			assertNotNull(testcase);
			
			String beforeExec = ((AbstractTestSequence)testcase).toDot();
			ExecutionResult result = executor.executeTestcase(testcase);
			executor.reset();
			assertNotNull (result);
			if (result.isSuccess()) {
				System.out.println("******** Feasible path found");
				System.out.println(beforeExec);
				System.out.println(((AbstractTestSequence)testcase).toDot());
				System.out.println(((AbstractTestSequence)testcase).toString());
			}
		}
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor#executeTestSuite(java.util.List)}.
	 */
	@Test
	void testExecuteTestSuite() {
		// TODO
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor#reset()}.
	 */
	@Test
	void testReset() {
		RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
		assertNotNull(testFactory);
		Testcase testcase = testFactory.getTestcase();
		assertNotNull(testcase);
		
		ExecutionResult result = executor.executeTestcase(testcase);
		executor.reset();
		assertTrue(efsm.getConfiguration().getState().equals(efsm.getInitialConfiguration().getState()));
		//TODO should check also the equality of the contexts. Currently Context does not implement a custom "equals"!
	}

}
