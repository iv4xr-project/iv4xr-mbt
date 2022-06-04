package eu.fbk.iv4xr.mbt.strategy;

import java.util.List;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * 
 * @author kifetew
 *
 */
public class WholesuiteStrategyTest {

	@Test
	public void testGenerateTests() {
		MBTProperties.SUT_EFSM = "labrecruits.random_large";
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
				ModelCriterion.TRANSITION 
		};
	
		
		SearchBasedStrategy sbStrategy = new WholesuiteStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests();
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		
		System.out.println("\nGenerated "+testChromosomes.size()+" test cases");
		for(MBTChromosome chr : testChromosomes) {
			AbstractTestSequence testcase = (AbstractTestSequence) chr.getTestcase();
			System.out.println("Fitness: "+testcase.getFitness());
			System.out.println(testcase.toString());
			
		}
		
	}
}
