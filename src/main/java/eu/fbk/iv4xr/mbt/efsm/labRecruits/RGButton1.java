package eu.fbk.iv4xr.mbt.efsm.labRecruits;

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
import eu.fbk.iv4xr.mbt.efsm.exp.IfThenElseOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;

public class RGButton1 implements EFSMProvider {

    // buttons
    public EFSMState br = new EFSMState("cbr");
    public EFSMState bg = new EFSMState("cbg");
    public EFSMState bb = new EFSMState("cbb");
    public EFSMState cs1 = new EFSMState("cs1");

    // Screen
    // public EFSMState cs = new EFSMState("CS1");

    // context variables
    public Var<Boolean> br_state = new Var<>("cbr", false);
    public Var<Boolean> bg_state = new Var<>("cbg", false);
    public Var<Boolean> bb_state = new Var<>("cbb", false);

    public Var<String> screen_color = new Var<>("cs1", "#000000");

    // input variables
    public Var<LRActions> action = new Var<LRActions>("action", null);

    public EFSM getModel() {
        EFSMBuilder RGButton1EFSMBuilder = new EFSMBuilder(EFSM.class);
        EFSMContext RGButton1ctx = new EFSMContext(br_state, bg_state, bb_state, screen_color);
        LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();

        // operations
        Const<String> black = new Const<>("#000000");
        
        BoolNot not_br = new BoolNot(br_state);
        BoolNot not_bg = new BoolNot(bg_state);
        BoolNot not_bb = new BoolNot(bb_state);
        
        Assign<Boolean> br_toggle_assign = new Assign<>(br_state, not_br);
        Assign<String> screen_set_red = new Assign<>(screen_color, new IfThenElseOp<>(br_state, new Const<>("#FF0000"), black));

        Assign<Boolean> bg_toggle_assign = new Assign<>(bg_state, not_bg);
        Assign<String> screen_set_green = new Assign<>(screen_color, new IfThenElseOp<>(bg_state, new Const<>("#00FF00"), black));

        Assign<Boolean> bb_toggle_assign = new Assign<>(bb_state, not_bb);
        Assign<String> screen_set_blue = new Assign<>(screen_color, new IfThenElseOp<>(bb_state, new Const<>("#0000FF"), black));


        EFSMOperation br_toggle_var = new EFSMOperation(br_toggle_assign, screen_set_red);
        EFSMOperation bg_toggle_var = new EFSMOperation(bg_toggle_assign, screen_set_green);
        EFSMOperation bb_toggle_var = new EFSMOperation(bb_toggle_assign, screen_set_blue);

        // toggle input parameter
        Var<LRActions> toggleVar = new Var<>("toggle", LRActions.TOGGLE);
        EFSMParameter inputParToggle = new EFSMParameter(toggleVar);

        // toggle input parameter
        Var<LRActions> exploreVar = new Var<>("explore", LRActions.EXPLORE);
        EFSMParameter inputParExplore = new EFSMParameter(exploreVar);

        // screen color parameter check
        EFSMParameter inputParColor = new EFSMParameter(screen_color);

        
        // Button toggles
        // t_1: toggle br
        EFSMTransition t_1 = new EFSMTransition();
        t_1.setOp(br_toggle_var);
        t_1.setInParameter(inputParToggle);
        t_1.setId("t1");
        RGButton1EFSMBuilder.withTransition(br, br, t_1);

        // t_2: toggle bg
        EFSMTransition t_2 = new EFSMTransition();
        t_2.setOp(bg_toggle_var);
        t_2.setInParameter(inputParToggle);
        t_2.setId("t2");
        RGButton1EFSMBuilder.withTransition(bg, bg, t_2);

        // t_3: toggle bb
        EFSMTransition t_3 = new EFSMTransition();
        t_3.setOp(bb_toggle_var);
        t_3.setInParameter(inputParToggle);
        t_3.setId("t3");
        RGButton1EFSMBuilder.withTransition(bb, bb, t_3);


        // Screen color check
        // t_4: bb-> cs1 check
        EFSMTransition t_4 = new EFSMTransition();
        t_4.setInParameter(inputParColor);
        t_4.setId("t4");
        RGButton1EFSMBuilder.withTransition(bb, cs1, t_4);

        // t_5: bg-> cs1 check
        EFSMTransition t_5 = new EFSMTransition();
        t_5.setInParameter(inputParColor);
        t_5.setId("t5");
        RGButton1EFSMBuilder.withTransition(bg, cs1, t_5);

        // t_6: br-> cs1 check
        EFSMTransition t_6 = new EFSMTransition();
        t_6.setInParameter(inputParColor);
        t_6.setId("t6");
        RGButton1EFSMBuilder.withTransition(br, cs1, t_6);


        // Moving to different buttons
        // t_7 cs1 -> bb
        EFSMTransition t_7 = new EFSMTransition();
        t_7.setInParameter(inputParExplore);
        t_7.setId("t7");
        t_7.setGuard(new EFSMGuard(new BoolAnd (not_br, not_bg)));
        RGButton1EFSMBuilder.withTransition(cs1, bb, t_7);

        // t_8 cs1 -> bg
        EFSMTransition t_8 = new EFSMTransition();
        t_8.setInParameter(inputParExplore);
        t_8.setId("t8");
        t_8.setGuard(new EFSMGuard(new BoolAnd (not_br, not_bb)));
        RGButton1EFSMBuilder.withTransition(cs1, bg, t_8);

        // t_9 cs1 -> br
        EFSMTransition t_9 = new EFSMTransition();
        t_9.setInParameter(inputParExplore);
        t_9.setId("t9");
        t_9.setGuard(new EFSMGuard(new BoolAnd (not_bb, not_bg)));
        RGButton1EFSMBuilder.withTransition(cs1, br, t_9);
        
        return RGButton1EFSMBuilder.build(bb, RGButton1ctx, lrParameterGenerator);
    }

    public String getAnmlInstance() {
        return "";
    }
}