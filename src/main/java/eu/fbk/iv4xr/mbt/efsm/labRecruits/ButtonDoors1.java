package eu.fbk.iv4xr.mbt.efsm.labRecruits;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;




public class ButtonDoors1 {
	
	/*
	 *  States
	 */
	
	// buttons
	public EFSMState b_0 = new EFSMState("b_0");
	public EFSMState b_1 = new EFSMState("b_1");
	public EFSMState b_2 = new EFSMState("b_2");
	public EFSMState b_3 = new EFSMState("b_3");
	
	// doors
	public EFSMState d_1_m = new EFSMState("d_1_m");
	public EFSMState d_1_p = new EFSMState("d_1_p");
	public EFSMState d_2_m = new EFSMState("d_2_m");
	public EFSMState d_2_p = new EFSMState("d_2_p");
	public EFSMState d_3_m = new EFSMState("d_3_m");
	public EFSMState d_3_p = new EFSMState("d_3_p");
	public EFSMState d_T_m = new EFSMState("d_T_m");
	public EFSMState d_T_p = new EFSMState("d_T_p");
	
	//public EFSMState TR = new EFSMState("TR");
	
	/*
	 * Variables
	 */
	
	// context variables
	public Var<Boolean> d_1 = new Var<Boolean>("d_1", false);
	public Var<Boolean> d_2 = new Var<Boolean>("d_2", false);
	public Var<Boolean> d_T = new Var<Boolean>("d_T", false);
	
	// input variables
	public Var<LRActions> action = new Var<LRActions>("action", null );
	
	public ButtonDoors1(){
	}

