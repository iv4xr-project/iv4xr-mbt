/**
 * 
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import eu.fbk.iv4xr.mbt.concretization.GenericConcreteTestCase;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 */
public class MinecraftConcreteTestCase extends GenericConcreteTestCase {
    private ArrayNode actions;
    private static ObjectMapper mapper = new ObjectMapper();

    public MinecraftConcreteTestCase(){
        actions = mapper.createArrayNode();
    }

    public void addAction(ObjectNode action){
        actions.add(action);
    }

    public ObjectNode getJsonTestCase(String name){
        ObjectNode testCase = mapper.createObjectNode();
        testCase.put("id", name);
        testCase.set("actions", actions);
        return testCase;
    }; 
}
