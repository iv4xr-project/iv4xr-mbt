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
 * Aug 19, 2020
 */
public class LabRecruitsRandomEFSMTest {
/*	
	@Before
	public void createDataFolder() {
		File dataDirectory = new File("data/");
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
	}
	
	@Test
	public void generateHugeLevel() throws IOException  {
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_buttons = 50;
		MBTProperties.LR_n_doors = 21 ;
		
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		
		// generate and EFSM
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
			Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/level3";
		// save door graph in graphml formal
		//labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		// save the level
		labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		
		// plot initial state and context 
		Configuration<LabRecruitsState, LabRecruitsContext> configuration = testEFSM.getConfiguration();
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		LabRecruitsContext context = configuration.getContext();
		Set set = context.keySet();
		Iterator iterator = set.iterator();
		System.out.println("Context is ");
		while (iterator.hasNext()) {
			String doorId = (String) iterator.next();
			System.out.println(" door " + doorId + " has button " + context.get(doorId).getButtons().toString());
		}
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}
	}
	
	@Test
	public void generateLargeLevel() throws IOException  {
		MBTProperties.LR_seed = 32325439;
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_buttons = 40;
		MBTProperties.LR_n_doors = 15;
		
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		
		// generate and EFSM
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
			Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/level2";
		// save door graph in graphml formal
		//labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		// save the level
		labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		
		// plot initial state and context 
		Configuration<LabRecruitsState, LabRecruitsContext> configuration = testEFSM.getConfiguration();
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		LabRecruitsContext context = configuration.getContext();
		Set set = context.keySet();
		Iterator iterator = set.iterator();
		System.out.println("Context is ");
		while (iterator.hasNext()) {
			String doorId = (String) iterator.next();
			System.out.println(" door " + doorId + " has button " + context.get(doorId).getButtons().toString());
		}
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}
				
		
	}
	
	@Test
	// default parameters: nButtons = 5, nDoors = 4, meanButtonsPerRoom = 1
	public void generateLevelDefaultParameters() throws IOException  {		
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		
		// generate and EFSM
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
			Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> testEFSM = labRecruitsRandomEFSM.getEFMS();
		String levelId = "data/level1";
		// save door graph in graphml formal
		//labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		// save the level
		labRecruitsRandomEFSM.saveLabRecruitsLevel(levelId);
		
		// plot initial state and context 
		Configuration<LabRecruitsState, LabRecruitsContext> configuration = testEFSM.getConfiguration();		
		System.out.println("");
		System.out.println("***** " + levelId);
		System.out.println("Initial state is " + configuration.getState().toString());
		LabRecruitsContext context =  configuration.getContext(); 
		Set set =  context.keySet();
		Iterator iterator = set.iterator();
		System.out.println("Context is ");
		while(iterator.hasNext()) {
			String doorId = (String) iterator.next();
			System.out.println(" door "+doorId+" has button "+context.get(doorId).getButtons().toString());
		}
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}
		
		// empty input is not allowed
		assertNull(testEFSM.transition());
		// check if EXPLORE transitions are enabled
		assertTrue(testEFSM.canTransition( new LabRecruitsParameter(LabRecruitsAction.EXPLORE)));
		// check if TOGGLE transitions are enabled
		assertTrue(testEFSM.canTransition( new LabRecruitsParameter(LabRecruitsAction.TOGGLE)));
		
		// move to d2 and verify it is closed
		assertNotNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.EXPLORE), 
							new LabRecruitsState("d2+","door2")));
		assertNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.EXPLORE), 
							new LabRecruitsState("d2-","door2")));
		// return to button 0, press it, and then traverse door0
		assertNotNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.EXPLORE), 
				new LabRecruitsState("b0")));
		assertNotNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.TOGGLE)));
		assertNotNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.EXPLORE), 
				new LabRecruitsState("d2+","door2")));
		assertNotNull(testEFSM.transition(new LabRecruitsParameter(LabRecruitsAction.EXPLORE), 
				new LabRecruitsState("d2-","door2")));
		
	}
*/

}
