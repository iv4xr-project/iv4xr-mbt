package eu.fbk.iv4xr.mbt.expression;



import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.evosuite.shaded.org.hsqldb.lib.HashSet;
import org.junit.Test;

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.expression.VarSet;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Variable;
import eu.fbk.iv4xr.mbt.efsm4j.expression.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm4j.expression.bool.BoolConstant;
import eu.fbk.iv4xr.mbt.efsm4j.expression.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm4j.expression.bool.BoolVariable;
import eu.fbk.iv4xr.mbt.efsm4j.expression.integer.IntConstant;
import eu.fbk.iv4xr.mbt.efsm4j.expression.integer.IntVariable;
import eu.fbk.iv4xr.mbt.efsm4j.expression.integer.IntSum;

public class ExpressionTest {
	
	@Test
	public void testInteger() {
		
		IntConstant c1 = new IntConstant(10);
		IntVariable v1 = new IntVariable("v1",7);
		IntSum sum1 = new IntSum(c1, v1);
		Integer x = 2 + sum1.eval().getVal();
		assertEquals(x, 19); 
		
		assertEquals(sum1.eval().getVal(),17);
		v1.update(new VarSet(new IntVariable("v1",10)));
		assertEquals(sum1.eval().getVal(),20);
		
		sum1.update(new VarSet(new IntVariable("v1",2)));
		assertEquals(sum1.eval().getVal(),12);
		
		
		BoolConstant c2 = new BoolConstant(true);
		BoolVariable v2 = new BoolVariable("v2", false);
		BoolAnd and1 = new BoolAnd(c2,v2);
		BoolOr or1 = new BoolOr(and1, c2);
		assertEquals(or1.eval().getVal(), true);
		
		VarSet variable1 = or1.getVariables();
		System.out.println(variable1.getHash().toString());
	}

}