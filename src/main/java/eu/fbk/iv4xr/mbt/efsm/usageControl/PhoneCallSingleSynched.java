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
import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCallDoubleSynched.uconEvents;

public class PhoneCallSingleSynched  implements EFSMProvider{

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
	public EFSMState NoCall = new EFSMState("NoCall");
	public EFSMState RechargingWhileCalling1 = new EFSMState("RechargingWhileCalling1");
	
	///////////////////////////////////////////////////////////
	// EFSM context
	
	// variables
	Var<Integer> credit = new Var<Integer>("credit", initialCreditVal);
	Var<Integer> timeCall1 = new Var<Integer>("time1", 0);
	
	// Context
	EFSMContext context = new EFSMContext(credit, timeCall1);	
	
	
	///////////////////////////////////////////////////////////
	// Expressions
	
	// basic credit checks
	BoolOr enoughCredit1call = new BoolOr(new IntGreat(credit, creditCharge), new IntEq(credit, creditCharge));	
	IntLess notEnoughCredit1call = new IntLess(credit, creditCharge);
	// charging time that account for the margin
	IntLess noAlmostChargingTimeCall1  = new IntLess(timeCall1, new IntSubt(chargeTime,chargeTimeMargin ));
	IntGreat isAlmostChargingTimeCall1  = new IntGreat(timeCall1, new IntSubt(chargeTime, chargeTimeMargin));
	IntGreat isChargingTimeCall1  = new IntGreat(timeCall1, chargeTime);
	
	// credit
	IntSum addCredit = new IntSum(credit, rechargeAmount);
	IntSubt consumeCredit1call = new IntSubt(credit, creditCharge); 
	// time management
	IntSum timeCall1Increase = new IntSum(timeCall1, timeStep);
	IntSum rechargeTimeCall1Increase = new IntSum(timeCall1, timeRecharge);
	IntSum timeInternalDelayCall1Increase  = new IntSum(timeCall1, internalDelayCall);
	IntSum timeInternalDelayRechargeCall1Increase  = new IntSum(timeCall1, internalDelayRecharge);

	// combined expressions
	BoolAnd enoughCredit_and_isAlmostCharginTimeCall1 = new BoolAnd(enoughCredit1call, isAlmostChargingTimeCall1);
	BoolAnd notEnoughCredit_and_isAlmostCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, isAlmostChargingTimeCall1);
	BoolAnd enoughCredit_and_isCharginTimeCall1 = new BoolAnd(enoughCredit1call, isChargingTimeCall1);
	BoolAnd notEnoughCredit_and_isCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, isChargingTimeCall1);
	// BoolAnd enoughCredit_and_notIsCharginTimeCall1 = new BoolAnd(enoughCredit1call, noChargingTimeCall1);
	// BoolAnd notEnoughCredit_and_notIsCharginTimeCall1 = new BoolAnd(notEnoughCredit1call, noChargingTimeCall1);

	///////////////////////////////////////////////////////////
	// Assignments
	
	Assign<Integer> recharge = new Assign<>(credit, addCredit);
	Assign<Integer> charge = new Assign<>(credit, consumeCredit1call);
	Assign<Integer> timeCall1Pass = new Assign<>(timeCall1, timeCall1Increase);
	Assign<Integer> rechargeTimeCall1Pass = new Assign<>(timeCall1, rechargeTimeCall1Increase);
	Assign<Integer> timeCall1Reset = new Assign<>(timeCall1, timeZero);

	Assign<Integer> timeInternalDelayRechargeVal1 = new Assign<>(timeCall1, timeInternalDelayCall1Increase);
	Assign<Integer> timeInternalDelayCall1 = new Assign<>(timeCall1, timeInternalDelayCall1Increase);

	
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
	
	EFSMParameter in_par_recharge_time = new EFSMParameter(
			new Var<String>("type", "DELAY"),
			new Var<String>("params::delayMs", String.valueOf(timeRechargeVal))			
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
		
		return phoneCallEFSMBuilder.build(NoCall, context, null);
	}

}
