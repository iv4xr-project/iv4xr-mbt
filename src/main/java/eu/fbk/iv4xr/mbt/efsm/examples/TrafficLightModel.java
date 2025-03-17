package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

public class TrafficLightModel {
	
	
	public enum outSignal{ sigR, sigG, sigY };
	
	
	//// States
	// four states
	public EFSMState red = new EFSMState("Red");
	public EFSMState yellow = new EFSMState("Yellow");
	public EFSMState green = new EFSMState("Green");
	public EFSMState pending = new EFSMState("Pending");
	
	
	//// context 
	public Var<Integer> count = new Var<Integer>("count", 0);
	public EFSMContext tlContext = new EFSMContext(count);
	
	//// input
	public Var<Boolean> pedestrian = new Var<Boolean>("pedestrian",true);
	//public EFSMParameter input = new EFSMParameter(pedestrian);
			
	//// output 
	public Var<Enum> signal = new Var<Enum>("signal", outSignal.sigR);
	//public EFSMParameter output = new EFSMParameter(signal);

	
	//// operations
	// increment count
	Assign<Integer> incCount = new Assign(count, new IntSum(count, new Const(1)));
	// reset count
	Assign<Integer> resetCount = new Assign(count, new Const<Integer>(0));
	

	//// checks to be used in guards
	
	// constant 60
	Const<Integer> sixty = new Const<Integer>(60);
	// count greater that 60
	IntGreat countGreatThanSixty =  new IntGreat(count, sixty);
	// count equal 60
	IntEq countEqualSixty =  new IntEq(count, sixty);
	// count greater or equal 60
	BoolOr countGreatEqThanSixty = new BoolOr(countGreatThanSixty, countEqualSixty);
	// count less than 60
	BoolNot countLessThanSixty = new BoolNot(countGreatEqThanSixty);
	
	// constant 5
	Const<Integer> five = new Const<Integer>(5);
	// count greater that 60
	IntGreat countGreatThanFive = new IntGreat(count, five);
	// count equal 60
	IntEq countEqualfive = new IntEq(count, five);
	// count greater or equal 60
	BoolOr countGreatEqThanFive = new BoolOr(countGreatThanFive, countEqualfive);
	// count less than 60
	BoolNot countLessThanFive = new BoolNot(countGreatEqThanFive);
		
	// with pedestrian
	BoolAnd countGreatEqThanSixtyAndPedestrian = new BoolAnd(countGreatEqThanSixty,pedestrian);
	BoolAnd countLessThanSixtyAndPedestrian = new BoolAnd(countLessThanSixty,pedestrian);
	
	
	public TrafficLightModel() {
		
	}
	
	public EFSM getModel() {
	
		//// Define transition
		
		// t_0 : red -> red # increment count
		EFSMTransition t_0 = new EFSMTransition();
		t_0.setGuard(new EFSMGuard(countLessThanSixty));
		t_0.setOp(new EFSMOperation(incCount));
		
		// t_1 : red -> green
		EFSMTransition t_1 = new EFSMTransition();
		t_1.setGuard(new EFSMGuard(countGreatEqThanSixty));
		t_1.setOp(new EFSMOperation(resetCount));
		Var<Enum> t1Out = new Var<Enum>("signal", outSignal.sigG);
		t_1.setOutParameter(new EFSMParameter(t1Out));
		
		// t_2 : green -> green
		EFSMTransition t_2 = new EFSMTransition();
		t_2.setGuard(new EFSMGuard(countLessThanSixty));
		t_2.setOp(new EFSMOperation(incCount));
				
		// t_3 : green -> yellow 
		EFSMTransition t_3 = new EFSMTransition();
		// input
		Var<Boolean> t3In = new Var<Boolean>("pedestrian",true);		
		t_3.setInParameter(new EFSMParameter(t3In));
		// guard
		t_3.setGuard(new EFSMGuard(countGreatEqThanSixtyAndPedestrian));
		// operation
		t_3.setOp(new EFSMOperation(resetCount));
		// output
		Var<Enum> t3Out = new Var<Enum>("signal", outSignal.sigY);
		t_3.setOutParameter(new EFSMParameter(t3Out));
		
		// t_4 : green -> pedestrian
		EFSMTransition t_4 = new EFSMTransition();
		// input
		Var<Boolean> t4In = new Var<Boolean>("pedestrian",true);		
		t_4.setInParameter(new EFSMParameter(t4In));
		// guard
		t_4.setGuard(new EFSMGuard(countLessThanSixtyAndPedestrian));
		// no operation and no output
		
		
		
		
		//// The model and the associated builder
		EFSM trafficLightEFSM;

		EFSMBuilder trafficLightEFSMBuilder = new EFSMBuilder(EFSM.class);

		// parameter generator 
		// FIXME
		LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();
		
		
		trafficLightEFSM = trafficLightEFSMBuilder
	    		.withTransition(red, red, t_0)
	    		.withTransition(red, green, t_1)
	    		.withTransition(green, green, t_2)
	    		.withTransition(green, yellow, t_3)
	    		.withTransition(green, pending, t_4)
	    		.build(red,tlContext, lrParameterGenerator);
	    
	    return(trafficLightEFSM);
	}
	
	
}
