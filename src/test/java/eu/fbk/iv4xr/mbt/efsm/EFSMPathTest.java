/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.TestExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.utils.Randomness;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

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

//	/**
//	 * Test method for {@link eu.fbk.iv4xr.mbt.efsm.EFSMPath#isConnected()}.
//	 * @throws FileNotFoundException 
//	 * @throws CloneNotSupportedException 
//	 */
//	@Test
//	void testIsConnected1() throws FileNotFoundException, CloneNotSupportedException {
//		Testcase temp;
//		for (int i = 0; i < 5000; i++) {
//			Testcase testcase = testFactory.getTestcase();
//			temp = testcase.clone(); 
//			EFSMPath path = ((AbstractTestSequence)testcase).getPath();
//			assertTrue(path.isConnected());
//			ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
//			EFSMTestExecutor.getInstance().reset();
//			
//			// serialize test to file
//			if (!executionResult.isSuccess()) {
//				executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
//				if (executionResult.isSuccess()) {
//					String filename = "tests/test_" + i + ".ser";
//					TestSerializationUtils.saveTestSequence((AbstractTestSequence) temp, filename);
//					System.out.println(testcase.toString());
//					assertTrue(executionResult.isSuccess());
//				}
//			}
//			
//		}
//	}
//
//	@Test
//	void testSerializedTest () throws FileNotFoundException {
//		String path = "tests/";
//		File dir = new File(path);
//		File[] tests = dir.listFiles();
//		for (File test : tests) {
//			AbstractTestSequence testSequence = TestSerializationUtils.loadTestSequence(test.getAbsolutePath());
//			boolean originalResult = testSequence.isValid();
//			ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testSequence);
//			assertTrue (executionResult.isSuccess() == originalResult);
//		}
//	}
	
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
