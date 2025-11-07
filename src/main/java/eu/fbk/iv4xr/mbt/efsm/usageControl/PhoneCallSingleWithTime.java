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
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCall1.actions;

public class PhoneCallSingleWithTime implements EFSMProvider {

	public enum actions { StartCall, InCall, Recharge, StopManual, Charge, StopNoCredit, TimePass };
	
	// const values	
	private static final Integer initialCreditVal = 50;
	private static final Integer creditChargeVal = 10;
	private static final Integer chargeTimeVal = 10;
	private static final Integer rechargeAmountVal = 50;
	private static final Integer timeStepVal = 1;
	
	// constants
	Const<Integer> initialCredit = new Const<Integer>(initialCreditVal);
	Const<Integer> creditCharge = new Const<Integer>(creditChargeVal);	
	Const<Integer> rechargeAmount = new Const<Integer>(rechargeAmountVal);
	Const<Integer> timeStep = new Const<Integer>(timeStepVal);
	Const<Integer> timeZero = new Const<Integer>(0);
	Const<Integer> chargeTime = new Const<Integer>(chargeTimeVal);
	
	// variables
	Var<Integer> credit = new Var<Integer>("credit", initialCreditVal);
	Var<Integer> time = new Var<Integer>("time", 0);

	// Context
	EFSMContext context = new EFSMContext(credit, time);
	
	// expression
	BoolOr enoughCredit = new BoolOr(new IntGreat(credit, creditCharge), new IntEq(credit, creditCharge));
	IntLess notEnoughCredit = new IntLess(credit, creditCharge);
	IntLess noCharginTime  = new IntLess(time, chargeTime);
	IntEq isCharginTime  = new IntEq(time, chargeTime);
	
	IntSum addCredit = new IntSum(credit, rechargeAmount);
	IntSubt consumeCredit = new IntSubt(credit, creditCharge); 
	IntSum timeIncrease = new IntSum(time, timeStep);
	
	BoolAnd enoughCredit_and_isCharginTime = new BoolAnd(enoughCredit, isCharginTime);
	BoolAnd notEnoughCredit_and_isCharginTime = new BoolAnd(notEnoughCredit, isCharginTime);
	
	// assign
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	Assign<Integer> call = new Assign<>(credit, consumeCredit);
	Assign<Integer> charge = new Assign<>(credit, consumeCredit);
	Assign<Integer> timePass = new Assign<>(time, timeIncrease);
	Assign<Integer> timeReset = new Assign<>(time, timeZero);
	
	// states	
	public EFSMState Calling = new EFSMState("Calling");
	public EFSMState NoCall = new EFSMState("NoCall");

	
	@Override
	public EFSM getModel() {

		// create the model builder		
		EFSMBuilder phoneCallEFSMBuilder = new EFSMBuilder(EFSM.class);
				
		// t0: NoCall - recharge -> NoCall
		EFSMTransition t0 = new EFSMTransition();
		t0.setOp(new EFSMOperation(recharge));
		Var<Enum> t0Out = new Var<Enum>("action", actions.Recharge);
		t0.setOutParameter(new EFSMParameter(t0Out));
		phoneCallEFSMBuilder.withTransition(NoCall, NoCall, t0);
				
		// t1: NoCall - start -> Calling
		EFSMTransition t1 = new EFSMTransition();
		t1.setGuard(new EFSMGuard(enoughCredit));
		t1.setOp(new EFSMOperation(charge, timeReset));
		Var<Enum> t1Out = new Var<Enum>("action", actions.StartCall);
		t1.setOutParameter(new EFSMParameter(t1Out));
		phoneCallEFSMBuilder.withTransition(NoCall, Calling, t1);
				
		// t2: Calling - timePass -> Calling
		EFSMTransition t2 = new EFSMTransition();
		t2.setGuard(new EFSMGuard(noCharginTime));
		t2.setOp(new EFSMOperation(timePass));
		Var<Enum> t2Out = new Var<Enum>("action", actions.TimePass);
		t2.setOutParameter(new EFSMParameter(t2Out));
		phoneCallEFSMBuilder.withTransition(Calling, Calling, t2);
		
		// t3: Calling - charge -> Calling
		EFSMTransition t3 = new EFSMTransition();
		t3.setGuard(new EFSMGuard(enoughCredit_and_isCharginTime));
		t3.setOp(new EFSMOperation(charge, timeReset));
		Var<Enum> t3Out = new Var<Enum>("action", actions.Charge);
		t3.setOutParameter(new EFSMParameter(t3Out));
		phoneCallEFSMBuilder.withTransition(Calling, Calling, t3);
		
		
		// t4: Calling - charge -> Calling
		EFSMTransition t4 = new EFSMTransition();
		t4.setOp(new EFSMOperation(recharge));
		Var<Enum> t4Out = new Var<Enum>("action", actions.Recharge);
		t4.setOutParameter(new EFSMParameter(t4Out));
		phoneCallEFSMBuilder.withTransition(Calling, Calling, t4);
		
		// t5: Calling - endCall -> Calling
		EFSMTransition t5 = new EFSMTransition();
		t5.setOp(new EFSMOperation(timeReset));
		Var<Enum> t5Out = new Var<Enum>("action", actions.StopManual);
		t5.setOutParameter(new EFSMParameter(t5Out));
		phoneCallEFSMBuilder.withTransition(Calling, NoCall, t5);
		
		// t6: Calling - noCredit -> Calling
		EFSMTransition t6 = new EFSMTransition();
		t6.setGuard(new EFSMGuard(notEnoughCredit_and_isCharginTime));
		t6.setOp(new EFSMOperation(timeReset));
		Var<Enum> t6Out = new Var<Enum>("action", actions.StopNoCredit);
		t6.setOutParameter(new EFSMParameter(t6Out));
		phoneCallEFSMBuilder.withTransition(Calling, NoCall, t6);
		
		return phoneCallEFSMBuilder.build(NoCall, context, null);
		
	}

}
