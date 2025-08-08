/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.List;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.algorithm.random.RandomSearch;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * Iteratively generate random tests. If adding the random test
 * leads to improved fitness, keep it, otherwise drop it again.
 * 
 * Class adapted from EvoSuite
 * 
 * This class is not currently used since it does the same thing as {@link SearchBasedStrategy} 
 * only with the {@link RandomSearch} algorithm. It could eventually be removed if no longer used.
 * 
 * @author kifetew
 *
 */
@Deprecated
public class RandomTestStrategy<T extends Chromosome<T>> extends GenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(RandomTestStrategy.class);
	
	
	/**
	 * Set some Evosuite global parameters that control properties of the search algorithms
	 */
	private static void configureEvosuiteSettings () {
		
		Properties.LOG_LEVEL = "WARN";
		Properties.SEARCH_BUDGET = MBTProperties.SEARCH_BUDGET;
		MBTProperties.ALGORITHM = Algorithm.RANDOM_SEARCH;
		Properties.ALGORITHM = Properties.Algorithm.RANDOM_SEARCH;
	}
	
	@Override
	public SuiteChromosome generateTests() {
		
		configureEvosuiteSettings();
		
		LoggingUtils.getEvoLogger().info("* Using random test generation");

		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		List<?> goals = algorithmFactory.getCoverageGoals();

		// setup the search algorithm
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		
		searchAlgorithm.addFitnessFunctions((List<FitnessFunction<T>>) goals);
		
		coverageTracker = new CoverageTracker(goals);
		searchAlgorithm.addListener(getCoverageTracker());
		searchAlgorithm.addStoppingCondition(getCoverageTracker());
		
		
		StoppingCondition stoppingCondition = getStoppingCondition();

		searchAlgorithm.addStoppingCondition(stoppingCondition);
		
		searchAlgorithm.generateSolution();
		
		return getCoverageTracker().getTestSuite();
		
	}
	
}
