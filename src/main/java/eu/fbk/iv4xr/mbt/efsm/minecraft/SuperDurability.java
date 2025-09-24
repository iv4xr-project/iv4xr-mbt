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
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.IfThenElseOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

public class SuperDurability implements EFSMProvider {
    public static final int NUMBER_OF_BLOCKS = 64 * 3;
    public static final double DISTANCE = 2;
    public static final int STONEP_DURABILITY = 131;
    public static final int GOLDP_DURABILITY = 32;
    public static final int IRONP_DURABILITY = 250;

    public EFSMState stoneState = new EFSMState("stone");
    public EFSMState stonePlace = new EFSMState("place_stone");

    public EFSMState woodState = new EFSMState("wood");
    public EFSMState woodPlace = new EFSMState("place_wood");

    public EFSMState goldPick = new EFSMState("golden_pickaxe");
    public EFSMState stonePick = new EFSMState("stone_pickaxe");
    public EFSMState ironPick = new EFSMState("iron_pickaxe");

    // helper states
    public EFSMState start = new EFSMState("start");
    public EFSMState selected = new EFSMState("selected");
    public EFSMState durabilityCalc = new EFSMState("durability");

    // variables
    public Var<Integer> stonePDamage = new Var<>("inventory^stonep::damage", 0);
    public Var<Integer> ironPDamage = new Var<>("inventory^ironp::damage", 0);
    public Var<Integer> goldPDamage = new Var<>("inventory^goldp::damage", 0);

    public Var<Integer> stoneCount = new Var<>("inventory^stonec::count", NUMBER_OF_BLOCKS);
    public Var<Integer> woodCount = new Var<>("inventory^woodc::count", NUMBER_OF_BLOCKS);

    public Var<Integer> durabilityCost = new Var<>("cost", 1);
    public Var<String> selectedItem = new Var<>("select::item", "stone_pickaxe");
    public Var<Integer> selectedItemInt = new Var<>("selected_item", 0);

