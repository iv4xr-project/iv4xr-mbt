package eu.fbk.iv4xr.mbt.efsm.exp;


public class Assign<T extends Object> {

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
	
}
