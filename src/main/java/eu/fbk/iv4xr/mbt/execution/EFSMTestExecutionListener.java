/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.testcase.Testcase;

/**
 * @author kifetew, prandi
 *
 */
public class EFSMTestExecutionListener implements ExecutionListener{

	private static final Logger logger = LoggerFactory.getLogger(EFSMTestExecutor.class);
	
	private ExecutionTrace executionTrace = null;
	
	private double pathApproachLevel = Double.MAX_VALUE;
	private double pathBranchDistance = Double.MAX_VALUE;
	private int passedTransitions = 0;
	private int pathLength;
	
	private Testcase testcase;
	
	private List<EFSMContext> contexts;
	
	
	
	/**
	 * Constructor take a test case and a coverage goal
	 */
	public EFSMTestExecutionListener(Testcase testcase) {
		this.testcase = testcase;
		
		executionTrace = new ExecutionTrace();
		pathLength = this.testcase.getLength();
		contexts = new ArrayList<EFSMContext>();
	}

	@Override
	public void executionStarted(TestExecutor testExecutor) {
		
	}
	
	/**
	 * When the execution is finished compute all the components needed for fitness function
	 */
	@Override
	public void executionFinished(TestExecutor testExecutor, boolean successful) {
		
		if (successful) { // path is valid
			pathApproachLevel = 0d;
			pathBranchDistance = 0d;
		}else {
			// this case should already be handled when the transition failed (in transitionFinished())
			//NOPE
		}
		
		// write data into execution trace
		executionTrace.setPathApproachLevel(pathApproachLevel);
		executionTrace.setPathBranchDistance(pathBranchDistance);
		
		executionTrace.setPassedTransitions(passedTransitions);
		
		executionTrace.setContexts(contexts);
		
		executionTrace.setSuccess(successful);
	}

	@Override
	public void transitionStarted(TestExecutor testExecutor, EFSMTransition t) {
		
	}

	/**
	 * When a transition is executed use execution information to compute 
	 * pathBranchDistance, pathApproachLevel, targetBranchDistance, targetApproachLevel
	 * @param testExecutor
	 * @param t
	 * @param successful
	 */
	@Override
	public void transitionFinished(TestExecutor testExecutor, EFSMTransition t, boolean successful) {
		if (successful) {
			passedTransitions ++;
			executionTrace.getCoveredTransitions().add(t);
			executionTrace.getCoveredStates().add(t.getSrc());
			executionTrace.getCoveredStates().add(t.getTgt());
		}else {
			// compute branch distance of failing guard
			EFSMGuard guard = t.getGuard();
			Exp<Boolean> guardExpression = guard.getGuard();
			double pathBD = CoverageGoal.computeBranchDistance (guardExpression);
			logger.debug("Guard2: {} BD: {}", guardExpression.toDebugString(), pathBD);
			pathBranchDistance = pathBD; //, parameter, contextVars);
			
			pathApproachLevel = pathLength - passedTransitions - 1;
		}
		
		// add the context corresponding to the transition just executed to the trace
		// get the current configuration from the model. At this point the context should contain the current context of execution
		contexts.add(EFSMFactory.getInstance().getEFSM().getConfiguration().getContext().clone());
				
	}
	
	/**
	 * @return the executionTrace
	 */
	@Override
	public ExecutionTrace getExecutionTrace() {
		return executionTrace;
	}
}
