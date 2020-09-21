/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;

import eu.fbk.iv4xr.mbt.efsm4j.Configuration;
import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;

/**
 * @author kifetew
 *
 */
public class EFSMMachine<State, Parameter, Context extends IEFSMContext<Context>, Trans extends Transition<State, Parameter, Context>>
		extends EFSM<State, Parameter, Context, Trans> {

	protected EFSMMachine(Graph<State, Trans> baseGraph, State initialState, Context initalContext) {
		super(baseGraph, initialState, initalContext);
	}

	/**
	 * Checks if the given input leads to a new configuration, returns the output
	 * for the transition taken.
	 *
	 * @param input, transition
	 * @return The output for the taken transition or null if the input is not
	 *         accepted in the current configuration
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

	public boolean applyTransitions(List<Trans> transitions, List<Parameter> parameters) {
		boolean success = true;
		for (int i = 0; i < transitions.size(); i++) {
			Trans t = transitions.get(i);
			Parameter p = parameters.get(i);
			Set<Parameter> output = transition(p, t);
			if (output == null) {
				success = false;
				break;
			}
		}
		return success;
	}

}
