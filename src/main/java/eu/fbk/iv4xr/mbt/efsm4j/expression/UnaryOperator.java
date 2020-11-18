package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashSet;

public abstract class UnaryOperator<T,K> implements Expression<T,K> {
	
	private Expression<T,K> parameter;
	
	public UnaryOperator(Expression<T,K> parameter){
		this.parameter = parameter;
	}
	
	public Expression<T,K> getParameter() {
		return parameter;
	}
	
	public VarSet<T,K> getVariables(){	
		return parameter.getVariables();
	}
	
	public void update(VarSet<T,K>  varList) {
		this.parameter.update(varList);
	};
}
