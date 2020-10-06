/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameterGenerator;

/**
 * @author kifetew
 *
 */
public class AbstractTestSequence<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends Transition<State, Parameter, Context>> implements Testcase {

	private Path<State, Parameter, Context, Trans> path;
	private boolean valid = false;
	private double fitness = 0d;
	
	/** Coverage goals this test covers */
	private transient Set<FitnessFunction<?>> coveredGoals = new LinkedHashSet<FitnessFunction<?>>();
	
	/**
	 * 
	 */
	public AbstractTestSequence() {
		
	}

	/**
	 * @return the path
	 */
	public Path<State, Parameter, Context, Trans> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(Path<State, Parameter, Context, Trans> path) {
		this.path = path;
	}
	
	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		return path.toDot();
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
		LinkedList<Trans> newTransitions = new LinkedList<Trans>();
		LinkedList<Parameter> newParameters = new LinkedList<Parameter>();
		for (int i = 0; i <= position1; i++) {
			newTransitions.add(path.getTransitionAt(i));
			newParameters.add(path.parameterValues.get(i));
		}
		for (int i = position2+1; i < other.getLength(); i++) {
			AbstractTestSequence<State, Parameter, Context, Trans> otherTc = (AbstractTestSequence<State, Parameter, Context, Trans>)other;
			newTransitions.add(otherTc.path.getTransitionAt(i));
			newParameters.add((Parameter) otherTc.path.parameterValues.get(i));
		}
		path = new Path(newTransitions, newParameters);
	}

	@Override
	public void mutate() {
		int index = Randomness.nextInt(getLength());
		path.parameterValues.set(index, (Parameter) new LabRecruitsParameterGenerator().getRandom());
		
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
