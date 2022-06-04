/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.MBTProperties.Strategy;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 */
public class WholesuiteStrategy<T extends Chromosome> extends SearchBasedStrategy {
	
	private static Logger logger = LoggerFactory.getLogger(WholesuiteStrategy.class);
	
	public WholesuiteStrategy() {
		super();
	}
	
	@Override
	public SuiteChromosome generateTests() {
		// set some Evosuite properties that control the search algorithms
		configureEvosuiteSettings();
		
		// Wholesuite settings
		MBTProperties.ALGORITHM = Algorithm.STEADY_STATE_GA;
		MBTProperties.STRATEGY = Strategy.SUITE;
		
		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		
		// setup the search algorithm
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		
		List<?> goals = algorithmFactory.getCoverageGoals();
		searchAlgorithm.addFitnessFunctions((List<FitnessFunction<T>>) goals);
		logger.debug("Total goals: {}", goals.size());
		
		coverageTracker = new CoverageTracker(goals);
		searchAlgorithm.addListener(coverageTracker);
		searchAlgorithm.addStoppingCondition(coverageTracker);
		
		// invoke generate solution on the algorithm
		searchAlgorithm.generateSolution();

		return coverageTracker.getTestSuite();
	}
}
