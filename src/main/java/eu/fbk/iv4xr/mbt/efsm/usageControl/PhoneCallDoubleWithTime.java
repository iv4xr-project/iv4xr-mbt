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
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCallSingleWithTime.uconEvents;

public class PhoneCallDoubleWithTime implements EFSMProvider{

	// public enum actions { StartCall, InCall, Recharge, StopManual, Charge, StopNoCredit, TimePass };
	public enum uconEvents { EVAL_PERMIT, EVAL_DENY, REQUEST_STOPPED, RE_EVAL_PERMIT, RE_EVAL_DENY, SKIP};
	
	// const values	
	private static final Integer initialCreditVal = 20;
	private static final Integer creditChargeVal = 10;
	private static final Integer chargeTimeVal = 10000; //milliseconds
	private static final Integer rechargeAmountVal = 20;
	private static final Integer timeStepVal = 2000; // milliseconds
	private static final Integer timeRechargeVal = 4000; // milliseconds
	
	// constants
	Const<Integer> initialCredit = new Const<Integer>(initialCreditVal);
	Const<Integer> creditCharge = new Const<Integer>(creditChargeVal);
	Const<Integer> creditCharge2 = new Const<Integer>(creditChargeVal*2);	
	Const<Integer> rechargeAmount = new Const<Integer>(rechargeAmountVal);
	Const<Integer> timeStep = new Const<Integer>(timeStepVal);
	Const<Integer> rechargeTimeStep = new Const<Integer>(timeRechargeVal);
	Const<Integer> timeZero = new Const<Integer>(0);
	Const<Integer> chargeTime = new Const<Integer>(chargeTimeVal);
	
	
	// variables
	Var<Integer> credit = new Var<Integer>("credit", initialCreditVal);
	Var<Integer> timeCall1 = new Var<Integer>("time1", 0);
	Var<Integer> timeCall2 = new Var<Integer>("time2", 0);
	
	// Context
	EFSMContext context = new EFSMContext(credit, timeCall1, timeCall2);
	
	// expression
	BoolOr enoughCredit = new BoolOr(new IntGreat(credit, creditCharge), new IntEq(credit, creditCharge));
	BoolOr enoughCredit2 = new BoolOr(new IntGreat(credit, creditCharge2), new IntEq(credit, creditCharge2));	
	IntLess notEnoughCredit = new IntLess(credit, creditCharge);
	IntLess notEnoughCredit2 = new IntLess(credit, creditCharge2);
	BoolAnd enoughCredit_and_notEnoughCredit2 = new BoolAnd(enoughCredit, notEnoughCredit2);
	IntLess noChargingTimeCall1  = new IntLess(timeCall1, chargeTime);
	IntLess noChargingTimeCall2  = new IntLess(timeCall2, chargeTime);
	BoolOr isChargingTimeCall1  = new BoolOr(new IntEq(timeCall1, chargeTime), new IntGreat(timeCall1, chargeTime));
	BoolOr isChargingTimeCall2  = new BoolOr(new IntEq(timeCall2, chargeTime), new IntGreat(timeCall2, chargeTime));
	
	IntSum addCredit = new IntSum(credit, rechargeAmount);
	IntSubt consumeCredit = new IntSubt(credit, creditCharge); 
	IntSubt consumeCredit2 = new IntSubt(credit, new IntSum(creditCharge,creditCharge));
	IntSum timeCall1Increase = new IntSum(timeCall1, timeStep);
	IntSum timeCall2Increase = new IntSum(timeCall2, timeStep);
	IntSum rechargeTimeCall1Increase = new IntSum(timeCall1, rechargeTimeStep);
	IntSum rechargeTimeCall2Increase = new IntSum(timeCall2, rechargeTimeStep);
	
	BoolAnd enoughCredit_and_isCharginTimeCall1 = new BoolAnd(enoughCredit, isChargingTimeCall1);
	BoolAnd enoughCredit_and_isCharginTimeCall2 = new BoolAnd(enoughCredit, isChargingTimeCall2);
	
	BoolAnd notEnoughCredit_and_isCharginTimeCall1 = new BoolAnd(notEnoughCredit, isChargingTimeCall1);
	BoolAnd notEnoughCredit_and_isCharginTimeCall2 = new BoolAnd(notEnoughCredit, isChargingTimeCall2);

