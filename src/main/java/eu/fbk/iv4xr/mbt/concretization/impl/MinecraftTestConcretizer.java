/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;

import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 */
public class MinecraftTestConcretizer extends TestConcretizer {
	private static ObjectMapper mapper = new ObjectMapper();

	public MinecraftTestConcretizer(EFSM model) {
		this.model = model;
	}
	
	@Override
	public MinecraftConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {
		Path path = abstractTestCase.getPath();
		
		model.reset();

		List<EFSMTransition> transitions = path.getTransitions();

		MinecraftConcreteTestCase concreteTestCase = new MinecraftConcreteTestCase();

		for (EFSMTransition rawTransition : transitions) {
			// execute the transition
			model.transition(rawTransition);

			// get the real transiton, with the updated variables
			EFSMTransition transition = model.getTransition(rawTransition.getId());

			LinkedHashMap<String, Var<Object>> combinedParams = combineParams(transition);

			String previousActionName = "";
			ObjectNode currentAction = null;

			for (Map.Entry<String, Var<Object>> entry : combinedParams.entrySet()) {

				String[] keys = entry.getKey().split(MBTProperties.MC_ACTION_NAME_SEPARATOR);
				String actionName = keys[0];

				// once a new action is created we need to set it up
				if (!previousActionName.equals(actionName)) {
					previousActionName = actionName;

					currentAction = mapper.createObjectNode();
					concreteTestCase.addAction(currentAction);

					// allow more of the same action on the same transition by using the same separator we use for states
					currentAction.put("name", actionName.split(MBTProperties.MC_SEPARATOR)[0]);

					// by convention the target will be the name of the target state of the transition, 
					// some actions don't need a target, but it's easier to just ignore it.

					// to allow for more states with the same target in mineflayertestbed, a sepatrator can be used
					String target = transition.getTgt().getId().split(MBTProperties.MC_SEPARATOR)[0];
					currentAction.put("target", target);
				}

				// some actions may not require parameters
				if (keys.length > 1) {
					Object value = entry.getValue().getValue();
					currentAction.putPOJO(keys[1], value);
				}
			}
		}

		return concreteTestCase;
	}

	private static LinkedHashMap<String, Var<Object>> combineParams(EFSMTransition t) {

		LinkedHashMap<String, Var<Object>> combined = new LinkedHashMap<>();

		if (t.getInParameter() != null) {
			combined.putAll(t.getInParameter().getParameter().getHash());
		}

		// add "check_" prefix on out_params
		if (t.getOutParameter() != null) {
			for (Map.Entry<String, Var<Object>> entry : ((LinkedHashMap<String, Var<Object>>) t.getOutParameter()
					.getParameter().getHash()).entrySet()) {
				combined.put( MBTProperties.MC_CHECK_PREFIX + entry.getKey(), entry.getValue());
			}
		}
		return combined;
	}

}
