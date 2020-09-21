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

}
