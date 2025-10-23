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
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

public class SuperDurability implements EFSMProvider {
        public static final int NUMBER_OF_BLOCKS = 64 * 3;
        public static final double DISTANCE = 2;
        public static final int STONEP_DURABILITY = 131;
        public static final int GOLDP_DURABILITY = 32;
        public static final int IRONP_DURABILITY = 250;
        public static final int DIAMONDP_DURABILITY = 1561;

        public EFSMState mob = new EFSMState("mob");

        public EFSMState stoneState = new EFSMState("stone");
        public EFSMState stonePlace = new EFSMState("place_stone");

        public EFSMState woodState = new EFSMState("wood");
        public EFSMState woodPlace = new EFSMState("place_wood");

        // helper states
        public EFSMState start = new EFSMState("start");
        public EFSMState selected = new EFSMState("selected");
        public EFSMState durabilityCalc = new EFSMState("durability");

        // variables
        public Var<Integer> stonePDamage = new Var<>("inventory^stonep::damage", 0);
        public Var<Integer> ironPDamage = new Var<>("inventory^ironp::damage", 0);
        public Var<Integer> goldPDamage = new Var<>("inventory^goldp::damage", 0);
        public Var<Integer> diamondPDamage = new Var<>("inventory^diamondp::damage", 0);

        public Var<Integer> stoneCount = new Var<>("inventory^stonec::count", NUMBER_OF_BLOCKS);
        public Var<Integer> woodCount = new Var<>("inventory^woodc::count", NUMBER_OF_BLOCKS);

        public Var<Integer> durabilityCost = new Var<>("cost", 1);
        public Var<String> selectedItem = new Var<>("select::item", "stone_pickaxe");
        public Var<Integer> selectedItemInt = new Var<>("selected_item", 0);

    public EFSM getModel() {
        EFSMBuilder DurabilitypEFSMBuilder = new EFSMBuilder(EFSM.class);
        EFSMContext DurabilitypCtx = new EFSMContext(stonePDamage, ironPDamage, goldPDamage, diamondPDamage, stoneCount, woodCount,
                durabilityCost, selectedItem, selectedItemInt);

        EFSMTransition t1s = new EFSMTransition("t1s");
        t1s.setGuard(new EFSMGuard(new IntLess(stonePDamage, new Const<Integer>(STONEP_DURABILITY))));
        t1s.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("stone_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(0))));
        DurabilitypEFSMBuilder.withTransition(start, selected, t1s);

        EFSMTransition t1i = new EFSMTransition("t1i");
        t1i.setGuard(new EFSMGuard(new IntLess(ironPDamage, new Const<Integer>(IRONP_DURABILITY))));
        t1i.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("iron_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(1))));
        DurabilitypEFSMBuilder.withTransition(start, selected, t1i);

        EFSMTransition t1g = new EFSMTransition("t1g");
        t1g.setGuard(new EFSMGuard(new IntLess(goldPDamage, new Const<Integer>(GOLDP_DURABILITY))));
        t1g.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("golden_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(2))));
        DurabilitypEFSMBuilder.withTransition(start, selected, t1g);


        EFSMTransition t1d = new EFSMTransition("t1d");
        t1d.setGuard(new EFSMGuard(new IntLess(diamondPDamage, new Const<Integer>(DIAMONDP_DURABILITY))));
        t1d.setOp(new EFSMOperation(new Assign<>(selectedItem, new Const<String>("diamond_pickaxe")),
                new Assign<>(selectedItemInt, new Const<Integer>(3))));
        DurabilitypEFSMBuilder.withTransition(start, selected, t1d);


        
        // attacking a mob should take 2 durability
        EFSMTransition t2 = new EFSMTransition("t2");
        t2.setInParameter(new EFSMParameter(
                new Var<Double>("move_to::distance", DISTANCE),
                selectedItem,
                new Var<Integer>("wait::ticks", 10),
                new Var<String>("attack", "")
        ));
        t2.setOp(new EFSMOperation(new Assign<Integer>(durabilityCost, new Const<Integer>(2))));
        DurabilitypEFSMBuilder.withTransition(selected, mob, t2);

        EFSMTransition t2r = new EFSMTransition("t2r");
        DurabilitypEFSMBuilder.withTransition(mob, durabilityCalc, t2r);

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
                new Var<Boolean>("break::expect_result", true)));
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
                new Var<Boolean>("break::expect_result", true)));
        DurabilitypEFSMBuilder.withTransition(woodPlace, woodState, t5b);

        EFSMTransition t5r = new EFSMTransition("t5r");
        DurabilitypEFSMBuilder.withTransition(woodState, durabilityCalc, t5r);

        // durability calc

        Exp<Integer> stone_durability = new IntSum(stonePDamage, durabilityCost);
        Exp<Boolean> stone_selected = new IntEq(selectedItemInt, new Const<Integer>(0));
        Exp<Boolean> stone_is_not_broken = new IntLess(stone_durability, new Const<Integer>(STONEP_DURABILITY));
        Exp<Boolean> stone_is_broken =  new IntGreat(stone_durability, new Const<Integer>(STONEP_DURABILITY - 1));        

