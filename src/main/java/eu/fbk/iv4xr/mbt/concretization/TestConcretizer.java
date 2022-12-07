/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization;

import java.util.List;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * @author kifetew
 *
 * This interface represents the concretization of an abstract test case to the corresponding concrete test case
 * Each SUT must provide an implementation for this interface so as to enable execution of tests on the actual SUT
 *
 */
public interface TestConcretizer {
	public List<GoalStructure> concretizeTestCase (TestAgent testAgent, AbstractTestSequence testCase);
}
