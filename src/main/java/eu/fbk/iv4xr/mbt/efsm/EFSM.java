package eu.fbk.iv4xr.mbt.efsm;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.scoring.AlphaCentrality;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.ClusteringCoefficient;
import org.jgrapht.alg.scoring.HarmonicCentrality;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.graph.DirectedPseudograph;

import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.utils.VertexToIntegerMap;





/**
 * 
 * This class is derived from from the EFMS4J project created by
 * Manuel Benz. @see <a href="https://github.com/mbenz89/EFSM4J">EFSM4J</a>
 * 
 * 
 * @author prandi
 *
 */

public  class EFSM implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6330491569340874532L;
	
	public static final String PROP_CONFIGURATION = "PROP_CONFIGURATION";
	protected final EFSMContext initialContext;
	protected final EFSMState initialState;
	// protected final ListenableGraph<State, EFSMTransition> initialBaseGraph;
	//protected final DirectedPseudograph<EFSMState, EFSMTransition> initialBaseGraph;
	protected EFSMState curState;
	protected EFSMContext curContext;
	//protected ListenableGraph<State, EFSMTransition> baseGraph;
	protected DirectedPseudograph<EFSMState, EFSMTransition> baseGraph;
	
	
	// String version of the model
	private String efsmString = ""; 
	
	// String version of the mutated model
	//private List<String> removeMutationString = new LinkedList<String>();
	//private List<String> addMutationString = new LinkedList<String>();
	
	// ANML version of the model
	private String anmlString = "";
	
	// the model in dot format
	private String dotString = "";
	
	// to add
	protected EFSMParameterGenerator inParameterSet;

	/*
	 *  helper to access and manage baseGraph
	 */
	
	/*
	 * / shortest path related fields  
	 */
	// give the length of the shortest path between two states
	private double[][] shortestPathsBetweenStates;
	// save all the shortest paths between two states
	private Set<EFSMPath>[][] shortestPaths;
	// class from jgrapht that create a map between states and integer
	private VertexToIntegerMap<EFSMState> vertexToIntegerMapping;
	
	// compute all paths between two vertex
	private AllDirectedPaths<EFSMState, EFSMTransition> allPathsCalculator;
	// compute distance metrics
	private GraphMeasurer<EFSMState, EFSMTransition> graphMeasurer;
	
	// convenience map for retreiving transitions by their id, used during test execution on model
	private Map<String, EFSMTransition> transitionsMap = new HashMap<>();

	// Constructors
	public EFSM(DirectedPseudograph<EFSMState, EFSMTransition> baseGraph, 
					EFSMState initialState, 
					EFSMContext initalContext,
					EFSMParameterGenerator parameterSet) {
		
		// TODO try to not clone initial state
		this.curState = this.initialState = initialState.clone();
		this.curContext =  initalContext.clone(); //(Context) initalContext.clone();
		this.initialContext = SerializationUtils.clone(initalContext);
		this.inParameterSet = parameterSet;
		this.baseGraph = baseGraph;
		
		//final DirectedPseudograph<State, EFSMTransition> tmp = new DirectedPseudograph<State, EFSMTransition>(EFSMTransition.class);
		//final DirectedPseudograph<State, Transition> tmp = new DirectedPseudograph<>(EFSMTransition.class);
		// final DirectedPseudograph<State, Transition> tmp = new DirectedPseudograph<>(null);
		//Graphs.addGraph(tmp, baseGraph);

		// TODO do we need listtenable graph
		//this.baseGraph = new DirectedPseudograph<EFSMState, EFSMTransition>(EFSMTransition.class);
		
		//this.baseGraph = new DefaultListenableGraph<State, EFSMTransition>(tmp, true);
		//this.initialBaseGraph = SerializationUtils.clone((DefaultListenableGraph<State, EFSMTransition>)this.baseGraph);

		this.setTransitionsMap();
	
	}

//	private EFSM(EFSM base, 
//			State initialState,
//			Context initialContext) {
//		this.initialContext = (Context) initialContext.clone();
//		this.curContext = (Context) initialContext.clone();
//		this.curState = this.initialState = initialState;
//		this.baseGraph = base.baseGraph;
//		//this.initialBaseGraph = SerializationUtils.clone((DefaultListenableGraph<State, EFSMTransition>)this.baseGraph);
//		
//		this.setTransitionsMap();
//	}
		
	/*
	 * setter and getter for string version
	 * of a level and its mutations
	 */
	public void setEFMSString(String s) {
		this.efsmString = s;
	}
	
	public String getEFSMString() {
		return this.efsmString;
	}
	
