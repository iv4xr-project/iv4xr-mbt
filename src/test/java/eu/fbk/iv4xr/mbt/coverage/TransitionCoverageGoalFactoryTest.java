/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;

//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;





/**
 * @author kifetew
 *
 */
class TransitionCoverageGoalFactoryTest {

	EFSM efsm;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		
		//FIXME this is a workaround to fix failing tests due to static a static field in model factory
		// this call (with parameter reset=true) forces the model factory to load the correct model
		// as indicated by the MBTProperties.SUT_EFSM property
		EFSMFactory factory = EFSMFactory.getInstance(true);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoalFactory#TransitionCoverageGoalFactory()}.
	 */
	@Test
	void testTransitionCoverageGoalFactory() {
		TransitionCoverageGoalFactory goalFactory = new TransitionCoverageGoalFactory();
		assertNotNull(goalFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoalFactory#getCoverageGoals()}.
	 */
	@Test
	void testGetCoverageGoals() {
		TransitionCoverageGoalFactory goalFactory = new TransitionCoverageGoalFactory();
		assertNotNull(goalFactory);
		List<TransitionCoverageGoal> coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		System.out.println("Num goals: " + coverageGoals.size());
		assertTrue(coverageGoals.size() == 30);
	}

}
