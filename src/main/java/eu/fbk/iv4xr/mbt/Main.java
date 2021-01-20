/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.TestExecutor;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

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
		//solution.
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence)solution.getTestChromosome(i).getTestcase();
			System.out.println("Valid: " + testcase.isValid());
			System.out.println(testcase.toDot());
			System.out.println(testcase.toString());
			if (!testcase.isValid()) {
				// re-execute for debugging
				executeForDebug (testcase);
			}
		}
	}
	
	private void executeForDebug(AbstractTestSequence testcase) {
//		TestExecutor executor = new EFSMTestExecutor<>();
		ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
		if (!executionResult.isSuccess()) {
			System.err.println("INVALID: " + testcase.toDot());
		}
	}


	private void setProperties () {
		MBTProperties.SEARCH_BUDGET = 500;
		MBTProperties.LR_mean_buttons = 1;
		MBTProperties.LR_n_buttons = 20;
		MBTProperties.LR_n_doors = 10 ;
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		MBTProperties.LR_seed = 370327;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		//main.setProperties();
		main.run();
		System.exit(0);
	}

}
