package eu.fbk.iv4xr.mbt.modelchecking;


import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm.modelcheckingInterface.InterfaceToIv4xrModelCheker;

public class InterfaceToMCTest {

	
	void checkLocation(InterfaceToIv4xrModelCheker adapter, String location) {
		assertEquals(location,adapter.getCurrentState().conf.getState().getId()) ;
	}
	
	void checkDoor(InterfaceToIv4xrModelCheker adapter, String doorname, boolean doorState) {
	     assertEquals(doorState,adapter.getCurrentState().conf.getContext().getContext().getVariable(doorname).getValue()) ;
	}
	
	void printState(InterfaceToIv4xrModelCheker adapter) {
		var state = adapter.getCurrentState();
		System.out.println("=== state:" + state);
		System.out.println("=== available transitions:");
		for (var tr : adapter.availableTransitions()) {

			System.out.println("    " + tr);
		}
	}
	
	void executeTransition(InterfaceToIv4xrModelCheker adapter, String transitionId) {
		for (var tr : adapter.availableTransitions()) {
			if(tr.getId().equals(transitionId)) {
				adapter.execute(tr);
				System.out.println(">>> executing " + tr) ;
				return ;
			}
		}
		// should not reach this point
		assertTrue(false) ;
	}
	
	/**
	 * Check that the Interface-to MC correctly identifies the current state, available transitions,
	 * correctly execute transition, and correctlly roll-back to the previous state when asked to do
	 * so.
	 */
	@Test
	public void test() {
		
		EFSM anLRmodel = (new ButtonDoors1()).getModel() ;	
		InterfaceToIv4xrModelCheker adapter = new InterfaceToIv4xrModelCheker(anLRmodel) ;
		adapter.reset();	
		printState(adapter) ;
		checkLocation(adapter,"b0") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
	
		executeTransition(adapter,"t_0") ;
		printState(adapter) ;
		checkLocation(adapter,"b1") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
	
		executeTransition(adapter,"t_7") ;
		printState(adapter) ;
		checkLocation(adapter,"b1") ;
		checkDoor(adapter,"door1",true) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
	
		System.out.println(">>>> backtrack") ;
		assertTrue(adapter.backTrackToPreviousState()) ;
		printState(adapter) ;
		checkLocation(adapter,"b1") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
		
		System.out.println(">>>> backtrack") ;
		assertTrue(adapter.backTrackToPreviousState()) ;
		printState(adapter) ;
		checkLocation(adapter,"b0") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
		
		System.out.println(">>>> backtrack") ;
		assertFalse(adapter.backTrackToPreviousState()) ;
		printState(adapter) ;
		checkLocation(adapter,"b0") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
		
		executeTransition(adapter,"t_3") ;
		printState(adapter) ;
		checkLocation(adapter,"b0") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
		
		executeTransition(adapter,"t_0") ;
		printState(adapter) ;
		checkLocation(adapter,"b1") ;
		checkDoor(adapter,"door1",false) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
	
		executeTransition(adapter,"t_7") ;
		printState(adapter) ;
		checkLocation(adapter,"b1") ;
		checkDoor(adapter,"door1",true) ;
		checkDoor(adapter,"door2",false) ;
		checkDoor(adapter,"door3",false) ;
	}

}
