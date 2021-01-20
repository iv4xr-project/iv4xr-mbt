/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Arrays;
import java.util.List;

import org.jgrapht.GraphPath;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public class Path<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>> extends 
		EFSMPath<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {
	
	public Path() {
		super();
	}

	public Path(List<Transition> transitions) {
		super(transitions);
	}

	public Path(Transition... transitions) {
		super(Arrays.asList(transitions));
	}
	
	public Path(GraphPath<State, Transition> basePath) {
	    super(basePath.getEdgeList());
	}

	/**
	 * IMPORTANT NOTE: this clone method DOES not do a deep clone, it's a mere copy.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Path clone = new Path(transitions);
		return clone;
	}
	
	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		String string = "";
		if (transitions != null) {
			string += "digraph g {\n";
			for (int i = 0; i < transitions.size(); i++) {
				Transition t = transitions.get(i);
				String label = t.getInParameter() + "/" + t.getGuard() + "/" + t.getOp() + "/" + t.getOutParameter();
				string += "\"" + t.getSrc() + "\" -> \"" + t.getTgt() + "\" [label = \"" + (i+1) + "-" + label + "\"];\n";				
			}
			string += "}";
		} 
		return string;
	}

	
	@Override
	public String toString() {
		String string = "";
		if (transitions != null) {
			for (Transition t : transitions) {
				string += t.toString() + "\n";
			}
		}
		return string;
	}
}
