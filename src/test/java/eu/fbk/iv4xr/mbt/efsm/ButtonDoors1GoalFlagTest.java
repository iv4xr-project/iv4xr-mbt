package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1GoalFlag;

public class ButtonDoors1GoalFlagTest {
	
	@Test
	public void testModel() {
		
		ButtonDoors1GoalFlag bd1f = new ButtonDoors1GoalFlag();
		EFSM m = bd1f.getModel();
		
		assertTrue(m.curState.equals(bd1f.b_0));	
		
		m.transition(null, bd1f.b_1);
		m.transition(null, bd1f.b_1);
		m.transition(null, bd1f.d_1_m);
		m.transition(null, bd1f.d_1_p);
		m.transition(null, bd1f.b_2);
		m.transition(null, bd1f.b_2);
		m.transition(null, bd1f.d_2_m);
		m.transition(null, bd1f.d_2_p);
		m.transition(null, bd1f.b_3);
		m.transition(null, bd1f.b_3);
		m.transition(null, bd1f.d_2_p);
		m.transition(null, bd1f.d_2_m);
		m.transition(null, bd1f.d_1_p);
		m.transition(null, bd1f.d_1_m);
		m.transition(null, bd1f.d_T_m);
		m.transition(null, bd1f.d_T_p);
		m.transition(null, bd1f.f_0);
		
		assertTrue(m.curState.equals(bd1f.f_0));
		m.transition(null, bd1f.d_T_p);
		assertTrue(m.curState.equals(bd1f.d_T_p));
		

		
	}

}
