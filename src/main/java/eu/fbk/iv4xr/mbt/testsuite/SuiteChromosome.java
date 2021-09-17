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
import org.evosuite.testsuite.AbstractTestSuiteChromosome;

import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;

/**
 * @author kifetew
 *
 */
public class SuiteChromosome extends AbstractTestSuiteChromosome<MBTChromosome> {

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
	public SuiteChromosome(ChromosomeFactory testChromosomeFactory) {
		super(testChromosomeFactory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param source
	 */
	public SuiteChromosome(AbstractTestSuiteChromosome source) {
		super(source);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Chromosome clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Chromosome> int compareSecondaryObjective(T o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean localSearch(LocalSearchObjective<? extends Chromosome> objective) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<FitnessFunction<?>> getCoveredGoals() {
		return coveredGoals;
	}

}
