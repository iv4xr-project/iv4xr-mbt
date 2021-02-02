package eu.fbk.iv4xr.mbt.efsm.exp;

import java.io.Serializable;

import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;

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
	
	public Exp<T> getExpression(){
		return this.expression;
	}
	
	public String toDebugString() {
		return variable.toDebugString()+":="+expression.toDebugString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Assign) {
			Assign a = (Assign)o;
			if (a.getVariable().equals(this.variable) && a.getExpression().equals(this.expression)  ) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}
	
}
