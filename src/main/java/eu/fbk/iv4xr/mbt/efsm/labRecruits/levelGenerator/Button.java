package eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator;

/*
 * @author wish
 */

import java.util.LinkedList;
import java.util.List;

public class Button {
	
	String ID ;
	boolean initialState = false ;
	List<Door> associatedDoors = new LinkedList<>() ;
	
	public Button(String id) { ID = id ; }

}
