package eu.fbk.iv4xr.mbt.efsm.exp;

import java.io.Serializable;

public interface Exp<T extends Object> extends Cloneable, Serializable {

	// Return the current value of the expression
	Const<T> eval();
	
	// Return the list of the variables id used in the expression
	VarSet<?> getVariables();
	
	// update the value of the variables in an expression given a varSet
	void update(VarSet<?>  varSet);
	
	// check also values of variables
	@Override
	boolean equals(Object o);
	
	// equals that consider only name of variable for managing cases where
	// the current value of a variable is not known
	boolean equalsUpToValue(Object o);
	
	String toDebugString();
	
}
