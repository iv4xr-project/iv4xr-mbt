/**
 * 
 */
package eu.fbk.iv4xr.mbt.algorithm.operators.crossover;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.utils.Randomness;

/**
 * @author kifetew
 *
 */
public class SinglePointPathCrossOver extends CrossOverFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1803725233480503132L;

	/**
	 * 
	 */
	public SinglePointPathCrossOver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void crossOver(Chromosome parent1, Chromosome parent2) throws ConstructionFailedException {
		if (parent1.size() < 2 || parent2.size() < 2) {
			return;
		}
		
		// Choose a position in the middle
		int point1 = Randomness.nextInt(parent1.size() - 1) + 1;
		int point2 = Randomness.nextInt(parent2.size() - 1) + 1;

		Chromosome t1 = parent1.clone();
		Chromosome t2 = parent2.clone();

		parent1.crossOver(t2, point1, point2);
		parent2.crossOver(t1, point2, point1);
	}

}
