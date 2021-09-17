package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;


import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.AssignSet;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

public class EFSMOperation implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 503965196716794551L;
	
	private AssignSet operations;
	
	public EFSMOperation(Assign... assigns) {
		if (operations == null) {
			operations = new AssignSet();
		}
		for(Assign a : assigns ) {
			operations.put(a);
		}	
	}
	
	@Override
	public EFSMOperation clone() {
		
		return new EFSMOperation((Assign[]) operations.getHash().values().toArray());
	}
	
	public AssignSet getAssignments() {
		return this.operations;
	}
	

	@Override
	public String toString() {
		if (operations != null) {
			return operations.toDebugString();
		} else {
			return "NOP";
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof EFSMOperation) {
			EFSMOperation op = (EFSMOperation)o;
			if (op.getAssignments().equals(this.operations) ) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}
}
