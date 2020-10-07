package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.io.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;

import eu.fbk.iv4xr.mbt.MBTProperties;
//import de.upb.testify.efsm.*;
//import eu.fbk.se.labrecruits.*;
import eu.fbk.iv4xr.mbt.efsm4j.*;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Button;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Corridor;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Door;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Room;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.RendererToLRLevelDef;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Layout;


/**
 * 
 * Generate a random EFSM that could be mapped into one or more Lab Recruits level.
 * A level is defined by the number of its buttons and doors, but we also need a way to
 * specify the number of rooms. In the current abstraction, a room has not a direct representation
 * but it constitutes a strongly connected graph, where each node is button. Two rooms can
 * be connected by one ore more doors, and a door can connect exactly two rooms (including
 * the case of empty rooms, i.e., with no buttons). 
 * <p>
 * We define the mean number of buttons M per room and we use binomial distribution with mean M to generate
 * the number of buttons in each room. In this way the number of rooms is not predefined but
 * depends on the random assignment of buttons to a room.  
 * 
 * NOTES: need to define a class for generating doors and button names
 * 
 * @author Davide Prandi
 *
 * Aug 19, 2020
 * 
 */
public class LabRecruitsRandomEFSM {
	
	
	// Random seed to generate level layout
	private long seed = MBTProperties.LR_seed;
	
	// Total number of buttons
	private int nButtons = MBTProperties.LR_n_buttons;
	
	// Total number of doors
	private int nDoors = MBTProperties.LR_n_doors;
	
	// Expected number of buttons per room
	// Buttons per room are selected following a Poisson
	private double meanButtonsPerRoom = MBTProperties.LR_mean_buttons;
	
	// random number generator (using Mersenne Twister rng) 
	private RandomDataGenerator rndGenerator = new RandomDataGenerator(new MersenneTwister(seed));
	
	// door graph
	private Pseudograph<Vector<LabRecruitsState>,Integer>  doorsGraph = new Pseudograph<>(Integer.class);
	
	// final efsm
	private EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> efsm = null; // to fix
	
	// csv version to feed Lab Recruits
	private String csvLevel = "";
	
	// parameters generator
	LabRecruitsParameterGenerator parameterGenerator = new LabRecruitsParameterGenerator();
		
	// Default parameters

	public LabRecruitsRandomEFSM() {
		
		int nTry = 5;
		while(this.csvLevel == "" & nTry > 0) {
			// generate a list of rooms
			// each room is a vector of buttons
			List<Vector<LabRecruitsState>> roomSet = generateRoomSet();	

			// the number of rooms are not predefined
			// int nRooms = roomSet.size(); // not needed?
			// connect rooms with doors
			// the problem is equivalent to the creation of a connected graph
			this.doorsGraph = generatePlanarDoorsGraph(roomSet);
			// DEBUG saveDoorGraph("data/test_"+nTry+".xml");
			// test if doorsGraph is effectively planar
			// need to update JGraphT
			
			// expand the doors graph to the corresponding EFSM
			// and add button-doors map
			this.efsm = doorsGraphToEFSM();
			
			// create the csv version that can be played
			this.csvLevel = generateCSV();
			nTry = nTry - 1;
		}
	}

	
	// NOTE: parameters are in MBTProperties and we could avoid changing them,
	//       but we keep for testing purposes
	// get and set parameters 	
	public int get_nButtons() {
		return(nButtons);
	}
	public void set_nButtons(int nB) {
		nButtons = nB;
	}
	public int get_nDoors() {
		return(nDoors);
	}
	public void set_nDoors(int nD) {
		nDoors = nD;
	}
	public double get_meanButtonsPerRoom() {
		return(meanButtonsPerRoom);
	}
	public void set_meanButtonsPerRoom(double mBR) {
		if (mBR >0 ) {
			meanButtonsPerRoom = mBR;
		}		
	}
	public long get_seed() {
		return(seed);
	}
	public void set_seed(long newSeed) {
		seed = newSeed;
		rndGenerator.reSeed(newSeed);
	}
	public String get_csv() {
		return csvLevel;
	}
	

