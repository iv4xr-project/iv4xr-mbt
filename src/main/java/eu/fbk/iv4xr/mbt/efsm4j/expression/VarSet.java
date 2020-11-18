/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashMap;
import java.util.List;

public class VarSet<T,K> implements Cloneable{
	
	private HashMap<String, Variable<T,K>> varSet = new HashMap<String, Variable<T,K>>();
	
	public VarSet(Variable<T,K> var) {
		this.varSet.put(var.getId(), var);
	}
	
	public VarSet(List<Variable<T,K>> varList) {
		for(Variable<T,K> var : varList) {
			this.varSet.put(var.getId(), var);
		}	
	}
	
	public VarSet() {
	}
	
	public HashMap<String, Variable<T,K>> getHash(){
		return this.varSet;
	}
	
	public Variable<T,K> getVariable(String id) {
		if (this.varSet.containsKey(id)) {
			return( this.varSet.get(id) );
		}else {
			throw new RuntimeException("Variabile id "+id+" is not present");
		}
	}
	
	public void add(VarSet<T,K> addVarSet){
		this.varSet.putAll(addVarSet.getHash());
	}
	
	public boolean contain(String id) {
		if (varSet.containsKey(id)) {
			return true;
		}else {
			return false;
		}
	}
}
