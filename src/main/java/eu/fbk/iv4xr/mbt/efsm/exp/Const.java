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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Const) {
			Const c = (Const)o;
			if (c.getVal().equals(this.value)) {
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}
	}

	@Override
	public boolean equalsUpToValue(Object o) {
		return equals(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


}
