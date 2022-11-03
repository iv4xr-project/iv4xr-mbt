package eu.fbk.iv4xr.mbt.efsm;





import java.util.Collection;
import java.util.Set;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;
import eu.fbk.iv4xr.mbt.efsm.spaceEngineering.SingleBlockWeldingAndGrinding;
import eu.fbk.iv4xr.mbt.efsm.spaceEngineering.SingleBlockWeldingAndGrinding.seActions;

public class SingleBlockWeldingAndGrindingTest {

	@Ignore
	//@Test
	public void test() {
		
		SingleBlockWeldingAndGrinding block = new SingleBlockWeldingAndGrinding();
		EFSM m = block.getModel();
		
		assertTrue(m.curState.equals(block.block_exists));
		
		Set<EFSMTransition> transitons = m.getTransitons();
		
		for(EFSMTransition t : transitons) {
			
			seActions value =  (seActions) t.getOutParameter().getParameter().getVariable("action").getValue();			
			Integer old_en = (Integer) m.curContext.getContext().getVariable("block_energy").getValue();
			m.transition(t);
			
			switch(value) {				
				case weld_10 :
					Integer new_en = (Integer) m.curContext.getContext().getVariable("block_energy").getValue();
					
					if (old_en <= 90) {
						assertTrue(new_en == (old_en + 10));	
					}else {
						assertTrue(new_en == 100);
					}					
					break;
			
			}
			
			
		}
		
		
		
		
		
		
	}
}
