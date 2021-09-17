package eu.fbk.iv4xr.mbt.efsm.exp;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;

public class AssignSet<T> implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626369483368748167L;
	
	/*
	 * In an AssignSet, differently from VarSet, order is important,
	 * so we use a linked hashmap
	 */
	private LinkedHashMap<String, Assign<T>> assignSet = new LinkedHashMap<String, Assign<T>>();
	
	public AssignSet(Assign<T> assign) {
		this.assignSet.put(assign.getVariable().getId(),assign);
	}
	
	public AssignSet(List<Assign<T>> assignList) {
		for(Assign<T> assign : assignList) {
			this.assignSet.put(assign.getVariable().getId(),assign);
		}	
	}
	
	public AssignSet() {
	}

	
	public LinkedHashMap<String, Assign<T>> getHash(){
		return this.assignSet;
	}
	
	public Assign<T> getAssignment(String id) {
		if (this.assignSet.containsKey(id)) {
			return( this.assignSet.get(id) );
		}else {
			throw new RuntimeException("Variabile id "+id+" is not present");
		}
	}
	
	public void add(AssignSet<T> addAssignSet){
		this.assignSet.putAll(addAssignSet.getHash());
	}
	
	public void put(Assign<T> assign){
		this.assignSet.put(assign.getVariable().getId(), assign);
	}
	
	
	public boolean contain(String id) {
		if (assignSet.containsKey(id)) {
			return true;
		}else {
			return false;
		}
	}
	
	public String toDebugString() {
		String out = "";
		for(String key : this.assignSet.keySet()) {
			out = out + this.assignSet.get(key).toDebugString()+"; "; 
		}
		
		return out;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof AssignSet) {
			AssignSet as = (AssignSet)o;
			if (as.getHash().equals(this.assignSet)) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}
}