//	public void setEFSMStringRemoveMutations(List<String> mut) {
//		this.removeMutationString = mut;
//	}
	
//	public List<String> getEFMStringRemoveMutations(){
//		return this.removeMutationString;
//	}
	
//	public void setEFSMStringAddMutations(List<String> mut) {
//		this.addMutationString = mut;
//	}
	
//	public List<String> getEFSMStringAddMutations(){
//		return this.addMutationString;
//	}
	
	
	public boolean canTransition(EFSMParameter input) {
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.isFeasible(curContext)) {
				return true;
			}
		}
		return false;
	}

	public boolean canTransition() {
		return canTransition(null);
	}
	
	/**
	 * Checks if the given input leads to a new configuration, returns the output
	 * for the transition taken.
	 *
	 * @param input
	 * @return The output for the taken transition or null if the input is not
	 *         accepted in the current configuration
	 */
	public Set<EFSMParameter> transition(EFSMParameter input) {
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.getInParameter().equals(input) && transition.isFeasible(curContext)) {
				EFSMConfiguration prevConfig = null;
				curState = transition.getTgt();
				Set<EFSMParameter> output = transition.take(curContext);
				return output;
			}
		}
		return null;
	}
	
//	/**
//	 * Checks if the empty input leads to a new configuration, returns the output
//	 * for the transition taken.
//	 *
//	 * @return The output for the taken transition or null if the input is not
//	 *         accepted in the current configuration
//	 */
//	public Set<OutParameter> transition() {
//		return transition(null);
//	}
	
	/**
	 * Checks if the given input leads to a new configuration, returns the new
	 * configuration or null otherwise.
	 *
	 * @param input
	 * @return The configuration after taking one of the possible transitions for
	 *         the given input or null if the input is not accepted in the current
	 *         configuration
	 */
	public EFSMConfiguration transitionAndDrop(EFSMParameter input) {
		if (transition(input) != null) {
			return getConfiguration();
		} else {
			return null;
		}
	}

	/**
	 * Checks if the empty input leads to a new configuration, returns the new
	 * configuration or null otherwise.
	 *
	 * @return The configuration after taking one of the possible transitions for
	 *         the empty input or null if the input is not accepted in the current
	 *         configuration
	 */
	public EFSMConfiguration transitionAndDrop() {
		return transitionAndDrop(null);
	}
	
	public EFSMConfiguration getConfiguration() {
		// this should be immutable or at least changes should not infer with the
		// state of this
		// machine
		//return new EFSMConfiguration(curState, curContext.clone());
		return new EFSMConfiguration(curState, curContext);
	}

	public EFSMConfiguration getInitialConfiguration() {
		return new EFSMConfiguration(initialState, initialContext.clone());
	}
	
	public Set<EFSMState> getStates() {
		return baseGraph.vertexSet();
	}

	public Set<EFSMTransition> getTransitons() {
		return baseGraph.edgeSet();
	}

	public Set<EFSMTransition> transitionsOutOf(EFSMState state) {
		return baseGraph.outgoingEdgesOf(state);
	}
	
	/**
	 * Transitions out of the given state, but only feasible for the given input
	 * 
	 * @param state
	 * @param input
	 * @return set of transitions possible from the current state, given the
	 *         specific input parameter
	 */
	public Set<EFSMTransition> transitionsOutOf(EFSMState state, EFSMParameter input) {
		Set<EFSMTransition> transitions = new HashSet<EFSMTransition>();
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(state)) {
			if (transition.getInParameter().equals(input) && transition.isFeasible(curContext)) {
				transitions.add(transition);
			}
		}
		return transitions;
	}
	
	public Set<EFSMTransition> transitionsInTo(EFSMState state) {
		return baseGraph.incomingEdgesOf(state);
	}

	public void reset() { 
		//forceConfiguration(new EFSMConfiguration(initialState, initialContext), initialBaseGraph);
		forceConfiguration(new EFSMConfiguration(initialState, initialContext));
	}
	
	public DirectedPseudograph<EFSMState, EFSMTransition> getBaseGraph() {
		return baseGraph;
	}
	
	/**
	 * This will track changes to the base graph but not configuration changes to
	 * the efsm. Also, no property listeners are copied. Shouldn't be that expensive
	 * since only the context and state change
	 *
	 * @return
	 */
	protected EFSM clone(EFSMState initialState, EFSMContext initialContext) {
		return SerializationUtils.clone(this);
	}

	/** 
	 * Reset EFSM to its initial state. The method uses clone method based on serialization and deserialization
	 * and it is quite inefficient for large graphs
	 * @param config
	 * @param initialBaseGraph2
	 */
