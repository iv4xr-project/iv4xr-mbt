package eu.fbk.iv4xr.mbt.efsm.minecraft;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMProvider;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

public class DurabilityTest implements EFSMProvider {

	public EFSMState stoneBlock = new EFSMState("stone");
	public EFSMState blockReference = new EFSMState("place_against");
	public EFSMState endState = new EFSMState("stone^end");

	// variables
	public Var<Integer> durability = new Var<Integer>("inventory__damage", 0);

	// transitions

	public EFSM getModel() {
		EFSMBuilder DurabilityEFSMBuilder = new EFSMBuilder(EFSM.class);
		EFSMContext DurabilityCtx = new EFSMContext(durability);
		LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();

		Exp<Boolean> has_uses_left = new IntLess(durability, new Const<Integer>(31));

		Assign<Integer> consume_uses = new Assign<Integer>(durability, new IntSum(durability, new Const<Integer>(1)));
		EFSMOperation consume_pickaxe_operation = new EFSMOperation(consume_uses);
		// guards
		EFSMGuard pickaxe_has_uses = new EFSMGuard(has_uses_left);
		EFSMGuard pickaxe_is_broken = new EFSMGuard(new BoolNot(has_uses_left));

		// actions
		EFSMParameter place_block = new EFSMParameter(
				new Var<String>("select__item", "stone"),
				new Var<String>("place__face", "top"));

		EFSMParameter break_block = new EFSMParameter(
				new Var<String>("select__item", "golden_pickaxe"),
				new Var<Boolean>("break__expect_result", true));

		// checks
		EFSMParameter durability_inv_check = new EFSMParameter(
				new Var<String>("inventory__item", "golden_pickaxe"),
				new Var<Boolean>("inventory__expect_result", true),
				durability);

		EFSMParameter has_no_pickaxe_check = new EFSMParameter(
				new Var<String>("inventory__item", "golden_pickaxe"),
				new Var<Boolean>("inventory__expect_result", false));

		EFSMTransition t_1 = new EFSMTransition();
		t_1.setInParameter(place_block);
		t_1.setId("t1");
		DurabilityEFSMBuilder.withTransition(stoneBlock, blockReference, t_1);
		
		EFSMTransition t_2 = new EFSMTransition();
		t_2.setOp(consume_pickaxe_operation);
		t_2.setInParameter(break_block);
		t_2.setGuard(pickaxe_has_uses);
		t_2.setOutParameter(durability_inv_check);
		t_2.setId("t2");
		DurabilityEFSMBuilder.withTransition(blockReference, stoneBlock, t_2);

		EFSMTransition t_3 = new EFSMTransition();
		t_3.setGuard(pickaxe_is_broken);
		t_3.setInParameter(break_block);
		t_3.setOutParameter(has_no_pickaxe_check);
		t_3.setId("t3");
		DurabilityEFSMBuilder.withTransition(blockReference, endState, t_3);

		return DurabilityEFSMBuilder.build(stoneBlock, DurabilityCtx, lrParameterGenerator);
	}

}
