package eu.fbk.iv4xr.mbt.concretization.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.fbk.iv4xr.mbt.concretization.GenericConcreteTestCase;

public class SafaxConcreteTestCase extends GenericConcreteTestCase {
	
	private ArrayNode commands;
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    
    public SafaxConcreteTestCase(){
    	commands = objectMapper.createArrayNode();    	
    }

    public void addCommand(ObjectNode command){
    	commands.add(command);
    }
    
    public ObjectNode getJsonTestCase(JsonNode configuration){
    	
    	ObjectNode testCase = objectMapper.createObjectNode();
    	
    	ObjectNode config = (ObjectNode) configuration.get("config");
        testCase.set("config", config);
    	
        ArrayNode openCommands = (ArrayNode) configuration.get("open_commands");
        ArrayNode closeCommands = (ArrayNode) configuration.get("close_commands");
        
        ArrayNode completeCommands = objectMapper.createArrayNode();
        
        completeCommands.addAll(openCommands);
        completeCommands.addAll(commands);
        completeCommands.addAll(closeCommands);
        
        
        testCase.set("commands", completeCommands);
        return testCase;
    }
    
    
    
    
    
}
