package eu.fbk.iv4xr.mbt.efsm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.Properties;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.util.VertexToIntegerMapping;

import eu.fbk.iv4xr.mbt.MBTProperties;





/**
 * 
 * This class is derived from from the EFMS4J project created by
 * Manuel Benz. @see <a href="https://github.com/mbenz89/EFSM4J">EFSM4J</a>
 * 
 * 
 * @author prandi
 *
 */

public  class EFSM<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>
	> implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6330491569340874532L;
	
	public static final String PROP_CONFIGURATION = "PROP_CONFIGURATION";
	protected final Context initialContext;
	protected final State initialState;
	protected final PropertyChangeSupport pcs;
	protected State curState;
	protected Context curContext;
	protected ListenableGraph<State, EFSMTransition> baseGraph;
	
	// to add
	protected EFSMParameterGenerator<InParameter> inParameterSet;

	// fields used to pre-compute shortest path
	private double[][] shortestPathsBetweenStates;
	private VertexToIntegerMapping vertexToIntegerMapping;
	
	// Constructors
	protected EFSM(Graph<State, Transition> baseGraph, 
					State initialState, 
					Context initalContext,
					EFSMParameterGenerator<InParameter> parameterSet) {
		this.curState = this.initialState = initialState;
		this.curContext = (Context) initalContext.clone();
		this.initialContext = (Context) initalContext.clone();
		this.inParameterSet = parameterSet;
		
		final DirectedPseudograph<State, EFSMTransition> tmp = new DirectedPseudograph<State, EFSMTransition>(EFSMTransition.class);
		//final DirectedPseudograph<State, Transition> tmp = new DirectedPseudograph<>(EFSMTransition.class);
		// final DirectedPseudograph<State, Transition> tmp = new DirectedPseudograph<>(null);

		Graphs.addGraph(tmp, baseGraph);

		this.baseGraph = new DefaultListenableGraph<State, EFSMTransition>(tmp, true);
		this.pcs = new PropertyChangeSupport(this);
	}

	private EFSM(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> base, 
			State initialState,
			Context initialContext) {
		this.initialContext = (Context) initialContext.clone();
		this.curContext = (Context) initialContext.clone();
		this.curState = this.initialState = initialState;
		this.baseGraph = base.baseGraph;
		// we do not want to delegate any events of this to the original listeners
		this.pcs = null;
	}
	
	public boolean canTransition(InParameter input) {
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
	public Set<OutParameter> transition(InParameter input) {
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.getInParameter().equals(input) && transition.isFeasible(curContext)) {
				EFSMConfiguration<State, Context> prevConfig = null;
				if (pcs != null) {
					prevConfig = getConfiguration();
				}
				curState = (State) transition.getTgt();
				Set<OutParameter> output = transition.take(curContext);
				if (pcs != null) {
					pcs.firePropertyChange(PROP_CONFIGURATION, prevConfig, Pair.of(getConfiguration(), transition));
				}
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
	public EFSMConfiguration<State, Context> transitionAndDrop(InParameter input) {
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
	public EFSMConfiguration<State, Context> transitionAndDrop() {
		return transitionAndDrop(null);
	}
	
	public EFSMConfiguration<State, Context> getConfiguration() {
		// this should be immutable or at least changes should not infer with the
		// state of this
		// machine
		return new EFSMConfiguration(curState, curContext.clone());
	}

	public EFSMConfiguration<State, Context> getInitialConfiguration() {
		return new EFSMConfiguration(initialState, initialContext.clone());
	}
	
	public Set<State> getStates() {
		return baseGraph.vertexSet();
	}

	public Set<EFSMTransition> getTransitons() {
		return baseGraph.edgeSet();
	}

	public Set<EFSMTransition> transitionsOutOf(State state) {
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
	public Set<Transition> transitionsOutOf(State state, InParameter input) {
		Set<Transition> transitions = new HashSet<Transition>();
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(state)) {
			if (transition.getInParameter().equals(input) && transition.isFeasible(curContext)) {
				transitions.add((Transition) transition);
			}
		}
		return transitions;
	}
	
	public Set<EFSMTransition> transitionsInTo(State state) {
		return baseGraph.incomingEdgesOf(state);
	}

	public void reset() {
		forceConfiguration(new EFSMConfiguration(initialState, initialContext));
	}
	
	public ListenableGraph<State, EFSMTransition> getBaseGraph() {
		return baseGraph;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * This will track changes to the base graph but not configuration changes to
	 * the efsm. Also, no property listeners are copied. Shouldn't be that expensive
	 * since only the context and state change
	 *
	 * @return
	 */
	protected EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> 
				clone(State initialState, Context initialContext) {
		return SerializationUtils.clone(this);
	}

	public void forceConfiguration(EFSMConfiguration<State, Context> config) {
		EFSMConfiguration<State, Context> prefConfig = null;
		if (pcs != null) {
			prefConfig = getConfiguration();
		}
		this.curState = config.getState();
		this.curContext = (Context) config.getContext().clone();
		if (pcs != null) {
			// pass null as transition since there was no valid transition
			this.pcs.firePropertyChange(PROP_CONFIGURATION, prefConfig, Pair.of(getConfiguration(), null));
		}
	}
	  
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
	public Set<OutParameter> transition(Transition transition) {
		if (transition.isFeasible(curContext)) {
			EFSMConfiguration<State, Context> prevConfig = null;
			if (pcs != null) {
				prevConfig = getConfiguration();
			}
			curState = transition.getTgt();
			Set<OutParameter> output = transition.take(curContext);
			if (pcs != null) {
				pcs.firePropertyChange(PROP_CONFIGURATION, prevConfig, Pair.of(getConfiguration(), transition));
			}
			return output;
		}

		return null;
	}
	  
	/**
	 * transition to a given state for testing
	 */
	public Set<OutParameter> transition(InParameter input, State state) {
		for (EFSMTransition transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.isFeasible(curContext) & transition.getTgt().equals(state)) {
				EFSMConfiguration<State, Context> prevConfig = null;
				if (pcs != null) {
					prevConfig = getConfiguration();
				}
				curState = (State) transition.getTgt();
				Set<OutParameter> output = transition.take(curContext);
				if (pcs != null) {
					pcs.firePropertyChange(PROP_CONFIGURATION, prevConfig, Pair.of(getConfiguration(), transition));
				}
				return output;
			}
		}

		return null;
	}

	public InParameter getRandomInput() {
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
	public double getShortestPathDistance (State source, State target) {
		
		if (this.baseGraph.vertexSet().contains(source) & this.baseGraph.vertexSet().contains(target)) {
			return this.shortestPathsBetweenStates[(int) this.vertexToIntegerMapping.getVertexMap().get(source)][(int) this.vertexToIntegerMapping.getVertexMap().get(target)];
		}else {
			return Double.MAX_VALUE;
		}
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
		
			Set<EFSMState> graphStates = (Set<EFSMState>) this.baseGraph.vertexSet();
			this.shortestPathsBetweenStates = new double[graphStates.size()][graphStates.size()];
			
			// mapping states to integer that can be used to access matrix
			this.vertexToIntegerMapping = new VertexToIntegerMapping<>(graphStates);
			Map<EFSMState,Integer> mapStateInteger = vertexToIntegerMapping.getVertexMap(); 
			
			// shortest path algorithm 
			// use FloydWarshallShortestPaths as it computes all possible shortest path in one pass
			FloydWarshallShortestPaths shortestPathAlg = new FloydWarshallShortestPaths(this.baseGraph);
			
			for(EFSMState src : graphStates) {
				for(EFSMState tgt : graphStates) {			
					if (src == tgt) {
						this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = 0d;
					}else {
						GraphPath<EFSMState, EFSMTransition> shortestPath = shortestPathAlg.getPath(src, tgt);
						if (shortestPath == null) {
							this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = Double.MAX_VALUE;		
						}else {
							this.shortestPathsBetweenStates[mapStateInteger.get(src)][mapStateInteger.get(tgt)] = (double)shortestPath.getLength();
						}
					}
				}
			}
		
		}	
	}	
}
