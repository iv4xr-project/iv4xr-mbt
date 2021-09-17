package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;


import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1FireWithDeath;

public class LabRecruitsButtonDoors1FireDeath {
	@Test
	public void testModel() throws IOException {
		ButtonDoors1FireWithDeath bd1 = new ButtonDoors1FireWithDeath();
		EFSM m = bd1.getModel();
		
		// got to fire repeatedly 
		for (int i = 0; i < 20; i++) {
			m.transition(null, bd1.b_1);
			m.transition(null, bd1.fire);
		}
		
		// check HP is 0
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 0) ;	
		
		// go to death state
		m.transition(null, bd1.death);
		
		// reset
		m.transition(null, bd1.b_0);
		
		// check HP is 100
		assertTrue((Integer)m.curContext.getContext().getVariable("hp").getValue() == 100) ;	
				
		
	}
}
