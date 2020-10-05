/**
 * 
 */
package eu.fbk.iv4xr.mbt.algorithm.operators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.evosuite.ga.ConstructionFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointRelativePathCrossOver;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;

/**
 * @author kifetew
 *
 */
class SinglePointRelativePathCrossOverTest {

	MBTChromosome chromosome1;
	MBTChromosome chromosome2;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		LabRecruitsEFSMFactory modelFactory = LabRecruitsEFSMFactory.getInstance();
		EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> efsm = modelFactory.getEFSM();
		TestFactory testFactory = new RandomParameterLengthTestFactory(efsm);
		RandomLengthTestChromosomeFactory<MBTChromosome> chromosomeFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		chromosome1 = chromosomeFactory.getChromosome();
		chromosome2 = chromosomeFactory.getChromosome();
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointRelativePathCrossOver#crossOver(org.evosuite.ga.Chromosome, org.evosuite.ga.Chromosome)}.
	 */
	@Test
	void testCrossOverChromosomeChromosome() {
		AbstractTestSequence<EFSMState, EFSMParameter<String>, LabRecruitsContext, Transition<EFSMState,EFSMParameter<String>,LabRecruitsContext>> tc1 = (AbstractTestSequence)chromosome1.getTestcase();
		AbstractTestSequence<EFSMState, EFSMParameter<String>, LabRecruitsContext, Transition<EFSMState,EFSMParameter<String>,LabRecruitsContext>> tc2 = (AbstractTestSequence)chromosome2.getTestcase();
		assertTrue (tc1.getPath().getStates().get(0).getId().equalsIgnoreCase(tc2.getPath().getStates().get(0).getId()));
		System.out.println("TC1: " + tc1.getPath().toDot());
		System.out.println("TC2: " + tc2.getPath().toDot());
		SinglePointRelativePathCrossOver crossoverFunction = new SinglePointRelativePathCrossOver();
		try {
			crossoverFunction.crossOver(chromosome1, chromosome2);
		} catch (ConstructionFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("TC1: " + tc1.getPath().toDot());
		System.err.println("TC2: " + tc2.getPath().toDot());
	}

}
