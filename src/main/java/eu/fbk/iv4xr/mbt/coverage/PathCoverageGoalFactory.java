/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;


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

	private TreeSingleSourcePathsImpl<State, EFSMTransition> pathFactory; 
	private EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = AlgorithmFactory.getModel();
	
	/**
	 * 
	 */
	public PathCoverageGoalFactory() {
		Map<State, Pair<Double, EFSMTransition>> distanceAndPredecessorMap = buildDistanceMap();
		pathFactory = new TreeSingleSourcePathsImpl<State, EFSMTransition>((Graph<State, EFSMTransition>) model.getBaseGraph(), model.getInitialConfiguration().getState(), distanceAndPredecessorMap );
	}

	private Map<State, Pair<Double, EFSMTransition>> buildDistanceMap() {
		Map<State, Pair<Double, EFSMTransition>> map = new HashMap<>();
		for (State s : model.getStates()) {
			Set<EFSMTransition> transitionsInTo = model.transitionsInTo(s);
			for (EFSMTransition t : transitionsInTo) {
				map.put(s, new Pair(1d, t));
			}
		}
		return map;
	}

	@Override
	public List<PathCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getCoverageGoals() {
		//State targetVertex = model.getInitialConfiguration().getState();
		List<PathCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> goals = new ArrayList<>();
		for (State targetVertex : model.getStates()) {
			GraphPath<State, EFSMTransition> path = pathFactory.getPath(targetVertex);
			EFSMPath efsmPath = new EFSMPath<>(path);
			PathCoverageGoal coverageGoal = new PathCoverageGoal<>(efsmPath);
			goals.add(coverageGoal);
		}
		return goals;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}



}
