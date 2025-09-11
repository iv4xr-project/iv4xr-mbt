
/**
 * @author kifetew
 * 
 */

package eu.fbk.iv4xr.mbt.concretization;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

/**
 * This interface defines a generic test concretizer
 */
public interface TestConcretizer {
	public ConcreteTestCase concretizeTestCase (AbstractTestSequence abstractTestCase);
}
