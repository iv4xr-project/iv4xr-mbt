package eu.fbk.iv4xr.mbt.efsm4j.expression;

public class Assignment<T,K> {
	
	Variable<T,K> variable;
	
	Expression<T,K> expression;
	
	Assignment(Variable<T,K> variable, Expression<T,K> expression ){
		this.variable = variable;
		this.expression = expression;
	}
	
	
	
}
