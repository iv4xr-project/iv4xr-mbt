/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.strategy.AlgorithmFactory;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import de.upb.testify.efsm.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameterGenerator;



/**
 * @author kifetew
 *
 */
public class AbstractTestSequence<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> implements Testcase {

	private Path<State, InParameter, OutParameter, Context, Operation, Guard, Transition> path;
	private boolean valid = false;
	private double fitness = 0d;
	
	/** Coverage goals this test covers */
	private transient Set<FitnessFunction<?>> coveredGoals = new LinkedHashSet<FitnessFunction<?>>();
	
//	/** Local EFSM copy to generate parameters **/
//	private EFSM<State, InParameter, OutParameter, Context, Operation,  Guard, Transition> efsm;
	
//	/**
//	 * 
//	 */
//	public AbstractTestSequence(EFSM<State, InParameter, OutParameter, Context, Operation,  Guard, Transition> model) {
//		this.efsm = model;
//	}

	/**
	 * @return the path
	 */
	public Path<State, InParameter, OutParameter, Context, Operation, Guard, Transition> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(Path<State, InParameter, OutParameter, Context, Operation, Guard, Transition> path) {
		this.path = path;
	}
	
	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		if (path != null) {
			return path.toDot();
		}else {
			return "";
		}
	}

	@Override
	public String toString() {
		if (path != null) {
			return path.toString();
		}else {
			return "";
		}
	}
	
	public int getLength() {
		return path.getLength();
	}

	@Override
	public int compareTo(Testcase t) {
		return Double.compare(getFitness(), t.getFitness());
	}

	@Override
	public double getFitness() {
		return fitness;
	}

	public void setFitness (double f) {
		fitness = f;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractTestSequence) {
			AbstractTestSequence ts = (AbstractTestSequence)obj;
			if (getLength() == ts.getLength()) {
				boolean eq = true;
				for (int i = 0; i < getLength(); i++) {
					if (!path.getTransitionAt(i).equals(ts.getPath().getTransitionAt(i))) {
						eq = false;
						break;
					}
				}
				return eq;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public Testcase clone() throws CloneNotSupportedException {
		AbstractTestSequence clone = new AbstractTestSequence();
		clone.setPath((Path) path.clone());
		clone.setFitness(fitness);
		clone.setValid(valid);
		return clone;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public void crossOver(Testcase other, int position1, int position2) {
		LinkedList<Transition> newTransitions = new LinkedList<Transition>();
		for (int i = 0; i <= position1; i++) {
			newTransitions.add(path.getTransitionAt(i));
		}
		for (int i = position2+1; i < other.getLength(); i++) {
			AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition> otherTc = (AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition>)other;
			newTransitions.add(otherTc.path.getTransitionAt(i));
		}
		path = new Path(newTransitions);
	}

	@Override
	public void mutate() {
		//System.err.println("BEFORE: " + path);
		if (Randomness.nextBoolean()) {
			insertSelfTransitionMutation ();
		} else {
			deleteSelfTransitionMutation ();
		}
		//System.err.println("AFTER: " + path);
		
	}

	private void deleteSelfTransitionMutation() {
		// find a self transition and remove it
		Set<Integer> indices = new HashSet<Integer>();
		for (Transition t : path.getTransitions()) {
			if (t.isSelfTransition()) {
				indices.add(path.getTransitions().indexOf(t));
			}
		}
		
		// choose one at random and remove it
		if (!indices.isEmpty()) {
			path.getModfiableTransitions().remove(Randomness.choice(indices));
		}else {
			// nothing to do, mutation fails to modify individual
		}
		
	}

	private void insertSelfTransitionMutation() {
		
		// from the model, get all possible self transitions (states)
		EFSM model = AlgorithmFactory.getModel();
		Map<State, Transition> selfTransitionStates = new HashMap<>();
		for (Object o : model.getTransitons()) {
			Transition t = (Transition)o;
			if (t.isSelfTransition()) {
				selfTransitionStates.put(t.getSrc(), t);
			}
		}
		
		// identify a state where self transition is possible
		selfTransitionStates.keySet().retainAll(path.getStates());
		
		// choose one at random
		State state = Randomness.choice(selfTransitionStates.keySet());
		
		
		int index = -1;
		for (Transition t : path.getTransitions()) {
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

	@Override
	public void clearCoveredGoals() {
		coveredGoals.clear();
	}

	@Override
	public Set<FitnessFunction<?>> getCoveredGoals() {
		return coveredGoals;
	}

	@Override
	public void addCoveredGoal(FitnessFunction goal) {
		coveredGoals.add(goal);
	}

	@Override
	public boolean isGoalCovered(FitnessFunction goal) {
		return coveredGoals.contains(goal);
	}
}
