package eu.fbk.iv4xr.mbt.efsm.exp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public class VarSet<T> implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4020261488465440013L;

	private HashMap<String, Var<T>> varSet = new HashMap<String, Var<T>>();
	
	
	public VarSet(Var<T> var) {
		this.varSet.put(var.getId(), var);
	}
	
	public VarSet(List<Var<T>> varList) {
		for(Var<T> var : varList) {
			this.varSet.put(var.getId(), var);
		}	
	}
	
	public VarSet() {
	}
	
	public HashMap<String, Var<T>> getHash(){
		return this.varSet;
	}
	
	public Var<T> getVariable(String id) {
		if (this.varSet.containsKey(id)) {
			return( this.varSet.get(id) );
		}else {
			throw new RuntimeException("Variabile id "+id+" is not present");
		}
	}
	
	public void add(VarSet<T> addVarSet){
		this.varSet.putAll(addVarSet.getHash());
	}
	
	public void put(Var<T> var){
		this.varSet.put(var.getId(), var);
	}
	
	
	public boolean contain(String id) {
		if (varSet.containsKey(id)) {
			return true;
		}else {
			return false;
		}
	}
		
	public void update(String id, T val) {
		if (this.contain(id)) {
			this.getVariable(id).setValue(val);
		}
	}
	
	public String toDebugString() {
		String out = "";
		for(String key : this.varSet.keySet()) {
			out = out + this.varSet.get(key).toDebugString()+";"; 
		}
		
		return out;
	}
}