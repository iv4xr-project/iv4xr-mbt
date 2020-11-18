package eu.fbk.iv4xr.mbt.efsm4j.expression.compare;

import eu.fbk.iv4xr.mbt.efsm4j.expression.BinaryOperator;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Constant;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Expression;

public class Greater extends BinaryOperator<Boolean,Integer>{

	public Greater(Expression<Boolean, Integer> parameter1, Expression<Boolean, Integer> parameter2) {
		super(parameter1, parameter2);
	}

	@Override
	public Constant<Boolean, Integer> eval() {
		return null;
		//Integer v1 = this.getParameter1().eval().getVal();
		//Integer v2 = this.getParameter2().eval().getVal();
		//Boolean outVal 
		//Constant<Boolean, Integer> out = Constant<Boolean, Integer>
		//return new Constant<Boolean, Integer>()
	}



}
