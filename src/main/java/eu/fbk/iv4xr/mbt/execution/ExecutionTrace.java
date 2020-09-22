/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.Collection;
import java.util.HashSet;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;

/**
 * @author kifetew
 *
 */
public class ExecutionTrace<
State,
Parameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> {
	private Collection<State> coveredStates = new HashSet<State>();
	Collection<Trans> coveredTransitions = new HashSet<Trans>();
	
	/**
	 * 
	 */
	public ExecutionTrace() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the coveredStates
	 */
	public Collection<State> getCoveredStates() {
		return coveredStates;
	}

	/**
	 * @param coveredStates the coveredStates to set
	 */
	public void setCoveredStates(Collection<State> coveredStates) {
		this.coveredStates = coveredStates;
	}

}
