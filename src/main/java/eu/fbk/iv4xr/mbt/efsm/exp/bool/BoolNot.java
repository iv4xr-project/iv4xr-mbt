package eu.fbk.iv4xr.mbt.efsm.exp.bool;

import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.UnaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;


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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof BoolNot) {
			BoolNot is = (BoolNot) o;
			if ( is.getParameter().equals(this.getParameter()) ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}