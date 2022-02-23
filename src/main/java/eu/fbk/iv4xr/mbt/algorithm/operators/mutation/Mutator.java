package eu.fbk.iv4xr.mbt.algorithm.operators.mutation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.utils.Randomness;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.testcase.Path;

public class Mutator implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353952869532319217L;
	protected static final Logger logger = LoggerFactory.getLogger(Mutator.class);
	private Path path;
	private int pathSize;
	private int minSubPathLenght = 2;
	private int maxSubPathLenght = 5;
	private int passedTransitions = 0;
	
	public Mutator(Path path) {
		this.path = path;
		this.pathSize = path.getLength();
	}
	
	public Path getPath() {
		return path;
	}
	

	
	/**
	 * Mutate use information about the execution to try to improve the
	 * probability of increasing the feasibility of a path
	 * @param executionResult
	 */
	public void mutate(ExecutionResult executionResult) {
		if (this.pathSize != path.getLength()) {
			this.pathSize = path.getLength();
		}
		// set the number of passed transitions
		if (executionResult != null) {
			// information about execution si available and can be used
			passedTransitions = executionResult.getExecutionTrace().getPassedTransitions();
			//mutate_path(executionResult);
			mutate_feasible(executionResult);			
		}else {
			// only FSM strcuture can be used
			passedTransitions = 0;
			mutate_path(executionResult);
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//
	// The individual has been already executed and information about
	// feasibility can be used
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Randomly choose a feasiblity aware mutation
	 * @param executionResult
	 */
	private void mutate_feasible(ExecutionResult executionResult) {
		//double choice = Randomness.nextDouble();
		removeFirstNotFeasible(executionResult);
	}
	
	
	/**
	 * try to remove the first not feasible transition
	 */
	private void removeFirstNotFeasible(ExecutionResult executionResult) {

		// case of passedTransitions equals to 0
		// no feasible transitions in the path so try to change the first one
		if (passedTransitions == 0) {
			// the path is indeed not feasible
			mutate_path(executionResult);

		} else if (passedTransitions == pathSize) {
			// passedTransitions equals path size
			// add few transitions at the end
			appendTransitionsAtPathEnd();
			
		} else if (passedTransitions > 0 && passedTransitions < pathSize) {
			// not all transitions are feasible
			// remove unfeasible subpath
			EFSMPath subPath = path.subPath(0, passedTransitions);
			path.getModfiableTransitions().clear();
			path.append(subPath);
			
		} else {
			throw new RuntimeException("\nError: path \n" + path.toString() + " has " + passedTransitions + " over "
					+ path.getLength() + " passed transitions");
		}
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////
	//
	// The individual is new due to crossover and not yet executed
	// Use only path information to add mutations
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Randomly choose a path mutation
	 * @param executionResult
	 */
	private void mutate_path(ExecutionResult executionResult) {
		double choice = Randomness.nextDouble();
		
		if (choice < 0.33) {
			insertSelfTransitionMutation();
		} else if (choice < 0.33) {
			deleteSelfTransitionMutation();
		} else {
			singleTransitionRemoval();
		}
		
	}
	
	/**
	 * Remove a random transition s1 -t-> s2 and search an alternative path from s1 to s2
	 */
	private void singleTransitionRemoval() {

		// get the model and the function to compute all paths between two states
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		AllDirectedPaths<EFSMState, EFSMTransition> allPathsCalculator = efsm.getAllDirectedPathCalculator();
		
		// Use the length of the path as a maximum length for the new subpath
		// if the path has length less that 2, use 2
		//Integer newPathMaxLength = Integer.min(Integer.max(path.getLength(),minSubPathLenght),maxSubPathLenght);
		
		// Take a random transition in the path
		Integer transitionToRemoveId = Randomness.nextInt(path.getLength());
		EFSMTransition transitionToRemove = (EFSMTransition) path.getTransitionAt(transitionToRemoveId);
		
		// first and last transition need to be treated separately
		if (transitionToRemoveId >= 0 & transitionToRemoveId < path.getLength()-1) {
			// Compute all path from src to the tgt of the removed transition
			List<GraphPath< EFSMState, EFSMTransition>> allPath = 
					allPathsCalculator.getAllPaths(transitionToRemove.getSrc(), transitionToRemove.getTgt(), false, maxSubPathLenght);
			// A flag to check if the new subpath has been choosen
			Boolean choosen = false;
			while(!choosen & allPath.size() > 0) {
				GraphPath selectePath = Randomness.choice(allPath);
				if (selectePath.getLength() > 1) {
					// if it is the first transition the new path would be the head of the new path
					if (transitionToRemoveId == 0) {
						Path newTrunk = new Path(selectePath);
						var tail = path.subPath(transitionToRemoveId+1,path.getLength());
						path.getModfiableTransitions().clear();
						path.append(newTrunk);
						path.append(tail);
					}else {
						var head =  path.subPath(0, transitionToRemoveId);
						var tail = path.subPath(transitionToRemoveId+1,path.getLength());
						Path newTrunk = new Path(selectePath);
						//logger.debug("MUTATION: ADDED "+newTrunk.getLength()+" transitions");
						path.getModfiableTransitions().clear();
						path.append(head);
						path.append(newTrunk);
						path.append(tail);
					}
		
					choosen = true;
				}else {
					allPath.remove(selectePath);
				}
			}
		}
		
		// if it is the last transition, remove it and add a random path from the source of the transition
		if (transitionToRemoveId == path.getLength()-1) {
			// select a	new target
			EFSMState newTgtState = (EFSMState) Randomness.choice(efsm.getStates());
			// Compute all path from src of the removed transition to the new tgt
			List<GraphPath<EFSMState, EFSMTransition>> allPath = allPathsCalculator.getAllPaths(transitionToRemove.getSrc(),
					transitionToRemove.getTgt(), false, maxSubPathLenght);
			// A flag to check if the new subpath has been choosen
			Boolean choosen = false;
			while (!choosen & allPath.size() > 0) {
				GraphPath selectePath = Randomness.choice(allPath);
				if (selectePath.getLength() > 1) {
					var head = path.subPath(0, transitionToRemoveId);
					var newTrunk = new Path(selectePath);
					// logger.debug("MUTATION: ADDED "+newTrunk.getLength()+" transitions");
					path.getModfiableTransitions().clear();
					path.append(head);
					path.append(newTrunk);
					choosen = true;
				} else {
					allPath.remove(selectePath);
				}
			}
		}

	}

	private void deleteSelfTransitionMutation() {
		if (path.getLength() < 2) {
			return;
		}
		// find a self transition and remove it
		Set<Integer> indices = new HashSet<Integer>();
		for (int i = 0; i < path.getLength(); i++) {
			EFSMTransition t = (EFSMTransition) path.getTransitionAt(i);
			if (t.isSelfTransition()) {
				indices.add(i);
			}
		}		
		// choose one at random and remove it
		if (!indices.isEmpty()) {
			path.getModfiableTransitions().remove(path.getModfiableTransitions().get(Randomness.choice(indices)));
		}else {
			// nothing to do, mutation fails to modify individual
		}
	}
	
	private void insertSelfTransitionMutation() {

		// from the model, get all possible self transitions (states)
		EFSM model = EFSMFactory.getInstance().getEFSM();
		Map<EFSMState, EFSMTransition> selfTransitionStates = new HashMap<>();
		for (Object o : model.getTransitons()) {
			EFSMTransition t = (EFSMTransition) o;
			if (t.isSelfTransition()) {
				selfTransitionStates.put(t.getSrc(), t);
			}
		}

		// identify a state where self transition is possible
		selfTransitionStates.keySet().retainAll(path.getStates());

		// choose one at random
		EFSMState state = Randomness.choice(selfTransitionStates.keySet());

		int index = -1;
		for (EFSMTransition t : (List<EFSMTransition>)path.getTransitions()) {
			if (t.getSrc().equals(state)) {
				index = path.getTransitions().indexOf(t);
				break;
			}
			if (t.getTgt().equals(state)) {
				index = path.getTransitions().indexOf(t) + 1;
				break;
			}
		}

		if (index == -1) {
			return; // mutation fails to change individual
		}

		// insert the new transition
		path.getModfiableTransitions().add(index, (EFSMTransition) selfTransitionStates.get(state).clone());

	}
	
	
	//////////////////////////////////////////////
	//
	// Utilities
	//
	//////////////////////////////////////////////
	
	/**
	 * Utility that append a random trnansition at the end of path
	 */
	private void appendRandomTransitionMutation() {
		EFSMState lastState = path.getTgt();
		Set<EFSMTransition> potentialTransitions = EFSMFactory.getInstance().getEFSM().transitionsOutOf(lastState);
		EFSMTransition t = (EFSMTransition) Randomness.choice(potentialTransitions);
		path.append(t);
	}
	
	/**
	 * Utility that append a random sequence of transitions to path
	 * The source state is know, while the other could be any state
	 */
	private void appendTransitionsAtPathEnd() {
		// get the model
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		AllDirectedPaths allDirectedPathCalculator = efsm.getAllDirectedPathCalculator();
		
		EFSMState initialState = path.getTransitionAt(pathSize - 1).getTgt();
		HashSet<EFSMState> initialSet = new HashSet<EFSMState>();
		initialSet.add(initialState);
		
		List<GraphPath> allNewExtension = 
				allDirectedPathCalculator.getAllPaths(initialSet, efsm.getStates(), false, minSubPathLenght);
		List<GraphPath> zeroTran = 
				allDirectedPathCalculator.getAllPaths(initialSet, efsm.getStates(), false, 0);
		allNewExtension.removeAll(zeroTran);
		if (allNewExtension.size() > 0) {
			GraphPath choice = Randomness.choice(allNewExtension);
			path.append(new Path(choice));
		}
		
	}
	

	
	
	//////////////////////////////////////////////
	//
	// Legacy code
	//
	//////////////////////////////////////////////
	
//	/**
//	 * try to remove the first not feasible transition
//	 */
//	public void _removeFirstNotFeasible() {
//
//		// get the model
//		EFSM efsm = EFSMFactory.getInstance().getEFSM();
//		AllDirectedPaths allDirectedPathCalculator = efsm.getAllDirectedPathCalculator();
//		
//		// case of passedTransitions equals to 0
//		// no feasible transitions in the path so try to change the first one
//		if (passedTransitions == 0) {
//			// get initial transitions
//			EFSMConfiguration<State, Context> initialConfiguration = efsm.getInitialConfiguration();
//			EFSMState initialState = initialConfiguration.getState();
//			Set<EFSMTransition> startingTransitions = efsm.transitionsOutOf(initialState);
//			// first transition of the path
//			EFSMTransition firstTransition = path.getTransitionAt(0);
//			// remove firstTransition from initial transitions
//			startingTransitions.remove(firstTransition);
//			// use remaining transitions as first transition
//			if (startingTransitions.size() == 0) {
//				// this EFMS need to be checked
//				logger.info("The EFSM has only one starting transition and it is not feasible");
//			} else {
//				// the path has only one transition
//				if (path.getLength() == 1) {
//					EFSMTransition newFirstTransition = Randomness.choice(startingTransitions);
//					path = new Path(newFirstTransition);
//				} else {
//					boolean done = false;
//					EFSMTransition newFirstTransition = null;
//					while (!done && startingTransitions.size() > 0) {
//						newFirstTransition = Randomness.choice(startingTransitions);
//						// find a path between newFirstTransition and the second transition of path
//						List<GraphPath> allPaths = allDirectedPathCalculator.getAllPaths(newFirstTransition.getTgt(),
//								firstTransition.getTgt(), false, maxSubPathLenght);
//						List<GraphPath> zeroPAths = allDirectedPathCalculator.getAllPaths(newFirstTransition.getTgt(),
//								firstTransition.getTgt(), false, 0);
//						allPaths.removeAll(zeroPAths);					
//						if (allPaths.size() > 0) {
//							// build the new path
//							GraphPath choice = Randomness.choice(allPaths);
//
//							Path firstTransitionPath = new Path(newFirstTransition);
//							Path secondChunkPath = new Path(choice);
//							Path subPath = (Path) path.subPath(1, pathSize);
//							path.getModfiableTransitions().clear();
//							path.append(firstTransitionPath);
//							path.append(secondChunkPath);
//							path.append(subPath);
//							done = true;
//						} else {
//							startingTransitions.remove(newFirstTransition);
//						}
//					}
//				}
//			}
//			return;
//		}
//
//		// case of all transitions are feasible
//		// passedTransitions equals path size
//		if (passedTransitions == pathSize) {
//			// add few transitions at the end
//			EFSMState endState = path.getTransitionAt(pathSize-1).getTgt();
//			HashSet<EFSMState> initialSet = new HashSet<EFSMState>();
//			initialSet.add(endState);
//			
//			List<GraphPath> allNewExtension = allDirectedPathCalculator.getAllPaths(initialSet, efsm.getStates(), false, minSubPathLenght);
//			List<GraphPath> zeroTran = allDirectedPathCalculator.getAllPaths(initialSet, efsm.getStates(), false, 0);
//			allNewExtension.removeAll(zeroTran);
//			GraphPath choice = Randomness.choice(allNewExtension);
//			path.append(new Path(choice));
//			
//			return;
//		}
//
//		// case passed transition are between 0 and path length
//		if (passedTransitions > 0 && passedTransitions < pathSize) {
//
//			
//			
//			
////			// get first unfeasible transition
////			EFSMTransition firstUnfeasibleTransition = path.getTransitionAt(passedTransitions);
////			
////			// find an alternative path between the src and the tgt of the first unfeasible transition
////			
////			// find all paths between src and tgt of 
////			List<GraphPath> newPaths = allDirectedPathCalculator.getAllPaths(firstUnfeasibleTransition.getSrc(), firstUnfeasibleTransition.getTgt(), false, minSubPathLenght);
////			List<GraphPath> zeroTran = allDirectedPathCalculator.getAllPaths(firstUnfeasibleTransition.getSrc(), efsm.getStates(), false, 0);
////			newPaths.removeAll(zeroTran);
////			
////			if (newPaths.size() > 1 ) {
////				EFSMPath head = path.subPath(0, passedTransitions);
////				GraphPath center = Randomness.choice(newPaths);
////				Path newPath = new Path<>();
////				//path.getModfiableTransitions().clear();
////				newPath.append(head);
////				newPath.append(new Path(center));
////				if (pathSize > passedTransitions + 1) {
////					// there is tail
////					EFSMPath tail = path.subPath(passedTransitions+1, pathSize);
////					newPath.append(tail);
////				}
////				path = newPath;
////			}
//			
//			// remove unfeasible subpath
//			EFSMPath subPath = path.subPath(0, passedTransitions);
//			path.getModfiableTransitions().clear();
//			path.append(subPath);
//			return;
//		}
//
//		// should not arrive here because or passedTransitions < 0 or > path length
//		throw new RuntimeException("\nError: path \n" + path.toString() + " has " + passedTransitions + " over "
//				+ path.getLength() + " passed transitions");
//
//	}	
}
