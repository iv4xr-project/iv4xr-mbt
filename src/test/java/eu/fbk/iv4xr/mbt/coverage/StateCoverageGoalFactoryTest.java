/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.upb.testify.efsm.EFSM;
import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory;
import eu.fbk.se.labrecruits.LabRecruitsContext;
import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
class StateCoverageGoalFactoryTest {

	EFSM<LabRecruitsState, String, LabRecruitsContext, 
	Transition<LabRecruitsState, String, LabRecruitsContext>> efsm;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "buttons_doors_1";
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.StateCoverageGoalFactory#StateCoverageGoalFactory()}.
	 */
	@Test
	void testStateCoverageGoalFactory() {
		StateCoverageGoalFactory goalFactory = new StateCoverageGoalFactory();
		assertNotNull(goalFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.StateCoverageGoalFactory#getCoverageGoals()}.
	 */
	@Test
	void testGetCoverageGoals() {
		StateCoverageGoalFactory goalFactory = new StateCoverageGoalFactory();
		assertNotNull(goalFactory);
		List<StateCoverageGoal> coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		assertTrue(coverageGoals.size() == 11);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.StateCoverageGoalFactory#getFitness(org.evosuite.ga.Chromosome)}.
	 */
//	@Test
//	void testGetFitness() {
//		fail("Not yet implemented");
//	}

}
