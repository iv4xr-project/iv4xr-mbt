package eu.fbk.iv4xr.mbt.efsm4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedPseudograph;

/** @author Manuel Benz created on 20.02.18 */
public class EFSM<
    State extends EFSMState,
    Parameter extends EFSMParameter,
    Context extends IEFSMContext<Context>,
    Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> {
  public static final String PROP_CONFIGURATION = "PROP_CONFIGURATION";
  protected final Context initialContext;
  protected final State initialState;
  protected final PropertyChangeSupport pcs;
  protected State curState;
  protected Context curContext;
  protected ListenableGraph<State, Trans> baseGraph;
  protected ParameterGenerator<Parameter> parameterSet;
  
  protected EFSM(Graph<State, Trans> baseGraph, State initialState, Context initalContext, 
		  ParameterGenerator<Parameter> parameterSet) {
    this.curState = this.initialState = initialState;
    this.curContext = initalContext.snapshot();
    this.initialContext = initalContext.snapshot();
    this.parameterSet = parameterSet;
    
    final DirectedPseudograph<State, Trans> tmp = new DirectedPseudograph<>(null);

    Graphs.addGraph(tmp, baseGraph);

    this.baseGraph = new DefaultListenableGraph<>(tmp, true);
    this.pcs = new PropertyChangeSupport(this);
  }

  private EFSM(
      EFSM<State, Parameter, Context, Trans> base,
      State initialState,
      Context initialContext) {
    this.initialContext = initialContext.snapshot();
    this.curContext = initialContext.snapshot();
    this.curState = this.initialState = initialState;
    this.baseGraph = base.baseGraph;
    // we do not want to delegate any events of this to the original listeners
    this.pcs = null;
  }

  public boolean canTransition(Parameter input) {
    for (Trans transition : baseGraph.outgoingEdgesOf(curState)) {
      if (transition.isFeasible(input, curContext)) {
        return true;
      }
    }

    return false;
  }

  public boolean canTransition() {
    return canTransition(null);
  }

  /**
   * Checks if the given input leads to a new configuration, returns the output for the transition
   * taken.
   *
   * @param input
   * @return The output for the taken transition or null if the input is not accepted in the current
   *     configuration
   */
  public Set<Parameter> transition(Parameter input) {
    for (Trans transition : baseGraph.outgoingEdgesOf(curState)) {
      if (transition.isFeasible(input, curContext)) {
        Configuration<State, Context> prevConfig = null;
        if (pcs != null) {
          prevConfig = getConfiguration();
        }
        curState = transition.getTgt();
        Set<Parameter> output = transition.take(input, curContext);
        if (pcs != null) {
          pcs.firePropertyChange(
              PROP_CONFIGURATION, prevConfig, Pair.of(getConfiguration(), transition));
        }
        return output;
      }
    }

    return null;
  }

  /**
   * Checks if the empty input leads to a new configuration, returns the output for the transition
   * taken.
   *
   * @return The output for the taken transition or null if the input is not accepted in the current
   *     configuration
   */
  public Set<Parameter> transition() {
    return transition(null);
  }

  /**
   * Checks if the given input leads to a new configuration, returns the new configuration or null
   * otherwise.
   *
   * @param input
   * @return The configuration after taking one of the possible transitions for the given input or
   *     null if the input is not accepted in the current configuration
   */
  public Configuration<State, Context> transitionAndDrop(Parameter input) {
    if (transition(input) != null) {
      return getConfiguration();
    } else {
      return null;
    }
  }

  /**
   * Checks if the empty input leads to a new configuration, returns the new configuration or null
   * otherwise.
   *
   * @return The configuration after taking one of the possible transitions for the empty input or
   *     null if the input is not accepted in the current configuration
   */
  public Configuration<State, Context> transitionAndDrop() {
    return transitionAndDrop(null);
  }

  public Configuration<State, Context> getConfiguration() {
    // this should be immutabable or at least changes should not infer with the state of this
    // machine
    return new Configuration(curState, curContext.snapshot());
  }

  public Configuration<State, Context> getInitialConfiguration() {
    return new Configuration(initialState, initialContext.snapshot());
  }

  public Set<State> getStates() {
    return baseGraph.vertexSet();
  }

  public Set<Trans> getTransitons() {
    return baseGraph.edgeSet();
  }

  public Set<Trans> transitionsOutOf(State state) {
    return baseGraph.outgoingEdgesOf(state);
  }

  /**
   * Transitions out of the given state, but only feasible for the given input
   * @param state
   * @param input
   * @return set of transitions possible from the current state, given the specific input parameter
   */
  public Set<Trans> transitionsOutOf(State state, Parameter input) {
	    Set<Trans> transitions = new HashSet<Trans>(); 
	    for (Trans transition : baseGraph.outgoingEdgesOf(state)) {
	    	if (transition.isFeasible(input, curContext)) {
	    		transitions.add(transition);
	    	}
	    }
		return transitions;
  }
  
  public Set<Trans> transitionsInTo(State state) {
    return baseGraph.incomingEdgesOf(state);
  }

  public void reset() {
    forceConfiguration(new Configuration<>(initialState, initialContext));
  }

  public ListenableGraph<State, Trans> getBaseGraph() {
    return baseGraph;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.removePropertyChangeListener(listener);
  }

  /**
   * This will track changes to the base graph but not configuration changes to the efsm. Also, no
   * property listeners are copied. Shouldn't be that expensive since only the context and state
   * change
   *
   * @return
   */
  protected EFSM<State, Parameter, Context, Trans> snapshot(
      State initialState, Context initialContext) {
    return new EFSM<>(this, initialState, initialContext);
  }

  /**
   * This will track changes to the base graph but not configuration changes to the efsm. Also, no
   * property listeners are copied. Shouldn't be that expensive since only the context and state
   * change
   *
   * @return
   */
  protected EFSM<State, Parameter, Context, Trans> snapshot() {
    return snapshot(this.curState, this.curContext);
  }

  public void forceConfiguration(Configuration<State, Context> config) {
    Configuration<State, Context> prefConfig = null;
    if (pcs != null) {
      prefConfig = getConfiguration();
    }
    this.curState = config.getState();
    this.curContext = config.getContext().snapshot();
    if (pcs != null) {
      // pass null as transition since there was no valid transition
      this.pcs.firePropertyChange(
          PROP_CONFIGURATION, prefConfig, Pair.of(getConfiguration(), null));
    }
  }
  
  
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
	public Set<Parameter> transition(Parameter input, Trans transition) {
		if (transition.isFeasible(input, curContext)) {
			Configuration<State, Context> prevConfig = null;
			if (pcs != null) {
				prevConfig = getConfiguration();
			}
			curState = transition.getTgt();
			Set<Parameter> output = transition.take(input, curContext);
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
	public Set<Parameter> transition(Parameter input, State state) {
		for (Trans transition : baseGraph.outgoingEdgesOf(curState)) {
			if (transition.isFeasible(input, curContext) & transition.getTgt().equals(state)) {
				Configuration<State, Context> prevConfig = null;
				if (pcs != null) {
					prevConfig = getConfiguration();
				}
				curState = transition.getTgt();
				Set<Parameter> output = transition.take(input, curContext);
				if (pcs != null) {
					pcs.firePropertyChange(PROP_CONFIGURATION, prevConfig, Pair.of(getConfiguration(), transition));
				}
				return output;
			}
		}

		return null;
	}

//	public boolean applyTransitions(List<Transition> transitions, List<Parameter> parameters) {
//		boolean success = true;
//		for (int i = 0; i < transitions.size(); i++) {
//			Transition t = transitions.get(i);
//			Parameter p = parameters.get(i);
//			Set<Parameter> output = transition(p, t);
//			if (output == null) {
//				success = false;
//				break;
//			}
//		}
//		return success;
//	}
	
	public Parameter getRandom() {
		return parameterSet.getRandom();
	}
	
	public EFSM clone() {
		return snapshot();
	}
	
	

}
