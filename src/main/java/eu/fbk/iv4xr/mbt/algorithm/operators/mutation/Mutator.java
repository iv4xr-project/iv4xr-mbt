package eu.fbk.iv4xr.mbt.algorithm.operators.mutation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.evosuite.utils.Randomness;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.testcase.Path;

public class Mutator<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353952869532319217L;
	protected static final Logger logger = LoggerFactory.getLogger(Mutator.class);
	private Path path;
	private Integer minSubPathLenght = 2;
	private Integer maxSubPathLenght = 5;
	private Integer passedTransitions = 0;
	
	public Mutator(Path path) {
		this.path = path;
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
		// set the number of passed transitions
		passedTransitions = executionResult.getExectionTrace().getPassedTransitions();
		double choice = Randomness.nextDouble();
		
		removeFirstNotFeasible();
	}
	
	
	
	/**
	 * try to remove the first not feasible transition
	 */
	public void removeFirstNotFeasible() {
		
		// case of passedTransitions equals to 0
		// no feasible transitions in the path
		if (passedTransitions == 0) {
			
			return;
		}
		
		// case of all transitions are feasible
		// passedTransitions equals path size
		if (passedTransitions == path.getLength()) {
			
			return;
		}
		
		// case passed transition are between 0 and path length
		if (passedTransitions > 0 && passedTransitions < path.getLength()) {
			
			return;
		}
	
		// should not arrive here because or passedTransitions < 0 or > path length
		throw new RuntimeException("\nError: path \n"+path.toString()+" has "+passedTransitions+" over "+path.getLength()+" passed transitions");
		
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//
	// Old versions based only on path without using execution information
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * First version that do not use information about execution 
	 * @param executionResult
	 */
	public void _mutate(ExecutionResult executionResult) {
		double choice = Randomness.nextDouble();
		logger.debug("Passed transitions: {}", executionResult.getExectionTrace().getPassedTransitions());
		//logger.debug("MUTATION: " + choice);
		if (choice < 0.33) {
			insertSelfTransitionMutation();
		} else if (choice < 0.33) {
			deleteSelfTransitionMutation();
	    // embedded in singleTransitionRemoval
		//} else if (choice < 0.75) {
		//	appendRandomTransitionMutation();
		} else {
			singleTransitionRemoval();
		}
		
	}
	
	
	private void singleTransitionRemoval() {

		EFSM efsm = EFSMFactory.getInstance().getEFSM();

		// It would be convenient to move path computing objects within the EFSM
		//AllDirectedPaths<State, Transition> allPathsCalculator = new AllDirectedPaths<>(efsm.getBaseGraph());
		//GraphMeasurer<State, Transition> graphMeasurer = new GraphMeasurer(efsm.getBaseGraph());
		AllDirectedPaths<State, Transition> allPathsCalculator = efsm.getAllDirectedPathCalculator();
		//GraphMeasurer<State, Transition> graphMeasurer = efsm.getGraphMeasurer();
		
		// Use the graph diameter as max path size to add: TOO slow
		// Double graphDiameter = graphMeasurer.getDiameter();
		// Integer newPathLength = graphDiameter.intValue()
		
		// Use the length of the path as a maximum length for the new subpath
		// if the path has length less that 2, use 2
		Integer newPathMaxLength = Integer.min(Integer.max(path.getLength(),minSubPathLenght),maxSubPathLenght);
		
		// Take a random transition in the path
		Integer transitionToRemoveId = Randomness.nextInt(path.getLength());
		Transition transitionToRemove = (Transition) path.getTransitionAt(transitionToRemoveId);


			
		// first and last transition need to be treated separately
		if (transitionToRemoveId >= 0 & transitionToRemoveId < path.getLength()-1) {
			// Compute all path from src to the tgt of the removed transition
			List<GraphPath<State, Transition>> allPath = allPathsCalculator.getAllPaths(transitionToRemove.getSrc(),
					transitionToRemove.getTgt(), false, newPathMaxLength);
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
			List<GraphPath<State, Transition>> allPath = allPathsCalculator.getAllPaths(transitionToRemove.getSrc(),
					transitionToRemove.getTgt(), false, newPathMaxLength);
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
			Transition t = (Transition) path.getTransitionAt(i);
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
		Map<State, Transition> selfTransitionStates = new HashMap<>();
		for (Object o : model.getTransitons()) {
			Transition t = (Transition) o;
			if (t.isSelfTransition()) {
				selfTransitionStates.put(t.getSrc(), t);
			}
		}

		// identify a state where self transition is possible
		selfTransitionStates.keySet().retainAll(path.getStates());

		// choose one at random
		State state = Randomness.choice(selfTransitionStates.keySet());

		int index = -1;
		for (Transition t : (List<Transition>)path.getTransitions()) {
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
		path.getModfiableTransitions().add(index, (Transition) selfTransitionStates.get(state).clone());

	}
	
	private void appendRandomTransitionMutation() {
		EFSMState lastState = path.getTgt();
		Set<Transition> potentialTransitions = EFSMFactory.getInstance().getEFSM().transitionsOutOf(lastState);
		Transition t = (Transition) Randomness.choice(potentialTransitions);
		path.append(t);
	}
}
