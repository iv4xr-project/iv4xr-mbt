/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;


import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;


import org.evosuite.utils.Randomness;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.GraphWalk;

/**
 * 
 * This factory generates paths in the model constrained by a given
 * {@link CoverageGoal}. The nature of the constraint is controlled by the
 * parameter {@link MBTProperties.GOAL_CONSTRAINT_ON_TEST_FACTORY}.
 * 
 * @author kifetew
 *
 */
public class CoverageGoalConstrainedTestFactory<State extends EFSMState, InParameter extends EFSMParameter, OutParameter extends EFSMParameter, Context extends EFSMContext, Operation extends EFSMOperation, Guard extends EFSMGuard, Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>>
		implements TestFactory {

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(CoverageGoalConstrainedTestFactory.class);

	private int maxLength = MBTProperties.MAX_PATH_LENGTH;
	EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> model = null;
	CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal = null;

	/**
	 * 
	 */
	public CoverageGoalConstrainedTestFactory(
			EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm,
			CoverageGoal<State, InParameter, OutParameter, Context, Operation, Guard, Transition> constrainingGoal) {
		model = efsm;
		this.constrainingGoal = constrainingGoal;
	}

	/**
	 * 
	 */
	public CoverageGoalConstrainedTestFactory(
			EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm, int max) {
		model = efsm;
		maxLength = max;
	}

	@Override
	public Testcase getTestcase() {
		// int randomLength = Randomness.nextInt(maxLength) + 1;
//		EFSMConfiguration<State, Context> initialConfiguration = model.getInitialConfiguration();
//		State currentState = (State)initialConfiguration.getState();
//		
//		
		List<Transition> transitions = new LinkedList<Transition>();
//		//int len = 0;
//		
//		
//		EFSMState goal = null;
//		if (constrainingGoal instanceof StateCoverageGoal) {
//			goal = ((StateCoverageGoal)constrainingGoal).getState();
//		}else {
//			throw new RuntimeException("Constrain goal not supported");
//		}
//		
		switch (MBTProperties.GOAL_CONSTRAINT_ON_TEST_FACTORY) {
		case ENDS_WITH_STATE:
			if (constrainingGoal instanceof StateCoverageGoal) {
				EFSMState goal = ((StateCoverageGoal) constrainingGoal).getState();
				if (!model.getStates().contains(goal)) {
					throw new RuntimeException("Goal " + constrainingGoal.toString() + " not valid for the current model");
				}
				transitions = fastGetTestCaseWithEnd(goal);
				break;
			} else {
				throw new RuntimeException("Goal " + constrainingGoal.toString() + " not compatible with "
						+ MBTProperties.GoalConstraintOnTestFactory.ENDS_WITH_STATE.toString());
			}
		default:
			throw new RuntimeException(MBTProperties.GOAL_CONSTRAINT_ON_TEST_FACTORY + " not implemented");
		}

//		// loop until random length reached or current state has not outgoing transitions (finalInParameter?)
//		while (len < randomLength && !model.transitionsOutOf(currentState).isEmpty()) {
//			Set<EFSMTransition> outgoingTransitions = model.transitionsOutOf(currentState);
//			
//			// pick one transition at random and add it to path
//			Transition transition = (Transition) Randomness.choice(outgoingTransitions);
//			transitions.add(transition);
//			
//			// take the state at the end of the chosen transition, and repeat
//			currentState = transition.getTgt();
//			
//			
//			// check if goal constraint is satisfied, if so quit the loop
//			EFSMState goal = null;
//			switch(MBTProperties.GOAL_CONSTRAINT_ON_TEST_FACTORY) {
//			case ENDS_WITH_STATE:
//				if (constrainingGoal instanceof StateCoverageGoal) {
//					goal = ((StateCoverageGoal)constrainingGoal).getState();
//				}
//				break;
//			}
//			if (goal != null && goal.equals(currentState) ) { //&& RandomnessMBT.nextBoolean()) {
//				break;
//			}
//			
//			// until maxLength is reached or final state is reached
//			len++;
//		}
		// model.reset();

		// build the test case
		Testcase testcase = new AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition>();
		Path path = new Path (transitions);
		((AbstractTestSequence)testcase).setPath(path);
		assert path.isConnected();
		assert path.getTransitionAt(0).getSrc().getId().equalsIgnoreCase(model.getInitialConfiguration().getState().getId());
		return testcase;
	}

	/**
	 * Randomly choose a state and compute all AllDirectPath of small size
	 * allPathLength; select a path and append it to the final test case. Repeat
	 * until required size, then append a path to finalState of size allPathLength.
	 * 
	 * @param finalState
	 * @return
	 */
//	private List<Transition> getEndWithStateTestCase(EFSMState finalState) {
//
//		int randomLength = Randomness.nextInt(maxLength) + 1;
//		// TODO to check
//		Integer allPathLength = 3;
//		List<Transition> transitions = new LinkedList<Transition>();
//		EFSMState initialState = model.getInitialConfiguration().getState();
//		Set<State> states = model.getStates();
//		AllDirectedPaths allDirectedPathCalculator = model.getAllDirectedPathCalculator();
//
//		// build path
//		EFSMState currentState = initialState;
//		while (transitions.size() + allPathLength < randomLength) {
//			// random pick a state
//			EFSMState nextState = (EFSMState) Randomness.choice(states);
//			List allPaths = allDirectedPathCalculator.getAllPaths(currentState, nextState, false, allPathLength);
//			if (allPaths.size() > 0) {
//				GraphWalk nextPath = (GraphWalk) Randomness.choice(allPaths);
//				transitions.addAll(nextPath.getEdgeList());
//				if (transitions.size() > 0) {
//					currentState = transitions.get(transitions.size() - 1).getTgt();
//					if (currentState.equals(finalState)) {
//						return transitions;
//					}
//				}
//			}
//		}
//
//		// add goal
//		List finalPaths = allDirectedPathCalculator.getAllPaths(currentState, finalState, false, allPathLength);
//		// the goal is not reachable from the last state
//		if (finalPaths.size() == 0) {
//			int idToRemove = transitions.size()-1;
//			while(finalPaths.size() == 0) {
//				idToRemove = Randomness.nextInt(1, transitions.size()-1);		
//				EFSMState tgt = (EFSMState) transitions.get(idToRemove).getTgt();
//				finalPaths = allDirectedPathCalculator.getAllPaths(tgt, finalState, false, allPathLength);
//			}
//			transitions = transitions.stream().limit(idToRemove+1).collect(Collectors.toList());
//			
//		}
//		
//		GraphWalk nextPath = (GraphWalk) Randomness.choice(finalPaths);
//		transitions.addAll(nextPath.getEdgeList());
//		
//		return transitions;
//
//	}

	/**
	 * 
	 * @param finalState
	 * @return
	 */
//	private List<Transition> getTestCaseWithEnd(EFSMState finalState) {
//		
//		Integer allPathLength = 3;
//		List<Transition> transitions = new LinkedList<Transition>();
//		EFSMState initialState = model.getInitialConfiguration().getState();
//		Set<State> states = model.getStates();
//		AllDirectedPaths allDirectedPathCalculator = model.getAllDirectedPathCalculator();
//		model.getShortestPathDistance(null, null);
//		
//		Boolean end = false;
//		
//		EFSMState currentState = initialState;
//		
//		while (!end) {
//			// try to build path to final state
//			List finalPaths = allDirectedPathCalculator.getAllPaths(currentState, finalState, true, allPathLength);
//			// if it is possible to reach the final state randomly decide to end the process
//			// the probability to end is inversely proportional to the lenght of the path
//			int guessSize = Randomness.nextInt(1, maxLength);
//			
//			if (finalPaths.size() > 0 && transitions.size() >= guessSize) {
//				GraphWalk nextPath = (GraphWalk) Randomness.choice(finalPaths);
//				if (nextPath.getEdgeList().size() > 0) {
//					transitions.addAll(nextPath.getEdgeList());
//					end = true;
//				}
//			}else if (transitions.size() < guessSize){
//				// add a random piece of path
//				EFSMState nextState = (EFSMState) Randomness.choice(model.getStatesWithinSPDistance((State)currentState, allPathLength));
//				List allPaths = allDirectedPathCalculator.getAllPaths(currentState, nextState, true, allPathLength);
//				if (allPaths.size() > 0) {
//					GraphWalk nextPath = (GraphWalk) Randomness.choice(allPaths);
//					if (nextPath.getEdgeList().size() > 0) {
//						transitions.addAll(nextPath.getEdgeList());
//						currentState = transitions.get(transitions.size() - 1).getTgt();					
//					}
//				}
//			}else if (transitions.size() >= maxLength/2){
//				// remove some nodes
//				Integer half = transitions.size()/2;
//				transitions = transitions.stream().limit(half).collect(Collectors.toList());
//				currentState = transitions.get(transitions.size() - 1).getTgt();	
//			}
//		}		
//		return transitions;		
//	}
//	
	/**
	 * 
	 * @param finalState
	 * @return
	 */
	private List<Transition> fastGetTestCaseWithEnd(EFSMState finalState) {
	
		// max lenght used in all path calculator
		Integer allPathLength = 3;
		
		List<Transition> transitions = new LinkedList<Transition>();
		
		EFSMState initialState = model.getInitialConfiguration().getState();
		Set<State> states = model.getStates();
		AllDirectedPaths allDirectedPathCalculator = model.getAllDirectedPathCalculator();
		model.getShortestPathDistance(null, null);
		
		Boolean end = false;
		
		EFSMState currentState = initialState;
		
		while (!end) {
			// int guessSize = Randomness.nextInt(1, maxLength);
			boolean stop = Randomness.nextBoolean();
			// check if it time to stop
			if (stop) {
				// check if the final state is reachable (shortest path are precomputed)
				if (model.getStatesWithinSPDistance((State) currentState, allPathLength).contains(finalState)) {
					// take a random path to the final state
					List finalPaths = allDirectedPathCalculator.getAllPaths(currentState, finalState, true,
							allPathLength);
					//GraphWalk nextPath = (GraphWalk) Randomness.choice(finalPaths);
					int nextInt = Randomness.nextInt(finalPaths.size());
					GraphWalk nextPath = (GraphWalk) finalPaths.get(nextInt);
					if (nextPath.getEdgeList().size() > 0) {
						transitions.addAll(nextPath.getEdgeList());
						end = true;
					}
				}
			} else if (transitions.size() >= maxLength - allPathLength) {
				// try to remove some transition from path
				Integer removePoint = Randomness.nextInt(1, transitions.size());
				transitions = transitions.stream().limit(removePoint).collect(Collectors.toList());
				currentState = transitions.get(transitions.size() - 1).getTgt();
			} else {
				// add a random transition
				Set<EFSMTransition> outgoingTransitions = model.transitionsOutOf((State) currentState);
				Transition transition = (Transition) Randomness.choice(outgoingTransitions);
				transitions.add(transition);
				currentState = transition.getTgt();
			}
		}
		
		return transitions;	
		
	}
	
}
