package eu.fbk.iv4xr.mbt.efsm;



import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import eu.fbk.iv4xr.mbt.efsm.exp.*;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.*;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleEq;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleLess;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleSum;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.*;


public class Expression {
	


	// integer variables and constants
	Const<Integer> ic1 = new Const<Integer>(10);
	Const<Integer> ic2 = new Const<Integer>(17);
	
	Var<Integer> iv1 = new Var<Integer>("iv1",7);
	Var<Integer> iv2 = new Var<Integer>("iv2",47);
		
	// boolean integer and constants
	Const<Boolean> bc1  = new Const<Boolean>(false);	
	Const<Boolean> bc2 = new Const<Boolean>(true);
	Var<Boolean> bv1 = new Var<Boolean>("bv1", false);		
	Var<Boolean> bv2 = new Var<Boolean>("bv2", true);	
	
	Const<Double> dc1 = new Const<Double>(1.0);
	Const<Double> dc2 = new Const<Double>(11.0);
	
	Var<Double> dv1 = new Var<Double>("dv1",7.0);
	Var<Double> dv2 = new Var<Double>("dv2",11.2);
	
	
	
	// this test verify that changes in iv1 in testEquals test are not visible
	// in the testInteger test
	@Test
	public void testExpressionEquals() {
		Var<Integer> v1 = new Var<Integer>("iv1",7);
		assertTrue(v1.equals(iv1));
		assertTrue(iv1.equals(v1));
		iv1.setValue(8);
		assertFalse(v1.equals(iv1));
		
		Var<Boolean> b1 = new Var<Boolean>("bv1", false);
		assertTrue(b1.equals(bv1));
		bv1.setValue(true);
		assertFalse(b1.equals(bv1));
		
		Const<Integer> c1 = new Const<Integer>(8);
		assertFalse(c1.equals(bc1));
		assertFalse(c1.equals(ic1));
		assertTrue(c1.equals(iv1.eval()));
		assertTrue(iv1.eval().equals(c1));
		
		IntSum sum1 = new IntSum(ic1, iv1);
		IntSum sum2 = new IntSum(iv1, ic1);
		assertTrue(sum1.equals(sum2));
		
		IntGreat gt1 = new IntGreat(iv1,ic1);
		IntGreat gt2 = new IntGreat(ic1,iv1);
		assertFalse(gt1.equals(gt2));
	}
	
	@Test
	public void testVarSetEquals() {
		// test order
		VarSet vs1 = new VarSet<>();
		vs1.put(bv1);
		vs1.put(bv2);
		vs1.put(iv1);
		
		VarSet vs2 = new VarSet<>();
		vs2.put(iv1);
		vs2.put(bv1);
		vs2.put(bv2);
		
		assertTrue(vs1.equals(vs2) && vs2.equals(vs1) );
		
		iv1.setValue(5);
		assertTrue(vs1.equals(vs2) && vs2.equals(vs1) );
		
		VarSet vs3 = new VarSet<>();
		vs3.put(iv1);
		vs3.put(bv1);
		vs3.put(bv1);
		assertFalse(vs1.equals(vs3) || vs3.equals(vs1) );
		
		VarSet vs4 = new VarSet<>();
		vs4.put(iv1);
		vs4.put(bv1);
		assertFalse(vs1.equals(vs4) || vs4.equals(vs1) );
		assertTrue(vs3.equals(vs4) && vs4.equals(vs3) );
		
		VarSet vs5 = new VarSet<>();
		vs5.put(iv1);
		vs5.put(new Var<Integer>("iv1",5));
		vs5.put(bv1);
		assertTrue(vs5.equals(vs4) && vs4.equals(vs5) );
		vs5.put(new Var<Integer>("iv1",7));
		assertFalse(vs5.equals(vs4) || vs4.equals(vs5) );
	}
	
	@Test
	public void testInteger() {
		
		// try a sum
		IntSum sum1 = new IntSum(ic1, iv1);
		System.out.println(sum1.toDebugString());
		// verify the result is computed correctly
		assertEquals(sum1.eval().getVal(),17);
		// use eq operator to check the result
		IntEq eq1 = new IntEq(sum1, ic2);
		System.out.println(eq1.toDebugString());
		assertTrue(eq1.eval().getVal());
		// mix exp operators and normal operators
		assertEquals(2 + sum1.eval().getVal(), 19); 
		
		// check if updates work
		iv1.update(new VarSet(new Var<Integer>("iv1",10)));		
		assertEquals(sum1.eval().getVal(),20);	
		sum1.update(new VarSet(new Var<Integer>("iv1",2)));
		assertEquals(sum1.eval().getVal(),12);
		
		// check comparison
		IntGreat cmp1 = new IntGreat(iv1, iv2);
		assertFalse(cmp1.eval().getVal());
		System.out.println(cmp1.toDebugString());
		
		
		Assign as1 = new Assign(iv1, sum1 );
		Assign as2 = new Assign(iv2, new IntSum(sum1,sum1) );
		AssignSet aset = new AssignSet();
		aset.put(as1);
		aset.put(as2);
		System.out.println(as1.toDebugString());
		System.out.println(as2.toDebugString());
		System.out.println(aset.toDebugString());
		
	}
	
	@Test
	public void testBoolean() {
		
		// mix integer comparison and boolean expressions
		IntGreat cmp1 = new IntGreat(iv1, iv2);
		BoolOr or1 = new BoolOr(bc2, bv1);
		assertEquals(or1.eval().getVal(), true);
		System.out.println(or1.toDebugString());
		
		BoolOr or2 = new BoolOr(bv1, cmp1);
		assertFalse(or2.eval().getVal());
		System.out.println(or2.toDebugString());
		
		
		BoolNot not1 = new BoolNot(or2);
		assertTrue(not1.eval().getVal());
		System.out.println(not1.toDebugString());
		
		
		VarSet vSet1 = or1.getVariables();
		VarSet vSet2 = or2.getVariables();
		assertTrue(vSet2.contain("bv1"));
		System.out.println(vSet1.toDebugString());
		System.out.println(vSet2.toDebugString());

	}
	

	@Test
	public void testDouble() {
		Var<Double> v1 =  new Var<Double>("dv1",7.0);
		
		assertTrue(v1.equals(dv1));
		assertTrue(dv1.equals(v1));
	
		assertFalse(v1.equals(dv2));
		assertFalse(dv2.equals(v1));
				
		// Equal op
		DoubleEq deq1 = new DoubleEq(v1, dv1);
		assertTrue(deq1.eval().getVal());
		DoubleEq deq2 = new DoubleEq(v1, dv2);
		assertFalse(deq2.eval().getVal());
		
		// Great op
		DoubleGreat dg1 = new DoubleGreat(dv1, v1);
		assertFalse(dg1.eval().getVal());
		DoubleGreat dg2 = new DoubleGreat(dv2, v1);
		assertTrue(dg2.eval().getVal());
		
		// Less op
		DoubleLess dl1 = new DoubleLess(dv1, v1);
		assertFalse(dl1.eval().getVal());
		DoubleLess dl2 = new DoubleLess(v1,dv2);
		assertTrue(dl2.eval().getVal());
		
		// Subtraction
		DoubleSubt dsub1 = new DoubleSubt(v1, dv1);
		assertTrue(dsub1.eval().getVal() == 0d);
		assertTrue(new DoubleLess(dsub1, v1).eval().getVal());
		
		// Sum
		DoubleSum dsum1 = new DoubleSum(v1, dv2);
		assertTrue(dsum1.eval().getVal() == 18.2d);
		
		
	}

	
}