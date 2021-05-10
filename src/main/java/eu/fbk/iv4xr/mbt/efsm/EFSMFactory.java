package eu.fbk.iv4xr.mbt.efsm;

import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1Fire;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;


/**
 * @author Davide Prandi
 *
 * Sep 18, 2020
 */
public class EFSMFactory {

	private static EFSMFactory instance;
	protected EFSM efsm;
	
	// a copy of the original model to be used only for serializing the original model to file
	// MUST not be modified
	private EFSM originalEfsm;
	
	/**
	 * Factory that depending on the SUT return the appropriate model
	 */
	private EFSMFactory() {
		switch (MBTProperties.SUT_EFSM) {
		case "labrecruits.buttons_doors_1" :
			ButtonDoors1 bd1 = new ButtonDoors1();
			efsm = bd1.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "labrecruits.buttons_doors_fire" :
			ButtonDoors1Fire bdf = new ButtonDoors1Fire();
			efsm = bdf.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		default:
			if (MBTProperties.SUT_EFSM.startsWith("labrecruits.random_")){
				switch (MBTProperties.SUT_EFSM) {
				case "labrecruits.random_default" :
					break;
				case "labrecruits.random_simple" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 5;
					MBTProperties.LR_n_doors = 4;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_medium" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 10;
					MBTProperties.LR_n_doors = 8;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_large" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 15;
					MBTProperties.LR_n_doors = 8;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_extreme" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 40;
					MBTProperties.LR_n_doors = 28;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_impossible" :
					MBTProperties.LR_mean_buttons = 5;
					MBTProperties.LR_n_buttons = 80;
					MBTProperties.LR_n_doors = 40;
					MBTProperties.LR_seed = 325439;
					break;
				default:
					throw new RuntimeException("Unrecognized random SUT: " + MBTProperties.SUT_EFSM);
				}
				LabRecruitsRandomEFSM randomGenerator = new LabRecruitsRandomEFSM();
				efsm = randomGenerator.getEFMS();
				efsm.setShortestPathsBetweenStates();
				efsm.setEFMSString(randomGenerator.get_csv());
				efsm.setAnmlString(randomGenerator.getAnml());
				efsm.setDotString(randomGenerator.getEFSMAsDot());
			//	efsm.setEFSMStringRemoveMutations(randomGenerator.getRemoveMutations());
			//	efsm.setEFSMStringAddMutations(randomGenerator.getAddMutations());
				
				originalEfsm = SerializationUtils.clone(efsm);
			}else {
				throw new RuntimeException("Unrecognized SUT: " + MBTProperties.SUT_EFSM);
			}
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
	
	public EFSM getEFSM() {
		return efsm;
	}
	
	public byte[] getOriginalEFSM() {
		return SerializationUtils.serialize(originalEfsm);
	}
	
}