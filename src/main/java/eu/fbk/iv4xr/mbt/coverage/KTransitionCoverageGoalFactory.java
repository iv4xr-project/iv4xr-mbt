/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;


/**
 * For each state of the EFMS model generate all paths with at most k transition and remove k-1 step paths. 
 * Use getAllPaths method from JGrapht
 * 
 * @author prandi
 *
 * @param <State>
 * @param <InParameter>
 * @param <OutParameter>
 * @param <Context>
 * @param <Operation>
 * @param <Guard>
 * @param <Transition>
 */
public class KTransitionCoverageGoalFactory<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		implements CoverageGoalFactory<KTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> {

	private List<KTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> coverageGoals = 
			new ArrayList<KTransitionCoverageGoal<State,InParameter,OutParameter,Context,Operation,Guard,Transition>>();
	/**
	 * Build the set of k transition coverage goals
	 */
	public KTransitionCoverageGoalFactory() {
		// get the model from the factory
		EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = EFSMFactory.getInstance().getEFSM();	
		// get the graph
		ListenableGraph<State, EFSMTransition> baseGraph = model.getBaseGraph();
		// All direct path computation
		AllDirectedPaths<State, Transition> allDirectedPathCalculator = model.getAllDirectedPathCalculator();
		
		Set<State> vertexSet = baseGraph.vertexSet();
		
		// iterate over states
		for(State s: vertexSet) {
			// get all >=k path from s
			Set<State> sourceSet = new LinkedHashSet<>();
			sourceSet.add(s);
			List<GraphPath<State, Transition>> allPaths = allDirectedPathCalculator.getAllPaths(sourceSet, vertexSet, false, MBTProperties.k_transition_size);
			// filter path with length less than k and transform into a KTransitionCoverageGoal
			for(GraphPath<State, Transition> path : allPaths ) {
				if (path.getLength() != MBTProperties.k_transition_size) {
					continue;
				}
				EFSMPath efsmPath = new EFSMPath<>(path);
				KTransitionCoverageGoal<State,InParameter,OutParameter,Context,Operation,Guard,Transition> goal = new KTransitionCoverageGoal<>(efsmPath);
				coverageGoals.add(goal);	
			}
		}	
	}
	
	
	@Override
	public List<KTransitionCoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}




}

