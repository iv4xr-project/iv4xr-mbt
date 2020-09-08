/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import javax.management.RuntimeErrorException;

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
		//FIXME implement correctly, this is only a placeholder!
		MBTChromosome clone = new MBTChromosome();
		try {
			clone.setTestcase(testcase.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBTChromosome other = (MBTChromosome) obj;
		if (testcase == null) {
			return other.testcase == null;
		} else return testcase.equals(other.testcase);
	}

	@Override
	public int hashCode() {
		return testcase.hashCode();
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
		return testcase.getLength();
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
