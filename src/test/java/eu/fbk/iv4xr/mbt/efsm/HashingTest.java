package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

import org.apache.commons.lang3.SerializationUtils;

public class HashingTest {
	
	public enum Day {
	    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
	    THURSDAY, FRIDAY, SATURDAY 
	}
	
	@Test
	public void varHashingTest(){
		
		// define two variables
		Var<Boolean> x1 = new Var<Boolean>("x", false);
		Var<Boolean> x2 = new Var<Boolean>("x", false);
		
		// the two should give the same has
		int hashCode1 = x1.hashCode();
		int hashCode2 = x2.hashCode();
		
		assertTrue(x1.equals(x2));
		assertTrue(hashCode1 == hashCode2 );
		
		Var<Integer> x3 = new Var<Integer>("x", 0);
		
	    int hashCode3 = x3.hashCode();
	    
	    assertFalse(x1.equals(x3));
		assertFalse(hashCode1 == hashCode3 );
		
		Var<Boolean> x4 = new Var<Boolean>("x", true);
		
	    int hashCode4 = x4.hashCode();
	    
	    assertFalse(x1.equals(x4));
		assertFalse(hashCode1 == hashCode4 );
		
	}
	
	@Test
	public void constHashingTest() {
		
		// define two constants
		Const<Integer> c1 = new Const<Integer>(5);
		Const<Integer> c2 = new Const<Integer>(5);
		
		// the two should give the same has
		int hashCode1 = c1.hashCode();
		int hashCode2 = c2.hashCode();
		
		assertTrue(c1.equals(c2));
		assertTrue(hashCode1 == hashCode2 );
		
		Const<Integer> c3 = new Const<Integer>(55);
		int hashCode3 = c3.hashCode();
		
		assertFalse(c1.equals(c3));
		assertFalse(hashCode1 == hashCode3 );

	}
	
	
	@Test
	public void varsetHashingTest() {
		
		// define two variables
		Var<Boolean> x1 = new Var<Boolean>("x", false);
		Var<Integer> x2 = new Var<Integer>("y", 9);
		Var<Integer> x3 = new Var<Integer>("z", 9);
		Var<Enum<Day>> x4 = new Var<Enum<Day>>("k",Day.MONDAY);
		
		VarSet vs1 = new VarSet(Arrays.asList(x1,x2,x3,x4));
		VarSet vs2 = SerializationUtils.clone(vs1);
		
		// the two should give the same has
		int hashCode1 = vs1.hashCode();
		int hashCode2 = vs2.hashCode();
		
		assertTrue(vs1.equals(vs2));
		assertTrue(hashCode1 == hashCode2 );
		
		VarSet vs3 = new VarSet(Arrays.asList(x3,x1,x4,x2));
		int hashCode3 = vs3.hashCode();
		
		assertTrue(vs1.equals(vs3));
		assertTrue(hashCode1 == hashCode3 );
		
		VarSet vs4 = new VarSet(Arrays.asList(x1,x2));
		int hashCode4 = vs4.hashCode();
		
		assertFalse(vs1.equals(vs4));
		assertFalse(hashCode1 == hashCode4 );
		
	}
	
	
	@Test
	public void contextHashingTest() {
		
		// define two variables
		Var<Boolean> x1 = new Var<Boolean>("x", false);
		Var<Integer> x2 = new Var<Integer>("y", 9);
		Var<Integer> x3 = new Var<Integer>("k", 9);
		Var<Enum<Day>> x4 = new Var<Enum<Day>>("z",Day.SUNDAY);
				
		EFSMContext cont1 = new EFSMContext(x1,x2,x3,x4);
		EFSMContext cont2 = SerializationUtils.clone(cont1);
		
		// the two should give the same has
		int hashCode1 = cont1.hashCode();
		int hashCode2 = cont2.hashCode();
		
		assertTrue(cont1.equals(cont2));
		assertTrue(hashCode1 == hashCode2 );
		
		EFSMContext cont3 = new EFSMContext(x3,x4,x1,x2);
		int hashCode3 = cont3.hashCode();
		
		assertTrue(cont1.equals(cont3));
		assertTrue(hashCode1 == hashCode3 );
		
		Var<Integer> x5 = new Var<Integer>("k", 10);
		EFSMContext cont4 = new EFSMContext(x1,x2,x5,x4);
		int hashCode4 = cont4.hashCode();
		
		assertFalse(cont1.equals(cont4));
		assertFalse(hashCode1 == hashCode4 );
		
	}
	
	@Test
	public void configurationHashingTest() {
		
		EFSMState d_1_m = new EFSMState("d1m");
		
		Var<Boolean> d_1 = new Var<Boolean>("door1", false);
		Var<Boolean> d_2 = new Var<Boolean>("door2", false);
		Var<Boolean> d_T = new Var<Boolean>("door3", false);
		
		EFSMContext context = new EFSMContext(d_1, d_2, d_T);
		
		// clone and check
		EFSMConfiguration conf1 = new EFSMConfiguration(d_1_m, context);
		EFSMConfiguration conf2 = SerializationUtils.clone(conf1);
		
		int hashCode1 = conf1.hashCode();
		int hashCode2 = conf2.hashCode();
		
		assertTrue(conf1.equals(conf2));	
		assertTrue(hashCode1 == hashCode2 );
		
		// use another variable for door1 but with the same name and value
		Var<Boolean> d_1p = new Var<Boolean>("door1", false);
		EFSMContext context2 = new EFSMContext(d_1p, d_2, d_T);
		EFSMConfiguration conf3 = new EFSMConfiguration(d_1_m, context2);

		int hashCode3 = conf3.hashCode();
		assertTrue(conf1.equals(conf3));	
		assertTrue(hashCode1 == hashCode3 );
		
		// use another state but with the same value
		EFSMState d_1_m_p = new EFSMState("d1m");
		EFSMConfiguration conf4 = new EFSMConfiguration(d_1_m_p, context);

		int hashCode4 = conf4.hashCode();
		assertTrue(conf1.equals(conf4));	
		assertTrue(hashCode1 == hashCode4 );
		
	}
	


}
