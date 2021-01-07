package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;

public class ModelCreation {

	// the context has an integer and a boolean var
	Var<Boolean> b1 = new Var<Boolean>("b1", false);
	Var<Integer> i1 = new Var<Integer>("i1", 0);
	Var<Integer> i2 = new Var<Integer>("i2", 5);
	IntSum sum1 = new IntSum(i1,i2);
	Assign<Integer> a1 = new Assign<Integer>(i2, sum1);
	Const<Boolean> bc1 = new Const<Boolean>(true);
 	BoolOr or1 = new BoolOr(b1,bc1);
	ButtonDoors1 bd1 = new ButtonDoors1();
 	
	@Test
	public void testContext() {
		EFSMContext cont = new EFSMContext(b1,i1);
	}
	
	@Test
	public void testParameters() {
		EFSMParameter par = new EFSMParameter(i1,i1,b1);
	}
	
	@Test
	public void testOperations() {
		EFSMOperation op = new EFSMOperation(a1);
	}
	
	@Test
	public void testGuard() {
		EFSMGuard guard = new EFSMGuard(or1);
	}
	
	@Test
	public void testBD1() {
		
	}
}
