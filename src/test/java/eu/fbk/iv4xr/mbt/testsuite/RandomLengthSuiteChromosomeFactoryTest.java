/**
 * 
 */
package eu.fbk.iv4xr.mbt.testsuite;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
class RandomLengthSuiteChromosomeFactoryTest {

	RandomLengthTestChromosomeFactory testCaseFactory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";
		EFSMFactory mFactory = EFSMFactory.getInstance();
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		TestFactory testFactory = new RandomParameterLengthTestFactory(efsm);
		testCaseFactory = new RandomLengthTestChromosomeFactory(testFactory);
		assertNotNull(testCaseFactory);
		MBTChromosome chromosome = (MBTChromosome) testCaseFactory.getChromosome();
		assertNotNull (chromosome);
		Testcase testcase = chromosome.getTestcase();
		assertNotNull(testcase);
		System.out.println(((AbstractTestSequence) testcase).toDot());
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testsuite.RandomLengthSuiteChromosomeFactory#RandomLengthSuiteChromosomeFactory()}.
	 */
	@Test
	void testRandomLengthSuiteChromosomeFactory() {
		RandomLengthSuiteChromosomeFactory suiteFactory = new RandomLengthSuiteChromosomeFactory(testCaseFactory);
		assertNotNull(suiteFactory);
		MBTSuiteChromosome suiteChromosome = suiteFactory.getChromosome();
		assertNotNull(suiteChromosome);
	}


	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.testsuite.RandomLengthSuiteChromosomeFactory#getChromosome()}.
	 */
	@Test
	void testGetChromosome() {
		RandomLengthSuiteChromosomeFactory suiteFactory = new RandomLengthSuiteChromosomeFactory(testCaseFactory);
		assertNotNull(suiteFactory);
		for (int i = 1; i < 100; i++) {
			MBTSuiteChromosome suiteChromosome = suiteFactory.getChromosome();
			assertNotNull(suiteChromosome);
			assert(suiteChromosome.size() > 0);
			assert(suiteChromosome.totalLengthOfTestCases() > 0);
			System.out.println(suiteChromosome.toString());
		}
	}

}
