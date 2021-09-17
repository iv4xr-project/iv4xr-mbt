/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.io.Serializable;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestFitnessFunction;

/**
 * @author kifetew
 *
 */
public interface Testcase extends Comparable<Testcase>, Serializable, Cloneable {
	
	/**
	 * Return the length of the test case (e.g., as number of transitions)
	 * @return length 
	 */
	public int getLength ();
	
	
	public double getFitness ();


	public Testcase clone()  throws CloneNotSupportedException;
	
	public boolean isValid ();
	
	public void setValid (boolean valid);


	public void crossOver(Testcase other, int position1, int position2);


	public void mutate();
	
	/**
	 * Remove all covered goals
	 */
	public void clearCoveredGoals();
	
	
	/**
	 * Retrieve all coverage goals covered by this test
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<FitnessFunction<?>> getCoveredGoals();
	
	
	public void addCoveredGoal(FitnessFunction<?> goal);
	
	public boolean isGoalCovered(FitnessFunction<?> goal);
}
