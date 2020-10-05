/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

//import de.upb.testify.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.MBTProperties;

/**
 * @author kifetew
 *
 */
class RandomLengthTestChromosomeFactoryTest {

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory#getChromosome()}.
	 */
	@Test
	void testGetChromosome() {
		MBTProperties.SUT_EFSM = "random_default";
		LabRecruitsEFSMFactory mFactory = LabRecruitsEFSMFactory.getInstance();
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		TestFactory testFactory = new RandomParameterLengthTestFactory(efsm);
		RandomLengthTestChromosomeFactory cFactory = new RandomLengthTestChromosomeFactory(testFactory);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		Testcase testcase = chromosome.getTestcase();
		assertNotNull(testcase);
		System.out.println(((AbstractTestSequence) testcase).toDot());
	}

}
