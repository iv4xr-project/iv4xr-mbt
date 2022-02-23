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

import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;

/**
 * @author kifetew
 *
 */
public class StateCoverageGoal extends CoverageGoal {

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
	public StateCoverageGoal(EFSMState s) {
		state = s;
	}

	
	@Override
	public boolean isMaximizationFunction() {
		return false;
	}


	@Override
	public String toString() {
		return state == null?"":state.toString();
	}

	/**
	 * @return the state
	 */
	public EFSMState getState() {
		return state;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (! (obj instanceof StateCoverageGoal)) {
			return false;
		}
		
		StateCoverageGoal other = (StateCoverageGoal)obj;
		return state.equals(other.getState());
		
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}

	@Override
	protected void updateCollateralCoverage(Chromosome individual, ExecutionResult executionResult) {
		// collateral coverage only if the individual is valid
		if (executionResult.isSuccess()) {
			ExecutionTrace executionTrace = executionResult.getExecutionTrace();
			for (Object state : executionTrace.getCoveredStates()) {
				EFSMState coveredState = (EFSMState)state;
				CoverageGoal goal = new StateCoverageGoal(coveredState);
				individual.addFitness(goal, 0d);
			}
		}
		
	}

}
