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

import eu.fbk.iv4xr.mbt.efsm4j.PGTransition;

public class LabRecruitsFreeTravelTransition  extends 
	PGTransition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>{

	/**
	 * Check if the input is equal to the next state
	 */
	@Override
	protected boolean inputGuard(LabRecruitsParameter input) {
		if (input == null) {
			return(false);
		}else {
			return(input.getValue() == LabRecruitsAction.EXPLORE);	
		}	
	}

	@Override
	protected boolean domainGuard(LabRecruitsContext context) {
		return true;
	}

	@Override
	protected Set<LabRecruitsParameter> operation(LabRecruitsParameter input, LabRecruitsContext context) {
		return null;
	}

	@Override
	public boolean hasOperation() {
		return false;
	}


}
