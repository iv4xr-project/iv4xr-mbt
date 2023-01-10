/**
 * 
 */
package eu.fbk.iv4xr.mbt.minimization;

import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 * Implementations of this interface need to implement a minimization function which given a test suite as a map of 
 * test cases and the set of goals covered by each test returns a similar map containing the minimal number of test cases 
 * that are required to cover all the goals.
 */
public interface Minimizer {
	
	SuiteChromosome minimize (SuiteChromosome solution);
}
