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
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleLess;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleSubt;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LRParameterGenerator;

public class DamageCheck implements EFSMProvider {
    public EFSMState start = new EFSMState("start");
    public EFSMState select_sword = new EFSMState("select_sword");
    public EFSMState mob_ref = new EFSMState("mob");
    public EFSMState end_state = new EFSMState("mob^end");

    // variables
    public Var<Double> health = new Var<Double>("entity::health", 100.0);
    public Var<Double> attackDamage = new Var<Double>("attack_damage", 1.0);

    public EFSM getModel() {
        EFSMBuilder DamageEFSMBuilder = new EFSMBuilder(EFSM.class);
        EFSMContext DamageCtx = new EFSMContext(health, attackDamage);
        LRParameterGenerator lrParameterGenerator = new LRParameterGenerator();

        Exp<Boolean> mob_has_heath = new DoubleGreat(health, attackDamage);
        // danger, does not take into account if they are equal
        Exp<Boolean> final_hit = new BoolNot(mob_has_heath);

        EFSMOperation set_stone_sword_dmg = new EFSMOperation(new Assign<Double>(attackDamage, new Const<Double>(5.0)));
        EFSMOperation set_iron_sword_dmg = new EFSMOperation(new Assign<Double>(attackDamage, new Const<Double>(6.0)));
        EFSMOperation set_diamond_sword_dmg = new EFSMOperation(new Assign<Double>(attackDamage, new Const<Double>(7.0)));
        EFSMOperation set_netherite_sword_dmg = new EFSMOperation(new Assign<Double>(attackDamage, new Const<Double>(8.0)));

        EFSMOperation update_health = new EFSMOperation(
                new Assign<Double>(health, new DoubleSubt(health, attackDamage)));

        EFSMParameter attack = new EFSMParameter( 
            new Var<Integer>("wait::ticks", 16),
            new Var<String>("attack", ""));
        
        EFSMTransition t_1 = new EFSMTransition();
        t_1.setId("t1");
        t_1.setGuard(new EFSMGuard(mob_has_heath));
        t_1.setInParameter(attack);
        t_1.setOp(update_health);
        t_1.setOutParameter(new EFSMParameter(health));
        DamageEFSMBuilder.withTransition(select_sword, mob_ref, t_1);

        EFSMTransition t_2 = new EFSMTransition();
        t_2.setId("t2");
        t_2.setGuard(new EFSMGuard(final_hit));
        t_2.setInParameter(attack);
        t_2.setOutParameter(new EFSMParameter(new Var<Double>("entity::health", 0.0)));
        DamageEFSMBuilder.withTransition(select_sword, end_state, t_2);

        EFSMTransition t_3 = new EFSMTransition();
        t_3.setId("t3");
        DamageEFSMBuilder.withTransition(mob_ref, start, t_3);

        EFSMTransition t_4 = new EFSMTransition();
        t_4.setId("t4");
        t_4.setOp(set_stone_sword_dmg);
        t_4.setInParameter(new EFSMParameter(new Var<String>("select::item", "stone_sword")));
        DamageEFSMBuilder.withTransition(start, select_sword, t_4);

        EFSMTransition t_5 = new EFSMTransition();
        t_5.setId("t5");
        t_5.setOp(set_iron_sword_dmg);
        t_5.setInParameter(new EFSMParameter(new Var<String>("select::item", "iron_sword")));
        DamageEFSMBuilder.withTransition(start, select_sword, t_5);

        EFSMTransition t_6 = new EFSMTransition();
        t_6.setId("t6");
        t_6.setOp(set_diamond_sword_dmg);
        t_6.setInParameter(new EFSMParameter(new Var<String>("select::item", "diamond_sword")));
        DamageEFSMBuilder.withTransition(start, select_sword, t_6);

        EFSMTransition t_7 = new EFSMTransition();
        t_7.setId("t7");
        t_7.setOp(set_netherite_sword_dmg);
        t_7.setInParameter(new EFSMParameter(new Var<String>("select::item", "netherite_sword")));
        DamageEFSMBuilder.withTransition(start, select_sword, t_7);


        return DamageEFSMBuilder.build(start, DamageCtx, lrParameterGenerator);
    }

}
