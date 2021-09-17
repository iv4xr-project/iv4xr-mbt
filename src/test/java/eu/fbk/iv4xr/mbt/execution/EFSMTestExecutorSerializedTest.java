/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.evosuite.Properties;
import org.junit.Ignore;
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
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

import org.evosuite.utils.Randomness;

/**
 * @author kifetew
 *
 */
class EFSMTestExecutorSerializedTest {

	@Test
	@Ignore
	void testDeserializeAndTest () {
		String serializedModelFile = getLocalFilePath ("serialization/EFSM_model.ser");
		String serializedTestFile = getLocalFilePath("serialization/test.ser");
		try {
//			// load serialized model
			EFSM efsm = TestSerializationUtils.loadEFSM(serializedModelFile);
			EFSMTestExecutor.getInstance().setEFSM(efsm);
			
			// load a serialzed test, execute it on model
			AbstractTestSequence testcase = TestSerializationUtils.loadTestSequence(serializedTestFile);
			ExecutionResult result = EFSMTestExecutor.getInstance().executeTestcase(testcase);
			assertFalse(result.isSuccess());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getLocalFilePath(String resourceName) {
		return this.getClass().getClassLoader().getResource(resourceName).getPath();
	}

}
