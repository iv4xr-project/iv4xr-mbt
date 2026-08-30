package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.examples.TrafficLight.outSignal;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

public class LiftSystem {
	
		public enum outSignal{ sig0, sig1, sig2, sigS };
	
	// four states
		public EFSMState floor0 = new EFSMState("Floor0");
		public EFSMState floor1 = new EFSMState("Floor1");
		public EFSMState floor2 = new EFSMState("Floor2");
		public EFSMState stop = new EFSMState("Stop");
		
		// context 
		public Var<Integer> floorNumber = new Var<Integer>("floorNumber", 0);
		public EFSMContext tlContext = new EFSMContext(floorNumber);
		
		// operation increment floor number
		Assign<Integer> incFloor = new Assign(floorNumber, new IntSum(floorNumber, new Const(1)));
		Assign<Integer> decFloor = new Assign(floorNumber, new IntSum(floorNumber, new Const(-1)));
		private Var<Boolean> stopButton = new Var<Boolean>("stopButton",true);
		//// input
		private Var<Boolean> pulse = new Var<Boolean>("pulse",true);
		
		//// output 
		public Var<Enum> signal = new Var<Enum>("signal", outSignal.sig0);
		//public EFSMParameter output = new EFSMParameter(signal);
		
		public EFSM getModel() {
			
			//// Define transition
			
			EFSMTransition t_0 = new EFSMTransition();
			EFSMTransition t_1 = new EFSMTransition();
			EFSMTransition t_2 = new EFSMTransition();
			EFSMTransition t_3 = new EFSMTransition();
			
			EFSMTransition t_4 = new EFSMTransition();
			EFSMTransition t_5 = new EFSMTransition();
			EFSMTransition t_6 = new EFSMTransition();
			
			EFSMTransition t_7 = new EFSMTransition();
			EFSMTransition t_8 = new EFSMTransition();
			EFSMTransition t_9 = new EFSMTransition();
			
			EFSMTransition t_10 = new EFSMTransition();
			EFSMTransition t_11 = new EFSMTransition();
			
			// t_1 : 0 -> 1
		
			t_1.setGuard(new EFSMGuard(pulse));
			t_1.setOp(new EFSMOperation(incFloor));
			Var<Enum> t01ut = new Var<Enum>("signal", outSignal.sig1);
			t_1.setOutParameter(new EFSMParameter(t01ut));
			
			
			// t_2 : 1 -> 2
			
			t_2.setGuard(new EFSMGuard(pulse));
			t_2.setOp(new EFSMOperation(incFloor));
			Var<Enum> t12ut = new Var<Enum>("signal", outSignal.sig2);
			t_2.setOutParameter(new EFSMParameter(t12ut));
			
			
			// t_3 : 2 -> 1
			
			t_3.setGuard(new EFSMGuard(pulse));
			t_3.setOp(new EFSMOperation(decFloor));
			Var<Enum> t21ut = new Var<Enum>("signal", outSignal.sig1);
			t_3.setOutParameter(new EFSMParameter(t21ut));
			
			// t_4 : 1 -> 0
			
			t_4.setGuard(new EFSMGuard(pulse));
			t_4.setOp(new EFSMOperation(decFloor));
			Var<Enum> t10ut = new Var<Enum>("signal", outSignal.sig0);
			t_4.setOutParameter(new EFSMParameter(t10ut));
			
			// t_5 : 0 -> 2
			
				t_5.setGuard(new EFSMGuard(pulse));
				t_5.setOp(new EFSMOperation(incFloor));
				t_5.setOp(new EFSMOperation(incFloor));
				Var<Enum> t02ut = new Var<Enum>("signal", outSignal.sig2);
				t_5.setOutParameter(new EFSMParameter(t02ut));
				
			// t_6 : 2 -> 0
				
				t_6.setGuard(new EFSMGuard(pulse));
				t_6.setOp(new EFSMOperation(decFloor));
				t_6.setOp(new EFSMOperation(decFloor));
				Var<Enum> t20ut = new Var<Enum>("signal", outSignal.sig0);
				t_6.setOutParameter(new EFSMParameter(t20ut));
				
				
			// STOP CASE
				
				t_7.setGuard(new EFSMGuard(pulse));
				Var<Boolean> t7In = new Var<Boolean>("stopButton",true);
				t_7.setInParameter(new EFSMParameter(t7In));
				Var<Enum> t7sut = new Var<Enum>("signal", outSignal.sigS);
				t_7.setOutParameter(new EFSMParameter(t7sut));
				
				
			// STOP CASE -> Floors
				
				t_8.setGuard(new EFSMGuard(pulse));
				Var<Boolean> t8In = new Var<Boolean>("stopButton",false);
				t_8.setInParameter(new EFSMParameter(t8In));
				Var<Enum> t8sut = new Var<Enum>("signal", outSignal.sig0);
				t_8.setOutParameter(new EFSMParameter(t8sut));
				
				t_9.setGuard(new EFSMGuard(pulse));
				Var<Boolean> t9In = new Var<Boolean>("stopButton",false);
				t_9.setInParameter(new EFSMParameter(t9In));
				Var<Enum> t9sut = new Var<Enum>("signal", outSignal.sig1);
				t_9.setOutParameter(new EFSMParameter(t9sut));
				
				
				t_10.setGuard(new EFSMGuard(pulse));
				Var<Boolean> t10In = new Var<Boolean>("stopButton",false);
				t_8.setInParameter(new EFSMParameter(t10In));
				Var<Enum> t10sut = new Var<Enum>("signal", outSignal.sig2);
				t_10.setOutParameter(new EFSMParameter(t10sut));
				
				

			EFSM liftSystemEFSM;

			EFSMBuilder liftSystemEFSMBuilder = new EFSMBuilder(EFSM.class);
			LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();
			
			
			
			liftSystemEFSM = liftSystemEFSMBuilder
		    		.withTransition(floor0, floor0, t_0)
		    		.withTransition(floor0, floor1, t_1)
		    		.withTransition(floor0, floor2, t_2)
		    		.withTransition(floor1, floor0, t_3)
		    		.withTransition(floor1, floor2, t_4)
		    		.withTransition(floor2, floor2, t_5)
		    		.withTransition(floor2, floor0, t_6)
		    		.withTransition(floor0, stop, t_7)
		    		.withTransition(floor1, stop, t_8)
		    		.withTransition(floor2, stop, t_8)
		     		.withTransition(stop, floor0, t_9)
		    		.withTransition(stop, floor1, t_10)
		    		.withTransition(stop, floor2, t_11)
		    		.build(floor0,tlContext, lrParameterGenerator);
		    
		    return(liftSystemEFSM);
		}
		
		
	}

		


