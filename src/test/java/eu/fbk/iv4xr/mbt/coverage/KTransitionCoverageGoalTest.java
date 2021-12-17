package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import java.util.List;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;
 



public class KTransitionCoverageGoalTest {
	
	@Test
	public void testGetFitnessChromosome() {
		// Load model
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		MBTProperties.k_transition_size = 3;
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		// create a random chromosome
		TestFactory testFactory = new RandomLengthTestFactory<>(efsm);
		RandomLengthTestChromosomeFactory<MBTChromosome> cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		// call goal factory
		KTransitionCoverageGoalFactory goalFactory = new KTransitionCoverageGoalFactory<>();
		assertNotNull(goalFactory);
		List<KTransitionCoverageGoal> coverageGoals = goalFactory.getCoverageGoals();
		assertFalse(coverageGoals.isEmpty());
		// select first
		KTransitionCoverageGoal goal = coverageGoals.get(0);
		// check fitness of chromosome
		double fitness = goal.getFitness(chromosome);
		assertTrue(fitness < 1);
		
	}

}
