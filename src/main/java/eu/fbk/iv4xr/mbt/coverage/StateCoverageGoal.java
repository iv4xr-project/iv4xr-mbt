/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.se.labrecruits.LabRecruitsState;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoal extends FitnessFunction<Chromosome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8816426341946761190L;

	private LabRecruitsState state;
	
	/**
	 * 
	 */
	public StateCoverageGoal(LabRecruitsState s) {
		state = s;
	}

	@Override
	public double getFitness(Chromosome individual) {
		double f = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			Path path = testcase.getPath();
			if (path.getStates().contains(state)) {
				f = 0;
			}else {
				f = 1;
			}
		}
		return f;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
