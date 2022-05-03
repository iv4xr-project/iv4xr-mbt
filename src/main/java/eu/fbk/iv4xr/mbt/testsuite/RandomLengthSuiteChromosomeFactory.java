/**
 * 
 */
package eu.fbk.iv4xr.mbt.testsuite;

import org.evosuite.Properties;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;

/**
 * @author kifetew
 *
 */
public class RandomLengthSuiteChromosomeFactory implements ChromosomeFactory<MBTSuiteChromosome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171599793081703149L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthSuiteChromosomeFactory.class);

	
	/** Factory to manipulate and generate method sequences */
	protected ChromosomeFactory<MBTChromosome> testChromosomeFactory;

	/**
	 * <p>Constructor for TestSuiteChromosomeFactory.</p>
	 */
//	public RandomLengthSuiteChromosomeFactory() {
//		testChromosomeFactory = null; //new RandomLengthTestChromosomeFactory<MBTChromosome>();
//	}

	/**
	 * <p>Constructor for TestSuiteChromosomeFactory.</p>
	 *
	 * @param testFactory a {@link org.evosuite.ga.ChromosomeFactory} object.
	 */
	public RandomLengthSuiteChromosomeFactory(ChromosomeFactory<MBTChromosome> testFactory) {
		testChromosomeFactory = testFactory;
	}

	/**
	 * <p>setTestFactory</p>
	 *
	 * @param factory a {@link org.evosuite.ga.ChromosomeFactory} object.
	 */
	public void setTestFactory(ChromosomeFactory<MBTChromosome> factory) {
		testChromosomeFactory = factory;
	}

	/** {@inheritDoc} */
	@Override
	public MBTSuiteChromosome getChromosome() {

		MBTSuiteChromosome chromosome = new MBTSuiteChromosome(testChromosomeFactory);
		//chromosome.clearTests();

		int numTests = Randomness.nextInt(Properties.MIN_INITIAL_TESTS,
		                                  Properties.MAX_INITIAL_TESTS + 1);

		for (int i = 0; i < numTests; i++) {
			MBTChromosome test = testChromosomeFactory.getChromosome();
			chromosome.addTest(test);
		}
		return chromosome;
	}

}
