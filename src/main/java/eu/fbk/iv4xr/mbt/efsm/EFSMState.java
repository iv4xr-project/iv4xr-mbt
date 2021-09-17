package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * A state is a non empty string. Passing an empty string results in a runtime exception 
 */
public class EFSMState implements Comparable<EFSMState>, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3837571448310382982L;
	
	private final String id;
	
	public EFSMState(String id) {
		if (id == "" || id == null) {
			throw new RuntimeException("EFSMState id cannot be empty string or null");
		}else {
			this.id = id;
		}
		
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
		if (other.getId().equals(id)) {
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
	
	@Override
	public String toString() {
		return id;
	}
	
}
