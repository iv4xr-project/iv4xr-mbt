/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;

import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;

/**
 * @author kifetew
 *
 */
public class PathCoverageGoal extends CoverageGoal{
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 8117932676170582125L;

	
	private EFSMPath path;
	
	/**
	 * 
	 */
	public PathCoverageGoal(EFSMPath p) {
		path = p;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (! (obj instanceof PathCoverageGoal)) {
			return false;
		}
		
		PathCoverageGoal other = (PathCoverageGoal)obj;
		return path.equals(other.path);
		
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	protected void updateCollateralCoverage(Chromosome individual, ExecutionResult executionResult) {
		// collateral coverage only if the individual is valid
		if (executionResult.isSuccess()) {
			// collect colateral coverage
		}
	}

	@Override
	public String toString() {
		return path == null? "" : path.toString();
	}

	

}
