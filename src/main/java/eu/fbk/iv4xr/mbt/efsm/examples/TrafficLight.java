package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

/**
 * Traffic light EFSM model from 
 * https://ptolemy.berkeley.edu/projects/chess/eecs149/lectures/ExtendedAndTimedAutomata.pdf
 * slide 3 
 * @author prandi
 *
 */

public class TrafficLight {

	// four states
	public EFSMState red = new EFSMState("Red");
	public EFSMState yellow = new EFSMState("Yellow");
	public EFSMState green = new EFSMState("Green");
	public EFSMState pending = new EFSMState("Pending");
	
	// context 
	public Var<Integer> count = new Var<Integer>("count", 0);

	EFSMContext tlContext = new EFSMContext(count);
	
	// operation increment count
	Assign<Integer> incCount = new Assign(count, new IntSum(count, new Const(1)));
	Assign<Integer> resetCount = new Assign(count, new Const<Integer>(0));
	
	// check counter values
	// constant 60
	Const<Integer> sixty = new Const<Integer>(60);
	// count greater that 60
	IntGreat countGreatThanSixty =  new IntGreat(count, sixty);
	// count equal 60
	IntEq countEqualSixty =  new IntEq(count, sixty);
	// count greater or equal 60
	BoolOr countGreatEqThanSixty = new BoolOr(countGreatThanSixty, countEqualSixty);
	// count less than 60
	BoolNot countLessThatSixty = new BoolNot(countGreatEqThanSixty);
	
	
	// define transition guards
	EFSMGuard guardCountGreatEqThanSixty = new EFSMGuard(countGreatEqThanSixty);
	EFSMGuard guardCountLessThanSixty = new EFSMGuard(countLessThatSixty);
	
	// define transition operations
	EFSMOperation operationIncCount = new EFSMOperation(incCount);
	EFSMOperation operationResetCount = new EFSMOperation(resetCount);
	
	// inputs and outputs
	// input are local to transitions 
	// - boolean variable pedestrian that check if pedestrian button has been pushed
	
	// output are local to transition
	// - enumerator for light color
	

	public TrafficLight() {
		
	}
	
	public EFSM getModel() {
		
		// t_0 : red -> red # increment count
		EFSMTransition t_0 = new EFSMTransition<>();
		t_0.setGuard(guardCountLessThanSixty);
		t_0.setOp(operationIncCount);
		
		// t_1 : red -> green 
		EFSMTransition t_1 = new EFSMTransition<>();
		t_1.setGuard(guardCountGreatEqThanSixty);  
		t_1.setOp(operationResetCount);
		
		/*
		 * EFSM declaration
		 */
		EFSM trafficLightEFSM;
	
	
	    EFSMBuilder trafficLightEFSMBuilder = new EFSMBuilder(EFSM.class);
		
	    
	    // parameter generator
	    // TODO Fix for traffic light
	    LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();
	    
	    trafficLightEFSM = trafficLightEFSMBuilder
	    		.withTransition(red, red, t_0)
	    		.withTransition(red, green, t_1)
	    		.build(red, tlContext, lrParameterGenerator);
	    
	    return(trafficLightEFSM);
	}
	
}
