package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1Fire;

public class LabRecruitsButtonDoors1Fire {
	
	@Test
	public void testModel() {
		ButtonDoors1Fire bd1 = new ButtonDoors1Fire();
		EFSM m = bd1.getModel();
		
		assertTrue(m.curState.equals(bd1.b_0));	
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 100) ;	
		m.transition(null, bd1.b_1);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 95) ;	
		
		m.transition(null, bd1.b_1);
		assertTrue(m.curState.equals(bd1.b_1));
		
		m.transition(null, bd1.d_T_m);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 90) ;	
		
		m.transition(null, bd1.d_T_m);
		assertTrue(m.curState.equals(bd1.d_T_m));
		
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 85) ;	

		m.transition(null, bd1.b_0);
		assertTrue(m.curState.equals(bd1.b_0));
		
		m.transition(null, bd1.d_1_m);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 80) ;	

		m.transition(null, bd1.b_1);
		m.transition(null, bd1.b_1);
		assertTrue((Boolean)m.curContext.getContext().getVariable("d_1").getValue()) ;	
		
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 75) ;	
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 70) ;	
		
		m.transition(null, bd1.d_1_m);
		m.transition(null, bd1.d_1_p);
		m.transition(null, bd1.d_1_m);
		m.transition(null, bd1.fire);
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 65) ;
		
		
		// continue to enter in the fire to decrease HP
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		m.transition(null, bd1.b_0);
		m.transition(null, bd1.fire);
		
		for(EFSMTransition t: (Set<EFSMTransition>)m.transitionsOutOf(m.curState)) {
			assertFalse(t.isFeasible(m.curContext));
		}
		
		
	}

}
