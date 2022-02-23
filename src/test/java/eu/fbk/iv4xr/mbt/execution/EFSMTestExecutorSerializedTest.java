/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

/**
 * @author kifetew
 *
 */
class EFSMTestExecutorSerializedTest {

	@Test
	@Ignore
	void testDeserializeAndTest () {
		// Serialized model from buttons_doors_1_with_count level
		String serializedModelFile = getLocalFilePath ("serialization/EFSM_model.ser");
		// Serialized test from buttons_doors_1_with_count (valid)
		String serializedTestFile = getLocalFilePath("serialization/test.ser");
		try {
//			// load serialized model
			EFSM efsm = TestSerializationUtils.loadEFSM(serializedModelFile);
			EFSMTestExecutor.getInstance().setEFSM(efsm);
			
			// load a serialzed test, execute it on model
			AbstractTestSequence testcase = TestSerializationUtils.loadTestSequence(serializedTestFile);
			ExecutionResult result = EFSMTestExecutor.getInstance().executeTestcase(testcase);
			assertTrue(result.isSuccess());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			fail();
			e.printStackTrace();
		}
	}

	private String getLocalFilePath(String resourceName) {
		return this.getClass().getClassLoader().getResource(resourceName).getPath();
	}

}
