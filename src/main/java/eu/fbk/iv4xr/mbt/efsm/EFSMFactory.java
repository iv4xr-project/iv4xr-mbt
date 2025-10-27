package eu.fbk.iv4xr.mbt.efsm;

import java.lang.reflect.Method;


import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.cps.BeamNgModelGenerator;
import eu.fbk.iv4xr.mbt.efsm.cps.NineStates;
import eu.fbk.iv4xr.mbt.efsm.examples.TrafficLight;
import eu.fbk.iv4xr.mbt.efsm.minecraft.CartTest;
import eu.fbk.iv4xr.mbt.efsm.minecraft.DamageCheck;
import eu.fbk.iv4xr.mbt.efsm.minecraft.DurabilityTest;
import eu.fbk.iv4xr.mbt.efsm.minecraft.SuperDurability;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1Count;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1Fire;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1FireWithDeath;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.efsm.spaceEngineering.SingleBlockWeldingAndGrinding;
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCall1;


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
		case "minecraft.multi_durability":
			SuperDurability sdb = new SuperDurability();
			efsm = sdb.getModel();
			efsm.getShortestPathsBetweenStates();
			break;
		case "minecraft.durability":
			DurabilityTest dbt = new DurabilityTest();
			efsm = dbt.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "minecraft.damage":
			DamageCheck dmc = new DamageCheck();
			efsm = dmc.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "minecraft.cart":
			CartTest crt = new CartTest();
			efsm = crt.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "labrecruits.buttons_doors_1" :
			ButtonDoors1 bd1 = new ButtonDoors1();
			efsm = bd1.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "labrecruits.buttons_doors_1_count":
			ButtonDoors1Count bd1count = new ButtonDoors1Count();
			efsm = bd1count.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "labrecruits.buttons_doors_fire" :
			ButtonDoors1Fire bdf = new ButtonDoors1Fire();
			efsm = bdf.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "labrecruits.buttons_doors_fire_with_death":
			ButtonDoors1FireWithDeath bdfwd = new ButtonDoors1FireWithDeath();
			efsm = bdfwd.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "se.weld_and_grind":
			SingleBlockWeldingAndGrinding sbwad = new SingleBlockWeldingAndGrinding();
			efsm = sbwad.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "cps.beamng_nine_states":
			NineStates nineStates = new NineStates();
			efsm = nineStates.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "cps.beamng_custom_model":
			BeamNgModelGenerator modelGenerator = new BeamNgModelGenerator();
			efsm = modelGenerator.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "examples.traffic_light":
			TrafficLight trafficLight = new TrafficLight();
			efsm = trafficLight.getModel();
			efsm.setShortestPathsBetweenStates();
			break;
		case "usagecontrol.phone_call_1":
			PhoneCall1 phoneCall1 = new PhoneCall1();
			efsm = phoneCall1.getModel();
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
					MBTProperties.LR_n_buttons = 20;
					MBTProperties.LR_n_doors = 15;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_extreme" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 40;
					MBTProperties.LR_n_doors = 28;
					MBTProperties.LR_seed = 325439;
					break;
				case "labrecruits.random_impossible" :
					MBTProperties.LR_mean_buttons = 0.5;
					MBTProperties.LR_n_buttons = 100;
					MBTProperties.LR_n_doors = 100;
					MBTProperties.LR_seed = 325439;
					break;
				default:
					throw new RuntimeException("Unrecognized random SUT: " + MBTProperties.SUT_EFSM);
				}
				LabRecruitsRandomEFSM randomGenerator = new LabRecruitsRandomEFSM();
				efsm = randomGenerator.getEFMS();
				efsm.setShortestPathsBetweenStates();
				efsm.setEFMSString(randomGenerator.get_csv());
				//efsm.setAnmlString(randomGenerator.getAnml());
				efsm.setDotString(randomGenerator.getEFSMAsDot());
			//	efsm.setEFSMStringRemoveMutations(randomGenerator.getRemoveMutations());
			//	efsm.setEFSMStringAddMutations(randomGenerator.getAddMutations());
				
			}else {
				// is the EFSM name a class name? if so try to instantiate it
				try {
					Class<?> clazz = Class.forName(MBTProperties.SUT_EFSM);
					Object object = clazz.getDeclaredConstructor().newInstance();
					Method method = clazz.getDeclaredMethod("getModel");
					
					if (method != null) {
						Object ret = method.invoke(object);
						efsm = (EFSM)ret;
						efsm.setShortestPathsBetweenStates();
					}else {
						throw new RuntimeException("Unable to get the EFSM model from the provided class: " + MBTProperties.SUT_EFSM);
					}
				}catch (Exception e) {
					throw new RuntimeException("Unrecognized SUT: " + MBTProperties.SUT_EFSM);
				}
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
	
}