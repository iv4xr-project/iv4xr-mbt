package eu.fbk.iv4xr.mbt.concretization.impl;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.concretization.ConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;

public class SafaxTestConcretizer extends TestConcretizer {

	
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Constructor requires the EFSM model
	 * @param model
	 */
	public SafaxTestConcretizer(EFSM model) {
		this.model = model;
	}
	
	
	/**
	 * Create the json file for the SAFAX client
	 */
	@Override
	public ConcreteTestCase concretizeTestCase(AbstractTestSequence abstractTestCase) {
		
		// Get the path on the EFSM
		Path path = abstractTestCase.getPath();
		
		// Reset the model
		model.reset();
		
		// get the list transitions 
		List<EFSMTransition> transitions = path.getTransitions();

		// data structure to store concrete test case information
		SafaxConcreteTestCase concreteTestCase = new SafaxConcreteTestCase();
		
		// iterate over transitions
		for(EFSMTransition tr: transitions ) {
			// execute the transition
			model.transition(tr);
			// get the actual transition, where the variables are updated
			EFSMTransition actual_tr = model.getTransition(tr.getId());
			
			ObjectNode currentCommand = parseInParameter(actual_tr.getInParameter());
			
			if ( currentCommand.size() > 0) {
				concreteTestCase.addCommand(currentCommand);	
			}
		}
		
		return concreteTestCase;
	}

	/**
	 * Parse transition input parameter and transform it into json fragment
	 * @param inParameter
	 * @return
	 */
	private ObjectNode parseInParameter(EFSMParameter inParameter) {

		Collection<Var<String>> allVariables = inParameter.getParameter().getAllVariables();
		
		ObjectNode command = mapper.createObjectNode();
		ObjectNode params = mapper.createObjectNode();
		
		for(Var<String> parVar : allVariables ) {

			// filedName encode the json structure
			String fieldName = parVar.getId();
			
			// current value
			String val = parVar.getValue();
			
			// type correspond to the field type
			if (fieldName.equalsIgnoreCase("type")) {
				command.put(fieldName, val);
				continue;
			}
			
			// id could be present
			if (fieldName.equalsIgnoreCase("id")) {
				command.put(fieldName, val);				
				continue;
			}
			
			// internal means no input to the sut is requried
			if (fieldName.equalsIgnoreCase("internal")) {
				continue;
			}
			
			// split 
			String[] splitField = fieldName.split(MBTProperties.SAFAX_CMD_SEPARATOR);
			if (splitField.length == 2 && splitField[0].equalsIgnoreCase("params")) {
				params.put(splitField[1], val);
			}else {
				throw new RuntimeException("Error parsing field "+fieldName);
			}
			
		}
		
		// if parameters are present, add them to the command
		if (params.size() > 0) {
			command.set("params", params);
		}
		
		return command;
		
	}
	
	
	

}
