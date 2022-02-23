/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;


/**
 * @author kifetew
 *
 */
public class PathCoverageGoalFactory implements CoverageGoalFactory {

//	private TreeSingleSourcePathsImpl<State, EFSMTransition> pathFactory; 
	private EFSM model = EFSMFactory.getInstance().getEFSM();
	
	/**
	 * 
	 */
	public PathCoverageGoalFactory() {
//		Map<State, Pair<Double, EFSMTransition>> distanceAndPredecessorMap = buildDistanceMap();
//		pathFactory = new TreeSingleSourcePathsImpl<State, EFSMTransition>((Graph<State, EFSMTransition>) model.getBaseGraph(), model.getInitialConfiguration().getState(), distanceAndPredecessorMap );
	}

//	private Map<State, Pair<Double, EFSMTransition>> buildDistanceMap() {
//		Map<State, Pair<Double, EFSMTransition>> map = new HashMap<>();
//		for (State s : model.getStates()) {
//			Set<EFSMTransition> transitionsInTo = model.transitionsInTo(s);
//			for (EFSMTransition t : transitionsInTo) {
//				map.put(s, new Pair(1d, t));
//			}
//		}
//		return map;
//	}

	@Override
	public List<PathCoverageGoal> getCoverageGoals() {
		//State targetVertex = model.getInitialConfiguration().getState();
//		List<PathCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> goals = new ArrayList<>();
//		for (State targetVertex : model.getStates()) {
//			GraphPath<State, EFSMTransition> path = pathFactory.getPath(targetVertex);
//			EFSMPath efsmPath = new EFSMPath<>(path);
//			PathCoverageGoal coverageGoal = new PathCoverageGoal<>(efsmPath);
//			goals.add(coverageGoal);
//		}
//		return goals;
		return null;
		
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}



}
