/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import java.util.Arrays;
import java.util.Collection;
//import java.util.LinkedList;
import java.util.Iterator;
import java.util.LinkedList;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import de.upb.testify.efsm.EFSMPath;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
import eu.fbk.iv4xr.mbt.efsm4j.ParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm4j.Transition;


/**
 * @author kifetew
 *
 */
public class Path<
	State extends EFSMState,
	Parameter extends EFSMParameter,
	Context extends IEFSMContext<Context>,
	Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends 
		EFSMPath<State, Parameter, Context, Trans> {
	
	LinkedList<Parameter> parameterValues = new LinkedList<Parameter>();
		
	public Path() {
		super();
		initializeParameterValues();
	}

	public Path(Collection<Trans> transitions) {
		super(transitions);
		initializeParameterValues();
	}

	public Path(Collection<Trans> transitions, Collection<Parameter> parameters) {
		super(transitions);
		parameterValues.addAll(parameters);
	}
	
	public Path(Trans... transitions) {
		super(Arrays.asList(transitions));
		initializeParameterValues();
	}
	
	/**
	 * We would like to have a random init? 
	 * Not sure about this init version
	 */
	private void initializeParameterValues () {
		for (int i = 0; i < transitions.size(); i++) {
			//parameterValues.add((Parameter) "");		#
			parameterValues.add((Parameter) new EFSMParameter() {

				@Override
				public boolean equals(Object obj) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public EFSMParameter clone() {
					// TODO Auto-generated method stub
					return null;
				}
			});
		}
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		//FIXME we need a proper clone implementation, this is just a placeholder!!
		Path clone = new Path(transitions, parameterValues);
		return clone;
	}
	
	/**
	 * 
	 * @return path in DOT format
	 */
	public String toDot() {
		String string = "";
		if (transitions != null && parameterValues != null) {
			string += "digraph g {\n";
			for (int i = 0; i < transitions.size(); i++) {
				Trans t = transitions.get(i);
				Parameter p = parameterValues.get(i);
				string += "\"" + t.getSrc() + "\" -> \"" + t.getTgt() + "\" [label = \"" + (i+1) + "-" + p.toString() + "\"];\n";				
			}
			string += "}";
		} 
		return string;
	}

	/**
	 * @return the parameterValues
	 */
	public LinkedList<Parameter> getParameterValues() {
		return parameterValues;
	}
}
