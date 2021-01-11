package eu.fbk.iv4xr.mbt.efsm.exp;

public class Const<T> implements Exp<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 605884951516085182L;
	
	private T value;
	
	public Const(T value) {
		this.value = value;
	}

	public T getVal() {
		return value;
	}
	
	@Override
	public Const<T> eval() {
		return this;
	}

	@Override
	public VarSet<?> getVariables() {
		// TODO Auto-generated method stub
		return new VarSet();
	}

	@Override
	public void update(VarSet<?> varSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toDebugString() {
		return value.toString();
	}

}
