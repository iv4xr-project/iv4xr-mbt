package eu.fbk.iv4xr.mbt.algorithm.operators.mutation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.utils.Randomness;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.GraphMeasurer;

import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointRelativePathCrossOver;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;
import eu.fbk.iv4xr.mbt.testcase.Path;

public class Mutator<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> {

	protected static final Logger logger = LoggerFactory.getLogger(Mutator.class);
	Path path;
	
	public Mutator(Path path) {
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void mutate() {
		
		
		double choice = Randomness.nextDouble();
		//logger.debug("MUTATION: " + choice);
		if (choice < 0.33) {
			insertSelfTransitionMutation();
		} else if (choice > 0.33 & choice < 0.66) {
			deleteSelfTransitionMutation();
		} else {
			singleTransitionRemoval();
		}
		
	}

	
	private void singleTransitionRemoval() {

		EFSM efsm = AlgorithmFactory.getModel();

		// It would be convenient to move path computing objects within the EFSM
		//AllDirectedPaths<State, Transition> allPathsCalculator = new AllDirectedPaths<>(efsm.getBaseGraph());
		//GraphMeasurer<State, Transition> graphMeasurer = new GraphMeasurer(efsm.getBaseGraph());
		AllDirectedPaths<State, Transition> allPathsCalculator = efsm.getAllDirectedPathCalculator();
		GraphMeasurer<State, Transition> graphMeasurer = efsm.getGraphMeasurer();
		
		// Use the graph diameter as max path size to add
		Double graphDiameter = graphMeasurer.getDiameter();

		// Take a random transition in the path
		Integer transitionToRemoveId = Randomness.nextInt(path.getLength());
		Transition transitionToRemove = (Transition) path.getTransitionAt(transitionToRemoveId);

		// Compute all path
		List<GraphPath<State, Transition>> allPath = allPathsCalculator.getAllPaths(transitionToRemove.getSrc(),
				transitionToRemove.getTgt(), true, graphDiameter.intValue());
		
		// first and last transition need to be treated separately
		if (transitionToRemoveId > 0 & transitionToRemoveId < path.getLength()-1) {
			Boolean choosen = false;
			while(!choosen & allPath.size() > 0) {
				GraphPath selectePath = Randomness.choice(allPath);
				if (selectePath.getLength() > 1) {
					var head =  path.subPath(0, transitionToRemoveId);
					var tail = path.subPath(transitionToRemoveId+1,path.getLength());
					Path newTrunk = new Path(selectePath);
					//Path outPath = new Path();
					//outPath.append(head);
					//outPath.append(newTrunk);
					//outPath.append(tail);
					//logger.debug("MUTATION: ADDED "+newTrunk.getLength()+" transitions");
					path.getModfiableTransitions().clear();
					path.append(head);
					path.append(newTrunk);
					path.append(tail);
					choosen = true;
				}else {
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
		EFSM model = AlgorithmFactory.getModel();
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
}
