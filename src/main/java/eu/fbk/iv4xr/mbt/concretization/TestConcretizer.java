
/**
 * @author kifetew
 * 
 */

package eu.fbk.iv4xr.mbt.concretization;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

/**
 * This interface defines a generic test concretizer
 */
public abstract class TestConcretizer {
	protected EFSM model;
	public abstract ConcreteTestCase concretizeTestCase (AbstractTestSequence abstractTestCase);
}
