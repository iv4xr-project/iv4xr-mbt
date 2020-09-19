package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

/**
 * 
 * Travel transition with input parameter indicating the next state.
 * The inputGuard check if the target state corresponds to the input.
 * This transition has no domain guard.
 * This transition does not perform operations.
 * 
 * @author Davide Prandi
 */

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.Transition;

public class LabRecruitsFreeTravelTransition  extends Transition<LabRecruitsState, String, LabRecruitsContext>{

	/**
	 * Check if the input is equal to the next state
	 */
	@Override
	protected boolean inputGuard(String input) {		
		if (this.getTgt().getId().equals(input)) {
			return true;
		}else {
			return false;
		}		
	}

	@Override
	protected boolean domainGuard(LabRecruitsContext context) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Set<String> operation(String input, LabRecruitsContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasOperation() {
		return false;
	}

	@Override
	public boolean hasDomainGuard() {
		return false;
	}

	@Override
	public boolean hasParameterGuard() {
		return true;
	}

}
