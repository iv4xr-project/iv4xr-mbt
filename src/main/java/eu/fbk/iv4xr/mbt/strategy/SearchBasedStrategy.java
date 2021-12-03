/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.SelectionFunction;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;


/**
 * @author kifetew
 *
 */
public class SearchBasedStrategy<T extends Chromosome> extends GenerationStrategy {

	private static Logger logger = LoggerFactory.getLogger(SearchBasedStrategy.class);
	
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
		
		// disable bloat control temporarily
		Properties.CHECK_BEST_LENGTH = false;
		
		Properties.LOG_LEVEL = "ERROR";
		
//		Properties.MUTATION_RATE = 0.2;
//		Properties.CROSSOVER_RATE = 0.6;
		Properties.SEARCH_BUDGET = MBTProperties.SEARCH_BUDGET;
		Properties.SELECTION_FUNCTION = SelectionFunction.TOURNAMENT;
		Properties.P_TEST_INSERTION = 0.2;
	}
	
	
	/**
	 * Generate tests to cover specific states in the model passed as arguments
	 * NOTE: MBT must be launched with the criterion STATE.
	 * @param states
	 * @return
	 */
	public SuiteChromosome generateTests(Set<String> states) {
		
		if (MBTProperties.MODELCRITERION.length != 1 || 
				MBTProperties.MODELCRITERION[0] != MBTProperties.ModelCriterion.STATE) {
			throw new RuntimeException("This method must be used only with STATE coverage criterion. Run mbt with -Dcriterion=STATE");
		}
		
		// set some Evosuite properties that control the search algorithms
		configureEvosuiteSettings();
		
		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		
		// setup the search algorithm
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		
		List<?> goals = algorithmFactory.getCoverageGoals();
		searchAlgorithm.addFitnessFunctions((List<FitnessFunction<T>>) goals);
		
		Iterator<?> iterator = goals.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			StateCoverageGoal state = (StateCoverageGoal)next;
			if (!states.contains(state.getState().getId())) {
				iterator.remove();
			}
		}
		
		
		logger.debug("Total goals: {}", goals.size());
		
		coverageTracker = new CoverageTracker(goals);
		searchAlgorithm.addListener(getCoverageTracker());
		searchAlgorithm.addStoppingCondition(getCoverageTracker());
		
		// invoke generate solution on the algorithm
		searchAlgorithm.generateSolution();

		return getCoverageTracker().getTestSuite();
	}
	
	@Override
	public SuiteChromosome generateTests() {
		
		// set some Evosuite properties that control the search algorithms
		configureEvosuiteSettings();
		
		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		
		// setup the search algorithm
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		
		List<?> goals = algorithmFactory.getCoverageGoals();
		searchAlgorithm.addFitnessFunctions((List<FitnessFunction<T>>) goals);
		logger.debug("Total goals: {}", goals.size());
		
		// MOSA has ots 
//		if (MBTProperties.ALGORITHM != Algorithm.MOSA) {
			coverageTracker = new CoverageTracker(goals);
			searchAlgorithm.addListener(getCoverageTracker());
			searchAlgorithm.addStoppingCondition(getCoverageTracker());
//		}
		
		// invoke generate solution on the algorithm
		searchAlgorithm.generateSolution();
//		List<T> bestIndividuals = searchAlgorithm.getBestIndividuals();
		
		// return result from coverageTracker (archive)
//		SuiteChromosome solution = new MBTSuiteChromosome();
//		for (MBTChromosome test : coverageTracker.getTestSuite()) {
//		for (T test : bestIndividuals) {
//			solution.addTest((MBTChromosome) test);
//		}
		return getCoverageTracker().getTestSuite();
	}

}
