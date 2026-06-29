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
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCallDoubleWithTime.uconEvents;

public class PhoneCallDoubleSynched  implements EFSMProvider{

	public enum uconEvents { EVAL_PERMIT, EVAL_DENY, REQUEST_STOPPED, RE_EVAL_PERMIT, RE_EVAL_DENY, SKIP, RE_EVAL_STARTED};
	
	///////////////////////////////////////////////////////////
	// Constant values	
	
	// parameters in safax attributes
	private static final Integer initialCreditVal = 20;
	private static final Integer creditChargeVal = 20;
	private static final Integer rechargeAmountVal = 10;
	// parameters coded in xacml policies	
	private static final Integer chargeTimeVal = 10000; // AttributeId updateInterval - PT10s
	// model parameters	
	private static final Integer timeStepVal = 2000; // the user call without taking an action (explicity delay)s
	private static final Integer timeRechargeVal = 500; // time required for a reacharge (explicity delay)
	private static final Integer chargeTimeMarginVal = 600; // the user cannot take action in the time interval  [  chargeTimeVal - chargeTimeMargin, chargeTimeVal] to avoid misalignment
	private static final Integer internalDelayRechargeVal = 350; // account the time required by SAFAX to do a recharge
	private static final Integer internalDelayCallVal = 350; // account the time required by SAFAX to start a call
	private static final Integer internalDelayChargeVal = 350; // account the time required by SAFAX to start a call
	
	///////////////////////////////////////////////////////////
	// Constant expressions representing constant values

	// parameters in safax attributes
	Const<Integer> initialCredit = new Const<Integer>(initialCreditVal);
	Const<Integer> creditCharge = new Const<Integer>(creditChargeVal);
	Const<Integer> creditCharge2 = new Const<Integer>(creditChargeVal*2); // convenient for 2 charge in one time	
	Const<Integer> rechargeAmount = new Const<Integer>(rechargeAmountVal);
	// parameters coded in xacml policies
	Const<Integer> chargeTime = new Const<Integer>(chargeTimeVal);
	// model parameters
	Const<Integer> timeStep = new Const<Integer>(timeStepVal);
	Const<Integer> timeRecharge = new Const<Integer>(timeRechargeVal);
	Const<Integer> chargeTimeMargin = new Const<Integer>(chargeTimeMarginVal);
	Const<Integer> internalDelayRecharge = new Const<Integer>(internalDelayRechargeVal);
	Const<Integer> internalDelayCall = new Const<Integer>(internalDelayCallVal);
	Const<Integer> internalDelayCharge = new Const<Integer>(internalDelayChargeVal);
	// other constants
	Const<Integer> timeZero = new Const<Integer>(0);
	
	
	
	///////////////////////////////////////////////////////////
	// States

	public EFSMState Calling1 = new EFSMState("Calling1");
	public EFSMState Calling2 = new EFSMState("Calling2");
	public EFSMState Calling1and2 = new EFSMState("Calling1and2");
	public EFSMState NoCall = new EFSMState("NoCall");
	public EFSMState RechargingWhileCalling1 = new EFSMState("RechargingWhileCalling1");
	public EFSMState RechargingWhileCalling2 = new EFSMState("RechargingWhileCalling2");
	public EFSMState RechargingWhileCalling1and2 = new EFSMState("RechargingWhileCalling1and2");
	// public EFSMState ClosingCall2 = new EFSMState("ClosingCall2"); REMOVED
			
	
	///////////////////////////////////////////////////////////
	// EFSM context
	
	// variables
	Var<Integer> credit = new Var<Integer>("credit", initialCreditVal);
	Var<Integer> timeCall1 = new Var<Integer>("time1", 0);
	Var<Integer> timeCall2 = new Var<Integer>("time2", 0);
	// Context
	EFSMContext context = new EFSMContext(credit, timeCall1, timeCall2);
		
	///////////////////////////////////////////////////////////
	// Expressions
	
