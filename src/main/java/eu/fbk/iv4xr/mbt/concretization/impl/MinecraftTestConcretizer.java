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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 */
public class MinecraftTestConcretizer extends GenericTestConcretizer {

	@Override
	public ConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {

		System.out.println("Concretizing...");
		Path path = abstractTestCase.getPath();
		List<EFSMTransition> transitions = path.getTransitions();
		System.out.println(transitions);

		// get the EFSM model and reset it
		EFSMFactory modelFactory = EFSMFactory.getInstance(true);
		EFSM model = modelFactory.getEFSM();

		// setup json mapper
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode actionsArray = mapper.createArrayNode();

		ConcreteTestCase concreteTestCase = new MinecraftConcreteTestCase();
		for (EFSMTransition transition : transitions) {
			System.out.println(transition);

			// execute the transition
			model.transition(transition);

			LinkedHashMap<String, Var<Object>> inParamSet = getParameterSafe(transition.getInParameter());
			LinkedHashMap<String, Var<Object>> outParamSet = getParameterSafe(transition.getOutParameter());

			String previousActionName = "";
			ObjectNode currentAction = null;

			for (Map.Entry<String, Var<Object>> entry : combineParams(inParamSet, outParamSet).entrySet()) {

				String[] keys = entry.getKey().split("__");
				String actionName = keys[0];
				System.out.println(actionName);

				// once a new action is created we need to set it up
				if (previousActionName != actionName) {
					previousActionName = actionName;

					currentAction = mapper.createObjectNode();
					actionsArray.add(currentAction);

					currentAction.put("name", actionName);

					// by convention the target will be the name of the state we are going to, some
					// action don't need a target, but it's easier to just ignore it.
					String target = transition.getTgt().getId().split("\\^")[0];
					currentAction.put("target", target);
				}

				// some actions may not require parameters
				String param = null;
				Object value = entry.getValue().getValue();
				if (keys.length > 1) {
					param = keys[1];
					currentAction.putPOJO(param, value);
				}
			}
		}

		try {
			System.out.println(actionsArray.toString());
			mapper.writeValue(new File("TESTACTIONS.json"), actionsArray);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// translate the transition to the desired concrete content ..
		// and add it to the concrete test case

		return concreteTestCase;
	}

	private static LinkedHashMap<String, Var<Object>> combineParams(Map<String, Var<Object>> inParams,
			Map<String, Var<Object>> outParams) {
		LinkedHashMap<String, Var<Object>> combined = new LinkedHashMap<>(inParams);

		if (outParams == null) {
			return combined;
		}

		combined.putAll(
				outParams.entrySet()
						.stream()
						.collect(Collectors.toMap(
								entry -> "check_" + entry.getKey(),
								Map.Entry::getValue)));

		return combined;
	}

	private static LinkedHashMap<String, Var<Object>> getParameterSafe(EFSMParameter param) {
		if (param == null) {
			return null;
		}
		return param.getParameter().getHash();
	}
}
