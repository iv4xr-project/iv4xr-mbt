package eu.fbk.iv4xr.mbt.efsm.examples;

import java.util.HashSet;
import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

/**
 * Example from paper 
 * Estimating the feasibility of transition paths in extended finite state machines
 * pag 6 or
 * 
 * Search-based software engineering A search-based approach for testing from extended finite state machine (EFSM) models
 * Figure 2.11 pag 43
 * @author prandi
 *
 */

public class InresProtocol {

	
	
		Set<EFSMState> finite_set = new HashSet<EFSMState> ();
		Set<EFSMState> inputs = new HashSet<EFSMState> ();
		Set<EFSMState> outputs = new HashSet<EFSMState> ();
		Set<EFSMTransition> transitions = new HashSet<EFSMTransition> ();
		Set<EFSMState> internal_var = new HashSet<EFSMState> ();
		
		
		public EFSMState disconnect = new EFSMState("Disconnect");
		public EFSMState wait = new EFSMState("Wait");
		public EFSMState connect = new EFSMState("Connect");
		public EFSMState sending = new EFSMState("Sending");
		
		// Definition of counter to check less or greater than 4
		public Var<Integer> count = new Var<Integer>("count", 0);

		EFSMContext tlContext = new EFSMContext(count);
		
		
		Assign<Integer> incCount = new Assign(count, new IntSum(count, new Const(1)));
		Assign<Integer> resetCount = new Assign(count, new Const<Integer>(0));
		
		
		
		public EFSM getModel() {
			// Finite Set of Logical States
			finite_set.add(connect);
			finite_set.add(disconnect);
			finite_set.add(sending);
			finite_set.add(wait);
			
			
			EFSM inresProtocol;
			
			
		    EFSMBuilder inresProtocolEFSMBuilder = new EFSMBuilder(EFSM.class);
			
		    
		    // parameter generator
		    // TODO Fix for traffic light
		    LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();
			
			return null;
			
			
		}
		
		
		
		
}