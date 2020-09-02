/**
 * 
 */
package eu.fbk.iv4xr.mbt.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.io.ExportException;
import org.junit.Before;
import org.junit.Test;

import de.upb.testify.efsm.Configuration;
import de.upb.testify.efsm.EFSM;
import eu.fbk.se.labrecruits.LabRecruitsContext;
import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author Davide Prandi
 *
 * Aug 19, 2020
 */
public class LabRecruitsRandomEFSMTest {
	
	@Before
	public void createDataFolder() {
		File dataDirectory = new File("data/");
		 if (! dataDirectory.exists()){
			 dataDirectory.mkdir();
		    }
	}
	
	@Test
	// default parameters: nButtons = 5, nDoors = 4, meanButtonsPerRoom = 1
	public void generateLevelDefaultParameters() throws IOException, ExportException  {		
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		
		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.generateLevel();
		String levelId = "data/level1";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
				
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
		
		// try to go door0
		assertNotNull(testEFSM.transition("d0+"));
		assertNull(testEFSM.transition("d0-"));
		// try to go over door3
		assertNotNull(testEFSM.transition("d3-"));
		assertNull(testEFSM.transition("d3+"));
		// press button 0 and go over door0
		assertNotNull(testEFSM.transition("b0"));
		assertNotNull(testEFSM.transition(""));
		assertNotNull(testEFSM.transition("d0+"));
		assertNotNull(testEFSM.transition("d0-"));
		// check if door1 and door3 are also open, while door2 is still closed
		context = (LabRecruitsContext) testEFSM.getConfiguration().getContext();
		assertTrue(context.getDoorStatus("door1"));
		assertTrue(context.getDoorStatus("door3"));
		assertFalse(context.getDoorStatus("door2"));
		// open door2
		assertNotNull(testEFSM.transition("b2"));
		assertNotNull(testEFSM.transition(""));
		// check if door2 is open
		context = (LabRecruitsContext) testEFSM.getConfiguration().getContext();
		assertTrue(context.getDoorStatus("door2"));

	}
	
	@Test
	// large number of buttons (20) and doors (20)
	public void generateLargerLevel() throws IOException, ExportException  {		
		// initalize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		labRecruitsRandomEFSM.set_nButtons(20);
		labRecruitsRandomEFSM.set_nDoors(20);
		
		
		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.generateLevel();
		String levelId = "data/level2";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		
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
		
		// press b0 and go through door1
		assertNotNull(testEFSM.transition(""));
		assertNotNull(testEFSM.transition("d1+"));
		assertNotNull(testEFSM.transition("d1-"));
		// all doors in the room  (door0, door1, door3, and door4) should be open
		context = (LabRecruitsContext) testEFSM.getConfiguration().getContext();
		assertTrue(context.getDoorStatus("door0"));
		assertTrue(context.getDoorStatus("door1"));
		assertTrue(context.getDoorStatus("door3"));
		assertTrue(context.getDoorStatus("door4"));		
		// press b11
		assertNotNull(testEFSM.transition("b11"));
		assertNotNull(testEFSM.transition(""));
		// doors door0 and door4 are now closed
		context = (LabRecruitsContext) testEFSM.getConfiguration().getContext();
		assertFalse(context.getDoorStatus("door0"));
		assertTrue(context.getDoorStatus("door1"));
		assertTrue(context.getDoorStatus("door3"));
		assertFalse(context.getDoorStatus("door4"));
		// take door3
		assertNotNull(testEFSM.transition("d3-"));
		assertNotNull(testEFSM.transition("d3+"));
		// press b7 and go over door6
		assertNotNull(testEFSM.transition("b7"));
		assertNotNull(testEFSM.transition(""));
		assertNotNull(testEFSM.transition("d6-"));
		assertNotNull(testEFSM.transition("d6+"));
		// buttons b8 and b9 are dummy
		context = (LabRecruitsContext) testEFSM.getConfiguration().getContext();
		assertNotNull(testEFSM.transition("b8"));
		assertNotNull(testEFSM.transition(""));
		LabRecruitsContext context2 =  (LabRecruitsContext) testEFSM.getConfiguration().getContext(); 
		for(String door: context.keySet()) {
			assertTrue(context.get(door).getStatus().equals(context2.get(door).getStatus()));
		}
		assertNotNull(testEFSM.transition("b9"));
		assertNotNull(testEFSM.transition(""));
		context2 =  (LabRecruitsContext) testEFSM.getConfiguration().getContext(); 
		for(String door: context.keySet()) {
			assertTrue(context.get(door).getStatus().equals(context2.get(door).getStatus()));
		}
		
	}
		
	@Test
	// almost one button per room, and many more doors
	public void generateEmptyRoomsLevel() throws IOException, ExportException  {		
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		labRecruitsRandomEFSM.set_meanButtonsPerRoom(0.1);
		labRecruitsRandomEFSM.set_nDoors(10);

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.generateLevel();
		String levelId = "data/level3";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		
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
		
	}
	
	@Test
	// many buttons: if not enough doors to connect generated room, exceeding buttons are discarded
	public void generateManyButtonsLevel() throws IOException, ExportException  {		
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();
		labRecruitsRandomEFSM.set_nButtons(20);
		labRecruitsRandomEFSM.set_meanButtonsPerRoom(4);

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.generateLevel();
		String levelId = "data/level4";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(levelId);
		
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
		
	}
	


}
