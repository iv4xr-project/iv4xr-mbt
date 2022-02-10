package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
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
	EFSMState red = new EFSMState("Red");
	EFSMState yellow = new EFSMState("Yellow");
	EFSMState green = new EFSMState("Green");
	EFSMState pending = new EFSMState("Pending");
	
	// context 
	public Var<Integer> count = new Var<Integer>("count", 0);

	EFSMContext tlContext = new EFSMContext(count);
	
	// operation increment count
	Assign<Integer> incCount = new Assign(count, new IntSum(count, new Const(1)));
	
	
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
		
		
		
		

		  
		/*
		 * EFSM declaration
		 */
		EFSM trafficLightEFSM;
	
	
	    EFSMBuilder trafficLightEFSMBuilder = new EFSMBuilder(EFSM.class);
		
	    trafficLightEFSM = trafficLightEFSMBuilder
	    		.withTransition(red, red, t_0)
	    		.build(red, tlContext, null);
	    
	    return(trafficLightEFSM);
	}
	
}
