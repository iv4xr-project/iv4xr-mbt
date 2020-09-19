package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;

/**
 * 
 * A state has an id. We need to specify if the state represent a door and, if so, record the name of the door.
 * 
 * @author Davide Prandi
 * 
 */

//import eu.fbk.iv4xr.mbt.efsm4j.*;

//public class LabRecruitsState implements Comparable<LabRecruitsState>{
public class LabRecruitsState extends EFSMState{

	private final String id;
	private final Boolean hasDoor;
	private final String doorId;
	
	
	public LabRecruitsState(String id) {
		this.id = id;
		this.hasDoor = false;
		this.doorId = "";
	}
	
	public LabRecruitsState(String id, String doorId) {
		this.id = id;
		this.hasDoor = true;
		this.doorId = doorId;
	}
	
	
	public String getId() {
		return id;
	} 

	protected Boolean hasDoor() {
		return hasDoor;
	}
	
	protected String getDoorId() {
		return doorId;
	}
	
	
	@Override
	public String toString() {
		if (hasDoor) {
			return id + "(with " + doorId + ")";
		}else {
			return id;
		}
		
	}

	/**
	 * To check if comparable is correctly implemented
	 */
	@Override
	public int compareTo(EFSMState o) {
		/*
		 * if (getId().compareTo(o.getId()) == 0){ return
		 * getDoorId().compareTo(o.getDoorId()); }
		 */
		return getId().compareTo(o.getId()) ;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof LabRecruitsState) {
//			LabRecruitsState other = (LabRecruitsState)obj;
//			return (compareTo(other) == 0);
//		}else {
//			return false;
//		}
//	}

	
	/**
	 * the following two methods are generated automatically by Eclipse
	 */
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((doorId == null) ? 0 : doorId.hashCode());
		result = prime * result + ((hasDoor == null) ? 0 : hasDoor.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof LabRecruitsState))
			return false;
		LabRecruitsState other = (LabRecruitsState) obj;
		if (doorId == null) {
			if (other.doorId != null)
				return false;
		} else if (!doorId.equals(other.doorId))
			return false;
		if (hasDoor == null) {
			if (other.hasDoor != null)
				return false;
		} else if (!hasDoor.equals(other.hasDoor))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}