	// basic credit checks
	BoolOr enoughCredit1call = new BoolOr(new IntGreat(credit, creditCharge), new IntEq(credit, creditCharge));
	BoolOr enoughCredit2calls = new BoolOr(new IntGreat(credit, creditCharge2), new IntEq(credit, creditCharge2));
	IntLess notEnoughCredit1call = new IntLess(credit, creditCharge);
	IntLess notEnoughCredit2calls = new IntLess(credit, creditCharge2);
	// combined credit checks
	BoolAnd enoughCredit1call_and_notEnoughCredit2calls = new BoolAnd(enoughCredit1call, notEnoughCredit2calls);
	// charging time that account for the margin
	IntLess noAlmostChargingTimeCall1  = new IntLess(timeCall1, new IntSubt(chargeTime,chargeTimeMargin ));
	IntLess noAlmostChargingTimeCall2  = new IntLess(timeCall2, new IntSubt(chargeTime,chargeTimeMargin ));
	IntGreat isAlmostChargingTimeCall1  = new IntGreat(timeCall1, new IntSubt(chargeTime, chargeTimeMargin)); // case equal to check??
	IntGreat isAlmostChargingTimeCall2  = new IntGreat(timeCall2, new IntSubt(chargeTime, chargeTimeMargin)); // case equal to check??
	
	BoolOr isChargingTimeCall1  = new BoolOr(new IntGreat(timeCall1, chargeTime), new IntEq(timeCall1, chargeTime)); 
	BoolOr isChargingTimeCall2  = new BoolOr(new IntGreat(timeCall2, chargeTime), new IntEq(timeCall2, chargeTime));
	IntLess noChargingTimeCall1 = new IntLess(timeCall1, chargeTime);
	IntLess noChargingTimeCall2 = new IntLess(timeCall2, chargeTime);
	
	// credit
	IntSum addCredit = new IntSum(credit, rechargeAmount);
	IntSubt consumeCredit1call = new IntSubt(credit, creditCharge); 
	IntSubt consumeCredit2calls = new IntSubt(credit, new IntSum(creditCharge,creditCharge));
	// time management
	IntSum timeCall1Increase = new IntSum(timeCall1, timeStep);
	IntSum timeCall2Increase = new IntSum(timeCall2, timeStep);
	IntSum rechargeTimeCall1Increase = new IntSum(timeCall1, timeRecharge);
	IntSum rechargeTimeCall2Increase = new IntSum(timeCall2, timeRecharge);
	IntSum timeInternalDelayCall1Increase  = new IntSum(timeCall1, internalDelayCall);
	IntSum timeInternalDelayCall2Increase  = new IntSum(timeCall2, internalDelayCall);
	IntSum timeInternalDelayRechargeCall1Increase  = new IntSum(timeCall1, internalDelayRecharge);
	IntSum timeInternalDelayRechargeCall2Increase  = new IntSum(timeCall2, internalDelayRecharge);
	
