/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

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
	private String STATISTICS_HEADER = "sut, goals, covered_goals, coverage, tests, budget, consumed_budget, algorithm, criteria, random_seed\n";
	private long startTime;

	Map<FitnessFunction<MBTChromosome>, MBTChromosome> coverageMap;
	double coverage;
	/**
	 * @param goals 
	 * 
	 */
	public CoverageTracker(List<?> goals) {
		// initialize statistics buffer and print header
		statistics = new StringBuffer();
//		appendStat(STATISTICS_HEADER);
		startTime = System.currentTimeMillis();
		
		coverage = 0d;
		coverageMap = new HashMap<FitnessFunction<MBTChromosome>, MBTChromosome>();
		for (Object goal : goals) {
			coverageMap.put((FitnessFunction<MBTChromosome>) goal, null);
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
	
	
	@Override
	public void fitnessEvaluation(Chromosome arg0) {
		MBTChromosome chromosome = (MBTChromosome)arg0;
		if (chromosome.getTestcase().isValid()) {
			for (Entry<FitnessFunction<?>, Double> entry : chromosome.getFitnessValues().entrySet()) {
				if (Double.compare(entry.getValue(), 0d) == 0) {
					updateCoverageMap (chromosome, entry.getKey());
				}
			}
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
		// update coverage
		int covered = 0;
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() != null) {
				covered ++;
			}
		}
		coverage = (double)covered / coverageMap.size();
		System.err.println("Coverage: " + coverage * 100 + " %");
	}

	@Override
	public void modification(Chromosome arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchFinished(GeneticAlgorithm<?> arg0) {
		// write statistics to buffer
		int tests = getTestSuite().size();
		int goals = coverageMap.size();
		int coveredGoals = getCoveredGoals ();
		double coverage = getCoverage();
		long budget = MBTProperties.SEARCH_BUDGET;
		long consumedBudget = (System.currentTimeMillis() - startTime)/1000;
		String criteria = "";
		for (ModelCriterion criterion : MBTProperties.MODELCRITERION) {
			criteria += criterion.toString() + ";";
		}
		criteria = criteria.substring(0, criteria.length()-1);
		
		//goals, covered_goals, coverage, tests, budget, consumed_budget
		String statLine = MBTProperties.SUT_EFSM + "," + goals + "," + coveredGoals + "," + coverage + "," + tests + "," + budget + "," + consumedBudget + "," + MBTProperties.ALGORITHM + "," + criteria + "," + MBTProperties.RANDOM_SEED;
		appendStat(statLine);
	}

	private int getCoveredGoals() {
		int count = 0;
		for (Entry<FitnessFunction<MBTChromosome>, MBTChromosome> entry : coverageMap.entrySet()) {
			if (entry.getValue() != null) {
				count ++;
			}
		}
		return count;
	}

	@Override
	public void searchStarted(GeneticAlgorithm<?> ga) {
		// TODO Auto-generated method stub
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

	/**
	 * @return the coverage
	 */
	public double getCoverage() {
		return coverage;
	}

	
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
}
