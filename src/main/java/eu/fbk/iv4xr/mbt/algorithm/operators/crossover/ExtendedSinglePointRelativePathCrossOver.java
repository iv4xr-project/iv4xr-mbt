package eu.fbk.iv4xr.mbt.algorithm.operators.crossover;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

public class ExtendedSinglePointRelativePathCrossOver extends CrossOverFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1038174961781624641L;

	/*
	 * execution parameters
	 */
	AbstractTestSequence testcase1;
	AbstractTestSequence testcase2;
	private int parent1PassedTransitions = 0;
	private int parent2PassedTransitions = 0;
	private int parent1PathLength = 0;
	private int parent2PathLength = 0;
	
	
	
	@Override
	public void crossOver(Chromosome arg0, Chromosome arg1) throws ConstructionFailedException {
		if (arg0.size() < 2 || arg1.size() < 2) {
			return;
		}
		
		MBTChromosome parent1 = (MBTChromosome)arg0;
		MBTChromosome parent2 = (MBTChromosome)arg1;
		
		// set feasible data
		testcase1 = (AbstractTestSequence) parent1.getTestcase();
		testcase2 = (AbstractTestSequence) parent2.getTestcase();
		parent1PassedTransitions = parent1.getExecutionResult().getExecutionTrace().getPassedTransitions();
		parent2PassedTransitions = parent2.getExecutionResult().getExecutionTrace().getPassedTransitions();
		parent1PathLength = testcase1.getLength();
		parent2PathLength = testcase2.getLength();
		
		// check if feasible data is coherent
		if (parent1PassedTransitions < 0 || parent1PassedTransitions > parent1PathLength) {
			throw new RuntimeException("\nPath "+testcase1.getPath().toString()+" has "+parent1PassedTransitions+" feasible transitions");
		}
		
		if (parent2PassedTransitions < 0 || parent2PassedTransitions > parent2PathLength) {
			throw new RuntimeException("\nPath "+testcase2.getPath().toString()+" has "+parent2PassedTransitions+" feasible transitions");
		}
		
		
		//int[] points = getCrossoverPoints (parent1, parent2);
		int[] points = getCrossoverPoints();
		if (points[0] < 0) {
			// the parents have no common transitions
			// try to find a new connecting path
			// need to refactor the code
			return;
		}else {
			// this is the same of SinglePointRelativePathCrossOver
			MBTChromosome t1 = (MBTChromosome)parent1.clone();
			MBTChromosome t2 = (MBTChromosome)parent2.clone();
	
			parent1.crossOver(t2, points[0], points[1]);
			parent2.crossOver(t1, points[1], points[0]);
		}

		
	}

	
	/**
	 * Get crossover point when both parents has some feasible prefix
	 * @return
	 */
	private int[] getCrossoverPoints() {
		int[] points = new int[2];
		points[0] = -1;
		points[1] = -1;
			
		EFSMPath tc1PassedPath = null;
		EFSMPath tc2PassedPath = null;
		
		
		// get feasible transitions of testcase1 and testcase2
		if (parent1PassedTransitions > 0) {
			tc1PassedPath = testcase1.getPath().subPath(0, parent1PassedTransitions );
		}
		if (parent2PassedTransitions > 0) {
			tc2PassedPath = testcase2.getPath().subPath(0, parent2PassedTransitions );
		}
		
		// use feasible prefixes to find crossover points 
		if (tc1PassedPath != null & tc2PassedPath != null) {
			
			// find common states
			List commonStates = tc1PassedPath.getStates();
			commonStates.retainAll(tc2PassedPath.getStates());
					
			if (commonStates.size() > 0) {
				// find an exchange point
				EFSMState crossoverState = (EFSMState) Randomness.choice(commonStates);
				
				// get ids of the transitions that end with crossover state
				
				List<EFSMTransition> tc1Transitions = tc1PassedPath.getTransitions();
				Set<Integer> tc1Intersection = new LinkedHashSet<Integer>();
				Integer tId = -1;
				for(EFSMTransition t : tc1Transitions) {
					tId += 1;
					if (t.getTgt().equals(crossoverState)) {
						tc1Intersection.add(tId);
					}
				}
				
				
				List<EFSMTransition> tc2Transitions = tc2PassedPath.getTransitions();
				Set<Integer> tc2Intersection = new LinkedHashSet<Integer>();
				tId = -1;
				for(EFSMTransition t : tc2Transitions) {
					tId += 1;
					if (t.getTgt().equals(crossoverState)) {
						tc2Intersection.add(tId);
					}
				}
				
				if (tc1Intersection.size() > 0 && tc2Intersection.size() > 0) {
					// choose t1 and t2
					points[0] = Randomness.choice(tc1Intersection);
					points[1] = Randomness.choice(tc2Intersection);
				}//else {
				//	System.out.println();
				//}
				

			}
		}
		return points;
	}
	
	
	// from SinglePointRelativePathCrossOver
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
		return points;
	}

}
