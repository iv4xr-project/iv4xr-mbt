/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import java.util.HashSet;

import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;

/**
 * @author Davide Prandi
 *
 * Oct 9, 2020
 */
public class ButtonDoors1 {

	public ButtonDoors1(){
	}

	public EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
	Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> getRoomReachabilityModel() {
		/**
		 * Doors
		 * 	Define set of buttons that act on the door
		 * 	Define the door itself
		 */		
		LabRecruitsDoor door1 = new LabRecruitsDoor("door1", new HashSet<String>());
		LabRecruitsDoor door2 = new LabRecruitsDoor("door2", new HashSet<String>());
		LabRecruitsDoor doorT = new LabRecruitsDoor("doorT", new HashSet<String>());
		
			
		/**
		 * States
		 */
		
		// Buttons
		LabRecruitsState b_0 = new LabRecruitsState("b_0");
		LabRecruitsState b_1 = new LabRecruitsState("b_1");
		LabRecruitsState b_2 = new LabRecruitsState("b_2");
		LabRecruitsState b_3 = new LabRecruitsState("b_3");
		
		// Doors
		LabRecruitsState d_1_m = new LabRecruitsState("d_1_m","door1");
		LabRecruitsState d_1_p = new LabRecruitsState("d_1_p","door1");
		LabRecruitsState d_2_m = new LabRecruitsState("d_2_m","door2");
		LabRecruitsState d_2_p = new LabRecruitsState("d_2_p","door2");
		LabRecruitsState d_T_m = new LabRecruitsState("d_T_m","doorT");
		LabRecruitsState d_T_p = new LabRecruitsState("d_T_p","doorT");
		
		// Treasure room
		LabRecruitsState TR = new LabRecruitsState("TR");
		
		
		// Transitions 
		// Names follow NF-EFSM model in MBT paper	
		
		// from b_0
		LabRecruitsFreeTravelTransition t_0 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_1 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_2 = new LabRecruitsFreeTravelTransition();
		LabRecruitsToggleTransition t_3 = new LabRecruitsToggleTransition();
		
		// from b_1
		LabRecruitsFreeTravelTransition t_4 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_5 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_6 = new LabRecruitsFreeTravelTransition();
		LabRecruitsToggleTransition t_7 = new LabRecruitsToggleTransition();
		
		// from d_1_m
		LabRecruitsFreeTravelTransition t_8 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_9 = new LabRecruitsFreeTravelTransition();	
		LabRecruitsDoorTravelTransition t_10 = new LabRecruitsDoorTravelTransition();
		LabRecruitsFreeTravelTransition t_11 = new LabRecruitsFreeTravelTransition();	
		
		// from d_T_m
		LabRecruitsFreeTravelTransition t_12 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_13 = new LabRecruitsFreeTravelTransition();	
		LabRecruitsDoorTravelTransition t_14 = new LabRecruitsDoorTravelTransition();
		LabRecruitsFreeTravelTransition t_15 = new LabRecruitsFreeTravelTransition();	
		
		// from d_1_p
		LabRecruitsDoorTravelTransition t_16 = new LabRecruitsDoorTravelTransition();
		LabRecruitsFreeTravelTransition t_17 = new LabRecruitsFreeTravelTransition();	
		LabRecruitsFreeTravelTransition t_18 = new LabRecruitsFreeTravelTransition();
		
		// from b_2
		LabRecruitsFreeTravelTransition t_19 = new LabRecruitsFreeTravelTransition();
		LabRecruitsToggleTransition t_20 = new LabRecruitsToggleTransition();	
		LabRecruitsFreeTravelTransition t_21 = new LabRecruitsFreeTravelTransition();
		
		// from d_2_m
		LabRecruitsFreeTravelTransition t_22 = new LabRecruitsFreeTravelTransition();
		LabRecruitsFreeTravelTransition t_23 = new LabRecruitsFreeTravelTransition();	
		LabRecruitsDoorTravelTransition t_24 = new LabRecruitsDoorTravelTransition();
		
		// from d_2_+
		LabRecruitsDoorTravelTransition t_25 = new LabRecruitsDoorTravelTransition();	
		LabRecruitsFreeTravelTransition t_26 = new LabRecruitsFreeTravelTransition();
		
		// from b_3
		LabRecruitsFreeTravelTransition t_27 = new LabRecruitsFreeTravelTransition();
		LabRecruitsToggleTransition t_28 = new LabRecruitsToggleTransition();
		
		// from d_T_p
		LabRecruitsDoorTravelTransition t_29 = new LabRecruitsDoorTravelTransition();
		LabRecruitsFreeTravelTransition t_30 = new LabRecruitsFreeTravelTransition();
		
		// EFSM and associated builder
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
			Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> buttonDoors1EFSM;
		
		
		
		EFSMBuilder<LabRecruitsState, 
		LabRecruitsParameter, 
					LabRecruitsContext, 
					Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>, 
					EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
					Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>>> 
				labRecruitsBuilder = new EFSMBuilder(EFSM.class);
		
		// associate doors to buttons
		door1.addButton("b_1");
		door1.addButton("b_2");
		door1.addButton("b_3");
		door2.addButton("b_2");
		doorT.addButton("b_3");
		
		// the context is made by the doors status
		LabRecruitsContext buttonDoors1Context = new LabRecruitsContext(door1, door2, doorT);
		
		// the input paramter generator
		LabRecruitsParameterGenerator parameterGenerator = new LabRecruitsParameterGenerator();
		/*
		 * for (int i = 0; i < 10; i++) {
		 * System.out.println(parameterGenerator.getRandom().getValue().toString()); }
		 */
		
		
		buttonDoors1EFSM = labRecruitsBuilder
				.withTransition(b_0, b_1, t_0)
				.withTransition(b_0, d_1_m, t_1)
				.withTransition(b_0, d_T_m, t_2)
				.withTransition(b_0, b_0, t_3)
				
				.withTransition(b_1, d_T_m, t_4)
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
				.withTransition(d_1_p, d_2_m, t_18)
				
				.withTransition(b_2, d_1_p, t_19)
				.withTransition(b_2, b_2, t_20)
				.withTransition(b_2, d_2_m, t_21)
				
				.withTransition(d_2_m, b_2, t_22)
				.withTransition(d_2_m, d_1_m, t_23)
				.withTransition(d_2_m, d_2_p, t_24)
				
				.withTransition(d_2_p, d_2_m, t_25)
				.withTransition(d_2_p, b_3, t_26)
				
				.withTransition(b_3, d_2_p, t_27)
				.withTransition(b_3, b_3, t_28)
				
				.withTransition(d_T_p, d_T_m, t_29)
				.withTransition(d_T_p, TR, t_30)
						
				.build(b_0, buttonDoors1Context, parameterGenerator);
		return buttonDoors1EFSM;

	}
	
}
