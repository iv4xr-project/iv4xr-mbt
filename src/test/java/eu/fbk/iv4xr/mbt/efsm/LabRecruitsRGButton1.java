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


		assertTrue(m.curState.equals(rbg1.bb));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));
        
        m.transition(null, rbg1.bb);
		assertTrue(m.curState.equals(rbg1.bb));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#0000FF"));

        m.transition(null, rbg1.bb);
		assertTrue(m.curState.equals(rbg1.bb));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));


        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));



        m.transition(null, rbg1.bg);
        assertTrue(m.curState.equals(rbg1.bg));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));
        
        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#00FF00"));

        m.transition(null, rbg1.bg);
		assertTrue(m.curState.equals(rbg1.bg));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));


        m.transition(null, rbg1.cs1);
		assertTrue(m.curState.equals(rbg1.cs1));



        m.transition(null, rbg1.br);
        assertTrue(m.curState.equals(rbg1.br));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));
        
        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#FF0000"));

        m.transition(null, rbg1.br);
		assertTrue(m.curState.equals(rbg1.br));
        assertTrue(m.curContext.getContext().getVariable("cs1").getValue().equals("#000000"));


    }
}