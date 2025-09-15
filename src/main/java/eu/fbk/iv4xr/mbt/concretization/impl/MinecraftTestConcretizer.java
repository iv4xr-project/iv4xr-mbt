/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;

import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

import eu.fbk.iv4xr.mbt.concretization.ConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.GenericTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;

import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 */
public class MinecraftTestConcretizer extends GenericTestConcretizer {
	private static ObjectMapper mapper = new ObjectMapper();


	@Override
	public MinecraftConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {
		Path path = abstractTestCase.getPath();
		List<EFSMTransition> transitions = path.getTransitions();

		// get the EFSM model and reset it
		EFSMFactory modelFactory = EFSMFactory.getInstance(true);
		EFSM model = modelFactory.getEFSM();

		// setup json mapper
		

		MinecraftConcreteTestCase concreteTestCase = new MinecraftConcreteTestCase();

		for (EFSMTransition rawTransition : transitions) {
			// execute the transition
			model.transition(rawTransition);

			// get the real transiton, with the uodated variables
			EFSMTransition transition = model.getTransition(rawTransition.getId());

			LinkedHashMap<String, Var<Object>> inParamSet = getParameterSafe(transition.getInParameter());
			LinkedHashMap<String, Var<Object>> outParamSet = getParameterSafe(transition.getOutParameter());
			
			String previousActionName = "";
			ObjectNode currentAction = null;
			
			for (Map.Entry<String, Var<Object>> entry : combineParams(inParamSet, outParamSet).entrySet()) {
				
				String[] keys = entry.getKey().split("__");
				String actionName = keys[0];
				

				// once a new action is created we need to set it up
				if (!previousActionName.equals(actionName)) {
					previousActionName = actionName;

					currentAction = mapper.createObjectNode();
					concreteTestCase.addAction(currentAction);

					currentAction.put("name", actionName);

					// by convention the target will be the name of the state we are going to, some
					// action don't need a target, but it's easier to just ignore it.
					String target = transition.getTgt().getId().split("\\^")[0];
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

	private static LinkedHashMap<String, Var<Object>> combineParams(Map<String, Var<Object>> inParams,
			Map<String, Var<Object>> outParams) {

		LinkedHashMap<String, Var<Object>> combined = new LinkedHashMap<>();

		if (inParams != null) {
			combined.putAll(inParams);
		}

		// add "check_" prefix on out_params
		if (outParams != null) {
			combined.putAll(
					outParams.entrySet()
							.stream()
							.collect(Collectors.toMap(
									entry -> "check_" + entry.getKey(),
									Map.Entry::getValue)));
		}

		return combined;
	}

	private static LinkedHashMap<String, Var<Object>> getParameterSafe(EFSMParameter param) {
		if (param == null) {
			return null;
		}
		return param.getParameter().getHash();
	}
}
