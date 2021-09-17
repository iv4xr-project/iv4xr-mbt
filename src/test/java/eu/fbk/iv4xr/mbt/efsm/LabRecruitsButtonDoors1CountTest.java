package eu.fbk.iv4xr.mbt.efsm;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1Count;

public class LabRecruitsButtonDoors1CountTest {

	@Test
	public void testModel() throws IOException {
		ButtonDoors1Count bd1 = new ButtonDoors1Count();
		EFSM m = bd1.getModel();
		
		assertTrue(m.curState.equals(bd1.b_0));	
		
		m.transition(null, bd1.d_1_m);
		assertTrue(m.curState.equals(bd1.d_1_m));	
		
		
	}
}
