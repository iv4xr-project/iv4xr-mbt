package eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator;

/*
 * @author wish
 */
/*
 * changes are made to complain Java 8 for using evosuite 1.0.6
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Room extends WalledStructure {
	
	public enum Direction { NORTH, EAST, SOUTH, WEST, UNKNOWN } 
	
	int ID ;
	List<Pair<Corridor,Direction>> connections  = new LinkedList<>() ;
	List<Button> buttons = new LinkedList<>() ;
	Agent agent = null ;
	
	public Room(int id) { ID = id ; }
	
	public Button addButton(Button B) {
		buttons.add(B) ; return B ;
	}
	
	public Button addButton(String buttonId) {
		return addButton(new Button(buttonId)) ;
	}
	
	public Agent placeAgent(String agentId) {
		agent = new Agent(agentId) ;
		return agent ;
	}
	
	/**
	 * Get the connection to the given room R.
	 */
	public Pair<Corridor,Direction> getConnection(Room R) {
		//for(var connection : connections) {
		for(Pair<Corridor,Room.Direction> connection : connections) {
			Room R2 = getConnectedRoom(connection.fst) ;
			if (R2 == R) {
				return connection ;
			}
		}
		return null ;
	}
	
	/**
	 * Get the connection (so, corridor and direction pair) that belongs to the given corridor.
	 */
	public Pair<Corridor,Direction> getConnection(Corridor cor) {
		//for(var connection : connections) {
		for(Pair<Corridor,Room.Direction> connection : connections) {
			if (connection.fst == cor) return connection ;
		}
		return null ;
	}
	
	/**
	 * Get all corridors connected to this room.
	 */
	public List<Corridor> getCorridors() {
		return connections.stream().map(C -> C.fst).collect(Collectors.toList()) ;
	}
	
	/**
	 * Get the destination room connected with the given corridor.
	 */
	public Room getConnectedRoom(Corridor cor) {
		if (!getCorridors().contains(cor)) throw new IllegalArgumentException() ;
		if (cor.from == cor.to) {
			// a self loop connection
			return cor.from ;
		}
		if (cor.from == this) return cor.to ;
		return cor.from ;
	}
	
	/**
	 * Check if the given direction is still unconnected. If so, return a connection with still
	 * unassigned direction, if one can be found.
	 * Else we get a null.
	 */
	public Pair<Corridor,Direction> getFreeConnection(Direction dir) {
		// check first if the given direction is already used:
		//for (var connection : connections) {
		for (Pair<Corridor,Room.Direction> connection : connections) {
			if (connection.snd == dir) return null ;
		}
		// ok so, the direction is free. Check if there is still a connection that is
		// not assigned to a direction yet:
		//for (var connection : connections) {
		for (Pair<Corridor,Room.Direction> connection : connections) {
			if (connection.snd == Direction.UNKNOWN) return connection ;
		}
		return null ;
	}
	
	
	public void clearConnectionsDirections() {
		for (Pair<Corridor,Direction> con : connections) {
			con.snd = Direction.UNKNOWN ;
		}
	}
	
	@Override
	public String toString() {
		String s = "Room" ;
		s += " " + ID + "  (#connection:" + connections.size() + ", #buttons:" + buttons.size() + ")\n" ;
		//for (var connection : connections) {
		for (Pair<Corridor,Room.Direction> connection : connections) {
			s += "  " ;
			switch(connection.snd) {
			   case EAST  : s += "E: " ; break ;
			   case WEST  : s += "W: " ; break ;
			   case NORTH : s += "N: " ; break ;
			   case SOUTH : s += "S: " ; break ;
			   default    : s += "?: " ;
			}
			s += getConnectedRoom(connection.fst).ID  + "\n";
		}
		s += "  buttons: " ;
		int k=0 ;
		//for (var b : buttons) {
		for (Button b : buttons) {
			if (k>0) s += ", " ;
			s += b.ID ;
			k++ ;
		}
		s += "\n" ;
		return s ;
	}
	
	/**
	 * Return all buttons collected from a set of rooms.
	 */
	public static List<Button> collectButtons(List<Room> rooms) {
		List<Button> buttons = new LinkedList<>() ;
		//for(var R : rooms) {
		for(Room R : rooms) {
			buttons.addAll(R.buttons) ;
		}
		return buttons ;
	}
	
	public static List<Button> collectButtons(Room[] rooms) {
	    List<Room> rooms_ = new LinkedList<>() ;
	    for(int i=0; i<rooms.length; i++) {
	    	rooms_.add(rooms[i]) ;
	    }
	    return collectButtons(rooms_) ;
	}
	/**
	 * Randomly generating a network of rooms. 
	 * @param numberOfRooms
	 * @param maxOutDegree
	 * @param maxButtonsPerRoom
	 * @param allowSelfLoop
	 * @return
	 */
	public static List<Room> randomGen(int numberOfRooms, 
			int maxOutDegree, 
			int maxButtonsPerRoom,
			int maxDoorsPerCorridor,
			boolean allowSelfLoop) {
		if (maxOutDegree > 3)
			throw new UnsupportedOperationException("Branching degree cannot exceed 3.") ;
		if (maxButtonsPerRoom > 5)
			throw new UnsupportedOperationException("#buttons per room cannot exceed 5.") ;
		if (maxDoorsPerCorridor>3)
			throw new UnsupportedOperationException("#doors per corridor cannot exceed 3.") ;
		if (allowSelfLoop)
			throw new UnsupportedOperationException("Corridor connecting a room to itself is disabled.") ;
		
		Random rnd = new Random(7498) ;
		
		List<Room> rooms = new LinkedList<>() ;
		for (int r=0; r<numberOfRooms; r++)  {
			rooms.add(new Room(r)) ;
		}
		
		// just adding agent Smith to room-0:
		rooms.get(0).agent = new Agent("Smith") ;
		
		
		List<Room> done = new LinkedList<>() ;
		
		for(Room R : rooms) {
			int nButtons = rnd.nextInt(maxButtonsPerRoom+1) ;
			for (int k=0; k<nButtons; k++) {
				R.buttons.add(new Button("b_R" + R.ID + "_" + k)) ;
			}
			
			if (R.connections.size() == maxOutDegree) {
				// R is already connected to max
				done.add(R) ;
				continue ;
			}
			// R is not full
			int newConnetions = 1 ;
			if (R.connections.isEmpty()) {
				// case-1 : R has no connection yet:
				newConnetions =  1 + rnd.nextInt(maxOutDegree) ;
				//newConnetions =  maxOutDegree ;
				
			}
			else {
				// case-2: R already has some connections:
				newConnetions = rnd.nextInt(maxOutDegree + 1 - R.connections.size()) ;
			}
			if (newConnetions == 0) {
				done.add(R) ;
				continue ;
			}
				
			// collect other rooms that still have connectors free:
			//var candidates = rooms
			List<Room> candidates = rooms
					   .stream()
					   .filter(R2 -> ! done.contains(R2)
							         && R2.connections.size() < maxOutDegree 
							         && (R != R2 || allowSelfLoop)
							   )
					   .collect(Collectors
					   .toList()) ;
			
			
			for (int connection = 0; connection<newConnetions && !candidates.isEmpty() ; connection++) {
				Room R2 = candidates.get(rnd.nextInt(candidates.size())) ;
				Corridor.connect(R,R2) ;
				candidates.remove(R2) ;
				//var cor = R.getConnection(R2).fst ;
				Corridor cor = R.getConnection(R2).fst ;
				int numOfDoors = rnd.nextInt(maxDoorsPerCorridor+1) ;
				for (int d=0;d<numOfDoors;d++) {
					//var door = new Door("d_" + R.ID + "_" + R2.ID + "_" + d) ;
					Door door = new Door("d_" + R.ID + "_" + R2.ID + "_" + d) ;
					cor.doors.add(door) ;
				}
			}
			done.add(R) ;
		}
		return rooms ;
	}
	
	public static void main(String[] args) {
		List<Room> rooms = randomGen(4,3,1,1,false) ;
		//for(var R : rooms) {
		for(Room R : rooms) {
			System.out.println("======") ;
			System.out.println(R.toString()) ;
		}
	}

}
