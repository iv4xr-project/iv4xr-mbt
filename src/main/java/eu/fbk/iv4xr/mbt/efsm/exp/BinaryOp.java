package eu.fbk.iv4xr.mbt.efsm.exp;

public abstract class BinaryOp<T> implements Exp<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -192971772488798547L;
	
	private Exp<?> parameter1;
	private Exp<?> parameter2;
	
	public BinaryOp(Exp<?> parameter1, Exp<?> parameter2){
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
	}
	
	
	public Exp<?> getParameter1() {
		return parameter1;
	}

	public Exp<?> getParameter2() {
		return parameter2;
	}
	
	@Override
	public VarSet<?> getVariables(){			
		VarSet out = new VarSet();
		out.add(parameter1.getVariables());
		out.add(parameter2.getVariables());
		return(out);
	}
	
	@Override
	public void update(VarSet<?> varSet) {
		this.parameter1.update(varSet);
		this.parameter2.update(varSet);
	}

	@Override
	public abstract boolean equals(Object o);
	
}
