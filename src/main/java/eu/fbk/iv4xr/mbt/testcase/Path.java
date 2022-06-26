/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class Path extends EFSMPath {
	
	public Path() {
		super();
	}

	public Path(List<EFSMTransition> transitions) {
		super(transitions);
	}

	public Path(EFSMTransition... transitions) {
		super(Arrays.asList(transitions));
	}
	
	public Path(GraphPath<EFSMState, EFSMTransition> basePath) {
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
				EFSMTransition t = transitions.get(i);
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
			for (EFSMTransition t : transitions) {
				string += t.toString() + "\n";
			}
		}
		return string;
	}
	
	/**
	 * debug method to count number of self transitions in the test
	 */
	public Map<EFSMTransition, Integer> selfTransitionCounts (){
		Map<EFSMTransition, Integer> counts = new HashMap<>(); 
		for (Object t : transitions) {
			EFSMTransition tr = ((EFSMTransition)t);
			if (tr.isSelfTransition()) { // && tr.getSrc().getId().equalsIgnoreCase("b0")) {
				if (!counts.containsKey(tr)) {
					counts.put(tr, 0);
				}
				counts.put(tr, counts.get(tr)+1);
			}
		}
		Iterator<Entry<EFSMTransition, Integer>> entryIterator = counts.entrySet().iterator();
//		for (Entry<Transition, Integer> entry : counts.entrySet()) {
		while (entryIterator.hasNext()) {
			Entry<EFSMTransition, Integer> entry = entryIterator.next();
			if (entry.getValue() % 2 == 1) {
				entryIterator.remove();
				System.out.println("Odd count removed");
			}
		}
		return counts;
	}
}
