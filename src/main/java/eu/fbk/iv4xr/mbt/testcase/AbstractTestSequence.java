/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.evosuite.ga.FitnessFunction;
import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.algorithm.operators.mutation.Mutator;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;

/**
 * @author kifetew
 *
 */
public class AbstractTestSequence<State extends EFSMState, InParameter extends EFSMParameter, OutParameter extends EFSMParameter, Context extends EFSMContext, Operation extends EFSMOperation, Guard extends EFSMGuard, Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>>
		implements Testcase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6113600146777909496L;
	private Path<State, InParameter, OutParameter, Context, Operation, Guard, Transition> path;
	private boolean valid = false;
	private double fitness = 0d;
	private boolean changed = true;

	/** Coverage goals this test covers */
	private transient Set<FitnessFunction<?>> coveredGoals = new LinkedHashSet<FitnessFunction<?>>();
	
	private ExecutionResult executionResult;

	private Mutator mutator;

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
		mutator = new Mutator(path);
	}

	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		if (path != null) {
			return path.toDot();
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		if (path != null) {
			return path.toString();
		} else {
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

	public void setFitness(double f) {
		fitness = f;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractTestSequence) {
			AbstractTestSequence ts = (AbstractTestSequence) obj;
			if (getLength() == ts.getLength()) {
				boolean eq = true;
				for (int i = 0; i < getLength(); i++) {
					if (!path.getTransitionAt(i).equals(ts.getPath().getTransitionAt(i))) {
						eq = false;
						break;
					}
				}
				return eq;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public Testcase clone() throws CloneNotSupportedException {
		AbstractTestSequence<State, InParameter, OutParameter, Context, Operation, Guard, Transition> clone = new AbstractTestSequence<>();
		clone.setPath((Path) path.clone());
		clone.setFitness(fitness);
		clone.setValid(valid);
		clone.setChanged(changed);
		clone.coveredGoals = new HashSet<FitnessFunction<?>>();
		for (FitnessFunction<?> goal : coveredGoals) {
			clone.addCoveredGoal(goal);
		}
		clone.setExecutionResult(executionResult.clone());
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

		/*
		 * if (!path.getTransitionAt(position1).getSrc() .equals(((AbstractTestSequence)
		 * other).path.getTransitionAt(position2).getSrc())) { EFSMTransition t1 =
		 * path.getTransitionAt(position1); EFSMTransition t2 = ((AbstractTestSequence)
		 * other).path.getTransitionAt(position2); EFSMState s1 = t1.getSrc(); EFSMState
		 * s2 = t2.getSrc(); s1.equals(s2); }
		 * 
		 * LinkedList<Transition> newTransitions = new LinkedList<Transition>(); for
		 * (int i = 0; i <= position1; i++) {
		 * newTransitions.add(path.getTransitionAt(i)); } for (int i = position2 + 1; i
		 * < other.getLength(); i++) { AbstractTestSequence<State, InParameter,
		 * OutParameter, Context, Operation, Guard, Transition> otherTc =
		 * (AbstractTestSequence<State, InParameter, OutParameter, Context, Operation,
		 * Guard, Transition>) other;
		 * newTransitions.add(otherTc.path.getTransitionAt(i)); } path = new
		 * Path(newTransitions);
		 */
		AbstractTestSequence othertc = (AbstractTestSequence) other;
		if (path.getTransitionAt(position1).equals(
				(othertc.path.getTransitionAt(position2)))){
			// the points correspond to a common transition
			var trunk1 = path.subPath(0, position1);
			var trunk2 = othertc.path.subPath(position2, other.getLength());
			path.getModfiableTransitions().clear();
			path.append(trunk1); 
			path.append(trunk2);	
		}else if (path.getTransitionAt(position1).getTgt().equals(
				othertc.path.getTransitionAt(position2).getTgt())) {
			// the points correspond to transitions with common tgt
			var trunk1 = path.subPath(0, position1+1);
			path.getModfiableTransitions().clear();
			path.append(trunk1); 
			if (position2+1 < other.getLength()) {
				var trunk2 = othertc.path.subPath(position2+1, other.getLength());
				path.append(trunk2);
			}
	
			
		}
		
		// changed = true; 

	}

	@Override
	public void mutate() {
		// System.err.println("BEFORE: " + path);
		// if (Randomness.nextBoolean()) {
		// insertSelfTransitionMutation ();
		// } else {
		// deleteSelfTransitionMutation ();
		// }
		// System.err.println("AFTER: " + path);
		
		// if the individual has changed but not evaluated, do not pass its execution result
		if (changed) {
			mutator.mutate(null);
		}else {
			mutator.mutate(executionResult);
		}
		// changed = true;

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

	public void setExecutionResult(ExecutionResult executionResult) {
		this.executionResult = executionResult;
	}
	
	public ExecutionResult getExecutionResult() {
		return this.executionResult;
	}

	/**
	 * @return the changed
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @param changed the changed to set
	 */
	@Override
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
