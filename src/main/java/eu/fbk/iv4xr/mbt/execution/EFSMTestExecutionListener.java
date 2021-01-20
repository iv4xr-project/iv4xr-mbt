/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.UnaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew
 *
 */
public class EFSMTestExecutionListener<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> 
		implements ExecutionListener<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {

	private ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionTrace = null;
	
	private int pathApproachLevel = 0;
	private double pathBranchDistance = 0d;
	private int passedTransitions = 0;
	private int pathLength;
	
	// number of transitions until the current target
	private int distanceToTarget;
	
	// how close is the current test to the current target
	private int targetApproachLevel = 0;
	
	private double targetBranchDistance = 0d;
	
	private Testcase testcase;
	private CoverageGoal goal;
	
	private boolean currentGoalCovered = false;
	
	/**
	 * 
	 */
	public EFSMTestExecutionListener(Testcase testcase, CoverageGoal testGoal) {
		this.testcase = testcase;
		goal = testGoal;
		
		executionTrace = new ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
		pathLength = this.testcase.getLength();
		distanceToTarget = getDistanceToTarget ();
	}

	private int getDistanceToTarget() {
		int d = 0;
		Transition targetTransition = null;
		if (goal instanceof StateCoverageGoal) {
			targetTransition = getFirstTransitionForState ((StateCoverageGoal)goal);
		}else if (goal instanceof TransitionCoverageGoal){
			targetTransition = (Transition) ((TransitionCoverageGoal)goal).getTransition();
		}else {
			throw new RuntimeException("Unsupported target type: " + goal);
		}
		if (targetTransition == null) { // testcase path does not contain the test goal
			// assume maximum distance in EFSM
			d = ((AbstractTestSequence)testcase).getPath().getTransitions().size();
//			throw new RuntimeException("State has no transition associated: " + goal);
		}else {
		
			d = ((AbstractTestSequence)testcase).getPath().getTransitions().indexOf(targetTransition);
		}
		return d;
	}

	private Transition getFirstTransitionForState(StateCoverageGoal targetState) {
		for (Object o : ((AbstractTestSequence)testcase).getPath().getTransitions()) {
			Transition t = (Transition)o;
			if (targetState.getState().equals(t.getSrc()) ||
					targetState.getState().equals(t.getTgt())) {
				return t;
			}
		}
		return null;
	}

	@Override
	public void executionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor) {
		
	}

	@Override
	public void executionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, boolean successful) {
		// check if the path is valid but current goal is not covered
		if (successful) {
			if (!currentGoalCovered) {
				targetApproachLevel = distanceToTarget;
			}
		}
		executionTrace.setCurrentGoalCovered(currentGoalCovered);
		
		executionTrace.setPathApproachLevel(pathApproachLevel);
		executionTrace.setPathBranchDistance(pathBranchDistance);
		
		executionTrace.setTargetApproachLevel(targetApproachLevel);
		executionTrace.setTargetBranchDistance(targetBranchDistance);
		
		executionTrace.setSuccess(successful);
	}

	@Override
	public void transitionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t) {
		
	}

	@Override
	public void transitionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean successful) {
		if (successful) {
			passedTransitions ++;
			executionTrace.getCoveredTransitions().add(t);
			executionTrace.getCoveredStates().add(t.getSrc());
			executionTrace.getCoveredStates().add(t.getTgt());
			
			if (isCurrentTarget(t)) {
				currentGoalCovered = true;
			}
		}else {
			// compute branch distance of failing guard
			Guard guard = t.getGuard();
			EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm = testExecutor.efsm;
			Exp<Boolean> guardExpression = guard.getGuard();
			EFSMConfiguration<State, Context> context = efsm.getConfiguration();
			VarSet contextVars = context.getContext().getContext();
			InParameter inParameter = t.getInParameter();
			VarSet parameter = inParameter.getParameter();
			pathBranchDistance = computeBranchDistance (guardExpression, parameter, contextVars);
			
			pathApproachLevel = pathLength - passedTransitions - 1;
			
			if (isCurrentTarget (t)) {
				targetBranchDistance = pathBranchDistance;
				targetApproachLevel = pathApproachLevel; 
			}
		}
		
	}

	// is the transition just executed the current test target (or part of it)?
	private boolean isCurrentTarget(Transition t) {
		if (goal instanceof TransitionCoverageGoal) {
			return t.equals(((TransitionCoverageGoal)goal).getTransition());
		} else if (goal instanceof StateCoverageGoal) {
			EFSMState state = ((StateCoverageGoal)goal).getState();
			return t.getSrc().equals(state) || t.getTgt().equals(state); 
		} else {
			throw new RuntimeException("Unsupported goal type: " + goal);
		}
	}

	private double computeBranchDistance(Exp<Boolean> guardExpression, VarSet parameter, VarSet contextVars) {
		double distance = Double.MAX_VALUE;
		// determine type of guard expression
		if (guardExpression instanceof Var<?>) {
			// this is the simplest case where the guard is a simple boolean variable
			// if the guard failed, by definition the value is opposite to the one expected, i.e, false
			// hence, branch distance will be 1
			distance = 1d;
		}else if (guardExpression instanceof BinaryOp<?>) {
			// further distinguish the types of binary operators
			
			// TODO here we should do the actual branch distance calculation according to the operator type
			if (guardExpression instanceof BoolOr) {
				
			}else if (guardExpression instanceof CompareOp) {
				
			}else {
				throw new RuntimeException("Unsupported BinaryOp: " + guardExpression.toDebugString());
			}
		}else if (guardExpression instanceof UnaryOp<?>) {
			// this case is similar to the simple Var case, distance should be one
			distance = 1d;
		}
		return distance;
	}

	/**
	 * @return the executionTrace
	 */
	@Override
	public ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> getExecutionTrace() {
		return executionTrace;
	}
	

}
