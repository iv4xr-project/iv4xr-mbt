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
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCall1.actions;

public class PhoneCallSingleWithTime implements EFSMProvider {

	// public enum actions { StartCall, InCall, Recharge, StopManual, Charge, StopNoCredit, TimePass };
	public enum uconEvents { EVAL_PERMIT, EVAL_DENY, REQUEST_STOPPED, RE_EVAL_PERMIT, RE_EVAL_DENY, SKIP};
	
	// const values	
	private static final Integer initialCreditVal = 20;
	private static final Integer creditChargeVal = 10;
	private static final Integer chargeTimeVal = 10000; //milliseconds
	private static final Integer rechargeAmountVal = 20;
	private static final Integer timeStepVal = 2000; // milliseconds
	private static final Integer timeRechargeVal = 3000; // milliseconds
	
	// constants
	Const<Integer> initialCredit = new Const<Integer>(initialCreditVal);
	Const<Integer> creditCharge = new Const<Integer>(creditChargeVal);	
	Const<Integer> rechargeAmount = new Const<Integer>(rechargeAmountVal);
	Const<Integer> timeStep = new Const<Integer>(timeStepVal);
	Const<Integer> rechargeTimeStep = new Const<Integer>(timeRechargeVal);
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
	IntLess noChargingTime  = new IntLess(time, chargeTime);
	BoolOr isChargingTime  = new BoolOr(new IntEq(time, chargeTime), new IntGreat(time, chargeTime));
	IntLess notIsChargingTime  = new IntLess(time, chargeTime);
	
	IntSum addCredit = new IntSum(credit, rechargeAmount);
	IntSubt consumeCredit = new IntSubt(credit, creditCharge); 
	IntSum timeIncrease = new IntSum(time, timeStep);
	IntSum rechargeTimeIncrease = new IntSum(time, rechargeTimeStep);
	
	BoolAnd enoughCredit_and_isCharginTime = new BoolAnd(enoughCredit, isChargingTime);
	BoolAnd notEnoughCredit_and_isCharginTime = new BoolAnd(notEnoughCredit, isChargingTime);
	
	// assign
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	//Assign<Integer> call = new Assign<>(credit, consumeCredit);
	Assign<Integer> charge = new Assign<>(credit, consumeCredit);
	Assign<Integer> timePass = new Assign<>(time, timeIncrease);
	Assign<Integer> rechargeTimePass = new Assign<>(time, rechargeTimeIncrease);
	Assign<Integer> timeReset = new Assign<>(time, timeZero);
	
	
	// states	
	public EFSMState Calling = new EFSMState("Calling");
	public EFSMState NoCall = new EFSMState("NoCall");
	public EFSMState ChargingWhileCalling = new EFSMState("ChargingWhileCalling");
	
	// input parameters encode SAFAX action
	EFSMParameter in_par_recharge = new EFSMParameter(
			new Var<String>("type", "EVAL"),
			new Var<String>("params::request", "RECHARGE"),
			new Var<String>("id", "topup")
			);
	
	EFSMParameter in_par_call_1 = new EFSMParameter(
			new Var<String>("type", "EVAL"),
			new Var<String>("params::request", "CALL"),
			new Var<String>("id", "call-1")
			);
	
	EFSMParameter in_par_time_pass = new EFSMParameter(
			new Var<String>("type", "DELAY"),
			new Var<String>("params::delayMs", String.valueOf(timeStepVal))			
			);
	
	EFSMParameter in_par_recharge_time = new EFSMParameter(
			new Var<String>("type", "DELAY"),
			new Var<String>("params::delayMs", String.valueOf(timeRechargeVal))			
			);
	
	EFSMParameter in_par_stop_call_1 = new EFSMParameter(
			new Var<String>("type", "STOP_REQUEST"),
			new Var<String>("params::requestId", "call-1")
			);
	