	/**
	 * Return the random level 
	 * @return and EFSM
	 */
	public EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> getEFMS()  {	
		return efsm;
	}
	
	/**
	 * Generate a set of rooms. The total number of buttons and the expected number of buttons per room are defined
	 * @return a list of vectors of LabRectuitsState
	 */
	private List<Vector<LabRecruitsState>> generateRoomSet(){		
		// buttons that need to be generated
		int availableButtons  = nButtons;
		// buttons names are in the form b_1, b_2, ...
		int currentButtonId = 0;
		// a room set is a list of rooms
		// a room is a Vector of LabRecruitsState
		List<Vector<LabRecruitsState>> roomSet = new ArrayList<Vector<LabRecruitsState>>();
		// generate rooms independently 
		while (availableButtons > 0) {			
			// generate the number of buttons in the room
			//int nRoomButtons = rndGenerator.nextBinomial((int) Math.round(meanButtonsPerRoom/probabilityOfSuccess), probabilityOfSuccess);
			int nRoomButtons = (int)rndGenerator.nextPoisson(meanButtonsPerRoom) + 1;
			// check to do not create more buttons than needed
			if (nRoomButtons > availableButtons) {
				nRoomButtons = availableButtons;
				availableButtons = 0;
			}else {
				availableButtons = availableButtons - nRoomButtons;
			}			
			// generate a room 
			Vector<LabRecruitsState> room = generateRoom(currentButtonId,nRoomButtons);
			roomSet.add(room);
			// update id for naming buttons
			currentButtonId = currentButtonId + nRoomButtons;			
		}		
		return roomSet;		
	}
	
	/**
	 * A room is a vector of buttons. Each button is named with an integer.
	 * @param currentButtonId
	 * @param nRoomButtons
	 * @return
	 */
	private Vector<LabRecruitsState>  generateRoom(int currentButtonId, int nRoomButtons) {	
		Vector<LabRecruitsState> room = new Vector<LabRecruitsState>(nRoomButtons);	
		for (int i = 0; i < nRoomButtons; i++) {
			Integer buttonName =  currentButtonId + i;
			LabRecruitsState button = new LabRecruitsState( "b"+Integer.toString(buttonName) );
			room.add(i,  button );
		}	
		return(room);		
	}
	
