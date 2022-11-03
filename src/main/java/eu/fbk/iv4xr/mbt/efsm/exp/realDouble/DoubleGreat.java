package eu.fbk.iv4xr.mbt.efsm.exp.realDouble;

import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;

public class DoubleGreat extends CompareOp {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3250778583769981499L;

	public DoubleGreat(Exp<?> parameter1, Exp<?> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Boolean> eval() {
		Double v1 = (Double) this.getParameter1().eval().getVal();
		Double v2 = (Double) this.getParameter2().eval().getVal();
		if (v1>v2) {
			return (new Const<Boolean>(true));
		}else {
			return (new Const<Boolean>(false));
		}
	}

	@Override
	public boolean equalsUpToValue(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof DoubleGreat) {
			DoubleGreat is = (DoubleGreat) o;
			if ((is.getParameter1().equalsUpToValue(this.getParameter1()) && is.getParameter2().equalsUpToValue(this.getParameter2()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toDebugString() {
		return "("+this.getParameter1().toDebugString()+" > "+this.getParameter2().toDebugString()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof DoubleGreat) {
			DoubleGreat is = (DoubleGreat) o;
			if ((is.getParameter1().equals(this.getParameter1()) && is.getParameter2().equals(this.getParameter2()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
