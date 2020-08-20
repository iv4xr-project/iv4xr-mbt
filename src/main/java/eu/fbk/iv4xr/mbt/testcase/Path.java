/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import de.upb.testify.efsm.EFSMPath;
import de.upb.testify.efsm.Transition;

/**
 * @author kifetew
 *
 */
public class Path extends EFSMPath {
	public Path() {
		super();
	}

	public Path(Collection<Transition> transitions) {
		super(transitions);
	}

	public Path(Transition... transitions) {
		super(Arrays.asList(transitions));
	}
}