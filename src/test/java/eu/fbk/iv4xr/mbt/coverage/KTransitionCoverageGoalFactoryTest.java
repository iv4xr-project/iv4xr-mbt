package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;

public class KTransitionCoverageGoalFactoryTest {
	
	@Test
	public void testCoverageGoals_ButtonDoors1() {
		
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		MBTProperties.k_transition_size = 3;
		EFSMFactory factory = EFSMFactory.getInstance(true);
		
		KTransitionCoverageGoalFactory goalFactory = new KTransitionCoverageGoalFactory<>();
		assertNotNull(goalFactory);
		List coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		System.out.println("Num goals: " + coverageGoals.size());
	}
	
	@Test
	public void  testCoverageGoals_custom1() {
		
	}

}
