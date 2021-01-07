package eu.fbk.iv4xr.mbt.efsm.exp.bool;

import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;


public class BoolOr extends BinaryOp<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5407624683789682880L;

	public BoolOr(Exp<Boolean> parameter1, Exp<Boolean> parameter2) {
		super(parameter1, parameter2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Const<Boolean> eval() {
		Boolean v1 = (Boolean) this.getParameter1().eval().getVal();
		Boolean v2 = (Boolean) this.getParameter2().eval().getVal();
		return ( new Const<Boolean>(v1 || v2)); 
	}

}
