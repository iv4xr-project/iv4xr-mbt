package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

/**
 * 
 * Toggle transition with input parameter "".
 * The inputGuard check if the target state corresponds to the source state.
 * This transition has no domain guard.
 * This transition update doors status.
 * 
 * @author Davide Prandi
 */

import java.util.HashMap;
import java.util.Map;
//import java.util.HashMap;

/**
 * 
 * Transition that correspond to switch interaction.
 * Input must be the empty string. 
 * No domain guard.
 * This transition update doors context each door connected with the switch 
 * 
 * @author Davide Prandi
 */

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.PGTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;

public class LabRecruitsToggleTransition extends 
	PGTransition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>{

	@Override
	protected boolean inputGuard(LabRecruitsParameter input) {
		if (input == null) {
			return(false);
		}else {
			return(input.getValue() == LabRecruitsAction.TOGGLE);	
		}		
	}
	
	@Override
	protected boolean domainGuard(LabRecruitsContext context) {
		return true;
	}

	@Override
	protected Set<LabRecruitsParameter> operation(LabRecruitsParameter input, LabRecruitsContext context) {
	    // iterate over doors in text context
		for(LabRecruitsDoor door : context.values()) {
			 if (door.getButtons().contains(this.getSrc().getId())) { 
				 door.updateStatus();
				 context.put(door.getId(), door);
		     }
		}		  
		// no output parameter
		return null;
	}

	@Override
	public boolean hasOperation() {
		// Operation update doors status
		return true;
	}


	

}