	public EFSM getModel() {
		
		

				
		// toggle input parameter
		Var toggleVar = new Var<LRActions>("toggle", LRActions.TOGGLE);
		EFSMParameter inputParToggle = new EFSMParameter(toggleVar);
		
		// toggle input parameter
		Var exploreVar = new Var<LRActions>("explore", LRActions.EXPLORE);
		EFSMParameter inputParExplore = new EFSMParameter(exploreVar);
		
		/*
		 * Context
		 */
		EFSMContext bd1Context = new EFSMContext(d_1,d_2,d_T);
		
		/*
		 * Guards
		 */
		EFSMGuard is_d_1_open = new EFSMGuard(d_1);
		EFSMGuard is_d_2_open = new EFSMGuard(d_2);
		EFSMGuard is_d_T_open = new EFSMGuard(d_T);
		
		/*
		 * Assignments
		 */
		Assign<Boolean> not_d_1 = new Assign(d_1, new BoolNot(d_1));
		Assign<Boolean> not_d_2 = new Assign(d_2, new BoolNot(d_2));
		Assign<Boolean> not_d_T = new Assign(d_T, new BoolNot(d_T));
		
		/*
		 * Operations
		 */
		EFSMOperation trigger_d_1 = new EFSMOperation(not_d_1);
		EFSMOperation trigger_d_1_d_2_d_T = new EFSMOperation(not_d_1, not_d_2, not_d_T);
		
		/*
		 * Transitions
		 */
		
		//// from b_0
		// t_0 : b_0 -> b_1
		EFSMTransition t_0 = new EFSMTransition<>();
		t_0.setInParameter(inputParExplore);
		// t_1 : b_0 -> d_1_m
		EFSMTransition t_1 = new EFSMTransition<>();
		t_1.setInParameter(inputParExplore);
		// t_2 : b_0 -> d_T_m
		EFSMTransition t_2 = new EFSMTransition<>();
		t_2.setInParameter(inputParExplore);
		// t_3 : b_0 -> b_0
		EFSMTransition t_3 = new EFSMTransition<>();
		t_3.setInParameter(inputParToggle);
		
		//// from b_1
		// t_4 : b_1 -> d_T_m
		EFSMTransition t_4 = new EFSMTransition<>();
		t_4.setInParameter(inputParExplore);
		// t_5 : b_1-> b_0
		EFSMTransition t_5 = new EFSMTransition<>();
		t_5.setInParameter(inputParExplore);
		// t_6 : b_1 -> d_1_m
		EFSMTransition t_6 = new EFSMTransition<>();
		t_6.setInParameter(inputParExplore);
		// t_7 : b_1 -> b_1
		EFSMTransition t_7 = new EFSMTransition<>();
		t_7.setOp(trigger_d_1);
		t_7.setInParameter(inputParToggle);
		
		//// from d_1_m
		// t_8 : d_1_m -> b_0
		EFSMTransition t_8 = new EFSMTransition<>();
		t_8.setInParameter(inputParExplore);
		// t_9 : d_1_m -> d_T_m
		EFSMTransition t_9 = new EFSMTransition<>();
		t_9.setInParameter(inputParExplore);
		// t_10 : d_1_m -> d_1_p
		EFSMTransition t_10 = new EFSMTransition<>();
		t_10.setGuard(is_d_1_open);
		t_10.setInParameter(inputParExplore);
		// t_11 : d_1_m -> b_1
		EFSMTransition t_11 = new EFSMTransition<>();
		t_11.setInParameter(inputParExplore);
		
		//// from d_T_m
		// t_12 : d_T_m -> d_1_m
		EFSMTransition t_12 = new EFSMTransition<>();
		t_12.setInParameter(inputParExplore);
		// t_13 : d_T_m -> b_1
		EFSMTransition t_13 = new EFSMTransition<>();
		t_13.setInParameter(inputParExplore);
		// t_14 : d_T_m -> d_T_p
		EFSMTransition t_14 = new EFSMTransition<>();
		t_14.setGuard(is_d_T_open);
		t_14.setInParameter(inputParExplore);
		//t_15 : d_T_m -> d_1_m
		EFSMTransition t_15 = new EFSMTransition<>();
		t_15.setInParameter(inputParExplore);
		
		//// from d_1_p
		// t_16 : d_1_p -> d_1_m
		EFSMTransition t_16 = new EFSMTransition<>();
		t_16.setGuard(is_d_1_open);
		t_16.setInParameter(inputParExplore);
		// t_17 : d_1_p -> b_2
		EFSMTransition t_17 = new EFSMTransition<>();
		t_17.setInParameter(inputParExplore);
		// t_18 : d_1_p -> d_2_m 
		EFSMTransition t_18 = new EFSMTransition<>();
		t_18.setInParameter(inputParExplore);
		
		//// from b_2
		// t_19 : b_2 -> d_1_p
		EFSMTransition t_19 = new EFSMTransition<>();
		t_19.setInParameter(inputParExplore);
		// t_20 : b_2 -> b_2
		EFSMTransition t_20 = new EFSMTransition<>();
		t_20.setOp(trigger_d_1_d_2_d_T);
		t_20.setInParameter(inputParToggle);
		// t_21 : b_2 -> d_2_m
		EFSMTransition t_21 = new EFSMTransition<>();
		t_21.setInParameter(inputParExplore);
		
		//// from d_2_m
		// t_22 : d_2_m -> b_2
		EFSMTransition t_22 = new EFSMTransition<>();
		t_22.setInParameter(inputParExplore);
		// t_23 : d_2_m -> d_1_p 
		EFSMTransition t_23 = new EFSMTransition<>();
		t_23.setInParameter(inputParExplore);
		// t_24 : d_2_m -> d_2_p
		EFSMTransition t_24 = new EFSMTransition<>();
		t_24.setGuard(is_d_2_open);
		t_24.setInParameter(inputParExplore);
		
		//// from d_2_p
		// t_25 : d_2_p -> d_2_m
		EFSMTransition t_25 = new EFSMTransition<>();
		t_25.setGuard(is_d_2_open);
		t_25.setInParameter(inputParExplore);
		// t_26 : d_2_p -> b_3
		EFSMTransition t_26 = new EFSMTransition<>();
		t_26.setInParameter(inputParExplore);
		
		//// from b_3
		// t_27 : b_3 -> d_2_p
		EFSMTransition t_27 = new EFSMTransition<>();
		t_27.setInParameter(inputParExplore);
		// t_28 : b_3 -> b_3
		EFSMTransition t_28 = new EFSMTransition<>();
		t_28.setOp(trigger_d_1);
		t_28.setInParameter(inputParToggle);
		
		// from d_T_p
		// t_29 : d_T_p -> TR
		//EFSMTransition t_29 = new EFSMTransition<>();
		//t_29.setInParameter(inputParExplore);
		// t_30 : t_T_p -> d_T_m
		EFSMTransition t_30 = new EFSMTransition<>();
		t_30.setGuard(is_d_T_open);
		t_30.setInParameter(inputParExplore);
		
		// TO REMOVE
		// ADDED ONLY FOR TESTING
		//EFSMTransition t_100 = new EFSMTransition<>();
		
		
		/*
		 * EFSM declaration
		 */
		EFSM buttonDoors1EFSM;
	
	
	
	    EFSMBuilder buttonDoors1EFSMBuilder = new EFSMBuilder(EFSM.class);
		
	    LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();

	    buttonDoors1EFSM = buttonDoors1EFSMBuilder
	    		.withTransition(b_0, b_1, t_0)

	    		.withTransition(b_0, d_1_m, t_1)
	    		.withTransition(b_0, d_T_m, t_2)
	    		.withTransition(b_0, b_0, t_3)
	    		.withTransition(b_1, d_T_m,t_4)
	    		.withTransition(b_1, b_0, t_5)
	    		.withTransition(b_1, d_1_m, t_6)
	    		.withTransition(b_1, b_1, t_7)
	    		.withTransition(d_1_m, b_0, t_8)
	    		.withTransition(d_1_m, d_T_m, t_9)
	    		.withTransition(d_1_m, d_1_p, t_10)
	    		.withTransition(d_1_m, b_1, t_11)
	    		.withTransition(d_T_m, d_1_m, t_12)
	    		.withTransition(d_T_m, b_1, t_13)
	    		.withTransition(d_T_m, d_T_p, t_14)
	    		.withTransition(d_T_m, b_0, t_15)
	    		.withTransition(d_1_p, d_1_m, t_16)
	    		.withTransition(d_1_p, b_2, t_17)
	    		.withTransition(d_1_p, d_2_m,t_18)
	    		.withTransition(b_2, d_1_p, t_19)
	    		.withTransition(b_2, b_2, t_20)
	    		.withTransition(b_2, d_2_m, t_21)
	    		.withTransition(d_2_m, b_2, t_22)
	    		.withTransition(d_2_m, d_1_p, t_23)
	    		.withTransition(d_2_m, d_2_p, t_24)
	    		.withTransition(d_2_p, d_2_m, t_25)
	    		.withTransition(d_2_p, b_3, t_26)
	    		.withTransition(b_3, d_2_p, t_27)
	    		.withTransition(b_3, b_3, t_28)
	    		//.withTransition(d_T_p, TR, t_29)
	    		.withTransition(d_T_p, d_T_m, t_30)
	    		//REMOVE
	    		//.withTransition(b_0, b_1, t_100)
	    		.build(b_0, bd1Context, lrParameterGenerator);
	    
	    return(buttonDoors1EFSM);
	}
	
}
