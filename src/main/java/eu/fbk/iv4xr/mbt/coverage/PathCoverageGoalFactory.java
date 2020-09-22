/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public class PathCoverageGoalFactory<
State,
Parameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> implements CoverageGoalFactory<PathCoverageGoal<State, Parameter, Context, Trans>> {

	/**
	 * 
	 */
	public PathCoverageGoalFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<PathCoverageGoal<State, Parameter, Context, Trans>> getCoverageGoals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}



}
