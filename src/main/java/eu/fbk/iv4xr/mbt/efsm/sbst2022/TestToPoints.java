package eu.fbk.iv4xr.mbt.efsm.sbst2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
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

	public List<Pair<Double, Double>> testcaseToPoints(Testcase testcase) {
		String x = "pos_x";
		String y = "pos_y";
		AbstractTestSequence tc = (AbstractTestSequence)testcase;
		if (!tc.getPath().isConnected()) {
			throw new RuntimeException("Path not connected: " + testcase.toString());
		}
		assert tc.getPath().getSrc().getId().equalsIgnoreCase(efsm.getInitialConfiguration().getState().getId());
		List<Pair<Double, Double>> points = new ArrayList<>();
		
		Object variable_x = efsm.getInitialConfiguration().getContext().getContext().getVariable(x).getValue();
		Object variable_y = efsm.getInitialConfiguration().getContext().getContext().getVariable(y).getValue();
		Double var_x;
		Double var_y;
		
		if (variable_x instanceof Integer) {
			var_x = Double.valueOf(String.valueOf(variable_x));
			var_y = Double.valueOf(String.valueOf(variable_y));
		}else {
			var_x = (Double)variable_x;
			var_y = (Double)variable_y;
		}
		points.add(Pair.of(
				Precision.round(var_x,3), 
				Precision.round(var_y,3)));
//		points.add(Pair.of(
//				Precision.round((double)efsm.getInitialConfiguration().getContext().getContext().getVariable(x).getValue(),3), 
//				Precision.round((double)efsm.getInitialConfiguration().getContext().getContext().getVariable(y).getValue(),3)));
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
			Object variable_x1 = efsm.getConfiguration().getContext().getContext().getVariable(x).getValue();
			Object variable_y1 = efsm.getConfiguration().getContext().getContext().getVariable(y).getValue();
			Double var_x1;
			Double var_y1;
			if (variable_x1 instanceof Integer) {
				var_x1 = Double.valueOf(String.valueOf(variable_x1));
				var_y1 = Double.valueOf(String.valueOf(variable_y1));
			}else {
				var_x1 = (Double)variable_x1;
				var_y1 = (Double)variable_y1;
			}
			points.add(Pair.of(
					Precision.round(var_x1,3), 
					Precision.round(var_y1,3)));
//			points.add(Pair.of((double)efsm.getConfiguration().getContext().getContext().getVariable(x).getValue(), 
//					(double)efsm.getConfiguration().getContext().getContext().getVariable(y).getValue()));
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
