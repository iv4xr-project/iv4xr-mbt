package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.labRecruits.RGButton1;

public class LabRecruitsRGButton1 {
    @Test
	public void testModel() throws IOException {
        RGButton1 rbg1 = new RGButton1();
        EFSM m = rbg1.getModel();


        // blue button
		assertTrue(m.curState.equals(rbg1.bb));
        assertTrue(m.curContext.getContext().getVariable("cbb").getValue().equals(false));
        assertTrue(m.curContext.getContext().getVariable("cbr").getValue().equals(false));
        assertTrue(m.curContext.getContext().getVariable("cbg").getValue().equals(false));
        
        m.transition(null, rbg1.bb);
        assertTrue(m.curContext.getContext().getVariable("cbb").getValue().equals(true));
        
        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));
        
        // check that i can't move to other states while button is pressed
        m.transition(null, rbg1.br);
        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.cs1));
        
        
        m.transition(null, rbg1.bb);
		assertTrue(m.curState.equals(rbg1.bb));

        m.transition(null, rbg1.bb);
        assertTrue(m.curContext.getContext().getVariable("cbb").getValue().equals(false));

        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));
        
        // red button
        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));

        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));

        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));
        
        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));
        assertTrue(m.curContext.getContext().getVariable("cbr").getValue().equals(true));

        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));
        assertTrue(m.curContext.getContext().getVariable("cbr").getValue().equals(false));

        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));

        // green button|
        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));

        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.bg));

        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));
        
        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));
        assertTrue(m.curContext.getContext().getVariable("cbg").getValue().equals(true));

        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));
        assertTrue(m.curContext.getContext().getVariable("cbg").getValue().equals(false));

        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));
    }
}