	BoolAnd enoughCredit_and_notIsCharginTimeCall1 = new BoolAnd(enoughCredit, noChargingTimeCall1);
	BoolAnd enoughCredit_and_notIsCharginTimeCall2 = new BoolAnd(enoughCredit, noChargingTimeCall2);
	
	BoolAnd notEnoughCredit_and_notIsCharginTimeCall1 = new BoolAnd(notEnoughCredit, noChargingTimeCall1);
	BoolAnd notEnoughCredit_and_notIsCharginTimeCall2 = new BoolAnd(notEnoughCredit, noChargingTimeCall2);

	BoolAnd noChargingTimeCall1_and_noChargingTimeCall2 = new BoolAnd(noChargingTimeCall1,noChargingTimeCall2);	
	BoolAnd enoughCredit_and_IsCharginTimeCall1_and_noChargingTimeCall2 = 
			new BoolAnd(enoughCredit, new BoolAnd(isChargingTimeCall1,noChargingTimeCall2));
	BoolAnd enoughCredit_and_noChargingTimeCall1_and_IsCharginTimeCall2 = 
			new BoolAnd(enoughCredit, new BoolAnd(noChargingTimeCall1,isChargingTimeCall2));
	BoolAnd notEnoughCredit_and_IsCharginTimeCall1_and_IsChargingTimeCall2 = 
			new BoolAnd(notEnoughCredit, new BoolAnd(isChargingTimeCall1,isChargingTimeCall2));
	BoolAnd enoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2 = 
			new BoolAnd(enoughCredit2, new BoolAnd(isChargingTimeCall1,isChargingTimeCall2));
	BoolAnd enoughCredit_and_notEnoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2 = 
			new BoolAnd(enoughCredit_and_notEnoughCredit2, new BoolAnd(isChargingTimeCall1,isChargingTimeCall2));
	// assign
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	Assign<Integer> charge = new Assign<>(credit, consumeCredit);
	Assign<Integer> charge2 = new Assign<>(credit, consumeCredit2);
	Assign<Integer> timeCall1Pass = new Assign<>(timeCall1, timeCall1Increase);
	Assign<Integer> timeCall2Pass = new Assign<>(timeCall2, timeCall2Increase);
	Assign<Integer> rechargeTimeCall1Pass = new Assign<>(timeCall1, rechargeTimeCall1Increase);
	Assign<Integer> rechargeTimeCall2Pass = new Assign<>(timeCall2, rechargeTimeCall2Increase);
	Assign<Integer> timeCall1Reset = new Assign<>(timeCall1, timeZero);
	Assign<Integer> timeCall2Reset = new Assign<>(timeCall2, timeZero);
	
	// states	
	public EFSMState Calling1 = new EFSMState("Calling1");
	public EFSMState Calling2 = new EFSMState("Calling2");
	public EFSMState Calling1and2 = new EFSMState("Calling1and2");
	public EFSMState NoCall = new EFSMState("NoCall");
	public EFSMState RechargingWhileCalling1 = new EFSMState("RechargingWhileCalling1");
	public EFSMState RechargingWhileCalling2 = new EFSMState("RechargingWhileCalling2");
	public EFSMState RechargingWhileCalling1and2 = new EFSMState("RechargingWhileCalling1and2");
	public EFSMState Call2Pending = new EFSMState("Call2Pending");
	
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
	
