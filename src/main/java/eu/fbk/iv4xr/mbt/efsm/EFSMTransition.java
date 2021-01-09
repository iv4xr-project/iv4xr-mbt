package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

/**
 * 
 * This class is derived from from the EFMS4J project created by
 * Manuel Benz. @see <a href="https://github.com/mbenz89/EFSM4J">EFSM4J</a>
 * 
 * 
 * @author prandi
 *
 */

public class EFSMTransition<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard> 
		implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2629819885606102521L;

	/**
	 * 
	 */
	private State src;
	private State tgt;
	private Operation op;
	private Guard guard;
	private InParameter inParameter;
	private OutParameter outParameter;
	
	/*
	 *  Source and target states are defined by the EFSM builder.
	 *  Other parameters are defined by setter methods.
	 *  Maybe this could be made safer.
	 */
	
	public EFSMTransition(Operation op, Guard guard, InParameter inParameter, OutParameter outParameter) {
		this.op = op;
		this.guard = guard;
		this.inParameter = inParameter;
		this.outParameter = outParameter;
	}
	
	public EFSMTransition() {
	}
	
	/*
	 * Setter and getter
	 */
	
	public State getSrc() {
		return src;
	}

	protected void setSrc(State src) {
		this.src = src;
	}

	public State getTgt() {
		return tgt;
	}

	protected void setTgt(State tgt) {
		this.tgt = tgt;
	}
	
	/*
	 * Setter are public
	 */
	
	public Operation getOp() {
		return op;
	}
	
	public void setOp(Operation op) {
		this.op = op;
	}
	
	public Guard getGuard() {
		return guard;
	}
	
	public void setGuard(Guard guard) {
		this.guard = guard;
	}
	
	public InParameter getInParameter() {
		return inParameter;
	}
	
	public void setInParameter(InParameter inParameter) {
		this.inParameter = inParameter;
	}
	
	public OutParameter getOutParameter() {
		return outParameter;
	}
	
	public void setOutParameter(OutParameter outParameter) {
		this.outParameter = outParameter;
	}
	
	
	
	/**
	 * 
	 * A transition is feasible if its guard is true. To evaluate a guard,
	 * the method collect context and input variables and eval the guard.
	 * 
	 * @param input
	 * @param context
	 * @return
	 */
	public boolean isFeasible(Context context) {
		
		// if no guard return true
		if (this.guard == null) {
			return true;
		}
		
		// maybe it is not necessary the updates because variables are passed by reference
		// TO CHECK
		
		// need to update the variable in the guard with the current context
		if (context != null) {
			this.guard.updateVariables(context.getContext());
		}
		
		
		// need to update the variable in the guard with input
		if (this.inParameter != null) {
			this.guard.updateVariables(this.inParameter.getParameter());	
		}
		
		// evaluate the guard
		Const<Boolean> currentGuardVal = this.guard.getGuard().eval();
		
		return currentGuardVal.getVal();
	}
	
	
	/**
	 * Assumes {@link Transition#isFeasible(Parameter, Context)} was called and
	 * returned true to work properly
	 *
	 * @param input
	 * @param context
	 * @return A set of output values
	 */
	public Set<OutParameter> take(Context contex) {
		Set<OutParameter> apply = operation(this.inParameter, contex, this.op);

		if (apply == null) {
			return Collections.emptySet();
		}

		return apply;
	}
	
	
	/**
	 * Tries to take the transition by calling
	 * {@link Transition#isFeasible(Parameter, Context)} itself and returning null
	 * if the transition cannot be taken.
	 *
	 * @param input
	 * @param context
	 * @return An (potentially empty) set of output values or null if the transition
	 *         is infeasible.
	 */
	public Set<OutParameter> tryTake(Context context) {
		if (isFeasible(context)) {
			return take(context);
		} else {
			return null;
		}
	}

	
	/*
	 * To implement
	 */
	protected Set<OutParameter> operation(InParameter input, Context context, Operation op){
	
		if (op != null) {
			LinkedHashMap tmp = op.getAssignments().getHash();
			Set<String> keys = tmp.keySet();
			for(String k : keys) {
				((Assign) tmp.get(k)).update();
			}	
			context.update(op.getAssignments());
		}		
		return null;	
	}
	
	/*
	 * Abstract methods
	 */
	//protected abstract boolean inputGuard(InParameter input);

	//protected abstract boolean domainGuard(Context context);

	//public abstract boolean hasOperation();

	//public abstract boolean hasDomainGuard();

	//public abstract boolean hasParameterGuard();

	@Override
	public EFSMTransition clone() {	
		return SerializationUtils.clone(this);
	}
}
