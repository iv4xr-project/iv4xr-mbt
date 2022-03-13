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
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
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
	
	public Const<Boolean> pedestrian = new Const<Boolean>(true);
	
<<<<<<< Updated upstream
	EFSMContext tlContext = new EFSMContext(count);
=======
	//public Var<Integer> pedestrian = new Var<Integer>("pedestrian", 60);
	
	
	EFSMContext tlContext = new EFSMContext(count);
	EFSMContext tlContext2 = new EFSMContext(pedestrian);
	
>>>>>>> Stashed changes
	
	// operation increment count
	Assign<Integer> incCount = new Assign(count, new IntSum(count, new Const(1)));
	Assign<Integer> resetCount = new Assign(count, new Const<Integer>(0));
	
<<<<<<< Updated upstream
=======
	
>>>>>>> Stashed changes
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
	
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
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
	
<<<<<<< Updated upstream
	 
	// Pedestrian
	
	//Const<Boolean> pedestrian = new Const<Boolean>(true);
	
	
	// count greater that 60
	//IntGreat p_countGreatThanSixty =  new IntGreat(count, pendingSixty);
	// count equal 60
	//IntEq p_countEqualSixty =  new IntEq(count, pendingSixty);
	// count greater or equal 60
	//BoolOr p_countGreatEqThanSixty = new BoolOr(p_countGreatThanSixty, p_countEqualSixty);
	// count less than 60
	//BoolNot p_countLessThanSixty = new BoolNot(p_countGreatEqThanSixty);
	BoolAnd checkPoint = new BoolAnd(countLessThanSixty,pedestrian);
	
	
	// Green is less than 60 and pedestrian is less than 60 //
	//BoolOr pending_status_less = new BoolOr(p_countLessThanSixty,countLessThanSixty);
=======
	
	// Green is less than 60 and pedestrian is less than 60 //
	Exp<Boolean> pending_input = new Const<Boolean>(true);
	BoolAnd pending_status_less = new BoolAnd(pending_input,countLessThanSixty);
	BoolNot pending_status_greater = new BoolNot(pending_status_less);
>>>>>>> Stashed changes
	
	// define transition guards
	EFSMGuard guardCountGreatEqThanSixty = new EFSMGuard(countGreatEqThanSixty);
	EFSMGuard guardCountLessThanSixty = new EFSMGuard(countLessThanSixty);
	
<<<<<<< Updated upstream
	
	EFSMGuard pending_check = new EFSMGuard(checkPoint);
=======
	//EFSMGuard guardPedGreatEqThanSixty = new EFSMGuard(countGreatEqThanSixty);
	EFSMGuard guardPedLessThanSixty = new EFSMGuard(pending_status_less);
	EFSMGuard guardPedGreaterThanSixty = new EFSMGuard(pending_status_greater);
	
	
	//EFSMGuard pending_check = new EFSMGuard(checkPoint);
>>>>>>> Stashed changes
	
	EFSMGuard guardCountGreatEqThanFive = new EFSMGuard(countGreatEqThanFive);
	EFSMGuard guardCountLessThanFive = new EFSMGuard(countLessThanFive);
	
	// define transition operations
<<<<<<< Updated upstream
	EFSMOperation operationIncCount_red = new EFSMOperation(incCount);
	EFSMOperation operationIncCount_green = new EFSMOperation(incCount);
	EFSMOperation operationIncCount_yellow = new EFSMOperation(incCount);
	
	EFSMOperation operationResetCount_red = new EFSMOperation(resetCount);
	EFSMOperation operationResetCount_green = new EFSMOperation(resetCount);
	EFSMOperation operationResetCount_yellow = new EFSMOperation(resetCount);
=======
	EFSMOperation operationIncCount = new EFSMOperation(incCount);
	//EFSMOperation operationIncCount_green = new EFSMOperation(incCount);
	//EFSMOperation operationIncCount_yellow = new EFSMOperation(incCount);
	//EFSMOperation operationIncCount_pedestrian = new EFSMOperation(incPedestrian);
	
	EFSMOperation operationResetCount = new EFSMOperation(resetCount);
	//EFSMOperation operationResetCount_green = new EFSMOperation(resetCount);
	//EFSMOperation operationResetCount_yellow = new EFSMOperation(resetCount);
	//EFSMOperation operationResetCount_pedestrian = new EFSMOperation(resetPedestrian);
