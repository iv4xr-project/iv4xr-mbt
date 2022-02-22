package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;

public class EFSMResetTest {

	@Test
	public void testEFSMReset() {
		// set sut
		MBTProperties.SUT_EFSM =  "labrecruits.buttons_doors_1";
		EFSMFactory factory = EFSMFactory.getInstance(true);
		assertNotNull(factory);
		EFSM efsm = factory.getEFSM();
		assertNotNull (efsm);
		
		EFSMConfiguration configuration = efsm.getConfiguration();
		
		Var door1Variable = configuration.getContext().getContext().getVariable("door1");
		Var guard1Variable = efsm.getTransition("t_10").getGuard().getGuard().getVariables().getVariable("door1");
		// the reference between context and guards are lost in the constructor
		assertTrue(door1Variable == guard1Variable);
		
		// get door1 value in the context
		Boolean contextDoor1 = (Boolean) door1Variable.getValue();
		// get door1 value on transition t_10
		Boolean guardDoor1 = (Boolean) guard1Variable.getValue();
		// should have the same value
		assertFalse(contextDoor1);
		assertFalse(guardDoor1);	
		assertTrue(contextDoor1.equals(guardDoor1));
				
		// simple test case that open door 1
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(efsm.getTransition("t_0"));  // b0 -> b1
		path.append(efsm.getTransition("t_7"));  // b1 -> b1
		path.append(efsm.getTransition("t_6"));  // b1 -> d1m
		path.append(efsm.getTransition("t_10"));  // d1m -> d1p
		
		t.setPath(path);
		
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		EFSMTransition trans = efsm.getTransition("t_10");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(trans);
		double fitness = goal.getFitness(c);
		// the get fitness resets the efsm, so the context should be all false
		
		// get door1 value in the context
		contextDoor1 = (Boolean) efsm.getConfiguration().getContext().getContext().getVariable("door1").getValue();
		// get door1 value on transition t_10
		guardDoor1 = (Boolean) trans.getGuard().getGuard().getVariables().getVariable("door1").getValue();
		// should have the same value
		assertFalse(contextDoor1);
		assertFalse(guardDoor1);	
		assertTrue(contextDoor1.equals(guardDoor1));
		
		// asking t_10 again to the efsm would solve the problem
		trans = efsm.getTransition("t_10");
		guardDoor1 = (Boolean) trans.getGuard().getGuard().getVariables().getVariable("door1").getValue();
		assertFalse(guardDoor1);	
		assertTrue(contextDoor1.equals(guardDoor1));
		
	}
	
}
