/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;

/**
 * @author kifetew
 *
 */
class StateCoverageGoalTest {

	LabRecruitsState state;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "buttons_doors_1";		
		state = new LabRecruitsState("b_0");
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
		LabRecruitsEFSMFactory mFactory = LabRecruitsEFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		TestFactory testFactory = new RandomParameterLengthTestFactory(efsm);
		RandomLengthTestChromosomeFactory<MBTChromosome> cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		
		StateCoverageGoal stateGoal = new StateCoverageGoal(state);
		assertNotNull(stateGoal);
		
		double fitness = stateGoal.getFitness(chromosome);
		System.out.println("Fitness: " + fitness);
//		assertTrue (fitness == 0d);
	}

}
