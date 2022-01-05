package eu.fbk.iv4xr.mbt.efsm.sbst2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.TestExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

public class TestToPoints<
State extends EFSMState,
InParameter extends EFSMParameter,
OutParameter extends EFSMParameter,
Context extends EFSMContext,
Operation extends EFSMOperation,
Guard extends EFSMGuard,
Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>>{

	protected EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm;
	private static TestToPoints instance = null;
	
	public static TestToPoints getInstance() {
		if (instance == null) {
			instance = new TestToPoints();
		}
		return instance;
	}


	private TestToPoints() {
		this.efsm = EFSMFactory.getInstance().getEFSM();
	}

	public List<Pair<Integer, Integer>> testcaseToPoints(Testcase testcase) {
		String x = "pos_x";
		String y = "pos_y";
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		if (!tc.getPath().isConnected()) {
			throw new RuntimeException("Path not connected: " + testcase.toString());
		}
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		List<Pair<Integer, Integer>> points = new ArrayList<>();
		points.add(Pair.of((int)efsm.getInitialConfiguration().getContext().getContext().getVariable(x).getValue(), 
				(int)efsm.getInitialConfiguration().getContext().getContext().getVariable(y).getValue()));
		boolean success = true;
		List<Transition> transitions = tc.getPath().getTransitions();
		for (Transition t : transitions) {
			Set<OutParameter> output = efsm.transition(t);
			if (output == null) {
				success = false;
			}	
			if (!success) {
				break;
			}
			points.add(Pair.of((int)efsm.getConfiguration().getContext().getContext().getVariable(x).getValue(), 
					(int)efsm.getConfiguration().getContext().getContext().getVariable(y).getValue()));
		}
		
		
		reset();
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		return points;
	}



	public boolean reset() {
		efsm.reset();
		return true;
	}

}
