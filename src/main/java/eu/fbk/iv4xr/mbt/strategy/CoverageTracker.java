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

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 */
public class CoverageTracker extends StoppingConditionImpl implements SearchListener {

	Map<FitnessFunction<MBTChromosome>, MBTChromosome> coverageMap;
	double coverage;
	/**
	 * @param goals 
	 * 
	 */
	public CoverageTracker(List<?> goals) {
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
		// TODO Auto-generated method stub

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
		return !coverageMap.values().contains(null);
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

}
