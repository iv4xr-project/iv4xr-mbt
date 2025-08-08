/**
 * 
 */
package eu.fbk.iv4xr.mbt.testsuite;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.localsearch.LocalSearchObjective;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class SuiteChromosome extends AbstractTestSuiteChromosome<SuiteChromosome, MBTChromosome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4537439043463765626L;

	/** Coverage goals this test covers */
	private transient List<FitnessFunction<?>> coveredGoals = new ArrayList<FitnessFunction<?>>();
	
	/**
	 * 
	 */
	public SuiteChromosome() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param testChromosomeFactory
	 */
	public SuiteChromosome(ChromosomeFactory<MBTChromosome> testChromosomeFactory) {
		super(testChromosomeFactory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param source
	 */
	public SuiteChromosome(SuiteChromosome source) {
		super(source);
		// TODO Auto-generated constructor stub
	}


	@Override
	public SuiteChromosome clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareSecondaryObjective(SuiteChromosome o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean localSearch(LocalSearchObjective<SuiteChromosome> objective) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<FitnessFunction<?>> getCoveredGoals() {
		return coveredGoals;
	}

	@Override
	public SuiteChromosome self() {
		return this;
	}

	@Override
	public MBTChromosome addTest(TestCase testCase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTestChromosome(TestChromosome testChromosome) {
		// 
		
	}

}
