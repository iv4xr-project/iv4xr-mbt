/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoalConstrainedTransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.KTransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMConfiguration;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.UnaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolEq;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.enumerator.EnumEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.utils.EFSMPathUtils;

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

	private static final Logger logger = LoggerFactory.getLogger(EFSMTestExecutor.class);
	
	private ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> executionTrace = null;
	
	private double pathApproachLevel = Double.MAX_VALUE;
	private double pathBranchDistance = Double.MAX_VALUE;
	private int passedTransitions = 0;
	private int pathLength;
	
	// number of transitions until the current target
	private int distanceToTarget;
	
	// how close is the current test to the current target
	private double targetApproachLevel = Double.MAX_VALUE;
	
	private double targetBranchDistance = Double.MAX_VALUE;
	
	private Testcase testcase;
	private EFSMPath path;
	private CoverageGoal goal;
	private boolean targetInPath;
	
	private boolean currentGoalCovered = false;
	
	// constant used in branch distance calculation
	final static int K = 1;
	
	public final static Double PENALITY1 = 100d;
	public final static Double PENALITY2 = 1000d; //Double.MAX_VALUE;
	/**
	 * 
	 */
	public EFSMTestExecutionListener(Testcase testcase, CoverageGoal testGoal) {
		this.testcase = testcase;
		path = ((AbstractTestSequence)testcase).getPath();
		goal = testGoal;
		
		targetInPath = EFSMPathUtils.pathContainsTarget(path, goal);
		
		executionTrace = new ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
		pathLength = this.testcase.getLength();
	}

	/**
	 * this method should handle the case where the path is valid but does not contain the target
	 * @return
	 */
	private void _computeDistanceToTarget() {
		double bd = Double.MAX_VALUE;
		double al = 0;
		
		EFSMState target;
		if (goal instanceof StateCoverageGoal) {
			target = ((StateCoverageGoal)goal).getState();
		}else if (goal instanceof TransitionCoverageGoal) {
			target = ((TransitionCoverageGoal)goal).getTransition().getSrc();
			al = 1;
		}else {
			throw new RuntimeException("Unsupported target type: " + goal);
		}
		
		// get shortest paths
		if (target.equals(path.getTgt())) {
			// this case should happen only when the target is a transition, and that the end of the path is the same as the source of the target transition
			// in this case, the al = 0, and bd should be calculated on the goal transition itself
			if (! (goal instanceof TransitionCoverageGoal)) {
				throw new RuntimeException("Path end and target beginning the same for target: " + goal.toString());
			}else {
				TransitionCoverageGoal targetTransition = (TransitionCoverageGoal)goal;
				if (targetTransition.getTransition().getGuard() == null) {
					bd = 0;
				}else {
					bd = computeBranchDistance(targetTransition.getTransition().getGuard().getGuard());
					logger.debug("Guard1: {} BD: {}", targetTransition.getTransition().getGuard().getGuard().toDebugString(), bd);
				}
			}
		}else {
			List<EFSMPath> shortestPaths = new ArrayList<>();
			Set sps = EFSMFactory.getInstance().getEFSM().getShortestPaths(path.getTgt(), target);
			shortestPaths.addAll(sps);
			
			// approach level is +1 in case the target is a Transition 
			al += shortestPaths.get(0).getLength();
	
			// calculate the minimum branch distance for the transitions outgoing from the end of the path
			for (EFSMPath sp : shortestPaths) {
				// compute branch distance for the first transition in the shortest path (outgoing from the last node of the path)
				double d;
				if (sp.getTransitionAt(0).getGuard() != null) {
					d = computeBranchDistance(sp.getTransitionAt(0).getGuard().getGuard());
				}else {
					d = 0;
				}
				if (d < bd) {
					bd = d;
				}
			}
		}
		targetApproachLevel = al;
		targetBranchDistance = normalize(bd);
		//System.err.println("TC: \n" + path.toString() + "\nTgt: " + goal.toString() + " : " + "AL: " + al + " BD: " + targetBranchDistance);
	}

	@Override
	public void executionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor) {
		
	}

	@Override
	public void executionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, boolean successful) {
		
		if (successful) { // path is valid
			pathApproachLevel = 0d;
			pathBranchDistance = 0d;
			
			if (targetInPath) {
				targetApproachLevel = 0d;
				targetBranchDistance = 0d;
			}else {
				targetApproachLevel = PENALITY1;
				targetBranchDistance = PENALITY1;
			}
		}else {
			// this case should already be handled when the transition failed (in transitionFinished())
			//NOPE
		}
		
		// write data into execution trace
		executionTrace.setCurrentGoalCovered(currentGoalCovered);
		
		executionTrace.setPathApproachLevel(pathApproachLevel);
		executionTrace.setPathBranchDistance(normalize(pathBranchDistance));
		
		executionTrace.setTargetApproachLevel(targetApproachLevel);
		executionTrace.setTargetBranchDistance(normalize(targetBranchDistance));
		
		executionTrace.setPassedTransitions(passedTransitions);
		
		executionTrace.setSuccess(successful);
	}

	@Override
	public void transitionStarted(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t) {
		
	}

