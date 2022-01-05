package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.NoSuchParameterException;
import org.evosuite.utils.Randomness;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

public class CoverageGoalConstrainedTestFactoryTest {

	@Test
	public void testGetTestcaseButtonDoors1() {

		try {
			org.evosuite.Properties.getInstance().setValue("random_seed", "16752");
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchParameterException e) {
			System.err.println("Unable to set Evosuite global property: " + "random_seed");
		}
		
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		EFSMFactory efsmFactory = EFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		
		Set<EFSMState> states = efsm.getStates();
		
		Integer nTests = 1000;
		for (int i = 0; i < nTests; i++) {
			//System.out.print(i+" ");

			EFSMState endState = (EFSMState) Randomness.choice(states);	
			assertTrue(states.contains(endState));
			
			StateCoverageGoal goal = new StateCoverageGoal(endState);
			
			CoverageGoalConstrainedTestFactory testFactory = new CoverageGoalConstrainedTestFactory(efsm,goal);
			assertNotNull(testFactory);
			
			AbstractTestSequence testcase = (AbstractTestSequence)testFactory.getTestcase();
			assertNotNull(testcase);
			assertTrue(testcase.getLength() <= MBTProperties.MAX_PATH_LENGTH);
			
			//System.out.println(testcase.getLength());
			
			EFSMTransition lastTranstion = testcase.getPath().getTransitionAt(testcase.getPath().getLength()-1);
			assertTrue(lastTranstion.getTgt().equals(endState));
			
		}
	}
	
	@Test
	public void testGetTestcaseRandom() {

		try {
			org.evosuite.Properties.getInstance().setValue("random_seed", "892327732");
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchParameterException e) {
			System.err.println("Unable to set Evosuite global property: " + "random_seed");
		}
	
		MBTProperties.SUT_EFSM = "labrecruits.random_impossible";
		EFSMFactory efsmFactory = EFSMFactory.getInstance();
		assertNotNull(efsmFactory);
		EFSM efsm = efsmFactory.getEFSM();
		assertNotNull (efsm);
		
		Set<EFSMState> states = efsm.getStates();
		
		Integer nTests = 1000;
		for (int i = 0; i < nTests; i++) {
			//System.out.print(i+" ");

			EFSMState endState = (EFSMState) Randomness.choice(states);	
			assertTrue(states.contains(endState));
			
			StateCoverageGoal goal = new StateCoverageGoal(endState);
			
			CoverageGoalConstrainedTestFactory testFactory = new CoverageGoalConstrainedTestFactory(efsm,goal);
			assertNotNull(testFactory);
			
			AbstractTestSequence testcase = (AbstractTestSequence)testFactory.getTestcase();
			assertNotNull(testcase);
			
			//System.out.println(testcase.getLength());
			
			EFSMTransition lastTranstion = testcase.getPath().getTransitionAt(testcase.getPath().getLength()-1);
			assertTrue(lastTranstion.getTgt().equals(endState));
			
		}
		
	}
	
}
