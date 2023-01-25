package eu.fbk.iv4xr.mbt.efsm.usageControl;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMProvider;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.examples.TrafficLight.outSignal;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

public class PhoneCall1 implements EFSMProvider {
	
	public enum actions { Start, Call, Recharge, StopManual, Charge, StopAuto };
	
	// const values
	private static final Integer initialCreditVal = 60;
	private static final Integer creditChargeVal = 10;
	private static final Integer rechargeSizeVal = 50; 
	
	// states
	public EFSMState Calling = new EFSMState("Calling");
	public EFSMState NoCall = new EFSMState("NoCall");
	
	// constants
	// Const<Integer> initialCredit = new Const<Integer>(initialCreditVal);
	Const<Integer> creditCharge = new Const<Integer>(creditChargeVal);
	Const<Integer> rechargeSize = new Const<Integer>(rechargeSizeVal);
	Const<Integer> noCredit = new Const<Integer>(0);
	


	// variables
	Var<Integer> credit = new Var<Integer>("credit", initialCreditVal);
	
	// Context
	EFSMContext context = new EFSMContext(credit);
	
	// expression
	IntGreat enoughCredit = new IntGreat(credit, noCredit); 
	IntGreat positiveCredit = new IntGreat(credit, noCredit);
	BoolOr creditFinished = new BoolOr(new IntLess(credit, noCredit), new IntEq(credit, noCredit));
	IntSum addCredit = new IntSum(credit, rechargeSize);
	IntSubt consumeCredit = new IntSubt(credit, creditCharge);
	
	// assign
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	Assign<Integer> call = new Assign<>(credit, consumeCredit);
	
	
	@Override
	public EFSM getModel() {
		
		// Transitions
		
		// t0 : NoCall - recharge -> NoCal
		EFSMTransition t0 = new EFSMTransition();
		t0.setOp(new EFSMOperation(recharge));
		Var<Enum> t0Out = new Var<Enum>("action", actions.Recharge);
		t0.setOutParameter(new EFSMParameter(t0Out));
		
		// t1 : NoCall - start -> Calling
		EFSMTransition t1 = new EFSMTransition();
		t1.setGuard(new EFSMGuard(enoughCredit));
		Var<Enum> t1Out = new Var<Enum>("action", actions.Start);
		t1.setOutParameter(new EFSMParameter(t1Out));
		
		// t2 : Calling - recharge -> Calling
		EFSMTransition t2 = new EFSMTransition();
		t2.setOp(new EFSMOperation(recharge));
		Var<Enum> t2Out = new Var<Enum>("action", actions.Recharge);
		t2.setOutParameter(new EFSMParameter(t2Out));
		
		// t3 : Calling - updateCredit -> Calling
		EFSMTransition t3 = new EFSMTransition();
		t3.setGuard(new EFSMGuard(enoughCredit));
		t3.setOp(new EFSMOperation(call));
		Var<Enum> t3Out = new Var<Enum>("action", actions.Charge);
		t3.setOutParameter(new EFSMParameter(t3Out));
		
		// t4 : Calling - stop -> NoCall
		EFSMTransition t4 = new EFSMTransition();
		Var<Enum> t4Out = new Var<Enum>("action", actions.StopManual);
		t4.setOutParameter(new EFSMParameter(t4Out));
		
		
		// t5 : Calling - noCredit -> NoCall
		EFSMTransition t5 = new EFSMTransition();
		t5.setGuard(new EFSMGuard(creditFinished));
		Var<Enum> t5Out = new Var<Enum>("action", actions.StopAuto);
		t5.setOutParameter(new EFSMParameter(t5Out));
		
		// create the mode
		EFSM phoneCall;
		EFSMBuilder phoneCallEFSMBuilder = new EFSMBuilder(EFSM.class);

		PhoneCallParameterGenerator parGenerator = new PhoneCallParameterGenerator();
		
		phoneCall = phoneCallEFSMBuilder
				.withTransition(NoCall, NoCall, t0)
				.withTransition(NoCall, Calling, t1)
				.withTransition(Calling, Calling, t2)
				.withTransition(Calling, Calling, t3)
				.withTransition(Calling, NoCall, t4)
				.withTransition(Calling, NoCall, t5)
				.build(NoCall, context, parGenerator);
		
		
		return phoneCall;
	}

}
