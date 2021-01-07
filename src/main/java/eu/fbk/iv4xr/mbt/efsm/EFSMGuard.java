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
	
}
