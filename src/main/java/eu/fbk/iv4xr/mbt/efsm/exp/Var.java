package eu.fbk.iv4xr.mbt.efsm.exp;

import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

public class Var<T> implements Exp<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 828018016187453541L;

	private String id;
	
	private T value;
	
	public Var(String id, T value) {
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return(id);
	}
	
	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return this.value;
	}
	
	@Override
	public Const<T> eval() {
		return new Const<T>(value);
	}

	@Override
	public VarSet<?> getVariables() {
		// TODO Auto-generated method stub
		return new VarSet(this);
	}

	@Override
	public void update(VarSet<?> varSet) {
		if (varSet.contain(this.id)) {
			this.value = (T) varSet.getVariable(id).getValue();
		}
		
	}

	@Override
	public String toDebugString() {
		return id+"["+value.toString()+"]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Var) {
			Var v = (Var)o;
			if (v.getValue().equals(this.value) && v.getId().equals(this.id)) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}

}