//	@Override
	public void _transitionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean successful) {
		if (successful) {
			passedTransitions ++;
			executionTrace.getCoveredTransitions().add(t);
			executionTrace.getCoveredStates().add(t.getSrc());
			executionTrace.getCoveredStates().add(t.getTgt());
			
			if (targetInPath) {
				currentGoalCovered = true;
				targetBranchDistance = 0d;
				targetApproachLevel = 0d;
			}
		}else {
			// compute branch distance of failing guard
			Guard guard = t.getGuard();
			Exp<Boolean> guardExpression = guard.getGuard();
			double pathBD = computeBranchDistance (guardExpression);
			logger.debug("Guard2: {} BD: {}", guardExpression.toDebugString(), pathBD);
			pathBranchDistance = pathBD; //, parameter, contextVars);
			
			pathApproachLevel = pathLength - passedTransitions - 1;
			//System.err.println(t.toString() + "BD=" + pathBD + "nBD=" + pathBranchDistance + "AL=" + pathApproachLevel);
			
//			if (isCurrentTarget (t)) {
//				targetBranchDistance = pathBranchDistance;
//				targetApproachLevel = pathApproachLevel; 
//			}
			
			// if path does not contain target, apply penality
			if (!targetInPath) {
				targetBranchDistance = PENALITY2;
				targetApproachLevel = PENALITY2;
			}else {
				// in this case, fitness should be feasiblity only
				targetBranchDistance = 0d;
				targetApproachLevel = 0d;
			}
		}
	}

	/**
	 * When a transition is executed use execution information to compute 
	 * pathBranchDistance, pathApproachLevel, targetBranchDistance, targetApproachLevel
	 * @param testExecutor
	 * @param t
	 * @param successful
	 */
	@Override
	public void transitionFinished(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean successful) {
		
		// depending on the goal use a different approach
		if (goal instanceof StateCoverageGoal) {
			transitionFinished_simpleUpdate(testExecutor, t, successful);
		}else if (goal instanceof TransitionCoverageGoal) {
			transitionFinished_simpleUpdate(testExecutor, t, successful);
		}else if (goal instanceof KTransitionCoverageGoal) {
			transitionFinished_simpleUpdate(testExecutor, t, successful);
		}else if (goal instanceof CoverageGoalConstrainedTransitionCoverageGoal) {
			transitionFinished_simpleUpdate(testExecutor, t, successful);
		}else {
			throw new RuntimeException("Unsupported target type: " + goal.toString());
		}
		
	}
	
	
	private void transitionFinished_simpleUpdate(TestExecutor<State, InParameter, OutParameter, Context, Operation, Guard, Transition> testExecutor, Transition t, boolean successful) {
		if (successful) {
			passedTransitions ++;
			executionTrace.getCoveredTransitions().add(t);
			executionTrace.getCoveredStates().add(t.getSrc());
			executionTrace.getCoveredStates().add(t.getTgt());
			
			if (targetInPath) {
				currentGoalCovered = true;
				targetBranchDistance = 0d;
				targetApproachLevel = 0d;
			}
		}else {
			// compute branch distance of failing guard
			Guard guard = t.getGuard();
			Exp<Boolean> guardExpression = guard.getGuard();
			double pathBD = computeBranchDistance (guardExpression);
			logger.debug("Guard2: {} BD: {}", guardExpression.toDebugString(), pathBD);
			pathBranchDistance = pathBD; //, parameter, contextVars);
			
			pathApproachLevel = pathLength - passedTransitions - 1;
			
			// if path does not contain target, apply penality
			if (!targetInPath) {
				targetBranchDistance = PENALITY2;
				targetApproachLevel = PENALITY2;
			}else {
				// in this case, fitness should be feasiblity only
				targetBranchDistance = 0d;
				targetApproachLevel = 0d;
			}
		}
	}
	
	
	
	
	
	private double normalize(double d) {
		// normalize to a value in [0,1]
		return d/(d+1);
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

	private double computeBranchDistance(Exp<?> guardExpression) {
		double distance = Double.MAX_VALUE;
		// determine type of guard expression
		if (guardExpression == null) {
			distance = 0d;
		}else if (guardExpression instanceof Var<?>) {
			// this is the simplest case where the guard is a simple boolean variable
			// if the guard failed, by definition the value is opposite to the one expected, i.e, false
			// hence, branch distance will be 1
			distance = 1d;
		}else if (guardExpression instanceof BinaryOp<?>) {
			// further distinguish the types of binary operators
			
			// TODO here we should do the actual branch distance calculation according to the operator type
			if (guardExpression instanceof BoolOr) {
				BoolOr orExp = (BoolOr)guardExpression;
				distance = Math.min(computeBranchDistance(orExp.getParameter1()), computeBranchDistance(orExp.getParameter2()));
			}else if (guardExpression instanceof CompareOp) {
				distance = getCompareDistance ((CompareOp)guardExpression);
			}else if (guardExpression instanceof BoolAnd) {
				// recursive call?
				BoolAnd boolAnd = (BoolAnd)guardExpression;
				Exp<?> parameter1 = boolAnd.getParameter1(); 
				Exp<?> parameter2 = boolAnd.getParameter2();
				distance = computeBranchDistance (parameter1) + computeBranchDistance (parameter2) ;
//			}else if (guardExpression instanceof BoolOr) {
				// recursive call
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
	 * calculate the branch distance for comparisons
	 * @param guardExpression
	 * @return
	 */
	private double getCompareDistance(CompareOp guardExpression) {
		double d = Double.MAX_VALUE;
		// identify by case
		if (guardExpression instanceof BoolEq) {
			d = ((BoolEq)guardExpression).eval().getVal()?0:K;
		}else if (guardExpression instanceof IntEq) {
			IntEq equality = ((IntEq)guardExpression);
			Const<?> val1 = equality.getParameter1().eval();
			Const<?> val2 = equality.getParameter2().eval();
			d = getEqualityDistance (val1, val2);
		}else if (guardExpression instanceof IntGreat) {
			IntGreat greater = ((IntGreat)guardExpression);
			Const<?> val1 = greater.getParameter1().eval();
			Const<?> val2 = greater.getParameter2().eval();
			d = getGreaterThanDistance (val1, val2);
		}else {
			throw new RuntimeException("Unsupported comparison expression: " + guardExpression.toDebugString());
		}
		return d;
	}

	private double getGreaterThanDistance(Const<?> val1, Const<?> val2) {
		double distance = Double.MAX_VALUE;
		if (compatible (val1, val2)) {
			String str1 = val1.toDebugString();
			String str2 = val2.toDebugString();
			// identify by type
			if (NumberUtils.isNumber(str1) && NumberUtils.isNumber(str2)) {
				distance = NumberUtils.createNumber(str2).doubleValue() - NumberUtils.createNumber(str1).doubleValue();
				if (distance < 0d) {
					distance = 0d;
				}else {
					//TODO check
					distance = Math.abs(distance) + K;
				}
			} else if (val1.getVal() instanceof Boolean && val2.getVal() instanceof Boolean) {
				distance = val1.getVal().equals(val2.getVal())?0:1;
			}else {
				throw new RuntimeException("Unsupported equality expression: " + val1.toDebugString() + " == " + val2.toDebugString());
			}
		}
		return distance;
	}

	/**
	 * a generic equality distance function
	 * TODO missing cases will be added gradually
	 * @param val1
	 * @param val2
	 * @return
	 */
	private double getEqualityDistance(Const<?> val1, Const<?> val2) {
		double distance = Double.MAX_EXPONENT;
		if (compatible (val1, val2)) {
			String str1 = val1.toDebugString();
			String str2 = val2.toDebugString();
			// identify by type
			if (NumberUtils.isNumber(str1) && NumberUtils.isNumber(str2)) {
				distance = Math.abs(NumberUtils.createNumber(str1).doubleValue() - NumberUtils.createNumber(str2).doubleValue());
				if (distance != 0d) {
					distance += K;
				}
			} else if (val1.getVal() instanceof Boolean && val2.getVal() instanceof Boolean) {
				distance = val1.getVal().equals(val2.getVal())?0:K;
			}else {
				throw new RuntimeException("Unsupported equality expression: " + val1.toDebugString() + " == " + val2.toDebugString());
			}
		}
		return distance;
	}

	private boolean compatible(Object val1, Object val2) {
		// TODO check for a more flexible compatibility, e.g., if both numbers
		return val1.getClass().equals(val2.getClass());
	}

	/**
	 * @return the executionTrace
	 */
	@Override
	public ExecutionTrace<State, InParameter, OutParameter, Context, Operation, Guard, Transition> getExecutionTrace() {
		return executionTrace;
	}
	

}
