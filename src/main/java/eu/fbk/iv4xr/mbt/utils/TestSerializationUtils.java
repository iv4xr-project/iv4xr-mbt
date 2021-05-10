/**
 * 
 */
package eu.fbk.iv4xr.mbt.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

/**
 * @author kifetew
 *
 */
public class TestSerializationUtils {

	/**
	 * 
	 */
	public TestSerializationUtils() {
		// TODO Auto-generated constructor stub
	}

	
	public static AbstractTestSequence loadTestSequence (String filename) throws FileNotFoundException {
		AbstractTestSequence ts = (AbstractTestSequence) SerializationUtils.deserialize(new FileInputStream(filename));
		return ts;
	}
	
	public static void saveTestSequence (AbstractTestSequence test, String filename) throws FileNotFoundException {
		SerializationUtils.serialize(test, new FileOutputStream(filename));
	}
	
	public static EFSM loadEFSM(String filename) throws FileNotFoundException {
		EFSM efsm = (EFSM) SerializationUtils.deserialize(new FileInputStream(filename));
		return efsm;
	}
	
	public static void saveEFSM(EFSM efsm, String filename) throws FileNotFoundException {
		SerializationUtils.serialize(efsm, new FileOutputStream(filename) );
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
