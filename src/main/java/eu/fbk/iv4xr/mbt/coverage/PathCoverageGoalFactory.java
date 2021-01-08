/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;


/**
 * @author kifetew
 *
 */
public class PathCoverageGoalFactory<State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
	implements CoverageGoalFactory<PathCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> {

	/**
	 * 
	 */
	public PathCoverageGoalFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<PathCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getCoverageGoals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}



}
