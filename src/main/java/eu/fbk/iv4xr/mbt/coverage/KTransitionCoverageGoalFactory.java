/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public class KTransitionCoverageGoalFactory<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> implements CoverageGoalFactory<KTransitionCoverageGoal<State, Parameter, Context, Trans>> {

	/**
	 * 
	 */
	public KTransitionCoverageGoalFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<KTransitionCoverageGoal<State, Parameter, Context, Trans>> getCoverageGoals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}




}
