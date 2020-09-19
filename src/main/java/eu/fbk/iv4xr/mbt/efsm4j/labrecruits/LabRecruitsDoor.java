package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 
 * A door has 3 elements:
 *  an id; 
 *  a boolean status: FALSE means closed, TRUE means open;
 *  a list of switches that change door status.
 * 
 * @author Davide Prandi
 *
 */

public class LabRecruitsDoor implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2524023069430598377L;

	
	private String id;
	private Boolean status;
	private HashSet<String> controlButtons;
	
	// By default a door is closed
	public LabRecruitsDoor(String id, HashSet<String> controlButtons) {
		this.id = id;
		this.status = Boolean.FALSE;	
		this.controlButtons = controlButtons;
	}
	
	// Support a custom door status initializations
	public LabRecruitsDoor(String id, Boolean status, HashSet<String> controlButtons) {
		this.id = id;
		this.status = status;
		this.controlButtons = controlButtons;
	}
	
	// Get current status
	public Boolean getStatus() {
		return this.status;
	}

	// Update current status
	public void updateStatus() {
		this.status = ! status;
	}
	
	// Get id
	public String getId() {
		return this.id;
	}
	
	// Add button controlling the door
	public void addButton(String id) {
		controlButtons.add(id);
	}
	
	// Return the buttons that control the door
	public HashSet<String> getButtons(){
		return this.controlButtons;
	}

}