        EFSMTransition t6s = new EFSMTransition("t6s");
        t6s.setGuard(new EFSMGuard(new BoolAnd(stone_selected, stone_is_not_broken)));
        t6s.setOp(new EFSMOperation(new Assign<Integer>(stonePDamage, stone_durability)));
        t6s.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^stonep::item", "stone_pickaxe"), stonePDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6s);

        EFSMTransition t6sb = new EFSMTransition("t6sb");
        t6sb.setGuard(new EFSMGuard( new BoolAnd(stone_selected, stone_is_broken)));
        t6sb.setOp(new EFSMOperation(new Assign<Integer>(stonePDamage, stone_durability)));
        t6sb.setOutParameter(
                new EFSMParameter(new Var<String>("inventory::item", "stone_pickaxe"), 
                                  new Var<Integer>("inventory::count", 0)
                                ));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6sb);

        Exp<Integer> iron_durability = new IntSum(ironPDamage, durabilityCost);
        Exp<Boolean> iron_selected = new IntEq(selectedItemInt, new Const<Integer>(1));
        Exp<Boolean> iron_is_not_broken = new IntLess(iron_durability, new Const<Integer>(IRONP_DURABILITY));
        Exp<Boolean> iron_is_broken =  new IntGreat(iron_durability, new Const<Integer>(IRONP_DURABILITY - 1));        

        EFSMTransition t6i = new EFSMTransition("t6i");
        t6i.setGuard(new EFSMGuard(new BoolAnd(iron_selected, iron_is_not_broken)));
        t6i.setOp(new EFSMOperation(new Assign<Integer>(ironPDamage, iron_durability)));
        t6i.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^ironp::item", "iron_pickaxe"), ironPDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6i);

        EFSMTransition t6ib = new EFSMTransition("t6ib");
        t6ib.setGuard(new EFSMGuard( new BoolAnd(iron_selected, iron_is_broken)));
        t6ib.setOp(new EFSMOperation(new Assign<Integer>(ironPDamage, iron_durability)));
        t6ib.setOutParameter(
                new EFSMParameter(new Var<String>("inventory::item", "iron_pickaxe"), 
                                  new Var<Integer>("inventory::count", 0)
                                ));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6ib);

        Exp<Integer> gold_durability = new IntSum(goldPDamage, durabilityCost);
        Exp<Boolean> gold_selected = new IntEq(selectedItemInt, new Const<Integer>(2));
        Exp<Boolean> gold_is_not_broken = new IntLess(gold_durability, new Const<Integer>(GOLDP_DURABILITY));
        Exp<Boolean> gold_is_broken =  new IntGreat(gold_durability, new Const<Integer>(GOLDP_DURABILITY - 1));        
        
        EFSMTransition t6g = new EFSMTransition("t6g");
        t6g.setGuard(new EFSMGuard( new BoolAnd(gold_selected, gold_is_not_broken)));
        t6g.setOp(new EFSMOperation(new Assign<Integer>(goldPDamage, gold_durability)));
        t6g.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^goldp::item", "golden_pickaxe"), goldPDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6g);

        EFSMTransition t6gb = new EFSMTransition("t6gb");
        t6gb.setGuard(new EFSMGuard( new BoolAnd(gold_selected, gold_is_broken)));
        t6gb.setOp(new EFSMOperation(new Assign<Integer>(goldPDamage, gold_durability)));
        t6gb.setOutParameter(
                new EFSMParameter(new Var<String>("inventory::item", "golden_pickaxe"), 
                                  new Var<Integer>("inventory::count", 0)
                                ));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6gb);

        Exp<Integer> diamond_durability = new IntSum(diamondPDamage, durabilityCost);
        Exp<Boolean> diamond_selected = new IntEq(selectedItemInt, new Const<Integer>(3));
        Exp<Boolean> diamond_is_not_broken = new IntLess(diamond_durability, new Const<Integer>(DIAMONDP_DURABILITY));
        Exp<Boolean> diamond_is_broken =  new IntGreat(diamond_durability, new Const<Integer>(DIAMONDP_DURABILITY - 1));        

        EFSMTransition t6d = new EFSMTransition("t6d");
        t6d.setGuard(new EFSMGuard(new BoolAnd(diamond_selected, diamond_is_not_broken)));
        t6d.setOp(new EFSMOperation(new Assign<Integer>(diamondPDamage, diamond_durability)));
        t6d.setOutParameter(
                new EFSMParameter(new Var<String>("inventory^diamondp::item", "diamond_pickaxe"), diamondPDamage));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6d);

        EFSMTransition t6db = new EFSMTransition("t6db");
        t6db.setGuard(new EFSMGuard( new BoolAnd(diamond_selected, diamond_is_broken)));
        t6db.setOp(new EFSMOperation(new Assign<Integer>(diamondPDamage, iron_durability)));
        t6db.setOutParameter(
                new EFSMParameter(new Var<String>("inventory::item", "diamond_pickaxe"), 
                                  new Var<Integer>("inventory::count", 0)));
        DurabilitypEFSMBuilder.withTransition(durabilityCalc, start, t6db);

        return DurabilitypEFSMBuilder.build(start, DurabilitypCtx, null);
    }

}