	// internal parameters are used when no input to SAFAX is required 
	EFSMParameter in_no_input = new EFSMParameter(
			new Var<String>("internal", "")			
			);
	
	
	@Override
	public EFSM getModel() {

		// create the model builder		
		EFSMBuilder phoneCallEFSMBuilder = new EFSMBuilder(EFSM.class);
				
		// t0: NoCall - recharge -> NoCall
		EFSMTransition t0 = new EFSMTransition();
		t0.setOp(new EFSMOperation(recharge));
		Var<Enum> t0Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t0.setInParameter(in_par_recharge);
		t0.setOutParameter(new EFSMParameter(t0Out));
		phoneCallEFSMBuilder.withTransition(NoCall, NoCall, t0);
				
		// t1: NoCall - start -> Calling
		EFSMTransition t1 = new EFSMTransition();
		t1.setGuard(new EFSMGuard(enoughCredit));
		t1.setOp(new EFSMOperation(charge, timeReset));
		Var<Enum> t1Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t1.setInParameter(in_par_call_1);
		t1.setOutParameter(new EFSMParameter(t1Out));
		phoneCallEFSMBuilder.withTransition(NoCall, Calling, t1);
			
		// t2: NoCall - start no money-> NoCall
		EFSMTransition t2 = new EFSMTransition();
		t2.setGuard(new EFSMGuard(notEnoughCredit));
		Var<Enum> t2Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t2.setInParameter(in_par_call_1);
		t2.setOutParameter(new EFSMParameter(t2Out));
		phoneCallEFSMBuilder.withTransition(NoCall, NoCall, t2);
		
		// t3: Calling - timePass -> Calling
		EFSMTransition t3 = new EFSMTransition();
		t3.setGuard(new EFSMGuard(noChargingTime));
		t3.setOp(new EFSMOperation(timePass));
		Var<Enum> t3Out = new Var<Enum>("action", uconEvents.SKIP);
		t3.setInParameter(in_par_time_pass);
		t3.setOutParameter(new EFSMParameter(t3Out));
		phoneCallEFSMBuilder.withTransition(Calling, Calling, t3);
		
		// t4: Calling - charge -> Calling
		EFSMTransition t4 = new EFSMTransition();
		t4.setGuard(new EFSMGuard(enoughCredit_and_isCharginTime));
		t4.setOp(new EFSMOperation(charge, timeReset));
		Var<Enum> t4Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t4.setInParameter(in_no_input); // no input to the SUT
		t4.setOutParameter(new EFSMParameter(t4Out));
		phoneCallEFSMBuilder.withTransition(Calling, Calling, t4);
				
		// t5: Calling - recharge -> ChargingWhileCalling
		EFSMTransition t5 = new EFSMTransition();
		t5.setGuard(new EFSMGuard(noChargingTime));
		t5.setOp(new EFSMOperation(recharge));
		Var<Enum> t5Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t5.setInParameter(in_par_recharge);
		t5.setOutParameter(new EFSMParameter(t5Out));
		phoneCallEFSMBuilder.withTransition(Calling, ChargingWhileCalling, t5);
		
		// t6: ChargingWhileCalling - timePass -> Calling
		EFSMTransition t6 = new EFSMTransition();
		t6.setOp(new EFSMOperation(rechargeTimePass));
		Var<Enum> t6Out = new Var<Enum>("action", uconEvents.SKIP);
		t6.setInParameter(in_par_recharge_time);
		t6.setOutParameter(new EFSMParameter(t6Out));
		phoneCallEFSMBuilder.withTransition(ChargingWhileCalling, Calling, t6);
		
		// t7: Calling - endCall -> NoCall
		EFSMTransition t7 = new EFSMTransition();
		t7.setGuard(new EFSMGuard(notIsChargingTime));
		t7.setOp(new EFSMOperation(timeReset));
		Var<Enum> t7Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t7.setInParameter(in_par_stop_call_1);
		t7.setOutParameter(new EFSMParameter(t7Out));
		phoneCallEFSMBuilder.withTransition(Calling, NoCall, t7);
		
		// t8: Calling - noCredit -> NoCall
		EFSMTransition t8 = new EFSMTransition();
		t8.setGuard(new EFSMGuard(notEnoughCredit_and_isCharginTime));
		t8.setOp(new EFSMOperation(timeReset));
		Var<Enum> t8Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
		t8.setInParameter(in_no_input);
		t8.setOutParameter(new EFSMParameter(t8Out));
		phoneCallEFSMBuilder.withTransition(Calling, NoCall, t8);
		
		return phoneCallEFSMBuilder.build(NoCall, context, null);
		
	}

}
