package eu.fbk.iv4xr.mbt.efsm4j.expression;

import java.util.HashSet;

/*
 * An expression has a general type T   
 */
public interface Expression<T,K> extends Cloneable{
	
	// Return the current value of the expression
	Constant<T,K> eval();
	
	// Return the list of the variables id used in the expression
	VarSet<T,K> getVariables();
	
	// update the value of the variables in an expression
	void update(VarSet<T,K>  varList);
}