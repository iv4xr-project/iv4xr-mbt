/**
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
package eu.fbk.iv4xr.mbt.algorithm.ga.mosa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.ProgressMonitor;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.SearchListener;
import org.evosuite.ga.metaheuristics.mosa.comparators.MOSADominanceComparator;
import org.evosuite.ga.operators.selection.SelectionFunction;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.secondaryobjectives.TestCaseSecondaryObjective;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

import eu.fbk.iv4xr.mbt.algorithm.ga.mosa.FastNonDominatedSorting;


/**
 * Abstract class for MOSA
 * 
 * @author Annibale Panichella, Fitsum M. Kifetew
 *
 * @param <T>
 */
public abstract class AbstractMOSA<
	T extends Chromosome,
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> extends GeneticAlgorithm<T> {

	private static final long serialVersionUID = 146182080947267628L;

	private static final Logger logger = LoggerFactory.getLogger(MOSA.class);

	/**  keep track of overall suite fitness and coverage */
	protected List<FitnessFunction> suiteFitnesses;

	/** Selection function to select parents */
	protected SelectionFunction<T> selectionFunction = new MOSATournamentSelection<T>();

	/** Selected ranking strategy **/
	protected Ranking<T> ranking;

	/**
	 * Constructor
	 * 
	 * @param factory
	 *            a {@link org.evosuite.ga.ChromosomeFactory} object
	 */
	public AbstractMOSA(ChromosomeFactory<T> factory) {
		super(factory);
		suiteFitnesses = new ArrayList<FitnessFunction>();
		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		for (MBTProperties.ModelCriterion criterion : MBTProperties.MODELCRITERION){
			suiteFitnesses.addAll(algorithmFactory.getCoverageGoals (criterion));
		}
		// set the ranking strategy
		if (MBTProperties.RANKING_TYPE ==  MBTProperties.RankingType.PREFERENCE_SORTING)
			ranking = new RankBasedPreferenceSorting<T>();
		else if (MBTProperties.RANKING_TYPE ==  MBTProperties.RankingType.FAST_NON_DOMINATED_SORTING)
			ranking = new FastNonDominatedSorting<T>();
		else
			ranking = new RankBasedPreferenceSorting<T>(); // default ranking strategy

		// set the secondary objectives of test cases (useful when MOSA compares two test
		// cases to, for example, update the archive)
		TestCaseSecondaryObjective.setSecondaryObjectives();
	}

	/**
	 * This method is used to generate new individuals (offsprings) from
	 * the current population
	 * @return offspring population
	 */
	@SuppressWarnings("unchecked")
	protected List<T> breedNextGeneration() {
		List<T> offspringPopulation = new ArrayList<T>(MBTProperties.POPULATION);
		// we apply only Properties.POPULATION/2 iterations since in each generation
		// we generate two offsprings
		for (int i=0; i < MBTProperties.POPULATION/2 && !isFinished(); i++){
			// select best individuals
			T parent1 = selectionFunction.select(population);
			T parent2 = selectionFunction.select(population);
			T offspring1 = (T) parent1.clone();
			T offspring2 = (T) parent2.clone();
			// apply crossover 
			try {
				if (Randomness.nextDouble() <= MBTProperties.CROSSOVER_RATE) {
					crossoverFunction.crossOver(offspring1, offspring2);
				} 
			} catch (ConstructionFailedException e) {
				logger.debug("CrossOver failed.");
				continue;
			} 

			//removeUnusedVariables(offspring1);
			//removeUnusedVariables(offspring2);

			// apply mutation on offspring1
			offspring1.mutate();
			notifyMutation(offspring1);
//			mutate(offspring1, parent1);
			if (offspring1.isChanged()) {
//				clearCachedResults(offspring1);
				offspring1.updateAge(currentIteration);
				calculateFitness(offspring1); 
				offspringPopulation.add(offspring1);
			}

			// apply mutation on offspring2
			offspring2.mutate();
			notifyMutation(offspring2);
//			mutate(offspring2, parent2);
			if (offspring2.isChanged()) {
//				clearCachedResults(offspring2);
				offspring2.updateAge(currentIteration);
				calculateFitness(offspring2);
				offspringPopulation.add(offspring2);
			}	
		}
		// Add new randomly generate tests
		for (int i = 0; i<MBTProperties.POPULATION * MBTProperties.P_TEST_INSERTION; i++){
			T tch = null;
//			if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()){
				tch = this.chromosomeFactory.getChromosome();
				tch.setChanged(true);
//			} else {
//				tch = (T) Randomness.choice(getArchive()).clone();
//				tch.mutate(); //tch.mutate();
//			}
//			if (tch.isChanged()) {
				tch.updateAge(currentIteration);
				calculateFitness(tch);
				offspringPopulation.add(tch);
//			}
		}
//		logger.info("Number of offsprings = {}", offspringPopulation.size());
		return offspringPopulation;
	}

	/**
	 * Method used to mutate an offspring
	 */
	private void mutate(T offspring, T parent){
		offspring.mutate();
//		MBTChromosome tch = (MBTChromosome) offspring;
		if (!offspring.isChanged()) {
			// if offspring is not changed, we try
			// to mutate it once again
			offspring.mutate();
		}
		notifyMutation(offspring);
	}

	/**
	 * This method clears the cached results for a specific chromosome (e.g., fitness function
	 * values computed in previous generations). Since a test case is changed via crossover
	 * and/or mutation, previous data must be recomputed.
	 * @param chromosome TestChromosome to clean
	 */
	public void clearCachedResults(T chromosome){
//		((MBTChromosome) chromosome).clearCachedMutationResults();
		((MBTChromosome) chromosome).clearCachedResults();
//		((MBTChromosome) chromosome).clearMutationHistory();
		((MBTChromosome) chromosome).getFitnessValues().clear();
	}

	/**
	 * Notify all search listeners of fitness evaluation
	 * 
	 * @param chromosome
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 */
	@Override
	protected void notifyEvaluation(Chromosome chromosome) {
		for (SearchListener listener : listeners) {
			if (listener instanceof ProgressMonitor)
				continue;
			listener.fitnessEvaluation(chromosome);
		}
	}

	/**
	 * Calculate fitness for the whole population
	 */
	protected void calculateFitness() {
		logger.debug("Calculating fitness for " + population.size() + " individuals");

		Iterator<T> iterator = population.iterator();
		while (iterator.hasNext()) {
			T c = iterator.next();
			if (isFinished()) {
				if (c.isChanged())
					iterator.remove();
			} else {
				calculateFitness(c);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBestIndividuals() {
		//get final test suite (i.e., non dominated solutions in Archive)
		List<T> finalTestSuite = this.getFinalTestSuite();
		if (finalTestSuite.isEmpty()) {
			return Arrays.asList((T) new TestSuiteChromosome());
		}

		SuiteChromosome bestTestCases = new MBTSuiteChromosome();
		for (T test : finalTestSuite) {
			bestTestCases.addTest( (MBTChromosome) test);
		}
		for (FitnessFunction<T> f : this.getCoveredGoals()){
			bestTestCases.getCoveredGoals().add((FitnessFunction) f);
		}
		// compute overall fitness and coverage
		double fitness = this.fitnessFunctions.size() - numberOfCoveredTargets();
		double coverage = ((double) numberOfCoveredTargets()) / ((double) this.fitnessFunctions.size());
		logger.debug("Fitness: {}, Coverage: {}", fitness, coverage);
//		for (TestSuiteFitnessFunction suiteFitness : suiteFitnesses){
//			bestTestCases.setFitness(suiteFitness, fitness);
//			bestTestCases.setCoverage(suiteFitness, coverage);
//			bestTestCases.setNumOfCoveredGoals(suiteFitness, (int) numberOfCoveredTargets());
//			bestTestCases.setNumOfNotCoveredGoals(suiteFitness, (int) (this.fitnessFunctions.size()-numberOfCoveredTargets()));
//		}
		List<T> bests = new ArrayList<T>(1);
		bests.addAll((Collection<? extends T>) bestTestCases.getTestChromosomes());
		return bests;
	}

	/** 
	 * This method computes the fitness scores only for the current goals
	 * @param c chromosome
	 */
	protected abstract void calculateFitness(T c);

	protected abstract List<T> getFinalTestSuite();

	protected abstract List<T> getArchive();

	/**
	 * This method extracts non-dominated solutions (tests) according to all covered goal (e.g., branches)
	 * @param solutionSet set of test cases to analyze with the "dominance" relationship
	 * @return the non-dominated set of test cases
	 */
	protected List<T> getNonDominatedSolutions(List<T> solutions){
		MOSADominanceComparator<T> comparator = new MOSADominanceComparator<>(this.getCoveredGoals());
		List<T> next_front = new ArrayList<T>(solutions.size());
		boolean isDominated;
		for (T p : solutions){
			isDominated = false;
			List<T> dominatedSolutions = new ArrayList<T>(solutions.size());
			for (T best : next_front){
				int flag = comparator.compare(p, best);
				if (flag == -1) {
					dominatedSolutions.add(best);
				}
				if (flag == +1){
					isDominated = true;
				}	
			}
			if (isDominated)
				continue;

			next_front.add(p);
			next_front.removeAll(dominatedSolutions);
		}
		return next_front;
	}

	/**
	 * This method verifies whether two TestCromosome contain
	 * the same test case. Here the equality is computed looking at
	 * the strings composing the tests. This method is strongly needed 
	 * in {@link AbstractMOSA#breedNextGeneration()}.
	 * @param test1 first test
	 * @param test2 second test
	 * @return true if the test1 and test 2 (meant as String) are equal
	 * to each other; false otherwise.
	 */
	protected boolean areEqual(T test1, T test2){
		TestChromosome tch1 = (TestChromosome) test1;
		TestChromosome tch2 = (TestChromosome) test2;

		if (tch1.size() != tch2.size())
			return false;
		if (tch1.size() == 0)
			return false;
		if (tch2.size() == 0)
			return false;

		return tch1.getTestCase().toCode().equals(tch2.getTestCase().toCode());
	}

	/** {@inheritDoc} */
	@Override
	public void initializePopulation() {
		logger.info("executing initializePopulation function");

		notifySearchStarted();
		currentIteration = 0;

		// Create a random parent population P0
		generateInitialPopulation(MBTProperties.POPULATION);
		// Determine fitness
		calculateFitness();
		this.notifyIteration();
	}

	protected abstract double numberOfCoveredTargets();

	public abstract Set<FitnessFunction<T>> getCoveredGoals();

}
