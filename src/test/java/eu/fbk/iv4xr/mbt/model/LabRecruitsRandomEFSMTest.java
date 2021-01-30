/**
 * 
 */
package eu.fbk.iv4xr.mbt.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

//import org.jgrapht.io.ExportException;
import org.junit.Before;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;

//import de.upb.testify.efsm.Configuration;
//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
//import eu.fbk.se.labrecruits.LabRecruitsContext;
//import eu.fbk.se.labrecruits.LabRecruitsState;

//import eu.fbk.iv4xr.mbt.efsm4j.*;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.*;

/**
 * @author Davide Prandi
 *
 *         Aug 19, 2020
 */
public class LabRecruitsRandomEFSMTest {

	@Before
	public void createDataFolder() {
		File dataDirectory = new File("data/");
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
	}

	@Test
	public void generateSimpleTest() throws IOException {
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_buttons = 5;
		MBTProperties.LR_n_doors = 4;

		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/simpleLevel";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		// save the level
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}else {
			// save the level
			labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		}

		// plot initial state and context
		EFSMConfiguration configuration = testEFSM.getConfiguration();
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		EFSMContext context = configuration.getContext();
		System.out.println("Context is " + configuration.getContext().toString());

		// take possibile states
		// Set<EFSMState> testEFSMStates = testEFSM.getStates();

		// Set<EFSMTransition> outTransitions = testEFSM.transitionsOutOf((EFSMState)
		// testEFSMStates.toArray()[6]);

		
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}

	}

	@Test
	public void generateLargeLevel() throws IOException {
		MBTProperties.LR_seed = 325439;
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_buttons = 40;
		MBTProperties.LR_n_doors = 28;
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/largeLevel";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		// save the level
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}else {
			// save the level
			labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		}

		// plot initial state and context
		EFSMConfiguration configuration = testEFSM.getConfiguration();
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		EFSMContext context = configuration.getContext();
		System.out.println("Context is " + configuration.getContext().toString());
	}

	@Test
	// default parameters: nButtons = 5, nDoors = 4, meanButtonsPerRoom = 1
	public void generateLevelDefaultParameters() throws IOException {
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/defaultParameters";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		

		// plot initial state and context
		EFSMConfiguration configuration = testEFSM.getConfiguration();
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		EFSMContext context = configuration.getContext();
		System.out.println("Context is " + configuration.getContext().toString());

		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}else {
			// save the level
			labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		}
		
	}

}
