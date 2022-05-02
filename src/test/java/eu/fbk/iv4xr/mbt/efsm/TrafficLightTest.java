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
		
		m.transition(null, tl.yellow);
		assertTrue(m.curState.equals(tl.red));	
		assertFalse(m.curState.equals(tl.yellow));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);		
		
	}
	
	
	@Test
	public void testModel_2() {
		TrafficLight tl = new TrafficLight();
		EFSM m = tl.getModel();
		
		/*red --> red */
		assertTrue(m.curState.equals(tl.red));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		for (int i = 0; i < 60; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.red);
		}
		
		/*red --> green */
		
		assertTrue(m.curState.equals(tl.red));			
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==60);
		m.transition(null, tl.green);
		
		/*green --> green */
		assertTrue(m.curState.equals(tl.green));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		for (int i = 0; i < 60; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.green);
		}
		
		
		/*FROM GREEN TO YELLOW WITH PENDING STATE */
		
		/*green --> pending  pedestrian time should be less than 60 */
		assertTrue(m.curState.equals(tl.green));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);	
		assertTrue(tl.getPedestrian().getValue());
		m.transition(null, tl.pending);
		
		
		/*pending --> pending */
		assertTrue(m.curState.equals(tl.pending));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		for (int i = 0; i < 60; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.pending);
		}
		
		
		/*pending --> yellow */
		
		assertTrue(m.curState.equals(tl.pending));			
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==60);
		m.transition(null, tl.yellow);
		
		
		/*FROM GREEN TO YELLOW WITHOUT PENDING STATE */

		/*green --> yellow  pedestrian time should be greater than 60
		assertTrue(m.curState.equals(tl.green));
		assertTrue(tl.getTime_pedestrian().getValue()>=60);
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()>=60);	
		assertTrue(tl.getPedestrian().getValue());
		m.transition(null, tl.yellow);*/
		
		/*yellow --> yellow */
		assertTrue(m.curState.equals(tl.yellow));
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		
		for (int i = 0; i < 5; i++) {
			assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==i);
			m.transition(null, tl.yellow);
		}
		
		/*yellow --> red */
		assertTrue(m.curState.equals(tl.yellow));			
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()<=60);
		m.transition(null, tl.red);

		
	}
}
	

