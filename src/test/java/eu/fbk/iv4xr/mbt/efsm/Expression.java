package eu.fbk.iv4xr.mbt.efsm;



import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import eu.fbk.iv4xr.mbt.efsm.exp.*;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.*;
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
	
	
}