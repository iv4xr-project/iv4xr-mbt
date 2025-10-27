/**
 * 
 */
package eu.fbk.iv4xr.mbt.testsuite;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class MBTSuiteChromosome extends SuiteChromosome {
	private static final long serialVersionUID = -6690969001537190802L;

	
	public MBTSuiteChromosome() {
		super();
	}
	
	public MBTSuiteChromosome(ChromosomeFactory<MBTChromosome> testChromosomeFactory) {
		super(testChromosomeFactory);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MBTSuiteChromosome clone() {
		MBTSuiteChromosome copy = new MBTSuiteChromosome(this.testChromosomeFactory);
		for (MBTChromosome test : tests) {
			copy.addTest((MBTChromosome) test.clone());
		}
		return copy;
	}
	
	
	@Override
	public double getFitness() {
		
		if (tests == null || tests.size() == 0) {
			return Double.MAX_VALUE;
		}
		
		double fitness = 0d;
		for (MBTChromosome test : tests) {
			fitness += test.getFitness();
		}
		
		return fitness;
	}
}
