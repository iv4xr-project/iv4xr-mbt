/**
 * 
 */
package eu.fbk.iv4xr.mbt.algorithm.operators.crossover;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class SinglePointRelativePathCrossOver extends CrossOverFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5721615664760681408L;
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(SinglePointRelativePathCrossOver.class);

	@Override
	public void crossOver(Chromosome arg0, Chromosome arg1) throws ConstructionFailedException {
		if (arg0.size() < 2 || arg1.size() < 2) {
			return;
		}
		
		MBTChromosome parent1 = (MBTChromosome)arg0;
		MBTChromosome parent2 = (MBTChromosome)arg1;
		
		int[] points = getCrossoverPoints (parent1, parent2);
		if (points[0] < 0) {
			return;
		}
		MBTChromosome t1 = (MBTChromosome)parent1.clone();
		MBTChromosome t2 = (MBTChromosome)parent2.clone();

		parent1.crossOver(t2, points[0], points[1]);
		parent2.crossOver(t1, points[1], points[0]);

	}

	private int[] getCrossoverPoints(MBTChromosome parent1, MBTChromosome parent2) {
		int[] points = new int[2];
		AbstractTestSequence tc1 = (AbstractTestSequence) parent1.getTestcase();
		AbstractTestSequence tc2 = (AbstractTestSequence) parent2.getTestcase();
		Set<EFSMTransition> commonTransitions = new HashSet<EFSMTransition>();
		commonTransitions.addAll(tc1.getPath().getTransitions());
		commonTransitions.retainAll(tc2.getPath().getTransitions());
		if (commonTransitions.isEmpty()) {
			points[0] = -1;
			points[1] = -1;
		}else {
			EFSMTransition intersection = Randomness.choice(commonTransitions);
//			logger.debug("INTERSECTION POINT: " + intersection.getSrc().getId() + "---" + intersection.getTgt().getId());
			points[0] = tc1.getPath().getTransitions().indexOf(intersection);
			points[1] = tc2.getPath().getTransitions().indexOf(intersection);
		} 
//		logger.debug("Point1: " + points[0] + " & " + points[1]);
		logger.debug("parent1: length: {}, passed: {}", tc1.getLength(), tc1.getExecutionResult().getExecutionTrace().getPassedTransitions());
		logger.debug("parent2: length: {}, passed: {}", tc2.getLength(), tc2.getExecutionResult().getExecutionTrace().getPassedTransitions());
		return points;
	}

}
