package eu.fbk.iv4xr.mbt.efsm.exp.integer;

import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;

public class IntLess extends CompareOp  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4877455098757808070L;

	public IntLess(Exp<?> parameter1, Exp<?> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Boolean> eval() {
		Integer v1 = (Integer) this.getParameter1().eval().getVal();
		Integer v2 = (Integer) this.getParameter2().eval().getVal();
		if (v1<v2) {
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
		if (o instanceof IntLess) {
			IntLess is = (IntLess) o;
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
		return "("+this.getParameter1().toDebugString()+" < "+this.getParameter2().toDebugString()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof IntLess) {
			IntLess is = (IntLess) o;
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
