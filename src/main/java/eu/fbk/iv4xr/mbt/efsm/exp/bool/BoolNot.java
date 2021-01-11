package eu.fbk.iv4xr.mbt.efsm.exp.bool;

import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.UnaryOp;


public class BoolNot extends UnaryOp<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6256036928178136187L;

	public BoolNot(Exp<Boolean> parameter) {
		super(parameter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Boolean> eval() {
		Boolean v = (Boolean) this.getParameter().eval().getVal();
		return ( new Const<Boolean>(! v));
	}

	@Override
	public String toDebugString() {
		return "!("+this.getParameter().toDebugString()+")";
	}
}