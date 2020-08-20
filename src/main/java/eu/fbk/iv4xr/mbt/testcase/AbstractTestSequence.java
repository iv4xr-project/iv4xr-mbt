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
				string += t.getSrc() + " -> " + t.getTgt() + " [label = \"" + i++ + "\"];\n";
			}
			string += "}";
		} 
		return string;
	}

	@Override
	public int getLength() {
		return path.getLength();
	}

}