	// combined expressions
	BoolAnd enoughCredit_and_isAlmostCharginTimeCall1 = new BoolAnd(enoughCredit1call, isAlmostChargingTimeCall1);
	BoolAnd enoughCredit_and_isAlmostCharginTimeCall2 = new BoolAnd(enoughCredit1call, isAlmostChargingTimeCall2);
	BoolAnd notEnoughCredit_and_isAlmostCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, isAlmostChargingTimeCall1);
	BoolAnd notEnoughCredit_and_isAlmostCharginTimeCall2 = new BoolAnd(notEnoughCredit1call, isAlmostChargingTimeCall2);
	BoolAnd enoughCredit_and_notIsAlmostCharginTimeCall1 = new BoolAnd(enoughCredit1call, noAlmostChargingTimeCall1);
	BoolAnd enoughCredit_and_notIsAlmostCharginTimeCall2 = new BoolAnd(enoughCredit1call, noAlmostChargingTimeCall2);
	BoolAnd notEnoughCredit_and_notIsAlmostCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, noAlmostChargingTimeCall1);
	BoolAnd notEnoughCredit_and_notIsAlmostCharginTimeCall2 = new BoolAnd(notEnoughCredit1call, noAlmostChargingTimeCall2);
	BoolAnd noAlmostChargingTimeCall1_and_noAlmostChargingTimeCall2 = new BoolAnd(noAlmostChargingTimeCall1,noAlmostChargingTimeCall2);
	BoolAnd enoughCredit1call_and_IsAlmostCharginTimeCall1_and_noAlmostChargingTimeCall2 = 
			new BoolAnd(enoughCredit1call, new BoolAnd(isAlmostChargingTimeCall1,noAlmostChargingTimeCall2));
	BoolAnd enoughCredit1call_and_noAlmostChargingTimeCall1_and_IsAlmostCharginTimeCall2 = 
			new BoolAnd(enoughCredit1call, new BoolAnd(noAlmostChargingTimeCall1,isAlmostChargingTimeCall2));			
	BoolAnd enoughCredit1call_and_notEnoughCredit2calls_and_IsAlmostChargingTimeCall1_and_IsAlmostCharginTimeCall2 = 
			new BoolAnd(enoughCredit1call_and_notEnoughCredit2calls, new BoolAnd(isAlmostChargingTimeCall1,isAlmostChargingTimeCall2));
	
	BoolAnd notEnoughCredit_and_isCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, isChargingTimeCall1);
	BoolAnd notEnoughCredit_and_isCharginTimeCall2 = new BoolAnd(notEnoughCredit1call, isChargingTimeCall2);
	BoolAnd enoughCredit_and_isCharginTimeCall1 = new BoolAnd(enoughCredit1call, isChargingTimeCall1);
	BoolAnd enoughCredit_and_isCharginTimeCall2 = new BoolAnd(enoughCredit1call, isChargingTimeCall2);
	
	BoolAnd enoughCredit1call_and_IsCharginTimeCall1_and_noChargingTimeCall2 = 
			new BoolAnd(enoughCredit1call, new BoolAnd(isChargingTimeCall1,noChargingTimeCall2));
	BoolAnd enoughCredit1call_and_noChargingTimeCall1_and_IsCharginTimeCall2 = 
			new BoolAnd(enoughCredit1call, new BoolAnd(noChargingTimeCall1,isChargingTimeCall2));
	
	///////////////////////////////////////////////////////////
	// Assignments
	
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	Assign<Integer> charge = new Assign<>(credit, consumeCredit1call);
	Assign<Integer> charge2 = new Assign<>(credit, consumeCredit2calls);
	Assign<Integer> timeCall1Pass = new Assign<>(timeCall1, timeCall1Increase);
	Assign<Integer> timeCall2Pass = new Assign<>(timeCall2, timeCall2Increase);
	Assign<Integer> rechargeTimeCall1Pass = new Assign<>(timeCall1, rechargeTimeCall1Increase);
	Assign<Integer> rechargeTimeCall2Pass = new Assign<>(timeCall2, rechargeTimeCall2Increase);
	Assign<Integer> timeCall1Reset = new Assign<>(timeCall1, timeZero);
	Assign<Integer> timeCall2Reset = new Assign<>(timeCall2, timeZero);
	
	Assign<Integer> timeInternalDelayRechargeVal1 = new Assign<>(timeCall1, timeInternalDelayCall1Increase);
	Assign<Integer> timeInternalDelayRechargeVal2 = new Assign<>(timeCall2, timeInternalDelayCall2Increase);

	Assign<Integer> timeInternalDelayCall1 = new Assign<>(timeCall1, timeInternalDelayCall1Increase);
	Assign<Integer> timeInternalDelayCall2 = new Assign<>(timeCall2, timeInternalDelayCall2Increase);
	
	
	///////////////////////////////////////////////////////////
	// Input parameters encoding SAFAX action
	
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
	
	EFSMParameter in_par_stop_call_1 = new EFSMParameter(
			new Var<String>("type", "STOP_REQUEST"),
			new Var<String>("params::requestId", "call-1")
			);

	EFSMParameter in_no_input = new EFSMParameter(
			new Var<String>("internal", "")			
			);

	EFSMParameter in_par_time_pass = new EFSMParameter(
			new Var<String>("type", "DELAY"),
			new Var<String>("params::delayMs", String.valueOf(timeStepVal))			
			);
	
	EFSMParameter in_par_call_2 = new EFSMParameter(
			new Var<String>("type", "EVAL"),
			new Var<String>("params::request", "CALL"),
			new Var<String>("id", "call-2")
			);
	
	EFSMParameter in_par_recharge_time = new EFSMParameter(
			new Var<String>("type", "DELAY"),
			new Var<String>("params::delayMs", String.valueOf(timeRechargeVal))			
			);
	
	EFSMParameter in_par_stop_call_2 = new EFSMParameter(
			new Var<String>("type", "STOP_REQUEST"),
			new Var<String>("params::requestId", "call-2")
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
		t1.setGuard(new EFSMGuard(notEnoughCredit1call));
		Var<Enum> t1Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t1.setInParameter(in_par_call_1);
		t1.setOutParameter(new EFSMParameter(t1Out));
		phoneCallEFSMBuilder.withTransition(NoCall, NoCall, t1);		

		// t2: NoCall - start -> Calling1
		EFSMTransition t2 = new EFSMTransition();
		t2.setGuard(new EFSMGuard(enoughCredit1call));
		t2.setOp(new EFSMOperation(charge, timeCall1Reset, timeInternalDelayCall1 ));
		Var<Enum> t2Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t2.setInParameter(in_par_call_1);
		t2.setOutParameter(new EFSMParameter(t2Out));
		phoneCallEFSMBuilder.withTransition(NoCall, Calling1, t2);

		// t3: Calling1 - endCall1 -> NoCall
		EFSMTransition t3 = new EFSMTransition();
		t3.setGuard(new EFSMGuard(noAlmostChargingTimeCall1));
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
		t5.setGuard(new EFSMGuard(noAlmostChargingTimeCall1));
		t5.setOp(new EFSMOperation(timeCall1Pass));
		Var<Enum> t5Out = new Var<Enum>("action", uconEvents.SKIP);
		t5.setInParameter(in_par_time_pass);
		t5.setOutParameter(new EFSMParameter(t5Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t5);	
		
		// t6: Calling1 - charge -> Calling1
		EFSMTransition t6 = new EFSMTransition();
		t6.setGuard(new EFSMGuard(enoughCredit_and_isCharginTimeCall1));
		t6.setOp(new EFSMOperation(timeCall1Reset, charge ));
		Var<Enum> t6Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t6.setInParameter(in_no_input);
		t6.setOutParameter(new EFSMParameter(t6Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t6);
		
		// t7: Calling1 - start call 2 fail -> Calling1
		EFSMTransition t7 = new EFSMTransition();
		t7.setGuard(new EFSMGuard(notEnoughCredit_and_notIsAlmostCharginTimeCall1)); 		
		Var<Enum> t7Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t7.setInParameter(in_par_call_2);
		t7.setOutParameter(new EFSMParameter(t7Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1, t7);
		
		// t8: Calling1 - recharge -> RechargingWhileCalling1
		EFSMTransition t8 = new EFSMTransition();
		t8.setGuard(new EFSMGuard(noAlmostChargingTimeCall1));
		t8.setOp(new EFSMOperation(recharge, timeInternalDelayRechargeVal1));
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
		t10.setGuard(new EFSMGuard(enoughCredit_and_notIsAlmostCharginTimeCall1));
		t10.setOp(new EFSMOperation(charge, timeCall2Reset, timeInternalDelayCall1));
		Var<Enum> t10Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t10.setInParameter(in_par_call_2);
		t10.setOutParameter(new EFSMParameter(t10Out));
		phoneCallEFSMBuilder.withTransition(Calling1, Calling1and2, t10);

		// t11: Calling1 - endCall2 -> Calling1and2
		EFSMTransition t11 = new EFSMTransition();
		t11.setGuard(new EFSMGuard(noAlmostChargingTimeCall1)); 
		t11.setOp(new EFSMOperation(timeCall2Reset, timeInternalDelayCall1));
		Var<Enum> t11Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t11.setInParameter(in_par_stop_call_2);
		t11.setOutParameter(new EFSMParameter(t11Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1, t11);

		// t12: Calling1and2 - timeCall1pass and timeCall2pass -> Calling1and2
		EFSMTransition t12 = new EFSMTransition();
		t12.setGuard(new EFSMGuard(noAlmostChargingTimeCall1_and_noAlmostChargingTimeCall2));
		t12.setOp(new EFSMOperation(timeCall1Pass, timeCall2Pass));
		Var<Enum> t12Out = new Var<Enum>("action", uconEvents.SKIP);
		t12.setInParameter(in_par_time_pass);
		t12.setOutParameter(new EFSMParameter(t12Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t12);

		// t13: Calling1and2 - charge 1 -> Calling1and2
		EFSMTransition t13 = new EFSMTransition();
		t13.setGuard(new EFSMGuard(enoughCredit1call_and_IsCharginTimeCall1_and_noChargingTimeCall2));
		t13.setOp(new EFSMOperation(charge, timeCall1Reset));	
		Var<Enum> t13Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t13.setInParameter(in_no_input);
		t13.setOutParameter(new EFSMParameter(t13Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t13);

		// t14: Calling1and2 - charge 2 -> Calling1and2
		EFSMTransition t14 = new EFSMTransition();
		t14.setGuard(new EFSMGuard(enoughCredit1call_and_noChargingTimeCall1_and_IsCharginTimeCall2));
		t14.setOp(new EFSMOperation(charge, timeCall2Reset));	
		Var<Enum> t14Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t14.setInParameter(in_no_input);
		t14.setOutParameter(new EFSMParameter(t14Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t14);

		// CANNOT HAPPEN
		// t15: Calling1and2 - charge 1 and 2 -> Calling1and2
//		EFSMTransition t15 = new EFSMTransition();
//		t15.setGuard(new EFSMGuard(enoughCredit2_and_IsChargingTimeCall1_and_IsCharginTimeCall2));
//		t15.setOp(new EFSMOperation(charge2, timeCall1Reset, timeCall2Reset));	
//		Var<Enum> t15Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
//		t15.setInParameter(in_no_input);
//		t15.setOutParameter(new EFSMParameter(t15Out));
//		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling1and2, t15);

		// t16: Calling1and2 - recharge -> RechargingWhileCalling1and2
		EFSMTransition t16 = new EFSMTransition();
		t16.setGuard(new EFSMGuard(noAlmostChargingTimeCall1_and_noAlmostChargingTimeCall2));
		t16.setOp(new EFSMOperation(recharge, timeInternalDelayRechargeVal1, timeInternalDelayRechargeVal2));
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

		// CANNOT HAPPEN
		// t18_1: Calling1and2 - noCredit for 2  -> ClosingCall2
//		EFSMTransition t18_1 = new EFSMTransition();
//		t18_1.setGuard(new EFSMGuard(enoughCredit1call_and_notEnoughCredit2calls_and_IsChargingTimeCall1_and_IsCharginTimeCall2));
//		t18_1.setOp(new EFSMOperation(timeCall1Reset, charge));
//		Var<Enum> t18_1Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
//		t18_1.setInParameter(in_no_input);
//		t18_1.setOutParameter(new EFSMParameter(t18_1Out));
//		phoneCallEFSMBuilder.withTransition(Calling1and2, ClosingCall2, t18_1);
		
		// CANNOT HAPPEN without t18_1
		// t18: ClosingCall2 - noCredit for 2  -> Calling1
//		EFSMTransition t18 = new EFSMTransition();
//		t18.setOp(new EFSMOperation(timeCall1Reset));
//		Var<Enum> t18Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
//		t18.setInParameter(in_no_input);
//		t18.setOutParameter(new EFSMParameter(t18Out));
//		phoneCallEFSMBuilder.withTransition(ClosingCall2, Calling1, t18);
		
		// t18: Calling1and2 - end call 1 -> Calling2
		EFSMTransition t18 = new EFSMTransition();
		t18.setGuard(new EFSMGuard(noAlmostChargingTimeCall1_and_noAlmostChargingTimeCall2));
		t18.setOp(new EFSMOperation(timeCall1Reset, timeInternalDelayCall2));
		Var<Enum> t18Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t18.setInParameter(in_par_stop_call_1);
		t18.setOutParameter(new EFSMParameter(t18Out));
		phoneCallEFSMBuilder.withTransition(Calling1and2, Calling2, t18);
		
		// t19: Calling2  - start call 1 -> Calling1and2
		EFSMTransition t19 = new EFSMTransition();
		t19.setGuard(new EFSMGuard(enoughCredit_and_notIsAlmostCharginTimeCall2));
		t19.setOp(new EFSMOperation(charge, timeInternalDelayCall2, timeInternalDelayCall1));
		Var<Enum> t19Out = new Var<Enum>("action", uconEvents.EVAL_PERMIT);
		t19.setInParameter(in_par_call_1);
		t19.setOutParameter(new EFSMParameter(t19Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling1and2, t19);
		
		// t20: Calling2  - start call 1 no money -> Calling2
		EFSMTransition t20 = new EFSMTransition();
		t20.setGuard(new EFSMGuard(notEnoughCredit_and_notIsAlmostCharginTimeCall2));
		Var<Enum> t20Out = new Var<Enum>("action", uconEvents.EVAL_DENY);
		t20.setInParameter(in_par_call_1);
		t20.setOutParameter(new EFSMParameter(t20Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t20);
		
		// t21: Calling2  - charge  -> Calling2
		EFSMTransition t21 = new EFSMTransition();
		t21.setGuard(new EFSMGuard(enoughCredit_and_isCharginTimeCall2));
		t21.setOp(new EFSMOperation(charge, timeCall2Reset));
		Var<Enum> t21Out = new Var<Enum>("action", uconEvents.RE_EVAL_PERMIT);
		t21.setInParameter(in_no_input);
		t21.setOutParameter(new EFSMParameter(t21Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t21);
		
		// t22: Calling2  - time pass  -> Calling2
		EFSMTransition t22 = new EFSMTransition();
		t22.setGuard(new EFSMGuard(noAlmostChargingTimeCall2));
		t22.setOp(new EFSMOperation(timeCall2Pass));
		Var<Enum> t22Out = new Var<Enum>("action", uconEvents.SKIP);
		t22.setInParameter(in_par_time_pass);
		t22.setOutParameter(new EFSMParameter(t22Out));
		phoneCallEFSMBuilder.withTransition( Calling2, Calling2, t22);
		
		// t23: Calling2  - stop call2  -> NoCall
		EFSMTransition t23 = new EFSMTransition();
		t23.setGuard(new EFSMGuard(noAlmostChargingTimeCall2));
		t23.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t23Out = new Var<Enum>("action", uconEvents.REQUEST_STOPPED);
		t23.setInParameter(in_par_stop_call_2);
		t23.setOutParameter(new EFSMParameter(t23Out));
		phoneCallEFSMBuilder.withTransition(Calling2, NoCall, t23);
		
		// t24: Calling2 - noCredit -> NoCall
		EFSMTransition t24 = new EFSMTransition();
		t24.setGuard(new EFSMGuard(notEnoughCredit_and_isCharginTimeCall2));
		t24.setOp(new EFSMOperation(timeCall2Reset));
		Var<Enum> t24Out = new Var<Enum>("action", uconEvents.RE_EVAL_DENY);
		t24.setInParameter(in_no_input);
		t24.setOutParameter(new EFSMParameter(t24Out));
		phoneCallEFSMBuilder.withTransition(Calling2, NoCall, t24);
		
		return phoneCallEFSMBuilder.build(NoCall, context, null);
	
	}

}
