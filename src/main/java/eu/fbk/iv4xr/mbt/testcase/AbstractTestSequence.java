/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Iterator;

import de.upb.testify.efsm.Transition;

/**
 * @author kifetew
 *
 */
public class AbstractTestSequence implements Testcase {

	private Path path;
	
	private double fitness = 0d;
	
	/**
	 * 
	 */
	public AbstractTestSequence() {
		
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(Path path) {
		this.path = path;
	}
	
	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		String string = "";
		if (path != null) {
			int i = 1;
			string += "digraph g {\n";
			Iterator iterator = path.iterator();
			while (iterator.hasNext()) {
				Transition t = (Transition) iterator.next();
				string += "'" + t.getSrc() + "' -> '" + t.getTgt() + "' [label = \"" + i++ + "\"];\n";
			}
			string += "}";
		} 
		return string;
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
	
}
