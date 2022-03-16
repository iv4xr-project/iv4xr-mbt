package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.math3.random.MersenneTwister;
import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

import eu.fbk.iv4xr.mbt.utils.EqualsWithNulls;
/**
 * 
 * This class is derived from from the EFMS4J project created by
 * Manuel Benz. @see <a href="https://github.com/mbenz89/EFSM4J">EFSM4J</a>
 * 
 * 
 * @author prandi
 *
 */

public class EFSMTransition implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2629819885606102521L;

	/**
	 * 
	 */
	private String id;
	private EFSMState src;
	private EFSMState tgt;
	private EFSMOperation op;
	private EFSMGuard guard;
	private EFSMParameter inParameter;
	private EFSMParameter outParameter;
	
	//MersenneTwister random = new MersenneTwister();
	
	/*
	 *  Source and target states are defined by the EFSM builder.
	 *  Other parameters are defined by setter methods.
	 *  Maybe this could be made safer.
	 */
	
	public EFSMTransition(String id, EFSMOperation op, EFSMGuard guard, EFSMParameter inParameter, 
			EFSMParameter outParameter) {
		this.id = id;
		this.op = op;
		this.guard = guard;
		this.inParameter = inParameter;
		this.outParameter = outParameter;
	}
	
	public EFSMTransition () {
		// assign a unique id
		id = generateUniqueId();
	}
	
	public EFSMTransition(String id) {
		if (id == null || id.isEmpty()) {
			this.id = generateUniqueId();
		}else {
			this.id = id;
		}
	}
	
	private String generateUniqueId () {
		return "" + Randomness.nextLong(); // Randomness.nextLong() + "_" + System.currentTimeMillis();
	}
	
	/*
	 * Setter and getter
	 */
	
	public EFSMState getSrc() {
		return src;
	}

	protected void setSrc(EFSMState src) {
		this.src = src;
	}

	public EFSMState getTgt() {
		return tgt;
	}

	protected void setTgt(EFSMState tgt) {
		this.tgt = tgt;
	}
	
	/*
	 * Setter are public
	 */
	
	public EFSMOperation getOp() {
		return op;
	}
	
	public void setOp(EFSMOperation op) {
		this.op = op;
	}
	
	public EFSMGuard getGuard() {
		return guard;
	}
	
	public void setGuard(EFSMGuard guard) {
		this.guard = guard;
	}
	
	public EFSMParameter getInParameter() {
		return inParameter;
	}
	
	public void setInParameter(EFSMParameter inParameter) {
		this.inParameter = inParameter;
	}
	
	public EFSMParameter getOutParameter() {
		return outParameter;
	}
	
	public void setOutParameter(EFSMParameter outParameter) {
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
	public boolean isFeasible(EFSMContext context) {
		
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
	public Set<EFSMParameter> take(EFSMContext contex) {
		Set<EFSMParameter> apply = operation(this.inParameter, contex, this.op);

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
	public Set<EFSMParameter> tryTake(EFSMContext context) {
		if (isFeasible(context)) {
			return take(context);
		} else {
			return null;
		}
	}

	
	/*
	 * To implement
	 */
	protected Set<EFSMParameter> operation(EFSMParameter input, EFSMContext context, EFSMOperation op){
	
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

	
	/**
	 * IMPORTANT NOTE: this clone method is NOT a deep copy. It just returns a copy. 
	 */
	@Override
	public EFSMTransition clone() {	
		EFSMTransition copy = new EFSMTransition(id, op, guard, inParameter, outParameter);
		copy.src = src;
		copy.tgt = tgt;
		return copy;
	}
	
	
	@Override
	public String toString() {
		if (src == null | tgt == null) {
			return "";
		}
		
		String inParStr = new String("");
		String guardString = new String("");
		
		if (inParameter != null) {
			inParStr = inParameter.toString();
		}
		
		if (guard != null) {
			guardString = guard.toString();
		}
		
		return src.toString()+"-{"+inParStr+"}->"+tgt.toString();
	}
	
	public boolean isSelfTransition () {
		return (src.equals(tgt));
	}

	public boolean exactEquals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EFSMTransition) {
			EFSMTransition t = (EFSMTransition)obj;
			return( t.getSrc().equals(src) && 
					t.getTgt().equals(tgt) && 
					EqualsWithNulls.test(t.getInParameter(),inParameter) &&
					EqualsWithNulls.test(t.getOutParameter(),outParameter) &&
					EqualsWithNulls.test(t.getGuard(), guard) &&
					EqualsWithNulls.test(t.getOp(), op));
		}else {
			return false;
		}
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EFSMTransition) {
			EFSMTransition t = (EFSMTransition)obj;
			return id.contentEquals(t.getId());
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode(); //Objects.hash(src, tgt, op, guard, inParameter, outParameter);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
