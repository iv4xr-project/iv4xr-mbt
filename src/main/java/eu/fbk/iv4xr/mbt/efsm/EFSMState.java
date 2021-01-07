package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

public class EFSMState implements Comparable<EFSMState>, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3837571448310382982L;
	
	private final String id;
	
	public EFSMState(String id) {
		this.id = id;
	}
	
	public String getId() {
		return(this.id);
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EFSMState))
			return false;
		EFSMState other = (EFSMState) obj;
		if (other.getId() == id) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(EFSMState o) {
		return this.getId().compareTo(o.getId()) ;
	}
	
	@Override
	public EFSMState clone() {	
		return SerializationUtils.clone(this);
	}
}
