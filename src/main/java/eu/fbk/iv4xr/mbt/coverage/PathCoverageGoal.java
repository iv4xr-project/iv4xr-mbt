/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 */
public class PathCoverageGoal<
State,
Parameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends CoverageGoal<State, Parameter, Context, Trans> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8117932676170582125L;

	/**
	 * 
	 */
	public PathCoverageGoal() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFitness(Chromosome individual) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
