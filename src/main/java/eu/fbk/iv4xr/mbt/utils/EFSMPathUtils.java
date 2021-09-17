package eu.fbk.iv4xr.mbt.utils;

import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;

/**
 * utilities for working with paths
 * @author kifetew
 *
 */
public class EFSMPathUtils {

	private EFSMPathUtils() {
		// TODO Auto-generated constructor stub
	}

	public static boolean pathContainsTarget (EFSMPath path, CoverageGoal target) {
		boolean contains = false;
		
		if (target instanceof StateCoverageGoal) {
			StateCoverageGoal targetState = (StateCoverageGoal)target;
			contains = path.getStates().contains(targetState.getState());
		}else if (target instanceof TransitionCoverageGoal) {
			TransitionCoverageGoal targetTransition = (TransitionCoverageGoal)target;
			contains = path.contains(targetTransition.getTransition());
		}else {
			throw new RuntimeException("Unsupported target type: " + target);
		}
		
		return contains;
	}
	
}
