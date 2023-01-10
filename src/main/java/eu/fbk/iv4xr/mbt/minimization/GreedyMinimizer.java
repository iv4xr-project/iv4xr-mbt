/**
 * 
 */
package eu.fbk.iv4xr.mbt.minimization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestCase;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 * Implements a simple greedy minimization heuristic that simply takes in test cases until all goals are covered.
 * The minimizer first sorts test cases in descending order based on the number of goals they cover
 * it keeps the set of all goals g (covered by all tests)
 * starting with the first test case tc in the sorted list, it tries to remove the covered goals of tc from g
 * 		if at least one goal was removed from g, then tc is added to the minimized suite
 * continues until either g is empty or all tests in the sorted list have been visited 
 */
public class GreedyMinimizer implements Minimizer {

	/**
	 * 
	 */
	public GreedyMinimizer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SuiteChromosome minimize(SuiteChromosome suite) {
		SuiteChromosome minimizedSuite = new MBTSuiteChromosome();
		
		Set<FitnessFunction<?>> uncoveredGoals = new HashSet<>();
		for (MBTChromosome c : suite.getTestChromosomes()) {
			uncoveredGoals.addAll(c.getTestcase().getCoveredGoals());
		}
	
		
		Comparator<MBTChromosome> sizeComparator = new Comparator<MBTChromosome>() {
			@Override public int compare(MBTChromosome c1, MBTChromosome c2) {
				// descending order
	            return Integer.compare(c2.getTestcase().getCoveredGoals().size(),c1.getTestcase().getCoveredGoals().size());
	        }
		};
		
		List<MBTChromosome> sortedTests = new ArrayList<MBTChromosome>();
		sortedTests.addAll(suite.getTestChromosomes());
		
		sortedTests.sort(sizeComparator);
		
		Iterator<MBTChromosome> iterator = sortedTests.iterator();
		while (!uncoveredGoals.isEmpty()
				&& iterator.hasNext()) {
			MBTChromosome c = iterator.next();
			if (uncoveredGoals.removeAll(c.getTestcase().getCoveredGoals())) {
				minimizedSuite.addTest(c);
			}
		}
		
		assert minimizedSuite.size() <= suite.size();
		System.out.println("Original size: " + suite.size() + " Minimized size: " + minimizedSuite.size());
		return minimizedSuite;
	}

}