//	public void _forceConfiguration(EFSMConfiguration<State, Context> config, ListenableGraph<State, EFSMTransition> initialBaseGraph2) {
//		//EFSMConfiguration<State, Context> prefConfig = null;
//		this.curState = (State) config.getState().clone();
//		this.curContext = (Context) config.getContext().clone();
//		//this.baseGraph = SerializationUtils.clone((DefaultListenableGraph<State, EFSMTransition>)initialBaseGraph2);
//		//setTransitionsMap();
//	}
//	
	
	/**
	 * Reset EFSM based that only updates variables value in the context
	 * @param config
	 * @param initialBaseGraph2
	 */
	public void forceConfiguration(EFSMConfiguration config) {
		//EFSMConfiguration<State, Context> prefConfig = null;

		this.curState = config.getState().clone();
		

		// update variables in cur context with values in initial context
		Set<String> contextVar = config.getContext().getContext().getHash().keySet();
		
		for(String varId: contextVar) {
			
			Var variable = config.getContext().getContext().getVariable(varId);
			curContext.getContext().update(varId, variable.getValue());
			
		}
		
		//this.curContext = (Context) config.getContext().clone();
		//this.baseGraph = SerializationUtils.clone((DefaultListenableGraph<State, EFSMTransition>)initialBaseGraph2);
		//setTransitionsMap();
	}
