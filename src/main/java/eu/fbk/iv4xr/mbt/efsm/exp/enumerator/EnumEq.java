package eu.fbk.iv4xr.mbt.efsm.exp.enumerator;

import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;

public class EnumEq extends CompareOp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2077177495643367567L;

	public EnumEq(Exp<Enum> parameter1, Exp<Enum> parameter2) {
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
