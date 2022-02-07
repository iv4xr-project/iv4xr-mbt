/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.io.Serializable;

/**
 * @author kifetew
 *
 */
public class ExecutionResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932533482647130787L;

	private boolean success;
	
	private ExecutionTrace exectionTrace;
	
	private double branchDistance;
	private int approachLevel;
	
	/**
	 * 
	 */
	public ExecutionResult() {
		success = false;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the exectionTrace
	 */
	public ExecutionTrace getExectionTrace() {
		return exectionTrace;
	}

	/**
	 * @param exectionTrace the exectionTrace to set
	 */
	public void setExectionTrace(ExecutionTrace exectionTrace) {
		this.exectionTrace = exectionTrace;
	}

	/**
	 * @return the branchDistance
	 */
	public double getBranchDistance() {
		return branchDistance;
	}

	/**
	 * @param branchDistance the branchDistance to set
	 */
	public void setBranchDistance(double branchDistance) {
		this.branchDistance = branchDistance;
	}

	/**
	 * @return the approachLevel
	 */
	public int getApproachLevel() {
		return approachLevel;
	}

	/**
	 * @param approachLevel the approachLevel to set
	 */
	public void setApproachLevel(int approachLevel) {
		this.approachLevel = approachLevel;
	}

}
