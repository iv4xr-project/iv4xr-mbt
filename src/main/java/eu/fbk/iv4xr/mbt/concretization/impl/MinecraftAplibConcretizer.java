/**
 * @author agusmeroli
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;

import eu.fbk.iv4xr.mbt.concretization.AplibTestConcretizer;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.minecraftlib.MinecraftGoalLib;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * 
 */
public class MinecraftAplibConcretizer extends AplibTestConcretizer {
	public MinecraftAplibConcretizer(TestAgent testAgent, EFSM model) {
		super(testAgent);
		this.model = model;
	}

	private MinecraftGoalLib goalLib = new MinecraftGoalLib();

	@Override
	public AplibConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {
		Path path = abstractTestCase.getPath();

		model.reset();

		List<EFSMTransition> transitions = path.getTransitions();

		AplibConcreteTestCase concreteTestCase = new AplibConcreteTestCase();

		for (EFSMTransition rawTransition : transitions) {
			// execute the transition
			model.transition(rawTransition);

			// get the real transiton, with the updated variables

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
				combined.put(MBTProperties.MC_CHECK_PREFIX + entry.getKey(), entry.getValue());
			}
		}
		return combined;
	}

	private GoalStructure toGoal(TestAgent agent, String name, Map<String, Object> params) {
		switch (name.toLowerCase()) {
			case "move_to":
				return goalLib.tagReached((String) params.get("target"));
			case "move_to_within":
				return goalLib.tagReachedWithinDistance((String) params.get("target"), (Double) params.get("distance"));
			case "break":
				return goalLib.mined((String) params.get("target"));
			case "attack":
				return goalLib.attacked((String) params.get("target"));
			case "select":
				return goalLib.selected((String) params.get("item"));
			case "place":
				return goalLib.placed((String) params.get("target"), (String) params.get("face"));
			case "click":
				return goalLib.clicked((String) params.get("target"));
			case "anvil_operation":
				return goalLib.usedAnvil((String) params.get("target"), (String) params.get("item_one"),
						(String) params.get("item_two"), (String) params.get("custom_name"));

			// Checks
			case "check_block":
				return goalLib.assertBlockIs(agent, (String)params.get("target"), (String)params.get("expected"), (String)params.get("nbt"));
			case "check_item":
				return goalLib.assertHasItem(agent, (String)params.get("item"), (Integer)params.get("count"));
			case "check_entity":
				return goalLib.assertEntityHealth(agent, (String)params.get("target"), (Float)params.get("health"));
			default:
				break;
		}
		return null;
	}

	@Override
	public GoalStructure convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t, EFSM model) {
		LinkedHashMap<String, Var<Object>> combinedParams = combineParams(t);
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>();
		GoalStructure g;

		String previousActionName = null;
		Map<String, Object> currentActionParams = new HashMap<>();

		for (Map.Entry<String, Var<Object>> entry : combinedParams.entrySet()) {

			String[] keys = entry.getKey().split(MBTProperties.MC_ACTION_NAME_SEPARATOR);
			String actionName = keys[0];

			// once a new action is created we need to set it up
			if (!previousActionName.equals(actionName)) {
				if (previousActionName != null) {
					String actualName = previousActionName.split(MBTProperties.MC_SEPARATOR)[0];
					g = toGoal(agent, actualName, currentActionParams);
					subGoals.add(g);
				}

				currentActionParams.clear();
				previousActionName = actionName;
				// allow more of the same action on the same transition by using the same
				// separator we use for states
				// by convention the target will be the name of the target state of the
				// transition,
				// some actions don't need a target, but it's easier to just ignore it.

				// to allow for more states with the same target in mineflayertestbed, a
				// sepatrator can be used
				String target = t.getTgt().getId().split(MBTProperties.MC_SEPARATOR)[0];
				currentActionParams.put("target", target);
			}

			// some actions may not require parameters
			if (keys.length > 1) {
				Object value = entry.getValue().getValue();
				currentActionParams.put(keys[1], value);
			}
		}

		String actualName = previousActionName.split(MBTProperties.MC_SEPARATOR)[0];
		g = toGoal(agent, actualName, currentActionParams);
		subGoals.add(g);

		if (subGoals.size() == 1) {
			return subGoals.get(0);
		} else {
			return SEQ(subGoals.toArray(new GoalStructure[0]));
		}
	}

}
