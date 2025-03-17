package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.examples.LiftSystem;
import junit.framework.Assert;


public class LiftSystemTest {





	@Test
	public void testModel_1() {
		LiftSystem ls = new LiftSystem();
		EFSM m = ls.getModel();
		
		assertTrue(m.curState.equals(ls.floor0));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
		assertTrue((Boolean)m.curContext.getContext().getVariable("stopButton").getValue()== false);
		assertTrue(m.curState.equals(ls.floor1));
		
		
		assertTrue(m.curState.equals(ls.floor1));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==1);
		assertTrue((Boolean)m.curContext.getContext().getVariable("stopButton").getValue()== false);
		assertTrue(m.curState.equals(ls.floor2));
		
		
		assertTrue(m.curState.equals(ls.floor2));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==2);
		assertTrue((Boolean)m.curContext.getContext().getVariable("stopButton").getValue()== false);
		assertTrue(m.curState.equals(ls.floor1));
		
		
		
		assertTrue(m.curState.equals(ls.floor1));	
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==1);
		assertTrue((Boolean)m.curContext.getContext().getVariable("stopButton").getValue()== false);
		assertTrue(m.curState.equals(ls.floor0));
		
		
				
		assertTrue((Boolean)m.curContext.getContext().getVariable("stopButton").getValue()== true);
		assertTrue((Integer)m.curContext.getContext().getVariable("count").getValue()==0);
			
			
	   
		
		
	}
	
	
}
	

