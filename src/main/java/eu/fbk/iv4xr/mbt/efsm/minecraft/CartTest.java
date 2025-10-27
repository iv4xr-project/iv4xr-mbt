package eu.fbk.iv4xr.mbt.efsm.minecraft;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMProvider;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;


public class CartTest implements EFSMProvider {

	public EFSMState initialState = new EFSMState("init");
	public EFSMState start = new EFSMState("start");
	public EFSMState railSwitch = new EFSMState("switch");
	public EFSMState poweredRail = new EFSMState("railpower");
	public EFSMState destinationPowered = new EFSMState("detector_powered");
	public EFSMState destinationUnpwered = new EFSMState("detector_unpowered");

    public Var<Boolean> switchState = new Var<Boolean>("switch", false);
    public Var<Boolean> poweredState = new Var<Boolean>("powered_rail_state", false);

    public EFSM getModel() {
        EFSMBuilder CartEFSMBuilder = new EFSMBuilder(EFSM.class);
        EFSMContext cartCtx = new EFSMContext(switchState, poweredState);

        EFSMOperation toggleSwitch = new EFSMOperation(new Assign<Boolean>(switchState, new BoolNot(switchState)));
        EFSMOperation toggleRailPower = new EFSMOperation(new Assign<Boolean>(poweredState, new BoolNot(poweredState)));

        EFSMGuard switchOn = new EFSMGuard(switchState);
        Exp<Boolean> switchOff = new BoolNot(switchState);

        EFSMTransition t_1 = new EFSMTransition();
        t_1.setId("t1");
        t_1.setOp(toggleRailPower);
        t_1.setGuard( new EFSMGuard(switchOff));
        t_1.setInParameter(new EFSMParameter(new Var<String>("click", "")));
        CartEFSMBuilder.withTransition(initialState, poweredRail, t_1);

        EFSMTransition t_2 = new EFSMTransition();
        t_2.setId("t2");
        t_2.setOp(toggleSwitch);
        t_2.setInParameter(new EFSMParameter(new Var<String>("click", "")));
        CartEFSMBuilder.withTransition(initialState, railSwitch, t_2);

        EFSMTransition t_3 = new EFSMTransition();
        t_3.setId("t3");
        CartEFSMBuilder.withTransition(railSwitch, initialState, t_3);
        
        EFSMTransition t_4 = new EFSMTransition();
        t_4.setId("t4");
        CartEFSMBuilder.withTransition(poweredRail, initialState, t_4);

        EFSMTransition t_5 = new EFSMTransition();
        t_5.setId("t5");
        t_5.setInParameter(new EFSMParameter(new Var<String>("click", ""), new Var<Integer>("wait::ticks", 40)));
        CartEFSMBuilder.withTransition(initialState, start, t_5);
        
        EFSMTransition t_6 = new EFSMTransition();
        t_6.setId("t6");
        t_6.setGuard(new EFSMGuard( new BoolAnd(switchOff, poweredState)));
        t_6.setOutParameter(new EFSMParameter(new Var<String>("block::expected", "detector_rail[powered=true]")));
        CartEFSMBuilder.withTransition(start, destinationPowered, t_6);

        EFSMTransition t_7 = new EFSMTransition();
        t_7.setId("t7");
        t_7.setGuard(switchOn);
        t_7.setOutParameter(new EFSMParameter(new Var<String>("block::expected", "detector_rail[powered=true]")));
        CartEFSMBuilder.withTransition(start, destinationUnpwered, t_7);

        EFSMTransition t_8 = new EFSMTransition();
        t_8.setId("t8");
        t_8.setGuard(new EFSMGuard( new BoolAnd(switchOff, new BoolNot(poweredState))));
        t_8.setOutParameter(new EFSMParameter(new Var<String>("block::expected", "detector_rail[powered=false]")));
        CartEFSMBuilder.withTransition(start, destinationPowered, t_8);


        return CartEFSMBuilder.build(initialState, cartCtx, null);
    }
}
