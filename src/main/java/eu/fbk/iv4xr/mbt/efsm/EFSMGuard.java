package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

public class EFSMGuard implements  Cloneable, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 986337555034767383L;

	private Exp<Boolean> guard;
	
	public EFSMGuard(Exp<Boolean> guard) {
		this.guard = guard;
	}
	
	@Override
	public EFSMGuard clone() {	
		return SerializationUtils.clone(this);
	}
	
	public void updateVariables(VarSet varSet) {
		guard.update(varSet);
	}
	
	public Exp<Boolean> getGuard() {
		return(this.guard);
	}
	
	public String toDebugStrig() {
		return guard.toDebugString();
	}
	
	public String toString() {
		if (guard == null) {
			return "";
		}
		return this.toDebugStrig();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof EFSMGuard) {
			EFSMGuard g = (EFSMGuard)o;
			if (g.getGuard().equals(((EFSMGuard) o).getGuard())) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}
}
