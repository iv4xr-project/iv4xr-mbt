package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
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
	BoolNot countLessThanSixty = new BoolNot(countGreatEqThanSixty);
	
	// Constant 5
	
	Const<Integer> five = new Const<Integer>(5);
	// count greater that 5
	IntGreat countGreatThanFive =  new IntGreat(count, five);
	// count equal 5
	IntEq countEqualFive =  new IntEq(count, five);
	// count greater or equal 5
	BoolOr countGreatEqThanFive = new BoolOr(countGreatThanFive, countEqualFive);
	// count less than 5
	BoolNot countLessThanFive = new BoolNot(countGreatEqThanFive);
	
	 
	// Pedestrian
	
	Const<Integer> pendingSixty = new Const<Integer>(60);
	// count greater that 60
	IntGreat p_countGreatThanSixty =  new IntGreat(count, pendingSixty);
	// count equal 60
	IntEq p_countEqualSixty =  new IntEq(count, pendingSixty);
	// count greater or equal 60
	BoolOr p_countGreatEqThanSixty = new BoolOr(p_countGreatThanSixty, p_countEqualSixty);
	// count less than 60
	BoolNot p_countLessThanSixty = new BoolNot(p_countGreatEqThanSixty);
	
	BoolAnd checkPoint = new BoolAnd(countLessThanSixty,p_countLessThanSixty);
	
	
	// Green is less than 60 and pedestrian is less than 60 //
	BoolOr pending_status_less = new BoolOr(p_countLessThanSixty,countLessThanSixty);
	
	// define transition guards
	EFSMGuard guardCountGreatEqThanSixty = new EFSMGuard(countGreatEqThanSixty);
	EFSMGuard guardCountLessThanSixty = new EFSMGuard(countLessThanSixty);
	
	EFSMGuard guardCountGreatEqThanFive = new EFSMGuard(countGreatEqThanFive);
	EFSMGuard guardCountLessThanFive = new EFSMGuard(countLessThanFive);
	
	// define transition operations
	EFSMOperation operationIncCount_red = new EFSMOperation(incCount);
	EFSMOperation operationIncCount_green = new EFSMOperation(incCount);
	EFSMOperation operationIncCount_yellow = new EFSMOperation(incCount);
	
	EFSMOperation operationResetCount_red = new EFSMOperation(resetCount);
	EFSMOperation operationResetCount_green = new EFSMOperation(resetCount);
	EFSMOperation operationResetCount_yellow = new EFSMOperation(resetCount);
	
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
		t_0.setOp(operationIncCount_red);
		
		// t_1 : red -> green 
		EFSMTransition t_1 = new EFSMTransition<>();
		t_1.setGuard(guardCountGreatEqThanSixty);  
		t_1.setOp(operationResetCount_red);
		

		
		
		// t_2 : yellow -> yellow # increment count
		EFSMTransition t_2 = new EFSMTransition<>();
		t_2.setGuard(guardCountLessThanFive);
		t_2.setOp(operationIncCount_yellow);
		
		// t_3 : yellow -> red 
		EFSMTransition t_3 = new EFSMTransition<>();
		t_3.setGuard(guardCountGreatEqThanFive);  
		t_3.setOp(operationResetCount_yellow);
		
		
		// t4: green -> green
		
		EFSMTransition t_4 = new EFSMTransition<>();

		//t_4.setGuard(guardCountLessThanSixty);  
		t_4.setOp(operationIncCount_green);
		
		// t5: green -> yellow
		
	/*	EFSMTransition<EFSMState, EFSMParameter, EFSMParameter, EFSMContext, EFSMOperation, EFSMGuard> t_5 = new EFSMTransition<>();
		t_5.setGuard(guardCountGreatEqThanSixty);  
		t_5.setOp(operationResetCount_green);*/
		
		
		
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
	    		.withTransition(yellow, yellow, t_2)
	    		.withTransition(yellow, red, t_3)
	    		.withTransition(green, green, t_4)
	    		.build(red, tlContext, lrParameterGenerator);
	    
	    return(trafficLightEFSM);
	}
	
}
