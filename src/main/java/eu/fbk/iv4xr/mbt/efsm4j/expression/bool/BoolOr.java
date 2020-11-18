package eu.fbk.iv4xr.mbt.efsm4j.expression.bool;

import eu.fbk.iv4xr.mbt.efsm4j.expression.BinaryOperator;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Expression;
import eu.fbk.iv4xr.mbt.efsm4j.expression.integer.IntConstant;

public class BoolOr extends BinaryOperator<Boolean,Boolean>{

	public BoolOr(Expression<Boolean,Boolean> parameter1, Expression<Boolean,Boolean> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BoolConstant eval() {
		Boolean v1 = this.getParameter1().eval().getVal();
		Boolean v2 = this.getParameter2().eval().getVal();
		return new BoolConstant(v1 || v2);
	}

}