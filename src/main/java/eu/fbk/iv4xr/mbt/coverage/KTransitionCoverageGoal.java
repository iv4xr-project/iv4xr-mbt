/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 * Represents a sequence of K transitions, where K >= 2
 */
public class KTransitionCoverageGoal implements CoverageGoal {

	private int k = 2;
	
	/**
	 * 
	 */
	public KTransitionCoverageGoal() {
		// TODO Auto-generated constructor stub
	}

	
	public KTransitionCoverageGoal(int k){
		this.k = k;
	}
	
	@Override
	public boolean isCovered(Testcase testcase) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCovered(List<Testcase> testSuite) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}


	/**
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}

}
