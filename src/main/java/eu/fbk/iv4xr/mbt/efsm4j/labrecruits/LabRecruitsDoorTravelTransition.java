package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

/**
 * 
 * 
 * Door travel transition with input parameter indicating the next state.
 * The inputGuard check if the target state corresponds to the input.
 * This transition has domain guard to check if the door is opened.
 * This transition does not perform operations.
 * 
 * @author Davide Prandi
 * 
 */

import java.util.Set;

import eu.fbk.iv4xr.mbt.efsm4j.PGDGTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;

public class LabRecruitsDoorTravelTransition  extends 
		PGDGTransition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>{

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
		return context.getDoorStatus(this.getSrc().getDoorId());
	}

	@Override
	protected Set<LabRecruitsParameter> operation(LabRecruitsParameter input, LabRecruitsContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasOperation() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
