package eu.fbk.iv4xr.mbt.efsm.exp.bool;

import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;

public class BoolEq extends CompareOp{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4934384277294510783L;

	public BoolEq(Exp<Boolean> parameter1, Exp<Boolean> parameter2) {
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

}
