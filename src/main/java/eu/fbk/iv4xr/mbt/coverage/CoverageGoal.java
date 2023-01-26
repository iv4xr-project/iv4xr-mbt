/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.BinaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.CompareOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.UnaryOp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolEq;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.enumerator.EnumEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleEq;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleLess;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionListener;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.ExecutionTrace;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.EFSMPathUtils;

/**
 * @author kifetew
 *
 */
public abstract class CoverageGoal extends FitnessFunction<Chromosome> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 6349999459611670393L;
	protected final double W_BD = 1;
	protected final double W_AL = 1;
	
	protected final static Double PENALITY1 = 100d;
	protected final static Double PENALITY2 = 1000d; //Double.MAX_VALUE;
	
	// constant used in branch distance calculation
	protected final static int K = 1;
	/**
	 * currently supports MBTChromosome only
	 */
	public double getFitness(Chromosome test) {
		if (test instanceof MBTChromosome) {
			MBTChromosome mbtTest = (MBTChromosome)test;
			ExecutionResult result = mbtTest.getExecutionResult();
			if (result == null || mbtTest.isChanged()) {
				result = runTest (mbtTest.getTestcase());
				mbtTest.setExecutionResult(result);
			}
			return getFitness (mbtTest, result);
		}else if (test instanceof MBTSuiteChromosome) {
			MBTSuiteChromosome mbtSuite = (MBTSuiteChromosome)test;
			for (MBTChromosome mbtTest : mbtSuite.getTestChromosomes()) {
				ExecutionResult result = mbtTest.getExecutionResult();
				if (result == null || mbtTest.isChanged()) {
					result = runTest (mbtTest.getTestcase());
					mbtTest.setExecutionResult(result);
				}
				getFitness(mbtTest, result);
			}
			return mbtSuite.getFitness();
		}else {
			throw new RuntimeException("Unsupported chromosome type: " + test.getClass().getName());
		}
	}
	
	public static ExecutionResult runTest (Testcase test) {
		ExecutionListener executionListner = new EFSMTestExecutionListener(test);
		EFSMTestExecutor.getInstance().addListner(executionListner);
		ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(test);
		ExecutionTrace trace = executionListner.getExecutionTrace();
		executionResult.setExecutionTrace(trace);
		EFSMTestExecutor.getInstance().removeListner(executionListner);
		return executionResult;
	}
	
	public double getFitness(Chromosome test, ExecutionResult executionResult) {
		double fitness = Double.MAX_VALUE;
		if (test instanceof MBTChromosome) {
			MBTChromosome chromosome = (MBTChromosome)test;
			AbstractTestSequence testcase = (AbstractTestSequence) chromosome.getTestcase();
			
			ExecutionTrace trace = executionResult.getExecutionTrace();
			
			// trivial case
			if (executionResult.isSuccess() && testContainsGoal(testcase)) {
				fitness = 0d;
			}else {
				double feasibilityFitness = W_AL * trace.getPathApproachLevel() + W_BD * trace.getPathBranchDistance();
				double targetFitness = W_AL * computeTargetApproachLevel(testcase, executionResult, testContainsGoal(testcase)) + 
						W_BD * computeTargetBranchDistance(testcase, executionResult, testContainsGoal(testcase));
				fitness = feasibilityFitness + targetFitness;
			}
			
			updateCollateralCoverage(test, executionResult);
		}
		test.setChanged(false);
		updateIndividual(this, test, fitness);
		return fitness;
	}
	
	
	/// for now this part remains generic
	// if needed, each coverage goal can override the methods and provide its own implementation
	protected double computeTargetApproachLevel(Testcase test, ExecutionResult executionResult, boolean targetInPath) {
		double targetApproachLevel = Double.MAX_VALUE;
		// select target approach level function
		switch (MBTProperties.TARGET_APPROACH_LEVEL) {
		case NONE:
			if (targetInPath) {
				targetApproachLevel = 0;
			}else {
				if (executionResult.isSuccess()) {
					targetApproachLevel  = PENALITY1;
				}else {
					targetApproachLevel  = PENALITY2;
				}
			}
			break;
		case MIN_SHORTEST_PATH:
			targetApproachLevel = getTargetApproachLevel_MIN_SHORTEST_PATH(test, executionResult);
			break;
		case SHORTEST_PATH_FROM_LAST_FEASIBLE:
			targetApproachLevel = getTargetApproachLevel_SHORTEST_PATH_FROM_LAST_FEASIBLE(test, executionResult);
			break;
		default:
			throw new RuntimeException("Unsupported target approach level: " + MBTProperties.TARGET_APPROACH_LEVEL);
		}
		return targetApproachLevel;
	}

	protected double computeTargetBranchDistance(Testcase test, ExecutionResult executionResult, boolean targetInPath) {
		double targetBranchDistance = Double.MAX_VALUE;
		// select target branch distance function
		switch (MBTProperties.TARGET_BRANCH_DISTANCE) {
		case NONE:
			if (targetInPath) {
				targetBranchDistance  = 0;
			}else {
				if (executionResult.isSuccess()) {
					targetBranchDistance = PENALITY1;
				}else {
					targetBranchDistance  = PENALITY2;
				}
			}
			break;
		case FIRST_BRANCH_GUARD:
			targetBranchDistance = getTargetBranchDistance(test, executionResult);
		}
		return normalize(targetBranchDistance);
	}

	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Code to compute target approach level
	//
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Compute the shortest path between all the and the target of the goal
	 * @return
	 */
	private double getTargetApproachLevel_MIN_SHORTEST_PATH(Testcase test, ExecutionResult executionResult) {
		
		EFSMState targetState;
		Integer goalLength = 0;
		// depending on the goal get the target state and the length of the goal
		if (this instanceof StateCoverageGoal) {
			StateCoverageGoal sgoal = (StateCoverageGoal) this;
			targetState = sgoal.getState();
		} else if (this instanceof TransitionCoverageGoal) {
			TransitionCoverageGoal sgoal = (TransitionCoverageGoal) this;
			targetState = sgoal.getTransition().getSrc();
			goalLength = 1;
		} else if (this instanceof KTransitionCoverageGoal) {
			KTransitionCoverageGoal sgoal = (KTransitionCoverageGoal) this;
			EFSMPath kTransition = sgoal.getKTransition();
			targetState = kTransition.getSrc();
			goalLength = kTransition.getLength();
		} else if (this instanceof CoverageGoalConstrainedTransitionCoverageGoal) {
			CoverageGoalConstrainedTransitionCoverageGoal sgoal = (CoverageGoalConstrainedTransitionCoverageGoal) this;
			targetState = sgoal.getTransition().getSrc();
			goalLength = 1;
		} else {
			throw new RuntimeException("Unsupported target type: " + this.toString());
		}
	
		// Ignore initial state?
		// EFSMState initialState = EFSMFactory.getInstance().getEFSM().getInitialConfiguration().getState();
		// double shortestPathDistance = EFSMFactory.getInstance().getEFSM().getShortestPathDistance(initialState, targetState);
		double shortestPathDistance = Double.MAX_VALUE;
		
		// iterate over path
		// TODO need to check if can be casted
		AbstractTestSequence ats = (AbstractTestSequence) test;
		int passedTransitions = executionResult.getExecutionTrace().getPassedTransitions();
		int branchPointPassedTransitions = -1;
		for (int i = 0; i < passedTransitions ; i++) {
			EFSMState src = ats.getPath().getTransitionAt(i).getTgt();
			double dist = EFSMFactory.getInstance().getEFSM().getShortestPathDistance(src, targetState);
			if (dist < shortestPathDistance ) {
				shortestPathDistance = dist;
				branchPointPassedTransitions = i;
			}		
		}
		// save the last valid node in the trace, later used for genetic operators
		executionResult.getExecutionTrace().setBranchPointPassedTransitions(branchPointPassedTransitions);
		return shortestPathDistance + goalLength;
	}
	
	
	/**
	 * 
	 * @return
	 */
	private double getTargetApproachLevel_SHORTEST_PATH_FROM_LAST_FEASIBLE(Testcase test, ExecutionResult executionResult) {
		int passedTransitions = executionResult.getExecutionTrace().getPassedTransitions();
		if (passedTransitions > 0) {
			EFSMState targetState;
			Integer goalLength = 0;
			// depending on the goal get the target state and the length of the goal
			if (this instanceof StateCoverageGoal) {
				StateCoverageGoal sgoal = (StateCoverageGoal) this;
				targetState = sgoal.getState();
			} else if (this instanceof TransitionCoverageGoal) {
				TransitionCoverageGoal sgoal = (TransitionCoverageGoal) this;
				targetState = sgoal.getTransition().getSrc();
				goalLength = 1;
			} else if (this instanceof KTransitionCoverageGoal) {
				KTransitionCoverageGoal sgoal = (KTransitionCoverageGoal) this;
				EFSMPath kTransition = sgoal.getKTransition();
				targetState = kTransition.getSrc();
				goalLength = kTransition.getLength();
			} else if (this instanceof CoverageGoalConstrainedTransitionCoverageGoal) {
				CoverageGoalConstrainedTransitionCoverageGoal sgoal = (CoverageGoalConstrainedTransitionCoverageGoal) this;
				targetState = sgoal.getTransition().getSrc();
				goalLength = 1;
			} else {
				throw new RuntimeException("Unsupported target type: " + this.toString());
			}
			AbstractTestSequence ats = (AbstractTestSequence) test;
			EFSMTransition lastValidTransition = ats.getPath().getTransitionAt(passedTransitions-1);
			double shortestPathDistance = EFSMFactory.getInstance().getEFSM().getShortestPathDistance(lastValidTransition.getTgt(), targetState);
			
			// save the last valid node in the trace, later used for genetic operators
			int branchPointPassedTransitions = passedTransitions-1;
			executionResult.getExecutionTrace().setBranchPointPassedTransitions(branchPointPassedTransitions);
			return(shortestPathDistance+goalLength);
		}else {
			return PENALITY1;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Code to compute target branch distance
	//
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compute the branch distance of the shortest path identified by method getTargetApproachLevel
	 * @return
	 */
	private double getTargetBranchDistance(Testcase test, ExecutionResult executionResult) {
		
		// if branchPointPassedTransitions < 0, the target is not reachable
		double targetBranchDistance = PENALITY1;
		
		// get the pre-calculated value from the trace
		int branchPointPassedTransitions = executionResult.getExecutionTrace().getBranchPointPassedTransitions();
		if (branchPointPassedTransitions > -1) {
			EFSMState targetState;
			if (this instanceof StateCoverageGoal) {
				StateCoverageGoal sgoal = (StateCoverageGoal) this;
				targetState = sgoal.getState();
			} else if (this instanceof TransitionCoverageGoal) {
				TransitionCoverageGoal sgoal = (TransitionCoverageGoal) this;
				targetState = sgoal.getTransition().getSrc();
			} else if (this instanceof KTransitionCoverageGoal) {
				KTransitionCoverageGoal sgoal = (KTransitionCoverageGoal) this;
				EFSMPath kTransition = sgoal.getKTransition();
				targetState = kTransition.getSrc();;
			} else if (this instanceof CoverageGoalConstrainedTransitionCoverageGoal) {
				CoverageGoalConstrainedTransitionCoverageGoal sgoal = (CoverageGoalConstrainedTransitionCoverageGoal) this;
				targetState = sgoal.getTransition().getSrc();
			} else {
				throw new RuntimeException("Unsupported target type: " + this.toString());
			}
			
			// get the shorted path between the branch point and the target
			AbstractTestSequence ats = (AbstractTestSequence) test;
			
			// get the branch path using the shortest path
			EFSMState branchState = ats.getPath().getTransitionAt(branchPointPassedTransitions).getTgt();				
			Set<EFSMPath> shortestPaths = EFSMFactory.getInstance().getEFSM().getShortestPaths(branchState, targetState);
			// check if a path to the target exists
			if (shortestPaths.iterator().hasNext()) {
				// take the context at branch point
				EFSMContext branchContext = executionResult.getExecutionTrace().getContexts().get(branchPointPassedTransitions);
				// compute branch distance at branch point, using the first transition of the shortes path	
				EFSMPath next = shortestPaths.iterator().next();
				EFSMTransition firstTransition = next.getTransitionAt(0);
				EFSMGuard guard = firstTransition.getGuard();
				if (guard != null) {
					Exp<Boolean> guardExpression = guard.getGuard();
					// update guard expression with branch Context
					guardExpression.update(branchContext.getContext());
					targetBranchDistance = computeBranchDistance (guardExpression);					
				}else {
					targetBranchDistance = 0;
				}			
			}			
		}
		return targetBranchDistance;
	}
	
	
	
	///// utility methods for calculating branch distance
	public static double computeBranchDistance(Exp<?> guardExpression) {
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
		return normalize(distance);
	}

	public static double normalize(double d) {
		// normalize to a value in [0,1]
		return d/(d+1);
	}
	
	/**
	 * calculate the branch distance for comparisons
	 * @param guardExpression
	 * @return
	 */
	private static double getCompareDistance(CompareOp guardExpression) {
		double d = Double.MAX_VALUE;
		// identify by case
		if (guardExpression instanceof BoolEq) {
			d = ((BoolEq)guardExpression).eval().getVal()?0:K;
		}else if(guardExpression instanceof EnumEq) {
			EnumEq equality = ((EnumEq)guardExpression);
			d = equality.eval().getVal()?0:K;			
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
		}else if (guardExpression instanceof IntLess) {
			IntLess lesser = ((IntLess)guardExpression);
			Const<?> val1 = lesser.getParameter1().eval();
			Const<?> val2 = lesser.getParameter2().eval();
			d = getGreaterThanDistance (val1, val2);
		}else if (guardExpression instanceof DoubleEq) {
			DoubleEq equality = ((DoubleEq)guardExpression);
			Const<?> val1 = equality.getParameter1().eval();
			Const<?> val2 = equality.getParameter2().eval();
			d = getEqualityDistance (val1, val2);
		}else if (guardExpression instanceof DoubleGreat){
			DoubleGreat greater = ((DoubleGreat)guardExpression);
			Const<?> val1 = greater.getParameter1().eval();
			Const<?> val2 = greater.getParameter2().eval();
			d = getGreaterThanDistance(val1, val2);
		}else if (guardExpression instanceof DoubleLess) {
			DoubleLess lesser = ((DoubleLess)guardExpression);
			Const<?> val1 = lesser.getParameter1().eval();
			Const<?> val2 = lesser.getParameter2().eval();
			d = getGreaterThanDistance(val2, val1);
		}else {
			throw new RuntimeException("Unsupported comparison expression: " + guardExpression.toDebugString());
		}
		return d;
	}

	private static double getGreaterThanDistance(Const<?> val1, Const<?> val2) {
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
	private static double getEqualityDistance(Const<?> val1, Const<?> val2) {
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

	private static boolean compatible(Object val1, Object val2) {
		// TODO check for a more flexible compatibility, e.g., if both numbers
		return val1.getClass().equals(val2.getClass());
	}
	
	//// end fitness function methods ////
	
	public boolean isMaximizationFunction() {
		return false;
	}
	
	
	public boolean testContainsGoal (Testcase test) {
		EFSMPath path = ((AbstractTestSequence)test).getPath();;
		return EFSMPathUtils.pathContainsTarget(path , this);
	}
	
	public double getShortestDistanceToTarget(Path path, EFSMState target) {
		double shortestDistance = Double.MAX_VALUE;
		for (Object s : path.getStates()) {
			EFSMState source = (EFSMState)s;
			double d = EFSMFactory.getInstance().getEFSM().getShortestPathDistance(source, target);
			if (d < shortestDistance) {
				shortestDistance = d;
			}
		}
		return shortestDistance;
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
	
	protected abstract void updateCollateralCoverage (Chromosome individual, ExecutionResult executionResult);
	
	@Override
	public abstract String toString();
	
}
