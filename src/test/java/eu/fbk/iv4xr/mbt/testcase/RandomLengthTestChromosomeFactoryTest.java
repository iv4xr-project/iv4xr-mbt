/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import de.upb.testify.efsm.EFSM;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory;

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
		RandomLengthTestChromosomeFactory cFactory = new RandomLengthTestChromosomeFactory(efsm);
		assertNotNull(cFactory);
		MBTChromosome chromosome = (MBTChromosome) cFactory.getChromosome();
		assertNotNull (chromosome);
		Testcase testcase = chromosome.getTestcase();
		assertNotNull(testcase);
		System.out.println(((AbstractTestSequence) testcase).toDot());
	}

}