	/**
	 * Generate the door graph
	 * @param roomSet
	 * @return doors graph
	 */
	private Pseudograph<Vector<LabRecruitsState>,Integer> generateDoorsGraph(List<Vector<LabRecruitsState>> roomSet){
		
		doorsGraph = new Pseudograph<>(Integer.class);
		
		// add vertex, i.e., set of labrecruits states
		for (Vector<LabRecruitsState> vector : roomSet) {
			doorsGraph.addVertex(vector);
		}
		
		
		
		// tmp values to store used and remaining rooms that need to be connected
		Set<Vector<LabRecruitsState>> usedRooms =  new LinkedHashSet<>();
		// need to clone because vertexSet returns a view
		Set<Vector<LabRecruitsState>> availableRooms = new LinkedHashSet<>(doorsGraph.vertexSet());
		
		
		// pick first node and update used and available rooms
		//Vector<?> firstNode = (Vector<?>) availableRooms.toArray()[rndGenerator.nextInt(0, availableRooms.size()-1)];
		int firstNodeId = rndGenerator.nextInt(0, availableRooms.size()-1);
		Vector<LabRecruitsState> firstNode = availableRooms.stream().skip(firstNodeId).iterator().next();
		availableRooms.remove(firstNode);
		usedRooms.add(firstNode);
		
		// iterate over nDoors and add an edge each time selecting
		// one node from the used and from the available
		for (int i = 0; i < nDoors; i++) {
			// random pick an available and a used node
			int nextAvailableId = rndGenerator.nextInt(0, availableRooms.size()-1);
			Vector<LabRecruitsState> unconnectedNode =  availableRooms.stream().skip(nextAvailableId).iterator().next();
			int nextUsedId = rndGenerator.nextInt(0, usedRooms.size()-1);
			Vector<LabRecruitsState> connectedNode =  usedRooms.stream().skip(nextUsedId).iterator().next();
			// add a new edge to the graph
			doorsGraph.addEdge(connectedNode, unconnectedNode,i);
			// update available and used rooms
			availableRooms.remove(unconnectedNode);
			usedRooms.add(unconnectedNode);
			// no more rooms but still some door to create
			if (availableRooms.size() == 0) {
				break;
			}
		}
		
		// 3 situations are possible
		
		// all rooms are used but there are still some available door: random add doors between random rooms
		if (doorsGraph.edgeSet().size() < nDoors) {
			for (int j = doorsGraph.edgeSet().size(); j < nDoors; j++) {
				// random pick two rooms id (could be the same)
				int firstRoomId = rndGenerator.nextInt(0, usedRooms.size()-1);
				int secondRoomId = rndGenerator.nextInt(0, usedRooms.size()-1);
				Vector<LabRecruitsState> firstRoom =  usedRooms.stream().skip(firstRoomId).iterator().next();
				if (firstRoomId != secondRoomId) {
					Vector<LabRecruitsState> secondRoom  =  usedRooms.stream().skip(secondRoomId).iterator().next();
					doorsGraph.addEdge(firstRoom, secondRoom,j);
				}else {
					// self loop are doors to empty rooms (solved when building the final EFSM)
					doorsGraph.addEdge(firstRoom, firstRoom, j);
				}
				
			}
		}
		
		// all doors are used but there are still some room: merge remaining rooms with used rooms
		// remove unused rooms
		for (Vector<LabRecruitsState> room : availableRooms) {
			doorsGraph.removeVertex(room);
		}
		// add unused buttons to already connected rooms
		/*
		 * for(Vector<LabRecruitsState> room : availableRooms) { int toMergeId =
		 * rndGenerator.nextInt(0, doorsGraph.vertexSet().size() - 1);
		 * doorsGraph.vertexSet().stream().skip(toMergeId).iterator().next().addAll(room
		 * ); }
		 */
		// all rooms and doors are used: exit
		return(doorsGraph);
	}
	
