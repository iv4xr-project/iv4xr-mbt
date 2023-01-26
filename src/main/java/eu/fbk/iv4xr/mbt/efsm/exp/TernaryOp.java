package eu.fbk.iv4xr.mbt.efsm.exp;

/**
 * Abstract class for ternary operators as 
 * if par1 then par2 else par3
 * 
 * @author Davide Prandi
 *
 * @param <T>
 */
public abstract class TernaryOp<T> implements Exp<T>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6983413887213029146L;

	
	private Exp<?> parameter1;
	private Exp<?> parameter2;
	private Exp<?> parameter3;
	
	public TernaryOp(Exp<?> parameter1, Exp<?> parameter2, Exp<?> parameter3 ){
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
	}
	
	public Exp<?> getParameter1() {
		return parameter1;
	}

	public Exp<?> getParameter2() {
		return parameter2;
	}
	
	public Exp<?> getParameter3() {
		return parameter3;
	}
	
	@Override
	public VarSet<?> getVariables(){			
		VarSet out = new VarSet();
		out.add(parameter1.getVariables());
		out.add(parameter2.getVariables());
		out.add(parameter3.getVariables());
		return(out);
	}
	
	@Override
	public void update(VarSet<?> varSet) {
		this.parameter1.update(varSet);
		this.parameter2.update(varSet);
		this.parameter3.update(varSet);
	}
	
	@Override
	public abstract boolean equals(Object o);
	
}
