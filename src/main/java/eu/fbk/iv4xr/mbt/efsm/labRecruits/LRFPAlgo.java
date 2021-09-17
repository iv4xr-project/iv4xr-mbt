package eu.fbk.iv4xr.mbt.efsm.labRecruits;

/**
 * 
 * This class implement feasibility path detection. TODO
 * 
 * @author Davide Prandi
 * 
 */

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.Configuration;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMPath;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.JGraphBasedFPALgo;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm.JGraphBasedFPALgo;

public class LRFPAlgo extends 
	JGraphBasedFPALgo{

	public LRFPAlgo(
			EFSM efsm) {
		super(efsm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EFSMPath getPath(
			EFSMConfiguration config, EFSMState tgt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EFSMPath> getPaths(
			EFSMConfiguration config, EFSMState tgt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pathExists(EFSMConfiguration config, EFSMState tgt) {
		// TODO Auto-generated method stub
		return false;
	}

	public EFSMPath getShortestPath (EFSMConfiguration config, EFSMState tgt){
		
		BellmanFordShortestPath algorithm = new BellmanFordShortestPath(efsm.getBaseGraph() );
		GraphPath path = algorithm.getPath(config.getState(), tgt);
		
		EFSMPath shortestPath = new EFSMPath(path);
		return shortestPath;
	}

}