	/*
	 * Generate the door graph trying to make it planar
	 * - no self loop
	 * - no multi-edges
	 * - at most 3 edges per node
	 * @param roomSet
	 * @return doors graph
	 */
	private Pseudograph<Vector<LabRecruitsState>,Integer> generatePlanarDoorsGraph(List<Vector<LabRecruitsState>> roomSet){
		
		Pseudograph<Vector<LabRecruitsState>,Integer>  doorsGraph = new Pseudograph<>(Integer.class);
		Pseudograph<Vector<LabRecruitsState>,Integer>  completeGraph = new Pseudograph<>(Integer.class);
		
		// add vertex, i.e., set of labrecruits states
		for (Vector<LabRecruitsState> vector : roomSet) {
			doorsGraph.addVertex(vector);
		}
		
		
		
		// tmp values to store used and remaining rooms that need to be connected
		Set<Vector<LabRecruitsState>> usedRooms =  new LinkedHashSet<>();
		// need to clone because vertexSet returns a view
		Set<Vector<LabRecruitsState>> availableRooms = new LinkedHashSet<>(doorsGraph.vertexSet());
		
		// rooms and initial doors to make the graph connected is the same as generateDoorsGraph
		// need to reconcile 
		
		// pick first node and update used and available rooms
		//Vector<?> firstNode = (Vector<?>) availableRooms.toArray()[rndGenerator.nextInt(0, availableRooms.size()-1)];
		int firstNodeId = rndGenerator.nextInt(0, availableRooms.size()-1);
		Vector<LabRecruitsState> firstNode = availableRooms.stream().skip(firstNodeId).iterator().next();
		availableRooms.remove(firstNode);
		usedRooms.add(firstNode);
		
		HashMap<Vector<LabRecruitsState>, Integer> roomsDoorsCount = new HashMap<Vector<LabRecruitsState>, Integer>();
		roomsDoorsCount.put(firstNode,0);
		
		// iterate over nDoors and add an edge each time selecting
		// one node from the used and from the available
		for (int i = 0; i < nDoors; i++) {
			// random pick an available and a used node
			int nextAvailableId = rndGenerator.nextInt(0, availableRooms.size()-1);
			Vector<LabRecruitsState> unconnectedNode =  availableRooms.stream().skip(nextAvailableId).iterator().next();
			int nextUsedId = rndGenerator.nextInt(0, usedRooms.size()-1);
			Vector<LabRecruitsState> connectedNode =  usedRooms.stream().skip(nextUsedId).iterator().next();
			// add a new edge to the graph
			doorsGraph.addEdge(connectedNode, unconnectedNode,i);
			roomsDoorsCount.put(connectedNode, roomsDoorsCount.get(connectedNode)+1);
			roomsDoorsCount.put(unconnectedNode, 1);
			
			if (roomsDoorsCount.get(connectedNode) >= 3) {
				usedRooms.remove(connectedNode);
			}
			
			// update available and used rooms
			availableRooms.remove(unconnectedNode);
			usedRooms.add(unconnectedNode);
			// no more rooms but still some door to create
			if (availableRooms.size() == 0 | usedRooms.size() == 0) {
				break;
			}
		}
		
		// compute degree of each node
		// we would like to have at most 3 out nodes per vertex (i.e., 3 doors for a room)
		/**
		 * need to check that while creating the connected graph we respect the 3 doors limit. Move above
		 */
		/**
		HashMap<Vector<LabRecruitsState>, Integer> roomsDoorsCount = new HashMap<Vector<LabRecruitsState>, Integer>();
		Iterator<Vector<LabRecruitsState>> iteratorUsedRoom =  usedRooms.iterator();
		while(iteratorUsedRoom.hasNext()) {
			Vector<LabRecruitsState> nextRoom = iteratorUsedRoom.next(); 
			roomsDoorsCount.put(nextRoom, doorsGraph.inDegreeOf(nextRoom));			
		}
		*/
		
		// create a set with all the possible transitions not yet used
		int unusedDoorId = 0;
		for(Vector<LabRecruitsState> src :  usedRooms) {
			for(Vector<LabRecruitsState> tgt :  usedRooms) {
				if (!src.equals(tgt)) {
					if (!completeGraph.containsEdge(src, tgt) & !doorsGraph.containsEdge(src, tgt) ) {
						if (!completeGraph.containsVertex(src)) {
							completeGraph.addVertex(src);
						}
						if (!completeGraph.containsVertex(tgt)) {
							completeGraph.addVertex(tgt);
						}					
						completeGraph.addEdge(src, tgt,unusedDoorId);
						unusedDoorId = unusedDoorId + 1;
					}					
				}
			}
		}
				
		// all rooms are used but there are still some available door: random add doors between random rooms
		while (doorsGraph.edgeSet().size() < nDoors & completeGraph.edgeSet().size() > 0) {
			// pick an edge from complete graph
			int nextTransition = rndGenerator.nextInt(0, completeGraph.edgeSet().size()-1);
			Integer newEdge = completeGraph.edgeSet().stream().skip(nextTransition).iterator().next();
			// add to door graph
			doorsGraph.addEdge(completeGraph.getEdgeSource(newEdge), completeGraph.getEdgeTarget(newEdge), doorsGraph.edgeSet().size());
			
			// update number of doors per room count
			roomsDoorsCount.put(completeGraph.getEdgeSource(newEdge) , roomsDoorsCount.get(completeGraph.getEdgeSource(newEdge))+1);
			roomsDoorsCount.put(completeGraph.getEdgeTarget(newEdge) , roomsDoorsCount.get(completeGraph.getEdgeTarget(newEdge))+1);
			// if the new edge makes some room R having 3 doors, remove from complete graph all edges with R 
			Set<Integer> edgesToRemove = new HashSet<Integer>();
			if (roomsDoorsCount.get(completeGraph.getEdgeSource(newEdge)) >= 3) {
				edgesToRemove.addAll(completeGraph.edgesOf(completeGraph.getEdgeSource(newEdge)));		
			}
			if (roomsDoorsCount.get(completeGraph.getEdgeTarget(newEdge)) >= 3) {
				edgesToRemove.addAll(completeGraph.edgesOf(completeGraph.getEdgeTarget(newEdge)));
			}	
			Iterator<Integer> edgesToRemoveIterator = edgesToRemove.iterator(); 
			while(edgesToRemoveIterator.hasNext()) {
				Integer nextToRemove = edgesToRemoveIterator.next();
				completeGraph.removeEdge(nextToRemove);
			}
			// remove from complete graph
			completeGraph.removeEdge(newEdge);

		}
	
		// all doors are used but there are still some room: merge remaining rooms with used rooms
		// remove unused rooms
		for (Vector<LabRecruitsState> room : availableRooms) {
			doorsGraph.removeVertex(room);
		}
		// add unused buttons to already connected rooms
		/*
		 * for(Vector<LabRecruitsState> room : availableRooms) { int toMergeId =
		 * rndGenerator.nextInt(0, doorsGraph.vertexSet().size() - 1);
		 * doorsGraph.vertexSet().stream().skip(toMergeId).iterator().next().addAll(room
		 * ); }
		 */
		// all rooms and doors are used: exit
		return(doorsGraph);
	}


