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

import eu.fbk.iv4xr.mbt.efsm4j.Transition;

public class LabRecruitsDoorTravelTransition  extends Transition<LabRecruitsState, String, LabRecruitsContext>{

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
		return context.getDoorStatus(this.getSrc().getDoorId());
	}

	@Override
	protected Set<String> operation(String input, LabRecruitsContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasOperation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasDomainGuard() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean hasParameterGuard() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
