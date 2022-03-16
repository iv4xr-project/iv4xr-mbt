package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.AssignSet;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
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
		//return SerializationUtils.clone(this);
		
		// get all variables in the context
		Collection allVariables = context.getAllVariables();

		// create a new context but keeping the same variables
		Var[] allVariablesArray = (Var[]) allVariables.toArray(new Var[allVariables.size()]);
		EFSMContext newContext = new EFSMContext(allVariablesArray);
		
		return newContext;

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
	
	// to string for debugging
	public String toDebugString() {
		return context.toDebugString();
	}
	
	public String toString() {
		return this.toDebugString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EFSMContext))
			return false;
		EFSMContext other = (EFSMContext) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		return true;
	}
	
}
