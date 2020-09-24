/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import org.evosuite.ga.Chromosome;
//import org.evosuite.ga.FitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;

import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionListener;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoal<
	State extends EFSMState,
	Parameter extends EFSMParameter,
	Context extends IEFSMContext<Context>,
	Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends CoverageGoal<State, Parameter, Context, Trans> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8816426341946761190L;

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(StateCoverageGoal.class);
	
	private EFSMState state;
	
	/**
	 * 
	 */
	public StateCoverageGoal(State s) {
		state = s;
		testExecutor = new EFSMTestExecutor<State, Parameter, Context, Trans>();
	}

	@Override
	public double getFitness(Chromosome individual) {
		double fitness = -1;
		if (individual instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)individual;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionListener<State, Parameter, Context, Trans> executionListner = new EFSMTestExecutionListener<State, Parameter, Context, Trans>();
			testExecutor.addListner(executionListner);
			ExecutionResult executionResult = testExecutor.executeTestcase(testcase);
			// get trace from the listner
			ExecutionTrace trace = executionListner.getExecutionTrace();
			
			Path path = testcase.getPath();
			if (executionResult.isSuccess()) {
				if (path.getStates().contains(state)) {
					fitness = 0;
				}else {
					fitness = 1;
				}
			}else {
				fitness = -100; //infeasible path
			}
		}
		updateIndividual(individual, fitness);
		return fitness;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
