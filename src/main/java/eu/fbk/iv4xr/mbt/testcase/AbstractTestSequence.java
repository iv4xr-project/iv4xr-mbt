/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Iterator;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;

/**
 * @author kifetew
 *
 */
public class AbstractTestSequence<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> implements Testcase {

	private Path<State, Parameter, Context, Trans> path;
	
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
		return clone;
	}
	
}
