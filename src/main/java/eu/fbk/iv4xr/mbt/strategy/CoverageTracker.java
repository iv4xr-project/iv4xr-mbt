/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.SearchListener;
import org.evosuite.ga.stoppingconditions.StoppingConditionImpl;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import me.tongfei.progressbar.ProgressBar;

/**
 * @author kifetew
 *
 */
public class CoverageTracker extends StoppingConditionImpl implements SearchListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2066478272595190175L;
	
	protected StringBuffer statistics;
	private String STATISTICS_HEADER = "id, sut, goals, covered_goals, coverage, tests, budget, "
										+ "consumed_budget, fitness_evaluations, feasible_paths, feasible_rate, algorithm, criteria, random_seed, lr_seed\n";
	private long startTime;
	private long lastSnapshotTime;

	Map<FitnessFunction<MBTChromosome>, MBTChromosome> coverageMap;
	double coverage;
	private int coveredGoals;
	private int totalGoals;
	private int fitnessEvaluations;
	private int feasiblePaths;
	private double feasibleRate;
	
	protected ProgressBar coveragePb;
	protected ProgressBar budgetPb;
	/**
	 * @param goals 
	 * 
	 */
	public CoverageTracker(List<?> goals) {
		// initialize statistics buffer and print header
		statistics = new StringBuffer();
//		appendStat(STATISTICS_HEADER);
		startTime = System.currentTimeMillis();
		lastSnapshotTime = startTime;
	
		totalGoals = goals.size();
		coveredGoals = 0;
		coverage = 0d;
		fitnessEvaluations = 0;
		feasiblePaths = 0;
		feasibleRate = 0d;
		coverageMap = new HashMap<FitnessFunction<MBTChromosome>, MBTChromosome>();
		for (Object goal : goals) {
			coverageMap.put((FitnessFunction<MBTChromosome>) goal, null);
		}
		
		if (MBTProperties.SHOW_PROGRESS) {
			//setup progress bar to number of goals to cover and search budget
			coveragePb = new ProgressBar(MBTProperties.ALGORITHM + " Coverage: ", goals.size()); //, ProgressBarStyle.ASCII);
			budgetPb = new ProgressBar(MBTProperties.ALGORITHM + " S.Budget: ", MBTProperties.SEARCH_BUDGET); //, ProgressBarStyle.ASCII);
		}
	}
	
	public SuiteChromosome getTestSuite () {
		Set<MBTChromosome> suite = new HashSet<MBTChromosome>();
		suite.addAll(coverageMap.values());
		suite.remove(null);
		
		// debug
		//System.out.println("Original suite size: " + coverageMap.values().size());
		//System.out.println("Uniques only size  : " + suite.size());
		
		SuiteChromosome testSuite = new MBTSuiteChromosome();
		testSuite.addTests(suite);

		return testSuite;
	}
	
	/**
	 * a convenience method that returns the coverage map with test cases as keys and covered goals as values
	 * @return
	 */
	public Map<MBTChromosome, Set<FitnessFunction<MBTChromosome>>> getInvertedCoverageMap (){
		Map<MBTChromosome, Set<FitnessFunction<MBTChromosome>>> invertedMap = new HashMap<>();
		
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			if (!invertedMap.containsKey(entry.getValue())) {
				invertedMap.put(entry.getValue(), new HashSet<>());
			}
			invertedMap.get(entry.getValue()).add(entry.getKey());
		}
		
		return invertedMap;
	}
	
	@Override
	public void fitnessEvaluation(Chromosome arg0) {
		fitnessEvaluations++;
		MBTChromosome chromosome = (MBTChromosome)arg0;
		if (chromosome.getTestcase().isValid()) {
			feasiblePaths++;
			boolean newGoalCovered = false;
			for (Entry<FitnessFunction<?>, Double> entry : chromosome.getFitnessValues().entrySet()) {
				if (Double.compare(entry.getValue(), 0d) == 0 && coverageMap.containsKey(entry.getKey())) {
					chromosome.getTestcase().getCoveredGoals().add(entry.getKey());
					updateCoverageMap (chromosome, entry.getKey());
					newGoalCovered = true;
				}
			}
			if (newGoalCovered) {
				coveredGoals = getCoveredGoals();
				assert (coveredGoals <= totalGoals);
				coverage = (double)coveredGoals / totalGoals;
			}
		}
		
		// time to take statistics snapshot
		if (takeSnapshot()) {
			statisticsSnapshot();
		}
	}

	/**
	 * Update the global coverage map with this individual
	 * @param chromosome
	 * @param fitnessFunction
	 */
	private void updateCoverageMap(MBTChromosome chromosome, FitnessFunction<?> fitnessFunction) {
		if (coverageMap.get(fitnessFunction) == null ||
				(coverageMap.get(fitnessFunction).size() > chromosome.size())) {
			coverageMap.put((FitnessFunction<MBTChromosome>) fitnessFunction, chromosome);
		}
	}

	@Override
	public void iteration(GeneticAlgorithm<?> arg0) {
		if (MBTProperties.SHOW_PROGRESS) {
			coveragePb.stepTo(coveredGoals);
			long consumedBudget = (System.currentTimeMillis() - startTime)/1000;
			budgetPb.stepTo(consumedBudget);
		}
	}

	@Override
	public void modification(Chromosome arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchFinished(GeneticAlgorithm<?> arg0) {
		statisticsSnapshot();
	}

	@Override
	public void searchStarted(GeneticAlgorithm<?> ga) {
		if (MBTProperties.SHOW_PROGRESS) {
			coveragePb.stepTo(0);
			budgetPb.stepTo(0);
		}
	}

	@Override
	public void forceCurrentValue(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getCurrentValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFinished() {
//		boolean finished = !coverageMap.values().contains(null);
		boolean finished = Double.compare(coverage, 1) == 0;
		return finished;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLimit(long arg0) {
		// TODO Auto-generated method stub
		
	}

	//////// utility methods for handling statistics

	/**
	 * time to take snapshot?
	 * @return
	 */
	private boolean takeSnapshot() {
		long now = System.currentTimeMillis();
		if ((now - lastSnapshotTime)/1000 >= MBTProperties.STATISTICS_INTERVAL){
			lastSnapshotTime = now;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * takes a snapshot of the current state of the search
	 */
	private void statisticsSnapshot() {
		// write statistics to buffer
		int tests = getTestSuite().size();
//		double coverage = getCoverage();
		long consumedBudget = (System.currentTimeMillis() - startTime)/1000;
		String criteria = "";
		for (ModelCriterion criterion : MBTProperties.MODELCRITERION) {
			criteria += criterion.toString() + ";";
		}
		criteria = criteria.substring(0, criteria.length()-1);
		
		feasibleRate = (double)feasiblePaths / (double)fitnessEvaluations;
		
		//goals, covered_goals, coverage, tests, budget, consumed_budget, fitness_evaluations, algorithm, cirterion, random_seed, lr_seed 
		
		List<String> stats = new ArrayList<String>();
		stats.add(MBTProperties.SessionId);
		stats.add(MBTProperties.SUT_EFSM);
		stats.add(""+totalGoals);
		stats.add(""+coveredGoals);
		stats.add(""+coverage);
		stats.add(""+tests);
		stats.add(""+MBTProperties.SEARCH_BUDGET);
		stats.add(""+consumedBudget);
		stats.add(""+fitnessEvaluations);
		stats.add(""+feasiblePaths);
		stats.add(""+feasibleRate);
		stats.add(MBTProperties.ALGORITHM.toString());
		stats.add(criteria);
		stats.add(""+MBTProperties.RANDOM_SEED);
		stats.add(""+MBTProperties.LR_seed);
		
		String statLine = String.join(",", stats);
		appendStat(statLine);
	}

	/**
	 * counts the number of covered goals
	 * @return
	 */
	private int getCoveredGoals() {
		int count = 0;
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() != null) {
				count ++;
			}
		}
		return count;
	}
	
	/**
	 * @return the coverage
	 */
	public double getCoverage() {
		return coverage;
	}

	/**
	 * appends the given snapshot ot the statistics buffer
	 * @param string
	 */
	private void appendStat(String string) {
		statistics.append(string + "\n");
		
	}
	
	/**
	 * NOTE: should be after the search is completed.
	 *  
	 * @return string containing the search statistics as csv
	 */
	public String getStatistics () {
		return statistics.toString();
	}
	
	/**
	 * Returns the header for the statistics data in csv
	 * @return
	 */
	public String getStatisticsHeader () {
		return STATISTICS_HEADER;
	}
	
	/**
	 * returns a list of all uncovered goals
	 * @return
	 */
	public List<FitnessFunction<MBTChromosome>> getUncoveredGoals (){
		List<FitnessFunction<MBTChromosome>> uncoveredGoals = new ArrayList<>();
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() == null) {
				uncoveredGoals.add(entry.getKey());
			}
		}
		return uncoveredGoals;
	}
	
	/**
	 * returns a map containing each coverage goal as key and the corresponding chromosome that covers it as value.
	 * if a coverage goal is not covered by any chromosome, the corresponding value will be null.
	 * @return
	 */
	public Map<FitnessFunction<MBTChromosome>, MBTChromosome> getCoverageMap (){
		return coverageMap;
	}
}
