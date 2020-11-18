package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashSet;

public abstract class BinaryOperator<T,K> implements Expression<T,K> {

	private Expression<T,K> parameter1;
	private Expression<T,K> parameter2;
	
	public BinaryOperator(Expression<T,K> parameter1, Expression<T,K> parameter2){
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
	}
	
	public VarSet<T,K> getVariables(){			
		VarSet<T,K> out = new VarSet<T,K>();
		out.add(parameter1.getVariables());
		out.add(parameter2.getVariables());
		return(out);
	}
	
	public Expression<T,K> getParameter1() {
		return parameter1;
	}

	public Expression<T,K> getParameter2() {
		return parameter2;
	}
	
	public void update(VarSet<T,K>  varList) {
		this.parameter1.update(varList);
		this.parameter2.update(varList);
	}
}
