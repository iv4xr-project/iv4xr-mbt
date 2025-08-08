package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;

public class KTransitionCoverageGoalFactoryTest {
	
	@Test
	public void testCoverageGoals_ButtonDoors1() {
		
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		MBTProperties.K_TRANSITION_SIZE = 3;
		EFSMFactory factory = EFSMFactory.getInstance(true);
		
		KTransitionCoverageGoalFactory goalFactory = new KTransitionCoverageGoalFactory();
		assertNotNull(goalFactory);
		List coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		assertTrue(coverageGoals.size() == 343);
	}
	
	@Test
	public void  testCoverageGoals_custom1() {
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		MBTProperties.K_TRANSITION_SIZE = 3;
		EFSMFactory factory = EFSMFactory.getInstance(true);
		
		KTransitionCoverageGoalFactory goalFactory = new KTransitionCoverageGoalFactory();
		assertNotNull(goalFactory);
		List coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		assertTrue(coverageGoals.size() == 779);
	}

}
