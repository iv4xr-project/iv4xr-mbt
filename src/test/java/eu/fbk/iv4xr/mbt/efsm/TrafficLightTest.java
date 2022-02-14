package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.examples.TrafficLight;


public class TrafficLightTest {

	@Test
	public void testModel_1() {
		TrafficLight tl = new TrafficLight();
		EFSM m = tl.getModel();
		
		assertTrue(m.curState.equals(tl.red));	
		
		m.transition(null, tl.red);
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue() == 1);	
		
		m.transition(null, tl.red);
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue() == 2);	
		
		m.transition(null, tl.green);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.green));	
		
	}
	
}