>>>>>>> Stashed changes
	
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
<<<<<<< Updated upstream
		t_0.setOp(operationIncCount_red);
=======
		t_0.setOp(operationIncCount);
>>>>>>> Stashed changes
		
		// t_1 : red -> green 
		EFSMTransition t_1 = new EFSMTransition<>();
		t_1.setGuard(guardCountGreatEqThanSixty);  
<<<<<<< Updated upstream
		t_1.setOp(operationResetCount_red);
=======
		t_1.setOp(operationResetCount);
>>>>>>> Stashed changes
		

		
		
		// t_2 : yellow -> yellow # increment count
		EFSMTransition t_2 = new EFSMTransition<>();
		t_2.setGuard(guardCountLessThanFive);
<<<<<<< Updated upstream
		t_2.setOp(operationIncCount_yellow);
=======
		t_2.setOp(operationIncCount);
>>>>>>> Stashed changes
		
		// t_3 : yellow -> red 
		EFSMTransition t_3 = new EFSMTransition<>();
		t_3.setGuard(guardCountGreatEqThanFive);  
<<<<<<< Updated upstream
		t_3.setOp(operationResetCount_yellow);
=======
		t_3.setOp(operationResetCount);
>>>>>>> Stashed changes
		
		
		// t4: green -> green
		
		EFSMTransition t_4 = new EFSMTransition<>();

<<<<<<< Updated upstream
		//t_4.setGuard(guardCountLessThanSixty);  
		t_4.setOp(operationIncCount_green);
		
		// t5: green -> yellow with pending
		
	EFSMTransition  t_5 = new EFSMTransition<>();
		t_5.setGuard(pending_check); 
		t_5.setOp(operationResetCount_green);
		
		
		
		/*
=======
		t_4.setGuard(guardCountLessThanSixty);  
		t_4.setOp(operationIncCount);
		
		// t5: green -> yellow with pedestrian
		
		EFSMTransition  t_5 = new EFSMTransition<>();
		t_5.setGuard(guardPedGreaterThanSixty);
		t_5.setOp(operationResetCount);
		
		
		// t6: green -> pending 
		
		EFSMTransition  t_6 = new EFSMTransition<>();
		t_6.setGuard(guardPedLessThanSixty);
		t_6.setOp(operationResetCount);
		
		
		// t7: pending -> pending 
		
		EFSMTransition  t_7 = new EFSMTransition<>();
		t_7.setGuard(guardCountGreatEqThanSixty);  
		t_7.setOp(operationResetCount);
		
				
		// t8: pending -> yellow 
				
		EFSMTransition  t_8 = new EFSMTransition<>();
		//t_7.setGuard(pending_check); 
		t_8.setOp(operationResetCount);
				
				
		
		/*
		 * 
>>>>>>> Stashed changes
		 * EFSM declaration
		 */
		EFSM trafficLightEFSM;
	
	
	    EFSMBuilder trafficLightEFSMBuilder = new EFSMBuilder(EFSM.class);
		
	    
	    // parameter generator

	    LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();
	    
	    trafficLightEFSM = trafficLightEFSMBuilder
	    		.withTransition(red, red, t_0)
	    		.withTransition(red, green, t_1)
	    		.withTransition(yellow, yellow, t_2)
	    		.withTransition(yellow, red, t_3)
	    		.withTransition(green, green, t_4)
	    		.withTransition(green, yellow, t_5)
<<<<<<< Updated upstream
=======
	    		.withTransition(green, pending, t_6)
	    		.withTransition(pending, pending, t_7)
	    		.withTransition(pending, yellow, t_8)
>>>>>>> Stashed changes
	    		.build(red,tlContext, lrParameterGenerator);
	    
	    return(trafficLightEFSM);
	}
	
}
