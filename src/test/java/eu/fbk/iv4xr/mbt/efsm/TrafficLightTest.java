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
			
		assertTrue(m.curState.equals(tl.red));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);
		
		m.transition(null, tl.green);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.green));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		
		// Pedestrian Test Part
		m.transition(null, tl.green);
		assertTrue(m.curState.equals(tl.red));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);	
		assertTrue(tl.pedestrian.getVal());	
		
		
		m.transition(null, tl.yellow);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.yellow));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);	
		
		
		
		
	}
	
	

	@Test
	public void testModel_2() {
		TrafficLight tl = new TrafficLight();
		EFSM m = tl.getModel();
		
		assertTrue(m.curState.equals(tl.red));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		for (int i = 0; i < 60; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.red);
		}
		
		assertTrue(m.curState.equals(tl.red));			
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==60);
				
		m.transition(null, tl.green);
		assertTrue(m.curState.equals(tl.green));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);

				
		for (int i = 0; i < 60; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.green);
		}
		
		assertTrue(m.curState.equals(tl.green));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);	
		assertTrue(tl.pedestrian.getVal());	
		m.transition(null, tl.yellow);
		
	}
}
	

