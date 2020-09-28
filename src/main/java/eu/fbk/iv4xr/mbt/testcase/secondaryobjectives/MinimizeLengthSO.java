/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase.secondaryobjectives;

import org.evosuite.ga.SecondaryObjective;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class MinimizeLengthSO extends SecondaryObjective<MBTChromosome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4343742256639449860L;

	
	@Override
	public int compareChromosomes(MBTChromosome chromosome1, MBTChromosome chromosome2) {
//		logger.debug("Comparing sizes: " + chromosome1.size() + " vs "
//		        + chromosome2.size());
		return chromosome1.size() - chromosome2.size();
	}

	@Override
	public int compareGenerations(MBTChromosome parent1, MBTChromosome parent2, MBTChromosome child1, MBTChromosome child2) {
//		logger.debug("Comparing sizes: " + parent1.size() + ", " + parent1.size()
//        + " vs " + child1.size() + ", " + child2.size());
		return Math.min(parent1.size(), parent2.size())
        - Math.min(child1.size(), child2.size());
	}

}