	/**
	 * Convert a doors graph to an EFSM. 
	 * For each node of the doors graph create a totally connected graph of buttons. For each edge of
	 * the  doors graph, create two connected door nodes in the EFMS. Each door node represents the side
	 * of a door, and has to be totally connected with the buttons in the respective room.
	 * @return an EFSM
	 */
	private EFSM<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext, 
		Transition<LabRecruitsState, LabRecruitsParameter, LabRecruitsContext>> doorsGraphToEFSM() {
		
		// create the EFSM builder
		EFSMBuilder	labRecruitsBuilder = new EFSMBuilder(EFSM.class);
		
	
		// iterate over vertex (rooms) of the door graph and create complete subgraphs
		Iterator<Vector<LabRecruitsState>> vertexIterator = doorsGraph.vertexSet().iterator();
		while(vertexIterator.hasNext()) {
			// build a complete graph in the EFSM with 
			// free transition between different buttons and
			// toggle transition (self-loop) 
			Vector<LabRecruitsState> room =  vertexIterator.next();
			for (int i = 0; i < room.size(); i++) {
				for (int j = 0; j < room.size(); j++) {
					if (room.get(i).equals(room.get(j))) {
						labRecruitsBuilder.withTransition(room.get(i), room.get(i), new LabRecruitsToggleTransition());
					}else {
						labRecruitsBuilder.withTransition(room.get(i), room.get(j), new LabRecruitsFreeTravelTransition());
					}
				}
			}
		}
		
		// iterate over edges (doors) of the door graph and connect with buttons connected subgraph
		Iterator<Integer> edgeInterator = doorsGraph.edgeSet().iterator();
		while(edgeInterator.hasNext()) {
			Integer edge = edgeInterator.next();
			// create door states (one for each side of the door)
			LabRecruitsState d_m = new LabRecruitsState("d"+edge.toString()+"-","door"+edge.toString());
			LabRecruitsState d_p = new LabRecruitsState("d"+edge.toString()+"+","door"+edge.toString());
			// add transition connecting the doors
			labRecruitsBuilder.withTransition(d_m, d_p, new LabRecruitsDoorTravelTransition());
			labRecruitsBuilder.withTransition(d_p, d_m, new LabRecruitsDoorTravelTransition());		
			// get the source of the edge, i.e, all the buttons in the room
			Vector<LabRecruitsState> sourceRoom = doorsGraph.getEdgeSource(edge);
			// connect d_m with each button in the source room
			for(int i =0; i < sourceRoom.size(); i++) {
				labRecruitsBuilder.withTransition(sourceRoom.get(i), d_m, new LabRecruitsFreeTravelTransition());
				labRecruitsBuilder.withTransition(d_m,sourceRoom.get(i), new LabRecruitsFreeTravelTransition());
			}
			// get the target of the edge
			Vector<LabRecruitsState> targetRoom = doorsGraph.getEdgeTarget(edge);
			// check if target room is different to the source room
			if (!targetRoom.equals(sourceRoom)) {
				for(int i =0; i < targetRoom.size(); i++) {
					labRecruitsBuilder.withTransition(targetRoom.get(i), d_p, new LabRecruitsFreeTravelTransition());
					labRecruitsBuilder.withTransition(d_p,targetRoom.get(i), new LabRecruitsFreeTravelTransition());
				}
			}			
		}
		
		// doors within the same room need to be connected by a free travel transition
		vertexIterator = doorsGraph.vertexSet().iterator();
		while(vertexIterator.hasNext()) {
			Vector<LabRecruitsState> room =  vertexIterator.next();

			// pick doors in the room
			List<LabRecruitsState> doorList = new ArrayList<LabRecruitsState>();
			
			// get in doors
			Set<Transition> inTransition = labRecruitsBuilder.incomingTransitionsOf(room.get(0));
			Iterator<Transition> tr = inTransition.iterator();
			while(tr.hasNext()) {
				LabRecruitsState state = (LabRecruitsState) tr.next().getSrc();
				if (state.hasDoor()) {
					doorList.add(state);
				}
			}
				
			/*
			 * 
			 * Set<Transition> outTransition =
			 * labRecruitsBuilder.outgoingTransitionsOf(room.get(0)); tr =
			 * outTransition.iterator(); while(tr.hasNext()) { LabRecruitsState state =
			 * (LabRecruitsState) tr.next().getTgt(); if (state.hasDoor()) {
			 * doorList.add(state); } }
			 */


			// connect all the doors in doorList
			for (int i = 0; i < doorList.size(); i++)
				for (int j = 0; j < doorList.size(); j++) {
					if (i != j) {
						labRecruitsBuilder.withTransition(doorList.get(i), doorList.get(j),
								new LabRecruitsFreeTravelTransition());
					}
				}
		}
		
		
		// add context, i.e., set which button activate which door
		// to build the context we start from the door graph
		// we build the context in such a way from a random starting room
		// there is a path to each other rooms
		
		// iterator over vertex set
		vertexIterator = doorsGraph.vertexSet().iterator();		
		if (vertexIterator.hasNext()) {
			// pick initial room
			Vector<LabRecruitsState> firstRoom = vertexIterator.next();
			// pick initial EFSM state from the first room
			LabRecruitsState initialState = firstRoom.get(rndGenerator.nextInt(0, firstRoom.size() - 1));

			// iterate over door graph and store door-button pairs while traveling	
			BFSShortestPath<Vector<LabRecruitsState>, Integer> shortestPath = new BFSShortestPath<Vector<LabRecruitsState>, Integer>(
					doorsGraph);
			
			HashMap<Integer,Set<LabRecruitsState>> doorButtonsMap = new HashMap<Integer,Set<LabRecruitsState>>();
			
			// keep track of the connected doors
			Set<Integer> connectedDoors = new HashSet<Integer>();
			
			while (vertexIterator.hasNext()) {
				Vector<LabRecruitsState> availableButtons = new Vector<LabRecruitsState>(firstRoom);
				Vector<LabRecruitsState> nextRoom = vertexIterator.next();
				
				// get the shortest path between initial room and the current room
				GraphPath<Vector<LabRecruitsState>, Integer> path = shortestPath.getPath(firstRoom, nextRoom);
				ArrayList<Vector<LabRecruitsState>> traversedRooms = (ArrayList<Vector<LabRecruitsState>>) path.getVertexList();		
				List<Integer> traversedDoors = path.getEdgeList();
				traversedRooms.remove(0);
				
				
				for(Integer currentDoor : traversedDoors) {
					connectedDoors.add(currentDoor);
					LabRecruitsState currentButton = availableButtons.get(rndGenerator.nextInt(0, availableButtons.size() - 1));
					Set<LabRecruitsState> newSet = new HashSet<LabRecruitsState>();
					if (doorButtonsMap.containsKey(currentDoor)) {		
						newSet.addAll(doorButtonsMap.get(currentDoor));
					}
					newSet.add(currentButton);
					doorButtonsMap.put(currentDoor, newSet);
					
					availableButtons.addAll(traversedRooms.get(0));
					traversedRooms.remove(0);
				}
			}
			
			// not all doors are connected to a button
			// complete with random mapping
			Set<Integer> missingDoors = new HashSet<Integer>(doorsGraph.edgeSet());
			missingDoors.removeAll(connectedDoors);			
			for(Integer missingDoor: missingDoors) {
				Vector<LabRecruitsState> randomRoom = doorsGraph.vertexSet().stream().skip(rndGenerator.nextLong(0, doorsGraph.vertexSet().size()-1)).iterator().next();
				LabRecruitsState randomButtom = randomRoom.get(rndGenerator.nextInt(0, randomRoom.size()-1));
				
				Set<LabRecruitsState> newSet = new HashSet<LabRecruitsState>();
				if (doorButtonsMap.containsKey(missingDoor)) {		
					newSet.addAll(doorButtonsMap.get(missingDoor));
				}
				newSet.add(randomButtom);
				doorButtonsMap.put(missingDoor, newSet);
				
			}
			
			// create context
			LabRecruitsDoor[] doorsList = new LabRecruitsDoor[doorButtonsMap.keySet().size()];
			int i = 0;
			for(Integer door: doorButtonsMap.keySet()) {
				LabRecruitsDoor newDoor = new LabRecruitsDoor("door"+door.toString(),new HashSet<String>());
				for(LabRecruitsState b : doorButtonsMap.get(door)) {
					newDoor.addButton(b.getId());
				}
				doorsList[i] = newDoor;
				i++;
			}
			
			LabRecruitsContext initialContext = new LabRecruitsContext(doorsList);
			
		
			return (labRecruitsBuilder.build(initialState, initialContext, this.parameterGenerator));
		}
		
		return(null);
		
	}	
	
