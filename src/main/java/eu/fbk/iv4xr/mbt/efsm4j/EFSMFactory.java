/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

/**
 * @author Davide Prandi
 *
 * Sep 18, 2020
 */
public class EFSMFactory {

	private static EFSMFactory instance;
	protected EFSM efsm;
	
	
	/**
	 * Factory that depending on the SUT return the appropriate model
	 */
	private EFSMFactory() {
		switch (MBTProperties.SUT_EFSM) {
		case "labrecruits.buttons_doors_1" :
			ButtonDoors1 bd1 = new ButtonDoors1();
			efsm = bd1.getRoomReachabilityModel();
			break;
		case "labrecruits.random_default" :
			LabRecruitsRandomEFSM randomGenerator = new LabRecruitsRandomEFSM();
			efsm = randomGenerator.getEFMS();
			break;
		default:
			throw new RuntimeException("Unrecognized SUT: " + MBTProperties.SUT_EFSM);
		}
	}
	

	public static EFSMFactory getInstance() {
		if (instance == null) {
			instance = new EFSMFactory();
		}
		return instance;
	}

	public static EFSMFactory getInstance(boolean reset) {
		if (reset || instance == null) {
			instance = new EFSMFactory();
		}
		return instance;
	}
	
	public EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
	Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> getEFSM() {
		return efsm;
	}
	
	
	
}
