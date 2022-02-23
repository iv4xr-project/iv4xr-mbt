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
	
	private ExecutionTrace executionTrace;
	
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
	public ExecutionTrace getExecutionTrace() {
		return executionTrace;
	}

	/**
	 * @param exectionTrace the exectionTrace to set
	 */
	public void setExecutionTrace(ExecutionTrace exectionTrace) {
		this.executionTrace = exectionTrace;
	}

	@Override
	public ExecutionResult clone() {
		ExecutionResult copy = new ExecutionResult();
		copy.setSuccess(success);
		if (executionTrace != null) {
			copy.setExecutionTrace(executionTrace.clone());
		}
		return copy;
	}
	
}
