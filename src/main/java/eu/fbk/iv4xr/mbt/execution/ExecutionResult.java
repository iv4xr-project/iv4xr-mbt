/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

/**
 * @author kifetew
 *
 */
public class ExecutionResult {

	private boolean success;
	
	private ExecutionTrace exectionTrace;
	
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

}
