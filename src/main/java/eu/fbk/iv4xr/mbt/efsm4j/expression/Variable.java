package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashSet;

public class Variable<T,K> implements Expression<T,K>{

	private String id;
	
	private K value;
	
	public Variable(String id, K value) {
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return(id);
	}
	
	public void setValue(K value) {
		this.value = value;
	}

	public K getValue() {
		return this.value;
	}
	
	@Override
	public Constant<T,K> eval() {
		return new Constant(value);
	}

	@Override
	public VarSet<T,K>  getVariables() {
		VarSet<T,K> out = new VarSet<T, K>(this);
		return(out);
	}

	@Override
	public void update(VarSet<T,K>  varList) {
		if (varList.contain(this.id)) {
			this.value = varList.getVariable(id).getValue();
		}
	}

}

