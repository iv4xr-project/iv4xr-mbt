package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashSet;

public class Constant<T,K> implements Expression<T,K> {
	
	private K value;
	
	public Constant(K value) {
		this.value = value;
	}

	public K getVal() {
		return value;
	}
	
	@Override
	public Constant<T,K> eval() {
		return this;
	}
	@Override
	public VarSet<T,K> getVariables() {
		return new VarSet<T,K>();
	}

	@Override
	public void update(VarSet<T, K> varList) {
	}
	
}
