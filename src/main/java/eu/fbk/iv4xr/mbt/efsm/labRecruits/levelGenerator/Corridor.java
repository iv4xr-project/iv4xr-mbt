package eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator;

/*
 * @author wish
 */
/*
 * changes are made to complain Java 8 for using evosuite 1.0.6
 */

import java.util.LinkedList;
import java.util.List;

//import eu.iv4xr.lrtools.levgen.Room.Direction;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Room.Direction;

/** 
 * A corridor connecting two rooms. It is bi-directional. The fields "from"
 * and "to" does not indicate direction.
 */
public class Corridor extends WalledStructure {
	
	public Room from ;
	public Room to ;
	// doors guarding the corridor
	public List<Door> doors = new LinkedList<>() ;
	
	public Door guard(Door d) {
		doors.add(d) ;
		return d ;
	}
	
	public Door guard(String doorId) {
		return guard(new Door(doorId)) ;
	}
	
	public static Corridor connect(Room src, Room dest) {
		//var c = new Corridor() ;
		Corridor c = new Corridor() ;
		c.from = src ; c.to = dest ;
		src.connections.add(new Pair(c,Direction.UNKNOWN)) ;
		dest.connections.add(new Pair(c,Direction.UNKNOWN)) ;
		return c ;
	}
	
	@Override
	public String toString() {
		return "Corridor " + from.ID + "---" + to.ID ;
	}

}
