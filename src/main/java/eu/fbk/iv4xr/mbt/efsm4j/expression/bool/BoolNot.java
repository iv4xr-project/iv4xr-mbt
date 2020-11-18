package eu.fbk.iv4xr.mbt.efsm4j.expression.bool;

import eu.fbk.iv4xr.mbt.efsm4j.expression.Expression;
import eu.fbk.iv4xr.mbt.efsm4j.expression.UnaryOperator;

public class BoolNot extends UnaryOperator<Boolean,Boolean> {

	public BoolNot(Expression<Boolean,Boolean> parameter) {
		super(parameter);
	}

	@Override
	public BoolConstant eval() {
		Boolean v = this.getParameter().eval().getVal();
		return new BoolConstant(! v);
	}

}
