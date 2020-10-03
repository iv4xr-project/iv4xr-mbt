/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

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
		for (int i = position1; i < path.getLength(); i++) {
			path.getModfiableTransitions().remove(i);
			path.parameterValues.remove(i);
		}
		for (int i = 0; i < position2; i++) {
			AbstractTestSequence<State, Parameter, Context, Trans> otherTc = (AbstractTestSequence<State, Parameter, Context, Trans>)other;
			path.getModfiableTransitions().add(otherTc.path.getTransitionAt(i));
			path.parameterValues.add((Parameter) otherTc.path.parameterValues.get(i));
		}
		
	}

	@Override
	public void mutate() {
		int index = Randomness.nextInt(getLength());
		path.parameterValues.set(index, (Parameter) new LabRecruitsParameterGenerator().getRandom());
		
	}
	
}
