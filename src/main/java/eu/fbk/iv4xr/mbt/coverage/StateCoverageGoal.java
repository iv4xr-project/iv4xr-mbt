/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoal extends FitnessFunction<Chromosome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8816426341946761190L;

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(StateCoverageGoal.class);
	
	private LabRecruitsState state;
	
	/**
	 * 
	 */
	public StateCoverageGoal(LabRecruitsState s) {
		state = s;
	}

	@Override
	public double getFitness(Chromosome individual) {
		double fitness = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			Path path = testcase.getPath();
			if (path.getStates().contains(state)) {
				fitness = 0;
			}else {
				fitness = 1;
			}
		}
		updateIndividual(individual, fitness);
		return fitness;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
