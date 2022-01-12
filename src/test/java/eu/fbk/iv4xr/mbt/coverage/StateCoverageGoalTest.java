/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;

import org.evosuite.Properties;
import org.evosuite.utils.Randomness;

/**
 * @author kifetew
 *
 */
class StateCoverageGoalTest {

	EFSMState state;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		Properties.RANDOM_SEED = 1234L;
		Randomness.getInstance();
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";		
		state = new EFSMState("b_0");
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal#StateCoverageGoal(eu.fbk.se.labrecruits.LabRecruitsState)}.
	 */
	@Test
	void testStateCoverageGoal() {
		StateCoverageGoal stateGoal = new StateCoverageGoal(state);
		assertNotNull(stateGoal);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal#getFitness(org.evosuite.ga.Chromosome)}.
	 */
	@Test
	void testGetFitnessChromosome() {
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		TestFactory testFactory = new RandomLengthTestFactory<>(efsm);
		RandomLengthTestChromosomeFactory<MBTChromosome> cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		
		StateCoverageGoal stateGoal = new StateCoverageGoal(state);
		assertNotNull(stateGoal);
		System.out.println("Chromosome: ("+chromosome.getTestcase().isValid()+") \n" + chromosome.toString());
		double fitness = stateGoal.getFitness(chromosome);
		System.out.println("Fitness: " + fitness);
		// assertTrue(fitness == 95d);
	}

}
