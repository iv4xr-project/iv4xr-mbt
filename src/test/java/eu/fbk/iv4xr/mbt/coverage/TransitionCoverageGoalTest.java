/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsFreeTravelTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;

/**
 * @author kifetew
 *
 */
class TransitionCoverageGoalTest {

	EFSMTransition transition;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";		
		//transition = new LabRecruitsFreeTravelTransition();
		transition = new EFSMTransition<>();
		
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal#TransitionCoverageGoal(eu.fbk.se.labrecruits.LabRecruitsState)}.
	 */
	@Test
	void testTransitionCoverageGoal() {
		TransitionCoverageGoal goal = new TransitionCoverageGoal(transition);
		assertNotNull(goal);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal#getFitness(org.evosuite.ga.Chromosome)}.
	 */
	@Test
	void testGetFitnessChromosome() {
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		TestFactory testFactory = new RandomParameterLengthTestFactory(efsm);
		RandomLengthTestChromosomeFactory<MBTChromosome> cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory, efsm);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		
		TransitionCoverageGoal goal = new TransitionCoverageGoal((EFSMTransition) efsm.getTransitons().toArray()[0]);
		assertNotNull(goal);
		
		double fitness = goal.getFitness(chromosome);
		System.out.println("Fitness: " + fitness);
		assertTrue(fitness == 0d);
	}

}
