/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.testsuite.TestSuiteChromosome;

import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
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
		generationStrategy = new SearchBasedStrategy();
	}

	
	private void run () {
		SuiteChromosome solution = generationStrategy.generateTests();
		System.out.println(solution.getFitness());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		main.run();

	}

}
