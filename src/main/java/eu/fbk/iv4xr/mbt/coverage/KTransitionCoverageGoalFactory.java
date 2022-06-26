/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;


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
public class KTransitionCoverageGoalFactory implements CoverageGoalFactory<KTransitionCoverageGoal> {

	private List<KTransitionCoverageGoal> coverageGoals = new ArrayList<KTransitionCoverageGoal>();
	
	/**
	 * Build the set of k transition coverage goals
	 */
	public KTransitionCoverageGoalFactory() {
		// get the model from the factory
		EFSM model = EFSMFactory.getInstance().getEFSM();	
		// get the graph
		DirectedPseudograph<EFSMState, EFSMTransition> baseGraph = model.getBaseGraph();
		// All direct path computation
		AllDirectedPaths<EFSMState, EFSMTransition> allDirectedPathCalculator = model.getAllDirectedPathCalculator();
		
		Set<EFSMState> vertexSet = baseGraph.vertexSet();
		
		// iterate over states
		for(EFSMState s: vertexSet) {
			// get all >=k path from s
			Set<EFSMState> sourceSet = new LinkedHashSet<>();
			sourceSet.add(s);
			List<GraphPath<EFSMState, EFSMTransition>> allPaths = allDirectedPathCalculator.getAllPaths(sourceSet, vertexSet, false, MBTProperties.K_TRANSITION_SIZE);
			// filter path with length less than k and transform into a KTransitionCoverageGoal
			for(GraphPath<EFSMState, EFSMTransition> path : allPaths ) {
				if (path.getLength() != MBTProperties.K_TRANSITION_SIZE) {
					continue;
				}
				EFSMPath efsmPath = new EFSMPath(path);
				KTransitionCoverageGoal goal = new KTransitionCoverageGoal(efsmPath);
				coverageGoals.add(goal);	
			}
		}	
	}
	
	
	@Override
	public List<KTransitionCoverageGoal> getCoverageGoals() {
		return coverageGoals;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}




}

