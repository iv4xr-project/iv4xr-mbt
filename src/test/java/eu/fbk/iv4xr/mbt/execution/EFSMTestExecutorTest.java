/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

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
		MBTProperties.SUT_EFSM = "buttons_doors_1";
		LabRecruitsEFSMFactory factory = LabRecruitsEFSMFactory.getInstance(true);
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
		for (int i = 0; i < 100; i++) {
			Testcase testcase = testFactory.getTestcase();
			assertNotNull(testcase);
			
			ExecutionResult result = executor.executeTestcase(testcase);
			executor.reset();
			assertNotNull (result);
			if (result.isSuccess()) {
				System.out.println("******** Feasible path found");
				System.out.println(((AbstractTestSequence)testcase).toDot());
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
