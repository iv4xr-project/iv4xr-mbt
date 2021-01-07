package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.AssignSet;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

public class EFSMContext implements Cloneable, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7951703032359100975L;
	
	private VarSet context;


	public EFSMContext(Var... vars) {
		if (context == null) {
			context = new VarSet();
		}
		for(Var v : vars ) {
			context.put(v);
		}	
	}
	
	@Override
	public EFSMContext clone() {	
		return SerializationUtils.clone(this);
	}

	public VarSet getContext() {
		return this.context;
	}
	
	// update the context with the input variables (a varset)
//	public void update(VarSet<?> vs) {
		
//	}
	
	// update the context with the operation (an assignset)
	public void update(AssignSet<?> as) {
		Set<String>  varToUpdate = as.getHash().keySet();
		for(String s: varToUpdate) {
			if (context.contain(s)) {
				context.update(s, as.getAssignment(s).getVariable().getValue());
			}
		}		
	}
	
}
