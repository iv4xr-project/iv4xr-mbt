/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.concretization.impl;

import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.ConcreteTestCase;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * 
 */
public class AplibConcreteTestCase implements ConcreteTestCase {

	private List<GoalStructure> goalStructures;

	/**
	 * @return the goalStructures
	 */
	public List<GoalStructure> getGoalStructures() {
		return goalStructures;
	}

	/**
	 * @param goalStructures the goalStructures to set
	 */
	public void setGoalStructures(List<GoalStructure> goalStructures) {
		this.goalStructures = goalStructures;
	}
	
}
