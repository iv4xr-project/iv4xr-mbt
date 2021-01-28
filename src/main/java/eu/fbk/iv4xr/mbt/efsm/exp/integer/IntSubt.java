package eu.fbk.iv4xr.mbt.efsm.exp.integer;


import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;

public class IntSubt extends BinaryOp<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5561533677788953434L;

	public IntSubt(Exp<?> parameter1, Exp<?> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Integer> eval() {
		Integer v1 = (Integer) this.getParameter1().eval().getVal();
		Integer v2 = (Integer) this.getParameter2().eval().getVal();
		return new Const<Integer>(v1-v2);
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
		if (o instanceof IntSubt) {
			IntSubt is = (IntSubt) o;
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
