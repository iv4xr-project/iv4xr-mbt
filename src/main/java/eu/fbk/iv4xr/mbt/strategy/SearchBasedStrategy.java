/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.List;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.testsuite.TestSuiteChromosome;

import eu.fbk.iv4xr.mbt.coverage.CoverageGoalFactory;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 */
public class SearchBasedStrategy<T extends Chromosome> extends GenerationStrategy {

	/**
	 * 
	 */
	public SearchBasedStrategy() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Set some Evosuite global parameters that control properties of the search algorithms
	 */
	private static void configureEvosuiteSettings () {
		// TODO currently Archive supports only TestChromosome/TestSuiteChromosome
		// we should extend it to include other types of chromosomes, e.g., MBTChromosome
		// and use it externally, since Evosuite is just a library here
		Properties.TEST_ARCHIVE = false;
		
		
	}
	
	@Override
	public SuiteChromosome generateTests() {
		
		// set some Evosuite properties that control the search algorithms
		configureEvosuiteSettings();
		
		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		
		// setup the search algorithm
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		
		CoverageGoalFactory<?> fitnessFactory = algorithmFactory.getFitnessFactory();
		List<?> goals = fitnessFactory.getCoverageGoals();
		searchAlgorithm.addFitnessFunctions((List<FitnessFunction<T>>) goals);
		
		// invoke generate solution on the algorithm
		searchAlgorithm.generateSolution();
		List<T> bestIndividuals = searchAlgorithm.getBestIndividuals();
		
		// return result
		SuiteChromosome solution = new SuiteChromosome();
		for (T test : bestIndividuals) {
			solution.addTest((MBTChromosome) test);
		}
		return solution;
	}

}
