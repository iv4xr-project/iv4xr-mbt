/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import org.evosuite.Properties;
import org.evosuite.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxFitnessEvaluationsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxGenerationStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTestsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.ga.stoppingconditions.ZeroFitnessStoppingCondition;

import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 */
public abstract class GenerationStrategy {

	public abstract SuiteChromosome generateTests();
	
	/** There should only be one */
	protected ZeroFitnessStoppingCondition zeroFitness = new ZeroFitnessStoppingCondition();
	
	/** There should only be one */
	protected StoppingCondition globalTime = new GlobalTimeStoppingCondition();

	protected CoverageTracker coverageTracker;
	
	
	/**
	 * Check if the budget has been used up. The GA will do this check
	 * on its own, but other strategies (e.g. random) may depend on this function.
	 * 
	 * @param chromosome
	 * @param stoppingCondition
	 * @return
	 */
	protected boolean isFinished(SuiteChromosome chromosome, StoppingCondition stoppingCondition) {
		if (stoppingCondition.isFinished())
			return true;

		if (Properties.STOP_ZERO) {
			if (chromosome.getFitness() == 0.0)
				return true;
		}

		if (!(stoppingCondition instanceof MaxTimeStoppingCondition)) {
			return globalTime.isFinished();
		}

		return false;
	}
	
	
	/**
	 * Convert property to actual stopping condition
	 * @return
	 */
	protected StoppingCondition getStoppingCondition() {
		switch (Properties.STOPPING_CONDITION) {
		case MAXGENERATIONS:
			return new MaxGenerationStoppingCondition();
		case MAXFITNESSEVALUATIONS:
			return new MaxFitnessEvaluationsStoppingCondition();
		case MAXTIME:
			return new MaxTimeStoppingCondition();
		case MAXTESTS:
			return new MaxTestsStoppingCondition();
		case MAXSTATEMENTS:
			return new MaxStatementsStoppingCondition();
		default:
			return new MaxGenerationStoppingCondition();
		}
	}


	/**
	 * @return the coverageTracker
	 */
	public CoverageTracker getCoverageTracker() {
		return coverageTracker;
	}

}
