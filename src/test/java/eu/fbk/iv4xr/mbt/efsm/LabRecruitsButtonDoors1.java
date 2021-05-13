package eu.fbk.iv4xr.mbt.efsm;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;

public class LabRecruitsButtonDoors1 {

	@Test
	public void testModel() throws IOException {
		ButtonDoors1 bd1 = new ButtonDoors1();
		EFSM m = bd1.getModel();
		
		assertTrue(m.curState.equals(bd1.b_0));	
		
		m.transition(null, bd1.d_1_m);
		assertTrue(m.curState.equals(bd1.d_1_m));	
		
		m.transition(null, bd1.d_1_p);
		assertFalse(m.curState.equals(bd1.d_1_p));
		assertFalse((Boolean)m.curContext.getContext().getVariable("door1").getValue());
		assertTrue(m.curState.equals(bd1.d_1_m));
		
		m.transition(null,bd1.b_1);
		assertTrue(m.curState.equals(bd1.b_1));
		
		m.transition(null,bd1.b_1);
		assertTrue(m.curState.equals(bd1.b_1));
		assertTrue((Boolean)m.curContext.getContext().getVariable("door1").getValue());
				
		m.transition(null, bd1.d_1_m);
		assertTrue(m.curState.equals(bd1.d_1_m));
		m.transition(null, bd1.d_1_p);
		assertTrue(m.curState.equals(bd1.d_1_p));
		
		m.transition(null, bd1.b_2);
		assertTrue(m.curState.equals(bd1.b_2));
		m.transition(null, bd1.b_2);
		assertTrue(m.curState.equals(bd1.b_2));
		assertFalse((Boolean)m.curContext.getContext().getVariable("door1").getValue());
		assertTrue((Boolean)m.curContext.getContext().getVariable("door2").getValue());
		assertTrue((Boolean)m.curContext.getContext().getVariable("door3").getValue());
		
		m.transition(null, bd1.d_2_m);
		assertTrue(m.curState.equals(bd1.d_2_m));
		m.transition(null, bd1.d_2_p);
		assertTrue(m.curState.equals(bd1.d_2_p));

		m.transition(null, bd1.b_3);
		assertTrue(m.curState.equals(bd1.b_3));	
		m.transition(null, bd1.b_3);
		assertTrue(m.curState.equals(bd1.b_3));	
		assertTrue((Boolean)m.curContext.getContext().getVariable("door1").getValue());
		assertTrue((Boolean)m.curContext.getContext().getVariable("door2").getValue());
		assertTrue((Boolean)m.curContext.getContext().getVariable("door3").getValue());		
		
		m.transition(null, bd1.d_2_p);
		assertTrue(m.curState.equals(bd1.d_2_p));	
		m.transition(null, bd1.d_2_m);
		assertTrue(m.curState.equals(bd1.d_2_m));	
		m.transition(null, bd1.d_1_p);
		assertTrue(m.curState.equals(bd1.d_1_p));	
		m.transition(null, bd1.d_1_m);
		assertTrue(m.curState.equals(bd1.d_1_m));
		m.transition(null, bd1.d_T_m);
		assertTrue(m.curState.equals(bd1.d_T_m));
		m.transition(null, bd1.d_T_p);
		assertTrue(m.curState.equals(bd1.d_T_p));
		//m.transition(null, bd1.TR);
		//assertTrue(m.curState.equals(bd1.TR));
	
		
		m.setShortestPathsBetweenStates();
		System.out.println("Distance between "+bd1.b_0.toString()+" and "+bd1.b_1.toString()+" is "+m.getShortestPathDistance(bd1.b_0, bd1.b_1));
		System.out.println("The number of shortest path between "+bd1.b_0.toString()+" and "+bd1.b_1.toString()+" is "+m.getShortestPaths(bd1.b_0, bd1.b_1).size());
		
		
		System.out.println("Distance between "+bd1.d_T_p.toString()+" and "+bd1.b_3.toString()+" is "+m.getShortestPathDistance(bd1.d_T_p, bd1.b_3));
		System.out.println("The number of shortest path between "+bd1.d_T_p.toString()+" and "+bd1.b_3.toString()+" is "+m.getShortestPaths(bd1.d_T_p, bd1.b_3).size());
		
		//System.out.println("Distance between "+bd1.b_3.toString()+" and "+bd1.TR.toString()+" is "+m.getShortestPathDistance(bd1.b_3, bd1.TR));
		
		// save to dot 
		//EFSMDotExporter exp = new EFSMDotExporter<>(m);
		EFSMExporter exp = new EFSMExporter<>(m);
		exp.writeOut(Paths.get("data/button_doors_1.dot"),"dot");
		exp.writeOut(Paths.get("data/button_doors_1.xml"),"gml");
	}
}