	EFSMParameter in_par_call_2 = new EFSMParameter(
			new Var<String>("type", "EVAL"),
			new Var<String>("params::request", "CALL"),
			new Var<String>("id", "call-2")
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

	EFSMParameter in_par_stop_call_2 = new EFSMParameter(
			new Var<String>("type", "STOP_REQUEST"),
			new Var<String>("params::requestId", "call-2")
			);
	
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
		
		// t1: NoCall - start no credit -> NoCall
		EFSMTransition t1 = new EFSMTransition();
		t1.setGuard(new EFSMGuard(notEnoughCredit));
		Var<Enum> t1Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t1.setInParameter(in_par_call_1);
		t1.setOutParameter(new EFSMParameter(t1Out));
		phoneCallEFSMBuilder.withTransition(NoCall, NoCall, t1);		
		
		// t2: NoCall - start -> Calling1
		EFSMTransition t2 = new EFSMTransition();
		t2.setGuard(new EFSMGuard(enoughCredit));
		t2.setOp(new EFSMOperation(charge, timeCall1Reset));
		Var<Enum> t2Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t2.setInParameter(in_par_call_1);
		t2.setOutParameter(new EFSMParameter(t2Out));
		phoneCallEFSMBuilder.withTransition(NoCall, Calling1, t2);
		
		// t3: Calling1 - endCall1 -> NoCall
		EFSMTransition t3 = new EFSMTransition();
		t3.setGuard(new EFSMGuard(noChargingTimeCall1));
		t3.setOp(new EFSMOperation(timeCall1Reset));
		Var<Enum> t3Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t3.setInParameter(in_par_stop_call_1);
		t3.setOutParameter(new EFSMParameter(t3Out));
		phoneCallEFSMBuilder.withTransition(Calling1, NoCall, t3);
		
		// t4: Calling1 - noCredit -> NoCall
		EFSMTransition t4 = new EFSMTransition();
		t4.setGuard(new EFSMGuard(notEnoughCredit_and_isCharginTimeCall1));
		t4.setOp(new EFSMOperation(timeCall1Reset));
		Var<Enum> t4Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
		t4.setInParameter(in_no_input);
		t4.setOutParameter(new EFSMParameter(t4Out));
		phoneCallEFSMBuilder.withTransition(Calling1, NoCall, t4);		
		
		// t5: Calling1 - timeCall1pass -> Calling1
		EFSMTransition t5 = new EFSMTransition();
		t5.setGuard(new EFSMGuard(noChargingTimeCall1));
		t5.setOp(new EFSMOperation(timeCall1Pass));
		Var<Enum> t5Out = new Var<Enum>("action", uconEvents.SKIP);
		t5.setInParameter(in_par_time_pass);
		t5.setOutParameter(new EFSMParameter(t5Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t5);		

		// t6: Calling1 - charge -> Calling1
		EFSMTransition t6 = new EFSMTransition();
		t6.setGuard(new EFSMGuard(enoughCredit_and_isCharginTimeCall1));
		t6.setOp(new EFSMOperation(timeCall1Reset, charge));
		Var<Enum> t6Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t6.setInParameter(in_no_input);
		t6.setOutParameter(new EFSMParameter(t6Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t6);
		
		// t7: Calling1 - start call 2 fail -> Calling1
		EFSMTransition t7 = new EFSMTransition();
		t7.setGuard(new EFSMGuard(notEnoughCredit_and_notIsCharginTimeCall1)); 		
		Var<Enum> t7Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t7.setInParameter(in_par_call_2);
		t7.setOutParameter(new EFSMParameter(t7Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t7);
		
		// t8: Calling1 - recharge -> RechargingWhileCalling1
		EFSMTransition t8 = new EFSMTransition();
		t8.setGuard(new EFSMGuard(noChargingTimeCall1));
		t8.setOp(new EFSMOperation(recharge));
		Var<Enum> t8Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t8.setInParameter(in_par_recharge);
		t8.setOutParameter(new EFSMParameter(t8Out));
		phoneCallEFSMBuilder.withTransition(Calling1, RechargingWhileCalling1, t8);
		
		// t9: RechargingWhileCalling1 - timePass -> Calling1
		EFSMTransition t9 = new EFSMTransition();
		t9.setOp(new EFSMOperation(rechargeTimeCall1Pass));
		Var<Enum> t9Out = new Var<Enum>("action", uconEvents.SKIP);
		t9.setInParameter(in_par_recharge_time);
		t9.setOutParameter(new EFSMParameter(t9Out));
		phoneCallEFSMBuilder.withTransition(RechargingWhileCalling1, Calling1, t9);		
		
		// t10: Calling1 - call -> Calling1and2
		EFSMTransition t10 = new EFSMTransition();
		t10.setGuard(new EFSMGuard(enoughCredit_and_notIsCharginTimeCall1));
		t10.setOp(new EFSMOperation(charge, timeCall2Reset));
		Var<Enum> t10Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t10.setInParameter(in_par_call_2);
		t10.setOutParameter(new EFSMParameter(t10Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1and2, t10);
		
		// t11: Calling1 - endCall2 -> Calling1and2
		EFSMTransition t11 = new EFSMTransition();
		t11.setGuard(new EFSMGuard(noChargingTimeCall1)); 
		t11.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t11Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t11.setInParameter(in_par_stop_call_2);
		t11.setOutParameter(new EFSMParameter(t11Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1, t11);

		// t12: Calling1and2 - timeCall1pass and timeCall2pass -> Calling1and2
		EFSMTransition t12 = new EFSMTransition();
		t12.setGuard(new EFSMGuard(noChargingTimeCall1_and_noChargingTimeCall2));
		t12.setOp(new EFSMOperation(timeCall1Pass, timeCall2Pass));
		Var<Enum> t12Out = new Var<Enum>("action", uconEvents.SKIP);
		t12.setInParameter(in_par_time_pass);
		t12.setOutParameter(new EFSMParameter(t12Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t12);
		
		// t13: Calling1and2 - charge 1 -> Calling1and2
		EFSMTransition t13 = new EFSMTransition();
		t13.setGuard(new EFSMGuard(enoughCredit_and_IsCharginTimeCall1_and_noChargingTimeCall2));
		t13.setOp(new EFSMOperation(charge, timeCall1Reset));	
		Var<Enum> t13Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t13.setInParameter(in_no_input);
		t13.setOutParameter(new EFSMParameter(t13Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t13);
		
		// t14: Calling1and2 - charge 2 -> Calling1and2
		EFSMTransition t14 = new EFSMTransition();
		t14.setGuard(new EFSMGuard(enoughCredit_and_noChargingTimeCall1_and_IsCharginTimeCall2));
		t14.setOp(new EFSMOperation(charge, timeCall2Reset));	
		Var<Enum> t14Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t14.setInParameter(in_no_input);
		t14.setOutParameter(new EFSMParameter(t14Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t14);
		
		// t15: Calling1and2 - charge 1 and 2 -> Calling1and2
		EFSMTransition t15 = new EFSMTransition();
		t15.setGuard(new EFSMGuard(enoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2));
		t15.setOp(new EFSMOperation(charge2, timeCall1Reset, timeCall2Reset));	
		Var<Enum> t15Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t15.setInParameter(in_no_input);
		t15.setOutParameter(new EFSMParameter(t15Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t15);
					
		// t16: Calling1and2 - recharge -> RechargingWhileCalling1and2
		EFSMTransition t16 = new EFSMTransition();
		t16.setGuard(new EFSMGuard(noChargingTimeCall1_and_noChargingTimeCall2));
		t16.setOp(new EFSMOperation(recharge));
		Var<Enum> t16Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t16.setInParameter(in_par_recharge);
		t16.setOutParameter(new EFSMParameter(t16Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, RechargingWhileCalling1and2, t16);
		
		// t17: RechargingWhileCalling1and2 - timePass -> Calling1and2
		EFSMTransition t17 = new EFSMTransition();
		t17.setOp(new EFSMOperation(rechargeTimeCall1Pass,rechargeTimeCall2Pass));
		Var<Enum> t17Out = new Var<Enum>("action", uconEvents.SKIP);
		t17.setInParameter(in_par_recharge_time);
		t17.setOutParameter(new EFSMParameter(t17Out));
		phoneCallEFSMBuilder.withTransition(RechargingWhileCalling1and2, Calling1and2, t17);	

		// t18: Calling1and2 - noCredit for 2  -> Calling1
		EFSMTransition t18 = new EFSMTransition();
		t18.setGuard(new EFSMGuard(enoughCredit_and_notEnoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2));
		t18.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t18Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t18.setInParameter(in_no_input);
		t18.setOutParameter(new EFSMParameter(t17Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1, t18);	
		
		// t19: Calling1and2 - end call 1 -> Calling2
		EFSMTransition t19 = new EFSMTransition();
		t19.setGuard(new EFSMGuard(noChargingTimeCall1_and_noChargingTimeCall2));
		t19.setOp(new EFSMOperation(timeCall1Reset));
		Var<Enum> t19Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t19.setInParameter(in_par_stop_call_1);
		t19.setOutParameter(new EFSMParameter(t19Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling2, t19);
		
		// t20: Calling2  - start call 1 -> Calling1and2
		EFSMTransition t20 = new EFSMTransition();
		t20.setGuard(new EFSMGuard(enoughCredit_and_notIsCharginTimeCall2));
		t20.setOp(new EFSMOperation(charge));
		Var<Enum> t20Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t20.setInParameter(in_par_call_1);
		t20.setOutParameter(new EFSMParameter(t20Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling1and2, t20);
		
		// t21: Calling2  - start call 1 no money -> Calling2
		EFSMTransition t21 = new EFSMTransition();
		t21.setGuard(new EFSMGuard(notEnoughCredit_and_notIsCharginTimeCall2));
		Var<Enum> t21Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t21.setInParameter(in_par_call_1);
		t21.setOutParameter(new EFSMParameter(t21Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t21);
		
		// t22: Calling2  - charge  -> Calling2
		EFSMTransition t22 = new EFSMTransition();
		t22.setGuard(new EFSMGuard(enoughCredit_and_isCharginTimeCall2));
		t22.setOp(new EFSMOperation(charge));
		Var<Enum> t22Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t22.setInParameter(in_no_input);
		t22.setOutParameter(new EFSMParameter(t22Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t22);
		
		// t23: Calling2  - time pass  -> Calling2
		EFSMTransition t23 = new EFSMTransition();
		t23.setGuard(new EFSMGuard(noChargingTimeCall2));
		t23.setOp(new EFSMOperation(timeCall2Pass));
		Var<Enum> t23Out = new Var<Enum>("action", uconEvents.SKIP);
		t23.setInParameter(in_par_time_pass);
		t23.setOutParameter(new EFSMParameter(t23Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t23);
		
		// t24: Calling2  - stop call2  -> NoCall
		EFSMTransition t24 = new EFSMTransition();
		t24.setGuard(new EFSMGuard(noChargingTimeCall2));
		t24.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t24Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t24.setInParameter(in_par_stop_call_2);
		t24.setOutParameter(new EFSMParameter(t24Out));
		phoneCallEFSMBuilder.withTransition(Calling2, NoCall, t24);
		
		// t25: Calling2 - noCredit -> NoCall
		EFSMTransition t25 = new EFSMTransition();
		t25.setGuard(new EFSMGuard(notEnoughCredit_and_isCharginTimeCall2));
		t25.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t25Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
		t25.setInParameter(in_no_input);
		t25.setOutParameter(new EFSMParameter(t25Out));
		phoneCallEFSMBuilder.withTransition(Calling2, NoCall, t25);	
		
		return phoneCallEFSMBuilder.build(NoCall, context, null);
	}

	// //
	// Removed
	// when the charge time for both are active and there is not money for 2
	// it suffices to close call 2 and go to call 1
	// then call 1 manage the decision depending if there is money 
	// the below version introduce not useful complication introducing a pending state
	
	//		// t18: Calling1and2 - noCredit for 2 but credit for 1 -> Call2Pending
//	EFSMTransition t18 = new EFSMTransition();
//	t18.setGuard(new EFSMGuard(enoughCredit_and_notEnoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2));
//	t18.setOp(new EFSMOperation(charge,timeCall1Reset));
//	Var<Enum> t18Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
//	t18.setInParameter(in_no_input);
//	t18.setOutParameter(new EFSMParameter(t18Out));
//	phoneCallEFSMBuilder.withTransition(Calling1and2, Call2Pending, t18);
//	
//	// t19: Call2Pending - nocredit -> Calling1
//	EFSMTransition t19 = new EFSMTransition();
//	t19.setOp(new EFSMOperation(timeCall2Reset));
//	Var<Enum> t19Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
//	t19.setInParameter(in_no_input);
//	t19.setOutParameter(new EFSMParameter(t19Out));
//	phoneCallEFSMBuilder.withTransition(Call2Pending, Calling1, t19);
//
//	// t20: Calling1and2 - noCredit -> Calling1
//	EFSMTransition t20 = new EFSMTransition();
//	t20.setGuard(new EFSMGuard(notEnoughCredit_and_IsCharginTimeCall1_and_IsChargingTimeCall2));
//	t20.setOp(new EFSMOperation(timeCall2Reset));
//	Var<Enum> t20Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
//	t20.setInParameter(in_no_input);
//	t20.setOutParameter(new EFSMParameter(t20Out));
//	phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1, t20);
}
