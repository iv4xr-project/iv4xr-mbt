/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.testify.efsm.EFSM;

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

	
	
	private EFSM efsm = null;
	private RandomLengthTestFactory testFactory = null;
	
	/**
	 * 
	 */
	public RandomLengthTestChromosomeFactory(EFSM efsm) {
		this.efsm = efsm;
		testFactory = new RandomLengthTestFactory(this.efsm);
	}

	@Override
	public T getChromosome() {
		T chromosome = (T) new MBTChromosome();
		Testcase testcase = testFactory.getTestcase();
		((MBTChromosome)chromosome).setTestcase(testcase);
		return chromosome;
	}

}
