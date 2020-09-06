/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.localsearch.LocalSearchObjective;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

/**
 * @author kifetew
 *
 */
public class MBTChromosome extends ExecutableChromosome {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3211675659294418756L;
	private Testcase testcase;

	/**
	 * 
	 */
	public MBTChromosome() {
		testcase = new AbstractTestSequence();
	}

	@Override
	protected void copyCachedResults(ExecutableChromosome other) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExecutionResult executeForFitnessFunction(TestSuiteFitnessFunction testSuiteFitnessFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chromosome clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Chromosome> int compareSecondaryObjective(T o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void crossOver(Chromosome other, int position1, int position2) throws ConstructionFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean localSearch(LocalSearchObjective<? extends Chromosome> objective) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the testcase
	 */
	public Testcase getTestcase() {
		return testcase;
	}

	/**
	 * @param testcase the testcase to set
	 */
	public void setTestcase(Testcase testcase) {
		this.testcase = testcase;
	}

}
