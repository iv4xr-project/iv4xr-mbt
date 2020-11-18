package eu.fbk.iv4xr.mbt.efsm4j.expression.integer;

import eu.fbk.iv4xr.mbt.efsm4j.expression.BinaryOperator;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Constant;
import eu.fbk.iv4xr.mbt.efsm4j.expression.Expression;

public class IntDiff extends BinaryOperator<Integer,Integer>{

	public IntDiff(Expression<Integer,Integer> parameter1, Expression<Integer,Integer> parameter2) {
		super(parameter1, parameter2);
	}

	@Override
	public IntConstant eval() {
		Integer v1 = this.getParameter1().eval().getVal();
		Integer v2 = this.getParameter2().eval().getVal();
		return new IntConstant(v1-v2);	}

}
