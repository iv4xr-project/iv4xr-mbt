package eu.fbk.iv4xr.mbt.efsm.exp.integer;

import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;


public class IntEq extends CompareOp{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6610983397881856917L;

	public IntEq(Exp<Integer> parameter1, Exp<Integer> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Boolean> eval() {
		if (this.getParameter1().eval().getVal().equals(this.getParameter2().eval().getVal())) {
			return new Const<Boolean>(true);
		} else {
			return new Const<Boolean>(false);
		}
	}

	@Override
	public String toDebugString() {
		return "("+this.getParameter1().toDebugString()+" = "+this.getParameter2().toDebugString()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof IntEq) {
			IntEq is = (IntEq) o;
			if ((is.getParameter1().equals(this.getParameter1()) && is.getParameter2().equals(this.getParameter2()))
					|| (is.getParameter1().equals(this.getParameter2())
							&& is.getParameter2().equals(this.getParameter1()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean equalsUpToValue(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof IntEq) {
			IntEq is = (IntEq) o;
			if ((is.getParameter1().equalsUpToValue(this.getParameter1()) && is.getParameter2().equalsUpToValue(this.getParameter2()))
					|| (is.getParameter1().equalsUpToValue(this.getParameter2())
							&& is.getParameter2().equalsUpToValue(this.getParameter1()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