    public EFSM getModel() {
        EFSMBuilder DurabilitypEFSMBuilder = new EFSMBuilder(EFSM.class);
        EFSMContext DurabilitypCtx = new EFSMContext(stonePDamage, ironPDamage, goldPDamage, stoneCount, woodCount,
                durabilityCost, selectedItem, selectedItemInt);

        EFSMTransition t1 = new EFSMTransition("t1");
        t1.setGuard(new EFSMGuard(new IntLess(stonePDamage, new Const<Integer>(STONEP_DURABILITY))));
        t1.setInParameter(new EFSMParameter(new Var<String>("select::item", "stone_pickaxe")));
        t1.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("stone_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(0))));
        DurabilitypEFSMBuilder.withTransition(start, stonePick, t1);

        EFSMTransition t1r = new EFSMTransition("t1r");
        DurabilitypEFSMBuilder.withTransition(stonePick, selected, t1r);

        EFSMTransition t2 = new EFSMTransition("t2");
        t2.setGuard(new EFSMGuard(new IntLess(ironPDamage, new Const<Integer>(IRONP_DURABILITY))));
        // t2.setInParameter(new EFSMParameter(new Var<String>("select::item",
        // "iron_pickaxe")));
        t2.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("iron_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(1))));
        DurabilitypEFSMBuilder.withTransition(start, ironPick, t2);

        EFSMTransition t2r = new EFSMTransition("t2r");
        DurabilitypEFSMBuilder.withTransition(ironPick, selected, t2r);

        EFSMTransition t3 = new EFSMTransition("t3");
        t3.setGuard(new EFSMGuard(new IntLess(goldPDamage, new Const<Integer>(GOLDP_DURABILITY))));
        // t3.setInParameter(new EFSMParameter(new Var<String>("select::item",
        // "golden_pickaxe")));
        t3.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("golden_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(2))));
        DurabilitypEFSMBuilder.withTransition(start, goldPick, t3);

        EFSMTransition t3r = new EFSMTransition("t3r");
        DurabilitypEFSMBuilder.withTransition(goldPick, selected, t3r);

        EFSMTransition t4 = new EFSMTransition("t4");
        t4.setGuard(new EFSMGuard(new IntGreat(stoneCount, new Const<Integer>(0))));
        t4.setInParameter(new EFSMParameter(
                new Var<Double>("move_to::distance", DISTANCE),
                new Var<String>("select::item", "stone"),
                new Var<String>("place::face", "top")));
        t4.setOutParameter(new EFSMParameter(new Var<String>("inventory^stonec::item", "stone"), stoneCount));
        t4.setOp(new EFSMOperation(new Assign<Integer>(stoneCount, new IntSubt(stoneCount, new Const<Integer>(1))),
                new Assign<Integer>(durabilityCost, new Const<Integer>(1))));
        DurabilitypEFSMBuilder.withTransition(selected, stonePlace, t4);

        EFSMTransition t4b = new EFSMTransition("t4b");
        t4b.setInParameter(new EFSMParameter(
                selectedItem,
                new Var<String>("break", "")));
        DurabilitypEFSMBuilder.withTransition(stonePlace, stoneState, t4b);

        EFSMTransition t4r = new EFSMTransition("t4r");
        DurabilitypEFSMBuilder.withTransition(stoneState, durabilityCalc, t4r);

        EFSMTransition t5 = new EFSMTransition("t5");
        t5.setGuard(new EFSMGuard(new IntGreat(woodCount, new Const<Integer>(0))));
        t5.setInParameter(new EFSMParameter(
                new Var<Double>("move_to::distance", DISTANCE),
                new Var<String>("select::item", "acacia_log"),
                new Var<String>("place::face", "top")));
        // t5.setOutParameter(new EFSMParameter(woodCount));
        t5.setOp(new EFSMOperation(new Assign<Integer>(woodCount, new IntSubt(woodCount, new Const<Integer>(1))),
                new Assign<Integer>(durabilityCost, new Const<Integer>(1))));
        DurabilitypEFSMBuilder.withTransition(selected, woodPlace, t5);

        EFSMTransition t5b = new EFSMTransition("t5b");
        t5b.setInParameter(new EFSMParameter(
                selectedItem,
                new Var<String>("break", "")));
        DurabilitypEFSMBuilder.withTransition(woodPlace, woodState, t5b);

        EFSMTransition t5r = new EFSMTransition("t5r");
        DurabilitypEFSMBuilder.withTransition(woodState, durabilityCalc, t5r);

        EFSMTransition t6s = new EFSMTransition("t6s");
        t6s.setGuard(new EFSMGuard(new IntEq(selectedItemInt, new Const<Integer>(0))));
        t6s.setOp(new EFSMOperation(new Assign<Integer>(stonePDamage, new IntSum(stonePDamage, durabilityCost))));
        t6s.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^stonep::item", "stone_pickaxe"), stonePDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6s);

        EFSMTransition t6i = new EFSMTransition("t6i");
        t6i.setGuard(new EFSMGuard(new IntEq(selectedItemInt, new Const<Integer>(1))));
        t6i.setOp(new EFSMOperation(new Assign<Integer>(ironPDamage, new IntSum(ironPDamage, durabilityCost))));
        t6i.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^ironp::item", "iron_pickaxe"), ironPDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6i);

        EFSMTransition t6g = new EFSMTransition("t6i");
        t6g.setGuard(new EFSMGuard(new IntEq(selectedItemInt, new Const<Integer>(2))));
        t6g.setOp(new EFSMOperation(new Assign<Integer>(goldPDamage, new IntSum(goldPDamage, durabilityCost))));
        t6g.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^goldp::item", "golden_pickaxe"), goldPDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6g);

        return DurabilitypEFSMBuilder.build(start, DurabilitypCtx, null);
    }

}