	/**
	 * Generate the level as a text file using Wishnu code
	 * @throws IOException 
	 */
	private String generateCSV() {
		
		
		List<Room> csvRooms = new LinkedList<>() ;			
		List<Button> buttonList = new LinkedList<>();		
		List<Door> doorList = new LinkedList<>();
		
		HashMap< Vector<LabRecruitsState> , Integer> GraphRoomToCsvRoom = new HashMap<Vector<LabRecruitsState>, Integer>();
		
		LabRecruitsContext doorButtonMap = efsm.getConfiguration().getContext();
		LabRecruitsState efsmInitialState = efsm.getInitialConfiguration().getState();
		
		HashMap<String, Button> buttonDoorMap = new HashMap<String,Button>();
				
				
		//HashMap<Integer, String> buttonList = new  HashMap<Integer, String>();
		//HashMap<Integer, Integer> doorList = new  HashMap<Integer, Integer>();
		
		int roomId = 0;
		
		// iterate over doors graph vertex 
		Iterator<Vector<LabRecruitsState>> vertexIterator = doorsGraph.vertexSet().iterator();
		while (vertexIterator.hasNext()) {
			Vector<LabRecruitsState> doorsGraphState = vertexIterator.next();
			Room room = new Room(roomId);
			// add buttons
			for(LabRecruitsState s : doorsGraphState ) {
				if (s.equals(efsmInitialState)) {
					room.placeAgent("Agent1");
				}
				Button b = room.addButton(s.getId());
				buttonList.add(b);
				buttonDoorMap.put(s.getId(),b);
			}
			csvRooms.add(room);
			GraphRoomToCsvRoom.put(doorsGraphState, roomId);
			roomId = roomId + 1;
			
		}
		
		// iterate over door graph edges
		Iterator<Integer> edgeIterator = doorsGraph.edgeSet().iterator();
		while (edgeIterator.hasNext()) {
			
			Integer doorsGraphEdge = edgeIterator.next();
			LabRecruitsDoor doorsGraphDoor = doorButtonMap.getDoor("door"+doorsGraphEdge.toString());
			HashSet<String> doorsGraphButtons = doorsGraphDoor.getButtons();
			
			Vector<LabRecruitsState> source = doorsGraph.getEdgeSource(doorsGraphEdge);
			Vector<LabRecruitsState> dest = doorsGraph.getEdgeTarget(doorsGraphEdge);
			
			Corridor corridor = Corridor.connect( csvRooms.get(GraphRoomToCsvRoom.get(source)) , csvRooms.get(GraphRoomToCsvRoom.get(dest)) );
			Door door = corridor.guard("door"+doorsGraphEdge.toString());
						
			Iterator<String> doorsGraphButtonsIterator = doorsGraphButtons.iterator();
			while(doorsGraphButtonsIterator.hasNext()) {			
				door.operatedBy(buttonDoorMap.get(doorsGraphButtonsIterator.next()));
			}						
			doorList.add(door);
		}
		
		// build the planar graph
		Layout layout = Layout.drawLayoutWithRetries(csvRooms);
		if (layout != null) {
			RendererToLRLevelDef renderer = new RendererToLRLevelDef();
			String stringLayout = renderer.renderLayout(Room.collectButtons(csvRooms), layout);
			return stringLayout;
		}else {
			return("");
		}
	}
		
