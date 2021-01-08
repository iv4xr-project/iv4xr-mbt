/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.Collection;
import java.util.HashSet;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;

/**
 * @author kifetew
 *
 */
public class ExecutionTrace<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> {
	
	private Collection<State> coveredStates = new HashSet<State>();
	Collection<Transition> coveredTransitions = new HashSet<Transition>();
	
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
