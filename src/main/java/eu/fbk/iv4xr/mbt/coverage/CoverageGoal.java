/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public interface CoverageGoal {
	boolean isCovered (Testcase testcase);
	
	boolean isCovered (List<Testcase> testSuite);
	
	
}
