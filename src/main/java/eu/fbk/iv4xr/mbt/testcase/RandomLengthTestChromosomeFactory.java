/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kifetew
 *
 */
public class RandomLengthTestChromosomeFactory<T extends Chromosome> implements ChromosomeFactory<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171599793081703149L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestChromosomeFactory.class);

	
	private TestFactory testFactory = null;
	
	/**
	 * 
	 */
	public RandomLengthTestChromosomeFactory(TestFactory testFactory) {
		this.testFactory = testFactory;
	}

	@Override
	public T getChromosome() {
		T chromosome = (T) new MBTChromosome();
		Testcase testcase = testFactory.getTestcase();
		((MBTChromosome)chromosome).setTestcase(testcase);
		return chromosome;
	}

}
