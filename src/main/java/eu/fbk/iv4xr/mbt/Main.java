/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
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
		generationStrategy = new 
				SearchBasedStrategy<Chromosome>();
	}

	
	private void run () {
		SuiteChromosome solution = generationStrategy.generateTests();
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence)solution.getTestChromosome(i).getTestcase();
			System.out.println("Valid: " + testcase.isValid());
			System.out.println(testcase.toDot());
		}
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
