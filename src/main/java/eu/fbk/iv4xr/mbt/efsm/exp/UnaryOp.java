package eu.fbk.iv4xr.mbt.efsm.exp;

public abstract class UnaryOp<T> implements Exp<T> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2563030693318267959L;

	private Exp<?> parameter;
	
	
	public UnaryOp(Exp<?> parameter){
		this.parameter = parameter;
	}
	
	
	public Exp<?> getParameter() {
		return parameter;
	}
	
	@Override
	public VarSet<?> getVariables() {
		return this.parameter.getVariables();
	}

	@Override
	public void update(VarSet<?> varSet) {
		this.parameter.update(varSet);		
	}

	@Override
	public abstract boolean equals(Object o);

}
