package eu.fbk.iv4xr.mbt.efsm.spaceEngineering;

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
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

/**
 * Example model for using grind and weld on a block. 
 * The agent is in front on the bloc at the beginning so no need to move
 * @author prand
 *
 */
public class SingleBlockWeldingAndGrinding {
	
	
	public enum seActions{ weld_10, weld_20, grind_10, grind_20, destroy_block, create_block };

	// states are block exist and block does not exist
	public EFSMState block_exists = new EFSMState("block_exists");
	public EFSMState block_not_exists = new EFSMState("block_not_exists");
	
	// variable represent block points, initial value is 100
	public Var<Integer> block_energy = new Var<Integer>("block_energy", 100);
	
	public EFSM getModel() {
		
		// context is given only by block energy variable
		EFSMContext ctx = new  EFSMContext(block_energy);
		
		
		// guards check that energy is greater than 0 or less than 100
		Exp<Boolean> energy_gt_0 = new IntGreat(block_energy, new Const(0));
		Exp<Boolean> energy_less_0 = new BoolNot( 
				new BoolOr ( new IntGreat(block_energy, new Const(0) ),
					     new IntEq(block_energy, new Const(0)) ) ) ;
		Exp<Boolean> energy_less_100 = new BoolNot( 
				new BoolOr ( new IntGreat(block_energy, new Const(100) ),
						     new IntEq(block_energy, new Const(100)) ) ) ;
		
		EFSMGuard check_energy_gt_0 = new EFSMGuard(energy_gt_0);
		EFSMGuard check_energy_less_0 = new EFSMGuard(energy_less_0);
		EFSMGuard check_energy_less_100 = new EFSMGuard(energy_less_100);
		
		
		
		// actions increments (use welder) or decrements energy (use grinder)
		Assign<Integer> inc_10 = new Assign(block_energy, new IntSum(block_energy, new Const<Integer>(10)));
		Assign<Integer> inc_20 = new Assign(block_energy, new IntSum(block_energy, new Const<Integer>(20)));
		
		Assign<Integer> dec_10 = new Assign(block_energy, new IntSubt(block_energy, new Const<Integer>(10)));
		Assign<Integer> dec_20 = new Assign(block_energy, new IntSubt(block_energy, new Const<Integer>(20)));
		
		Assign<Integer> set_100 = new Assign<>(block_energy, new Const<Integer>(100)); 
		
		EFSMOperation do_inc_10 = new EFSMOperation(inc_10);
		EFSMOperation do_inc_20 = new EFSMOperation(inc_20);
		EFSMOperation do_dec_10 = new EFSMOperation(dec_10);
		EFSMOperation do_dec_20 = new EFSMOperation(dec_20);
		EFSMOperation reset_energy = new EFSMOperation(set_100);
		
		// output are trivial and only signal the action performed
		Var<Enum> weld_10 = new Var<Enum>("action", seActions.weld_10);
		EFSMParameter o_weld_10 = new EFSMParameter(weld_10);
		Var<Enum> weld_20 = new Var<Enum>("action", seActions.weld_20);
		EFSMParameter o_weld_20 = new EFSMParameter(weld_20);
		Var<Enum> grind_10 = new Var<Enum>("action", seActions.grind_10);
		EFSMParameter o_grind_10 = new EFSMParameter(grind_10);
		Var<Enum> grind_20 = new Var<Enum>("action", seActions.grind_20);
		EFSMParameter o_grind_20 = new EFSMParameter(grind_20);
		Var<Enum> destroy = new Var<Enum>("action", seActions.destroy_block);
		EFSMParameter o_destroy = new EFSMParameter(destroy);
		Var<Enum> create = new Var<Enum>("action", seActions.create_block);
		EFSMParameter o_create = new EFSMParameter(create);
		
		
		// transitions apply welder (increment the energy) or the grinder (decrement the energy)
		// if applying the grinder the energy goes below zero there's a transition to block_not_exist
		// In this case a new block is created
		
		// t0 : block_exists - dec 10 -> block_exists
		EFSMTransition t0= new EFSMTransition();
		t0.setGuard(check_energy_gt_0);
		t0.setOp(do_dec_10);
		t0.setOutParameter(o_grind_10);
		
		// t1 : block_exists - dec 20 -> block_exists
		EFSMTransition t1= new EFSMTransition();
		t1.setGuard(check_energy_gt_0);
		t1.setOp(do_dec_20);
		t1.setOutParameter(o_grind_20);
		
		// t2 : block_exists - inc 10 -> block_exists
		EFSMTransition t2= new EFSMTransition();
		t2.setGuard(check_energy_less_100);
		t2.setOp(do_inc_10);
		t2.setOutParameter(o_weld_10);
		
		
		// t3 : block_exists - inc 20 -> block_exists
		EFSMTransition t3= new EFSMTransition();
		t3.setGuard(check_energy_less_100);
		t3.setOp(do_inc_20);
		t3.setOutParameter(o_weld_20);	
		
		
		// t4 : block_exists -> block_not_exists
		EFSMTransition t4 = new EFSMTransition();
		t4.setGuard(check_energy_less_0);
		t4.setOutParameter(o_destroy);
		
		
		// t5 : block_not_exists -> block_exists
		EFSMTransition t5 = new EFSMTransition();
		t5.setOp(reset_energy);
		t5.setOutParameter(o_create);
		
		EFSM singleBlockWeldingAndGrindingEFSM;
		
		EFSMBuilder singleBlockWeldingAndGrindingEFSMBuilder = new EFSMBuilder(EFSM.class);

		
	    LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();

	    

	    singleBlockWeldingAndGrindingEFSM = 
	    		singleBlockWeldingAndGrindingEFSMBuilder
	    		.withTransition(block_exists, block_exists, t0)
	    		.withTransition(block_exists, block_exists, t1)
	    		.withTransition(block_exists, block_exists, t2)
	    		.withTransition(block_exists, block_exists, t3)
	    		.withTransition(block_exists, block_not_exists, t4)
	    		.withTransition(block_not_exists, block_exists, t5)
	    		.build(block_exists, ctx, lrParameterGenerator);
	    
	    return singleBlockWeldingAndGrindingEFSM;
		
		
		
		
		
				
		
		
		
		
		
		 
		
		
		
	}
	
}
