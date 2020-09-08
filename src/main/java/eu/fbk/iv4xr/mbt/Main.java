/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 * Main entry point to the MBT
 */
public class Main {

	private GenerationStrategy generationStrategy;
	
	/**
	 * 
	 */
	public Main() {
		generationStrategy = new SearchBasedStrategy<Chromosome>();
	}

	
	private void run () {
		SuiteChromosome solution = generationStrategy.generateTests();
		System.out.println(((AbstractTestSequence)solution.getTestChromosome(0).getTestcase()).toDot());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		main.run();
		System.exit(0);
	}

}