	/** 
	 * Export door graph to graphml
	 * @param scenarioId
	 * @throws IOException
	 * @throws ExportException 
	 */
	public void saveDoorGraph(String scenarioId) throws IOException, ExportException  {
		// JgraphT 1.5.0
		//GraphMLExporter<Vector<LabRecruitsState>, Integer> gExporter = new GraphMLExporter();
		//gExporter.setExportEdgeWeights(true);
		//gExporter.setExportEdgeLabels(true);
		//gExporter.setExportVertexLabels(true);
		
		Function<Vector<LabRecruitsState>,String> vertexLabeler = (vertex) -> {
			String out = "";
			for (LabRecruitsState state : vertex) {
				out = out + " " + state.getId();
			}
			return (out);
		};
		
		Function<Integer, String> edgeLabeler = (edge) -> {
			return("d"+edge.toString());
		};
		
		GraphMLExporter<Vector<LabRecruitsState>, Integer> gExporter = 
				new GraphMLExporter(
						new IntegerComponentNameProvider<>(),
						v -> vertexLabeler.apply( (Vector<LabRecruitsState>) v),
						new IntegerComponentNameProvider<>(),
						e -> edgeLabeler.apply((Integer) e));

		Writer file = new FileWriter(scenarioId+"_doors.xml");

		gExporter.exportGraph(doorsGraph, file);
	}

	/**
	 * Export efsm to a dot file
	 * @param scenarioID
	 * @throws IOException 
	 */
	public void saveEFSMtoDot(String scenarioID) throws IOException {
		if (efsm != null) {
			Function<LabRecruitsState, String> stateLabeler = (state) -> { return(state.getId()); };
			Function<Transition, String> edgeLabeler = (edge) -> { return(""); };
			
			EFSMDotExporter dotExporter = new EFSMDotExporter(efsm,stateLabeler,edgeLabeler);
			
			
			Path outFile = Paths.get(scenarioID+"_efsm.dot");
			dotExporter.writeOut(outFile);
		}
	}
	
	/*
	 * Save the csv playable with Lab Recruits
	 */
	public void saveLabRecruitsLevel(String scenarioID) throws IOException{
		Path outFile = Paths.get(scenarioID+"_LR.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.toString()));
		writer.write(this.csvLevel);
        writer.close();
	}
	
}
 