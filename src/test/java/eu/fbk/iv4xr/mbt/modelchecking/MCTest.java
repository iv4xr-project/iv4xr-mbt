package eu.fbk.iv4xr.mbt.modelchecking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Objects;

import static org.junit.Assert.assertTrue;

//import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import eu.iv4xr.framework.extensions.ltl.IExplorableState;
import eu.iv4xr.framework.extensions.ltl.ITargetModel;
import eu.iv4xr.framework.extensions.ltl.ITransition;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM.StateType;
import eu.fbk.iv4xr.mbt.efsm.modelcheckingInterface.InterfaceToIv4xrModelCheker;
import eu.iv4xr.framework.extensions.ltl.BasicModelChecker;

public class MCTest {
	
	
	@Test
	public void test1() {
		EFSM anLRmodel = (new ButtonDoors1()).getModel() ;
		
		
		InterfaceToIv4xrModelCheker adapter = new InterfaceToIv4xrModelCheker(anLRmodel) ;
		
		adapter.reset();
		
		int k = 0 ;
		Random rnd = new Random() ;
		while (k<3) {
			System.out.println(">>" + k + " State: " + adapter.getCurrentState()) ;
			var actions = adapter.availableTransitions() ;
			System.out.print("   enabled: ") ;
			for(var a : actions) {
				System.out.print(", " + a) ;
			}
			k++ ;
			System.out.println("") ;
			var chosen = actions.get(rnd.nextInt(actions.size())) ;
			System.out.println("   DO " + chosen) ;
			adapter.execute(chosen);
		}
		System.out.println(">>" + k + " State: " + adapter.getCurrentState()) ;
		
		assertTrue(adapter.getCurrentState().equals(adapter.getCurrentState().clone())) ;
		
		//assertTrue(adapter.getCurrentState().equals(adapter.history.get(adapter.history.size()-1))) ;
	}
	
	@Test
	public void test2() {
		EFSM anLRmodel = (new ButtonDoors1()).getModel() ;
		
		Predicate<IExplorableState> goal = state -> {
			InterfaceToIv4xrModelCheker.EFSMStateWrapper state_ = (InterfaceToIv4xrModelCheker.EFSMStateWrapper) state ;
			return state_.conf.getState().getId().equals("d3p") ;
		} ;
		
		BasicModelChecker mc = new BasicModelChecker(new InterfaceToIv4xrModelCheker(anLRmodel)) ;
		
		var path = mc.findShortest(goal, 21); 
		
		
		System.out.println(">>> " + mc.stats) ;
		System.out.println(">>> Solution: " + path) ;
		
	}
	
    @Test 
	public void test3() {
		//MBTProperties.LR_seed = 178971287 ;
		//MBTProperties.LR_n_buttons = 70 ;
		//MBTProperties.LR_n_doors = 60 ;
		//MBTProperties.LR_n_rooms = 50 ;
    	// Using a smaller level to speed up the test :)
		MBTProperties.LR_n_buttons = 40 ;
		MBTProperties.LR_n_doors = 30 ;
		MBTProperties.LR_n_rooms = 18 ;
		//MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_goalFlags = 1 ;
		// generate an EFSM with the above params:
		LabRecruitsRandomEFSM gen = new LabRecruitsRandomEFSM();
		EFSM efsm = gen.getEFMS() ;
		assertTrue(efsm != null) ; 
		
		// ok.. need to het a reference to that single goal-flag to be used as the
		// goal for MC:
		EFSMState goalstate = null;
		
		for(var state : efsm.getStates()) {
			EFSMState state_ = (EFSMState) state ;
			if(LabRecruitsRandomEFSM.getStateType(state_) == StateType.GoalFlag) {
				goalstate = state_ ;
			}
		} ;
		assertTrue(goalstate != null) ;
		String goalid = goalstate.getId() ;
		System.out.println(">>> goalstate: " + goalid) ;
		
		// create the MC and goal:
		BasicModelChecker mc = new BasicModelChecker(new InterfaceToIv4xrModelCheker(efsm)) ;
		
		Predicate<IExplorableState> goal = state -> {
			InterfaceToIv4xrModelCheker.EFSMStateWrapper state_ = (InterfaceToIv4xrModelCheker.EFSMStateWrapper) state ;
			return state_.conf.getState().getId().equals(goalid) ;
		} ;
		
		// invoke the MC:
		var starttime = System.currentTimeMillis() ;
		var path = mc.find(goal, 40); 
		// use this instead if you want to get a shortest solving path:
		// var path = mc.findShortest(goal, 40); 
		float duration = ((float) (System.currentTimeMillis() - starttime)) / 1000f ; 
		
		// print stats:
		System.out.println(">>> #nodes in efsm: " + efsm.getStates().size()) ;
		System.out.println(">>> #transitions in efsm: " + efsm.getTransitons().size()) ;
		System.out.println(">>> #concrete states and transitions:\n" + mc.stats) ;
		System.out.println(">>> runtime(s): " + duration) ;
		if(path!=null) {
			System.out.println(">>> Solution length: " + path.path.size()) ;
		}
		System.out.println(">>> Solution: " + path) ;
		
	}
	
	@Test 
	public void test4() {
		
		
		//MBTProperties.LR_seed = 178971287 ;
		MBTProperties.LR_n_buttons = 40 ;
		MBTProperties.LR_n_doors = 30 ;
		MBTProperties.LR_n_rooms = 18 ;
		//MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_goalFlags = 1 ;
		// generate an EFSM with the above params:
		LabRecruitsRandomEFSM gen = new LabRecruitsRandomEFSM();
		EFSM efsm = gen.getEFMS() ;
		
		assertTrue(efsm != null) ; 
		
		
		BasicModelChecker mc = new BasicModelChecker(new InterfaceToIv4xrModelCheker(efsm)) ;
		
		System.out.println(">>> #nodes in efsm: " + efsm.getStates().size()) ;
		System.out.println(">>> #transitions in efsm: " + efsm.getTransitons().size()) ;
		
		// define which states to cover; well... all:
		var justAllStatesIds = (List<String>) efsm.getStates().stream()
				.map(S -> ((EFSMState) S).getId())
				.collect(Collectors.toList()) ;
		
		Function<IExplorableState,String> coverageFunction =
				S -> ((InterfaceToIv4xrModelCheker.EFSMStateWrapper) S).conf.getState().getId() ;

		// invoke the MC to generate a covering test suite:
		var starttime = System.currentTimeMillis() ;
		var suite = mc.testSuite(justAllStatesIds, coverageFunction, 40, false) ;
		float duration = ((float) (System.currentTimeMillis() - starttime)) / 1000f ; 
		
		// print stats:
		System.out.println(">>> #nodes in efsm: " + efsm.getStates().size()) ;
		System.out.println(">>> #transitions in efsm: " + efsm.getTransitons().size()) ;
		System.out.println(">>> #tests: " + suite.tests.size()) ;
		System.out.println(">>> coverage: " + suite.coverage()) ;
		System.out.println(">>> runtime(s): " + duration) ;
		
	}

}
