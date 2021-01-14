/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Arrays;
import java.util.Collection;
//import java.util.LinkedList;
//import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.evosuite.shaded.org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import de.upb.testify.efsm.EFSMPath;
//import de.upb.testify.efsm.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMPath;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.ParameterGenerator;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;


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
	
//	/**
//	 * We would like to have a random init? 
//	 * Not sure about this init version
//	 */
//	private void initializeParameterValues () {
//		for (int i = 0; i < transitions.size(); i++) {
//			//parameterValues.add((Parameter) "");		#
//			parameterValues.add((InParameter) new EFSMParameter() {
//
//				@Override
//				public boolean equals(Object obj) {
//					// TODO Auto-generated method stub
//					return false;
//				}
//
//				@Override
//				public EFSMParameter clone() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//			});
//		}
//	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		//FIXME we need a proper clone implementation, this is just a placeholder!!
		Path clone = new Path(SerializationUtils.clone(transitions));
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