//	
	
	/**
	 * Save in a file to path. 
	 * Parameter mode allow to specify the type of output 
	 */
	// public abstract void saveEFSM(String path, String mode);
	
	/**
	 * below are methods added for "executing" test cases on the model
	 */
	
	/**
	 * Checks if the given input leads to a new configuration, returns the output
	 * for the transition taken.
	 *
	 * @param input, transition
	 * @return The output for the taken transition or null if the input is not
	 *         accepted in the current configuration
	 */
	
	/*
	 * NOTE: do we need to check that curState == transition.getSrc()?
	 */
	public Set<EFSMParameter> transition(EFSMTransition transition1) {
		EFSMTransition transition = getTransition(transition1.getId());
//		Transition transition = getTransition(transition1);
		if (transition.isFeasible(curContext)) {
			EFSMConfiguration prevConfig = null;
			curState = transition.getTgt();
			Set<EFSMParameter> output = transition.take(curContext);
			return output;
		}

		return null;
	}
	 

	private EFSMTransition getTransition(EFSMTransition transition) {
		
		Set<EFSMTransition> availableTransitions = (Set<EFSMTransition>)baseGraph.getAllEdges(transition.getSrc(), transition.getTgt());
		if (availableTransitions.contains(transition)) {
			for (EFSMTransition t : availableTransitions) {
				if (t.equals(transition)) {
					return  t;
				}
			}
		}
		throw new RuntimeException("Transition not found in model: " +  transition);

	}

	
	/**
	 * transition to a given state for testing
	 */
	public Set<EFSMParameter> transition(EFSMParameter input, EFSMState state) {
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.isFeasible(curContext) & transition.getTgt().equals(state)) {
				EFSMConfiguration prevConfig = null;
				curState = (EFSMState) transition.getTgt();
				Set<EFSMParameter> output = transition.take(curContext);
				return output;
			}
		}

		return null;
	}

	public EFSMParameter getRandomInput() {
		return inParameterSet.getRandom();
	}
	
	public EFSM clone() {
		return SerializationUtils.clone(this);
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public double getShortestPathDistance (EFSMState source, EFSMState target) {
		
		if (this.baseGraph.vertexSet().contains(source) & this.baseGraph.vertexSet().contains(target)) {
			return this.shortestPathsBetweenStates[(int) this.vertexToIntegerMapping.getVertexMap().get(source)][(int) this.vertexToIntegerMapping.getVertexMap().get(target)];
		}else {
			return Double.MAX_VALUE;
		}
	}
	
	public Set<EFSMPath> getShortestPaths (EFSMState source, EFSMState target) {
		if (this.baseGraph.vertexSet().contains(source) & this.baseGraph.vertexSet().contains(target)) {
			return this.shortestPaths[(int) this.vertexToIntegerMapping.getVertexMap().get(source)][(int) this.vertexToIntegerMapping.getVertexMap().get(target)];
		}else {
			return new HashSet<EFSMPath>();
		}
	}
	
	public Set<EFSMState> getStatesWithinSPDistance(EFSMState source, double maxDist){
		Integer sourceId = (int) this.vertexToIntegerMapping.getVertexMap().get(source);
		baseGraph.vertexSet();
		Set<EFSMState> outSet = new LinkedHashSet();
		for(EFSMState s : baseGraph.vertexSet()) {
			if (this.shortestPathsBetweenStates[sourceId][this.vertexToIntegerMapping.getVertexMap().get(s)] < maxDist ) {
				outSet.add(s);
			}
		}
		return outSet;
	}
	
	/**
	 * @return the shortestPathsBetweenStates
	 */
	public double[][] getShortestPathsBetweenStates() {
		return shortestPathsBetweenStates;
	}

	/**
	 * @param shortestPathsBetweenStates the shortestPathsBetweenStates to set
	 */
	public void setShortestPathsBetweenStates() {
		if (this.baseGraph != null) {
		
			Set<EFSMState> graphStates = this.baseGraph.vertexSet();
			this.shortestPathsBetweenStates = new double[graphStates.size()][graphStates.size()];
			Set<EFSMPath> el = new HashSet<EFSMPath>();
			Class cls = el.getClass();
			this.shortestPaths = (Set<EFSMPath>[][])Array.newInstance(cls, graphStates.size(),graphStates.size());
			
			// mapping states to integer that can be used to access matrix
			this.vertexToIntegerMapping = new VertexToIntegerMap<EFSMState>(graphStates);
			Map<EFSMState,Integer> mapStateInteger = vertexToIntegerMapping.getVertexMap(); 
			
			// shortest path algorithm 
			// use FloydWarshallShortestPaths as it computes all possible shortest path in one pass
			FloydWarshallShortestPaths shortestPathAlg = new FloydWarshallShortestPaths(this.baseGraph);
			// to compute all shortes path we need to compute all paths with lengthe shortest path
			// AllDirectedPaths allPathsCalculator =  new AllDirectedPaths(this.baseGraph);
			
			
			for(EFSMState src : graphStates) {
				for(EFSMState tgt : graphStates) {			
					if (src == tgt) {
						this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = 0d;
						this.shortestPaths[mapStateInteger.get(src)][mapStateInteger.get(tgt)] =  el;
					}else {
						GraphPath<EFSMState, EFSMTransition> shortestPath = shortestPathAlg.getPath(src, tgt);
						if (shortestPath == null) {
							this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = Double.MAX_VALUE;		
							this.shortestPaths[mapStateInteger.get(src)][mapStateInteger.get(tgt)] =  el;
						}else {
							this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = (double)shortestPath.getLength();
							//List<GraphPath<State, Transition>> allPath = allPathsCalculator.getAllPaths(src,tgt, false, shortestPath.getLength());
							Set<EFSMPath> tmp = new HashSet<EFSMPath>();
//							for(GraphPath<State, Transition> gp : allPath) {
//								tmp.add(new EFSMPath<>(gp) );
//							}
							tmp.add(new EFSMPath(shortestPath) );
							this.shortestPaths[mapStateInteger.get(src)][mapStateInteger.get(tgt)] =  tmp;
						}
					}
				}
			}
		
		}	
	}	
	
	public void setTransitionsMap() {
		for (EFSMTransition t : getTransitons()) {
			transitionsMap.put(t.getId(), t);
		}
	}

	public EFSMTransition getTransition (String id) {
		return transitionsMap.get(id);
	}
	
	public AllDirectedPaths getAllDirectedPathCalculator() {
		//return this.allPathsCalculator;
		return new AllDirectedPaths<EFSMState, EFSMTransition>(this.baseGraph);
	}
	
	public GraphMeasurer getGraphMeasurer() {
		//return this.graphMeasurer;
		return new GraphMeasurer<EFSMState, EFSMTransition>(this.baseGraph);
	}
	
	// All paths
	public void setGraphWorker() {
		if (this.baseGraph != null) {
			setShortestPathsBetweenStates();
			// not serializable for cloning
			//allPathsCalculator = new AllDirectedPaths<State, EFSMTransition>(this.baseGraph);
			//graphMeasurer = new GraphMeasurer<State, EFSMTransition>(this.baseGraph);
		}
		
	}

	/**
	 * @return the anmlString
	 */
	public String getAnmlString() {
		return anmlString;
	}

	/**
	 * @param anmlString the anmlString to set
	 */
	public void setAnmlString(String anmlString) {
		this.anmlString = anmlString;
	}

	/**
	 * @return the dotString
	 */
	public String getDotString() {
		return dotString;
	}

	/**
	 * @param dotString the dotString to set
	 */
	public void setDotString(String dotString) {
		this.dotString = dotString;
	}

	public String getEfsmSummaryFeatures() {
		String features = "n States,n Transitions,n Variables,Diameter,Girth,Radius,"+
						  "Mean Clustering Coefficient,SD Clustering Coefficient,Min Clustering Coefficient,Max Clustering Coefficient,Sum Clustering Coefficient,"+
						  "Mean Alpha Centrality,SD Alpha Centrality,Min Alpha Centrality,Max Alpha Centrality,Sum Alpha Centrality,"+
						  "Mean Betweenness Centrality,SD Betweenness Centrality,Min Betweenness Centrality,Max Betweenness Centrality,Sum Betweenness Centrality,"+
						  "Mean Closeness Centrality,SD Closeness Centrality,Min Closeness Centrality,Max Closeness Centrality,Sum Closeness Centrality,"+
						  "Mean Harmonic Centrality,SD Harmonic Centrality,Min Harmonic Centrality,Max Harmonic Centrality,Sum Harmonic Centrality,"+
						  "Mean Page Rank,SD Page Rank,Min Page Rank,Max Page Rank,Sum Page Rank"+"\n";
		
		//int n_vertex = initialBaseGraph.vertexSet().size();
		//int n_edges = initialBaseGraph.edgeSet().size();
		int n_vertex = baseGraph.vertexSet().size();
		int n_edges = baseGraph.edgeSet().size();
				
		int n_vars = initialContext.getContext().getHash().keySet().size();
		
		double diameter = GraphMetrics.getDiameter(this.baseGraph);
		int girth = GraphMetrics.getGirth(this.baseGraph);
		double radius = GraphMetrics.getRadius(this.baseGraph);
		
		//ClusteringCoefficient cf = new ClusteringCoefficient(this.baseGraph);
		//double averageClusteringCoefficient = cf.getAverageClusteringCoefficient(); 
		
		Sum apacheSum = new Sum();
		
		ClusteringCoefficient cf = new ClusteringCoefficient(this.baseGraph);
		Collection<Double> cfValues = cf.getScores().values();
		Double[] cfArray = cfValues.toArray(new Double[cfValues.size()]);
		double cfMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(cfArray));
		double cfVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(cfArray));
		double cfSd = Math.sqrt(cfVariance);
		double cfMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(cfArray));
		double cfMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(cfArray));
		//double cfSum = Arrays.stream(cfArray).sum();
		double cfSum = apacheSum.evaluate(ArrayUtils.toPrimitive(cfArray));
		
		
		
		AlphaCentrality ac = new AlphaCentrality(this.baseGraph);
		Collection<Double> acValues = ac.getScores().values();
		Double[] acArray = acValues.toArray(new Double[acValues.size()]);
		double acMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(acArray));
		double acVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(acArray));
		double acSd = Math.sqrt(acVariance);
		double acMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(acArray));
		double acMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(acArray));
		//double acSum = Arrays.stream(acArray).sum();
		double acSum = apacheSum.evaluate(ArrayUtils.toPrimitive(acArray));
		
		
			
		BetweennessCentrality bc = new BetweennessCentrality<>(this.baseGraph);
		Collection<Double> bcValues = bc.getScores().values();
		Double[] bcArray = bcValues.toArray(new Double[bcValues.size()]);
		double bcMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(bcArray));
		double bcVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(bcArray));
		double bcSd = Math.sqrt(bcVariance);
		double bcMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(bcArray));
		double bcMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(bcArray));
		//double acSum = Arrays.stream(acArray).sum();
		double bcSum = apacheSum.evaluate(ArrayUtils.toPrimitive(bcArray));		
		
		ClosenessCentrality cc = new ClosenessCentrality<>(this.baseGraph);
		Collection<Double> ccValues = cc.getScores().values();
		Double[] ccArray = ccValues.toArray(new Double[ccValues.size()]);
		double ccMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(ccArray));
		double ccVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(ccArray));
		double ccSd = Math.sqrt(ccVariance);
		double ccMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(ccArray));
		double ccMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(ccArray));
		//double acSum = Arrays.stream(acArray).sum();
		double ccSum = apacheSum.evaluate(ArrayUtils.toPrimitive(ccArray));		
	
		HarmonicCentrality hc = new HarmonicCentrality<>(this.baseGraph);
		Collection<Double> hcValues = hc.getScores().values();
		Double[] hcArray = hcValues.toArray(new Double[hcValues.size()]);
		double hcMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(hcArray));
		double hcVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(hcArray));
		double hcSd = Math.sqrt(hcVariance);
		double hcMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(hcArray));
		double hcMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(hcArray));
		//double acSum = Arrays.stream(acArray).sum();
		double hcSum = apacheSum.evaluate(ArrayUtils.toPrimitive(hcArray));		
		
		
		PageRank pr = new PageRank<>(this.baseGraph);
		Collection<Double> prValues = pr.getScores().values();
		Double[] prArray = prValues.toArray(new Double[prValues.size()]);
		double prMean = org.apache.commons.math3.stat.StatUtils.mean(ArrayUtils.toPrimitive(prArray));
		double prVariance = org.apache.commons.math3.stat.StatUtils.variance(ArrayUtils.toPrimitive(prArray));
		double prSd = Math.sqrt(prVariance);
		double prMin = org.apache.commons.math3.stat.StatUtils.min(ArrayUtils.toPrimitive(prArray));
		double prMax = org.apache.commons.math3.stat.StatUtils.max(ArrayUtils.toPrimitive(prArray));
		//double acSum = Arrays.stream(acArray).sum();
		double prSum = apacheSum.evaluate(ArrayUtils.toPrimitive(prArray));		
		
		
		
		features += Integer.toString(n_vertex) + "," +
					Integer.toString(n_edges)+","+
					Integer.toString(n_vars)+","+
					Double.toString(diameter)+","+
					Integer.toString(girth)+","+
					Double.toString(radius)+","+
					Double.toString(cfMean)+","+
					Double.toString(cfSd)+","+
					Double.toString(cfMin)+","+
					Double.toString(cfMax)+","+
					Double.toString(cfSum)+","+		
					Double.toString(acMean)+","+
					Double.toString(acSd)+","+
					Double.toString(acMin)+","+
					Double.toString(acMax)+","+
					Double.toString(acSum)+","+			
					Double.toString(bcMean)+","+
					Double.toString(bcSd)+","+
					Double.toString(bcMin)+","+
					Double.toString(bcMax)+","+
					Double.toString(bcSum)+","+						
					Double.toString(ccMean)+","+
					Double.toString(ccSd)+","+
					Double.toString(ccMin)+","+
					Double.toString(ccMax)+","+
					Double.toString(ccSum)+","+	
					Double.toString(hcMean)+","+
					Double.toString(hcSd)+","+
					Double.toString(hcMin)+","+
					Double.toString(hcMax)+","+
					Double.toString(hcSum)+","+					
					Double.toString(prMean)+","+
					Double.toString(prSd)+","+
					Double.toString(prMin)+","+
					Double.toString(prMax)+","+
					Double.toString(prSum)+"\n";
					
					
		return features;
	}

}
