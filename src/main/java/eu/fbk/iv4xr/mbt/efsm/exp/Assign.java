package eu.fbk.iv4xr.mbt.efsm.exp;

import java.io.Serializable;

public class Assign<T extends Object> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2925157913689406695L;

	Var<T> variable;
	
	Exp<T> expression;
	
	public Assign(Var<T> variable, Exp<T> expression ){
		this.variable = variable;
		this.expression = expression;
	}
	
	public void update() {		
		variable.setValue(expression.eval().getVal());		
	}

	public Var<T> getVariable(){
		return this.variable;
	}
	
	public String toDebugString() {
		return variable.toDebugString()+":="+expression.toDebugString();
	}
	
}
