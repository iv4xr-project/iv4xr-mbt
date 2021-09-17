/**
 * 
 */
package eu.fbk.iv4xr.mbt.testsuite;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class MBTSuiteChromosome extends SuiteChromosome {
	@Override
	public double getFitness() {
		if (tests.isEmpty()) {
			return Double.MAX_VALUE;
		}
		double fitness = 0;
		for (MBTChromosome test : tests) {
			fitness += test.getFitness();
		}
		return fitness;
	}
	
}
