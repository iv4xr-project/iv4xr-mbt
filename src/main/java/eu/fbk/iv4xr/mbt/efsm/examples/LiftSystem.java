package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

public class LiftSystem {
	
	// four states
		public EFSMState floor0 = new EFSMState("Floor0");
		public EFSMState floor1 = new EFSMState("Floor1");
		public EFSMState floor2 = new EFSMState("Floor2");
		public EFSMState stop = new EFSMState("Stop");
		
		// context 
		public Var<Integer> floorNumber = new Var<Integer>("count", 0);
		
		// operation increment floor number
		Assign<Integer> incFloor = new Assign(floorNumber, new IntSum(floorNumber, new Const(1)));
		Assign<Integer> decFloor = new Assign(floorNumber, new IntSum(floorNumber, new Const(-1)));
		
		

		

}
