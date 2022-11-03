package eu.fbk.iv4xr.mbt.efsm.exp.realDouble;

import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;

public class DoubleSubt extends BinaryOp<Double>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7965015418943257180L;

	public DoubleSubt(Exp<?> parameter1, Exp<?> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Double> eval() {
		Double v1 = (Double) this.getParameter1().eval().getVal();
		Double v2 = (Double) this.getParameter2().eval().getVal();
		return new Const<Double>(v1-v2);
	}

	@Override
	public boolean equalsUpToValue(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof DoubleSubt) {
			DoubleSubt is = (DoubleSubt) o;
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
		return "("+this.getParameter1().toDebugString()+" - "+this.getParameter2().toDebugString()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof DoubleSubt) {
			DoubleSubt is = (DoubleSubt) o;
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
