package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

public class EFSMParameter implements  Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6839254675772433933L;

	private VarSet parameter; 
	
	public EFSMParameter(Var... vars) {
		if (parameter == null) {
			parameter = new VarSet();
		}
		for(Var v : vars ) {
			parameter.put(v);
		}	
	}
	
	@Override
	public EFSMParameter clone() {	
		return SerializationUtils.clone(this);
	}
	
	public VarSet getParameter() {
		return this.parameter;
	}
	
	public String toDebugString() {
		return this.parameter.toDebugString();
	}
	
	@Override
	public String toString() {
		return this.toDebugString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof EFSMParameter) {
			EFSMParameter v = (EFSMParameter)o;
			if (v.getParameter().equals(this.parameter)) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}
}
