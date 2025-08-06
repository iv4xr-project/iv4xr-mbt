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
/**
 * 
 */
package eu.fbk.iv4xr.mbt.algorithm.random;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * RandomSearch class adapted from Evosuite. It implements a simple random search algorithm
 * The algorithm gets a new individual and evaluates its fitness, until stopping condition is reached.
 * The tests that cover new goals are collected by the CoverageTracker instance attached to the algorithm.
 * CoverageTracker is both a search listener and a stopping condition, hence when full coverage is reached by the collected tests, 
 * it ends the search (even before the search budget is completed).
 * </p>
 * 
 * @author Gordon Fraser
 */
public class RandomSearch<T extends Chromosome<T>> extends GeneticAlgorithm<T> {

	private static final Logger logger = LoggerFactory.getLogger(RandomSearch.class);

	/** Boolean vector to indicate whether each test goal is covered or not. **/
	protected Set<FitnessFunction<T>> uncoveredGoals = new LinkedHashSet<FitnessFunction<T>>();
	
	/**
	 * <p>
	 * Constructor for RandomSearch.
	 * </p>
	 * 
	 * @param factory
	 *            a {@link org.evosuite.ga.ChromosomeFactory} object.
	 */
	public RandomSearch(ChromosomeFactory<T> factory) {
		super(factory);
	}

	private static final long serialVersionUID = -7685015421245920459L;

	/* (non-Javadoc)
	 * @see org.evosuite.ga.GeneticAlgorithm#evolve()
	 */
	/** {@inheritDoc} */
	@Override
	protected void evolve() {
		T newChromosome = chromosomeFactory.getChromosome();
		calculateFitness(newChromosome);
//		getFitnessFunction().getFitness(newChromosome);
//		notifyEvaluation(newChromosome);
		currentIteration++;
	}

	
	/** {@inheritDoc} */
	protected void calculateFitness(T c) {
		for (FitnessFunction<T> fitnessFunction : this.fitnessFunctions) {
			// evaluate only if the goal is uncovered?
			if (uncoveredGoals.contains(fitnessFunction)) {
				double value = fitnessFunction.getFitness(c);
				if (value == 0.0) {
					uncoveredGoals.remove(fitnessFunction);
				}
				notifyEvaluation(c);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.evosuite.ga.GeneticAlgorithm#initializePopulation()
	 */
	/** {@inheritDoc} */
	@Override
	public void initializePopulation() {
		generateRandomPopulation(1);
		calculateFitnessAndSortPopulation();
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.GeneticAlgorithm#generateSolution()
	 */
	/** {@inheritDoc} */
	@Override
	public void generateSolution() {
		// keep track of covered goals
		for (FitnessFunction<T> goal : fitnessFunctions) {
			uncoveredGoals.add(goal);
		}
		
		notifySearchStarted();

		currentIteration = 0;
		while (!isFinished()) {
			evolve();
			this.notifyIteration();
		}
		notifySearchFinished();
	}
}
