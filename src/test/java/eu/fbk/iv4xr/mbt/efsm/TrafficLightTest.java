package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.examples.TrafficLight;
import junit.framework.Assert;


public class TrafficLightTest {

	@Test
	public void testModel_1() {
		TrafficLight tl = new TrafficLight();
		EFSM m = tl.getModel();
		
		
		
		/*public static assertInRange(String message, float expected, float delta, float actual) {
			   if (delta < 0) {
			     delta = -delta;
			   }
			   if (actual < expected - delta)
			     Assert.fail(message + actual + " is less than " + expected + " - " + delta);
			   if (actual > expected + delta) 
			     Assert.fail(message + actual + " is more than " + expected + " + " + delta);
			}*/
		
		
		//assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==1);
		
		assertTrue(m.curState.equals(tl.red));	
		//for (int i = 0; i < 60; i++) {
			//m.transition(null, tl.red);
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);
		//}
		
		m.transition(null, tl.green);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.green));
		
		//for (int i = 0; i < 60; i++) {
			m.transition(null, tl.green);
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);
		//}
		
		m.transition(null, tl.yellow);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.yellow));
		
		
		//for (int i = 0; i < 5; i++) {
			m.transition(null, tl.yellow);
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=5);
		//}
		
	}
	
	

	

	
}
