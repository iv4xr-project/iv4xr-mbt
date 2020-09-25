/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.execution.TestExecutor;

/**
 * @author kifetew
 *
 */
public abstract class CoverageGoal<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends FitnessFunction<Chromosome> {
	
	protected TestExecutor<State, Parameter, Context, Trans> testExecutor;
	
	public abstract double getFitness(Chromosome test);
	
}
