/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.SecondaryObjective;
import org.evosuite.ga.localsearch.LocalSearchObjective;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public class MBTChromosome extends ExecutableChromosome<MBTChromosome> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3211675659294418756L;
	private Testcase testcase;
	private eu.fbk.iv4xr.mbt.execution.ExecutionResult executionResult;
	/** Secondary objectives used during ranking */
	private static final List<SecondaryObjective<MBTChromosome>> secondaryObjectives = new ArrayList<>();
	
	/**
	 * 
	 */
	public MBTChromosome() {
		testcase = new AbstractTestSequence();
	}

	public eu.fbk.iv4xr.mbt.execution.ExecutionResult getExecutionResult() {
		return executionResult;
	}
	
	public void setExecutionResult(eu.fbk.iv4xr.mbt.execution.ExecutionResult result) {
		executionResult = result;
	}
	
	@Override
	protected void copyCachedResults(MBTChromosome other) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExecutionResult executeForFitnessFunction(TestSuiteFitnessFunction testSuiteFitnessFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MBTChromosome clone() {
		MBTChromosome clone = new MBTChromosome();
		try {
			clone.setTestcase(testcase.clone());
			if (executionResult != null) {
				clone.setExecutionResult(executionResult.clone());
			}
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
	public int compareTo(MBTChromosome o) {
		int result = super.compareTo(o);
		if (result != 0) {
			return result;
		}
		// make this deliberately not 0
		// because then ordering of results will be random
		// among tests of equal fitness
		if (o instanceof MBTChromosome) {
			return ((AbstractTestSequence)testcase).toDot().compareTo( ((AbstractTestSequence)((MBTChromosome) o).testcase).toDot());
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return testcase.hashCode();
	}

	@Override
	public int compareSecondaryObjective(MBTChromosome o) {
		int objective = 0;
		int c = 0;

		while (c == 0 && objective < secondaryObjectives.size()) {

			SecondaryObjective<MBTChromosome> so = secondaryObjectives.get(objective++);
			if (so == null)
				break;
			c = so.compareChromosomes(this, o);
		}
		return c;
	}

	@Override
	public void mutate() {
		testcase.mutate(executionResult);
		testcase.clearCoveredGoals();
		setChanged(true);
	}

	 @Override
	public void crossOver(MBTChromosome other, int position1, int position2) throws ConstructionFailedException {
		testcase.crossOver (other.getTestcase(), position1, position2); 
		testcase.clearCoveredGoals();
		setChanged(true);
	}

	@Override
	public boolean localSearch(LocalSearchObjective<MBTChromosome> objective) {
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
	
	/**
	 * Add an additional secondary objective to the end of the list of
	 * objectives
	 *
	 * @param objective
	 *            a {@link org.evosuite.ga.SecondaryObjective} object.
	 */
	public static void addSecondaryObjective(SecondaryObjective<MBTChromosome> objective) {
		secondaryObjectives.add(objective);
	}

	public static void ShuffleSecondaryObjective() {
		Collections.shuffle(secondaryObjectives);
	}

	public static void reverseSecondaryObjective() {
		Collections.reverse(secondaryObjectives);
	}

	/**
	 * Remove secondary objective from list, if it is there
	 *
	 * @param objective
	 *            a {@link org.evosuite.ga.SecondaryObjective} object.
	 */
	public static void removeSecondaryObjective(SecondaryObjective<?> objective) {
		secondaryObjectives.remove(objective);
	}

	/**
	 * <p>
	 * Getter for the field <code>secondaryObjectives</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public static List<SecondaryObjective<MBTChromosome>> getSecondaryObjectives() {
		return secondaryObjectives;
	}
	
	@Override
	public String toString() {
		if (testcase != null) {
			return testcase.toString();
		}else {
			return "";
		}
	}
	
	
	/**
	 * Set changed status to @param changed
	 * 
	 * @param changed
	 *            a boolean.
	 */
	@Override
	public void setChanged(boolean changed) {
		super.setChanged(changed);
		testcase.setChanged(changed);
	}

	@Override
	public MBTChromosome self() {
		return this;
	}


}
