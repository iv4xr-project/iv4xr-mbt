package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.efsm.usageControl.PhoneCall1;

public class UsageControlTest {

	@Test
	public void testPhoneCall1() {
		
		PhoneCall1 phoneCall1 = new PhoneCall1();
		EFSM model = phoneCall1.getModel();
		
		assertTrue(model.curState.equals(phoneCall1.NoCall));
		assertTrue((Integer)model.curContext.getContext().getVariable("credit").getValue() == 60);
		
		// recharge
		Set<EFSMTransition> transitionsOutOf = model.transitionsOutOf(phoneCall1.NoCall);
		List<EFSMTransition> listInitialTransition = new ArrayList<EFSMTransition>();
		listInitialTransition.addAll(transitionsOutOf);
		model.transition(listInitialTransition.get(0));
		
		System.out.println();
		
	}
	
}
