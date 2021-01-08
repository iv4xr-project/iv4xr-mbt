/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.Serializable;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.SearchListener;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.testsuite.TestSuiteChromosome;


public class ProgressMeter implements SearchListener, Serializable {

	private static final long serialVersionUID = -8518559681906649686L;

	private StoppingCondition stoppingCondition = null;
	private long max = 1;
	private int currentCoverage = 0;

	protected int lastCoverage = 0;
	protected int lastProgress = 0;
	protected int iteration = 0;


	/**
	 * <p>
	 * updateStatus
	 * </p>
	 * 
	 * @param percent
	 *            a int.
	 */
	public void updateStatus(int percent) {
		lastProgress = percent;
		lastCoverage = currentCoverage;
		System.out.println(percent);
		System.out.println(currentCoverage);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.SearchListener#searchStarted(org.evosuite.ga.GeneticAlgorithm)
	 */
	/** {@inheritDoc} */
	@Override
	public void searchStarted(GeneticAlgorithm<?> algorithm) {
		for(StoppingCondition cond : algorithm.getStoppingConditions()) {
			if(cond.getLimit() == 0) // No ZeroStoppingCondition
				continue;
			stoppingCondition = cond;
			max = stoppingCondition.getLimit();
			break;
		}
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.SearchListener#iteration(org.evosuite.ga.GeneticAlgorithm)
	 */
	/** {@inheritDoc} */
	@Override
	public void iteration(GeneticAlgorithm<?> algorithm) {
		long current = stoppingCondition.getCurrentValue();
		currentCoverage = (int) Math.floor(((TestSuiteChromosome) algorithm.getBestIndividual()).getCoverage() * 100);
		updateStatus((int) (100 * current / max));
		iteration++;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.SearchListener#searchFinished(org.evosuite.ga.GeneticAlgorithm)
	 */
	/** {@inheritDoc} */
	@Override
	public void searchFinished(GeneticAlgorithm<?> algorithm) {
		currentCoverage = (int) Math.floor(((TestSuiteChromosome) algorithm.getBestIndividual()).getCoverage() * 100);
		if(currentCoverage > lastCoverage) {
			updateStatus((int) (100 * stoppingCondition.getCurrentValue() / max));
		}
		// System.out.println("");
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.SearchListener#fitnessEvaluation(org.evosuite.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public void fitnessEvaluation(Chromosome individual) {
		int current = (int) ((int)(100 * stoppingCondition.getCurrentValue())/max);
		currentCoverage = (int) Math.floor(((TestSuiteChromosome) individual).getCoverage() * 100);
		if(currentCoverage > lastCoverage || current > lastProgress)
			updateStatus(current);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.SearchListener#modification(org.evosuite.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public void modification(Chromosome individual) {
		// TODO Auto-generated method stub

	}


}
