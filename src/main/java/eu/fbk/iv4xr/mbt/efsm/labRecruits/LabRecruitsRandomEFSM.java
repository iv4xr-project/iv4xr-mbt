package eu.fbk.iv4xr.mbt.efsm.labRecruits;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.Pseudograph;
//import org.jgrapht.io.*;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMDotExporter;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransitionMapper;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
//import de.upb.testify.efsm.*;
//import eu.fbk.se.labrecruits.*;
//import eu.fbk.iv4xr.mbt.efsm4j.*;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Button;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Corridor;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Door;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.GoalFlag;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Room;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.RendererToLRLevelDef;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Layout;


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
	
	public enum StateType { Button, GoalFlag, DoorSide };
	
	// Random seed to generate level layout
	private long seed = MBTProperties.LR_seed;
	
	// Total number of buttons
	private int nButtons = MBTProperties.LR_n_buttons;
	
	// Total number of doors
	private int nDoors = MBTProperties.LR_n_doors;
	
	// Expected number of buttons per room
	// Buttons per room are selected following a Poisson
	private double meanButtonsPerRoom = MBTProperties.LR_mean_buttons;
	
	// Number of rooms
	private int nRooms = MBTProperties.LR_n_rooms;
	
	// Number of flags
	private int nGoalFlags = MBTProperties.LR_n_goalFlags;
	
	// random number generator (using Mersenne Twister rng) 
	private RandomDataGenerator rndGenerator = new RandomDataGenerator(new MersenneTwister(seed));
	
	// door graph
	private Pseudograph<Vector<EFSMState>,Integer>  doorsGraph = new Pseudograph<>(Integer.class);
	
	// final efsm
	private EFSM efsm = null; // to fix
	
	// csv version to feed Lab Recruits
	private String csvLevel = "";
	
	// mutations
	private List<String> removeMutations = new LinkedList<String>();
	private List<String> addMutations = new LinkedList<String>();
	
	
	// map from doors to the set of activating button
	HashMap<Integer, Set<EFSMState>> doorButtonsMap;
	

	// store mutants of the csv version of the level
	// mutant where a link between a door and a button is removed
	//private LinkedList<String> removeLinkMutants = new LinkedList<String>();
	// mutant where a link between a door and a button is added
	//private LinkedList<String> addLinkMutants = new LinkedList<String>();
	
	// parameters generator
	//LabRecruitsParameterGenerator parameterGenerator = new LabRecruitsParameterGenerator();
		
	// Default parameters

	public LabRecruitsRandomEFSM() {
				
		int nTry = MBTProperties.LR_n_try_generation;
		while(this.csvLevel == "" & nTry > 0) {
			// generate a list of rooms
			// each room is a vector of buttons
			List<Vector<EFSMState>> roomSet = generateRoomSet();	

			// add goal flags, if needed
			if (nGoalFlags > 0) {
			 	roomSet = addGoalFlags(roomSet);
			}
			
			// the number of rooms are not predefined
			// int nRooms = roomSet.size(); // not needed?
			// connect rooms with doors
			// the problem is equivalent to the creation of a connected graph
			this.doorsGraph = generatePlanarDoorsGraph(roomSet);
	
			
			// compute the embedding, if possible
			// if the graph is not planar it is not possible to generate csv
			BoyerMyrvoldPlanarityInspector planaryInspector = new BoyerMyrvoldPlanarityInspector(this.doorsGraph);
			if (planaryInspector.isPlanar()) {
				this.doorsGraph = (Pseudograph<Vector<EFSMState>, Integer>) planaryInspector.getEmbedding().getGraph();
				// DEBUG saveDoorGraph("data/test_"+nTry+".xml");
				// test if doorsGraph is effectively planar
				// need to update JGraphT
				
				// expand the doors graph to the corresponding EFSM
				// and add button-doors map
				this.efsm = doorsGraphToEFSM();
				
				// create the csv version that can be played
				this.csvLevel = generateCSV();	
			}else if (nTry == 1) {
				this.efsm = doorsGraphToEFSM();
			}
			nTry = nTry - 1;
		}
		
		if (this.csvLevel != "") {
			// create mutants by removing or adding button-door links
			//generateMutants();
		}
	}



	
	/**
	 * Add goal flags to rooms
	 * @param roomSet
	 * @return
	 */
	private List<Vector<EFSMState>> addGoalFlags(List<Vector<EFSMState>> roomSet) {
		
		
		if (nGoalFlags > 0) {
			int nConsumedGoalFlags = 0;
			while (nConsumedGoalFlags < nGoalFlags) {
				// generate a flag state
				EFSMState goalFlagState = new EFSMState(nameGoalFlag(nConsumedGoalFlags));
				// random choose a room
				int roomId = rndGenerator.getRandomGenerator().nextInt(roomSet.size());
				// add the flag
				roomSet.get(roomId).add(goalFlagState);
				// increment consumed goal flags
				nConsumedGoalFlags +=1;
			}
			return roomSet;
		}else {
			return roomSet;
		}
		
	}
	

	// NOTE: parameters are in MBTProperties and we could avoid changing them,
	//       but we keep for testing purposes
	// get and set parameters 	
	public int get_nButtons() {
		return(nButtons);
	}
	/*
	public void set_nButtons(int nB) {
		nButtons = nB;
	}
	*/
	public int get_nDoors() {
		return(nDoors);
	}
	/*
	public void set_nDoors(int nD) {
		nDoors = nD;
	}
	*/
	public double get_meanButtonsPerRoom() {
		return(meanButtonsPerRoom);
	}
	/*
	public void set_meanButtonsPerRoom(double mBR) {
		if (mBR >0 ) {
			meanButtonsPerRoom = mBR;
		}		
	}
	*/
	public int get_nRooms() {
		return(nRooms);
	}
	
	public long get_seed() {
		return(seed);
	}
	/*
	public void set_seed(long newSeed) {
		seed = newSeed;
		rndGenerator.reSeed(newSeed);
	}
	*/
	public String get_csv() {
		return csvLevel;
	}
	

	/**
	 * Return the random level 
	 * @return and EFSM
	 */
	public EFSM getEFMS()  {	
		return efsm;
	}
	
	/*
	 * Return remove mutations
	 */
	public List<String> getRemoveMutations(){
		return removeMutations;
	}
	
	/*
	 * Return add mutations
	 */
	public List<String> getAddMutations(){
		return addMutations;
	}
	
	public String getAnml () {
		StringBuffer anml = new StringBuffer();
		String anmlDomainFile = "anml_templates/lab_recruits/buttons_doors.anml";
		
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(anmlDomainFile);
		try {
			String content = new String(inputStream.readAllBytes());
			anml.append(content);
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String anmlInstance;
		if (MBTProperties.MODELCRITERION[0] == ModelCriterion.STATE) {
			anmlInstance = getAnmlInstanceMultiGoal(ModelCriterion.STATE);
		}else if (MBTProperties.MODELCRITERION[0] == ModelCriterion.TRANSITION) {
			anmlInstance = getAnmlInstanceMultiGoal(ModelCriterion.TRANSITION);
		}else {
			throw new RuntimeException("PlanningBasedGenerator does not support cirterion: " + MBTProperties.MODELCRITERION[0]);
		}
		anml.append(anmlInstance);
		return anml.toString();
	}
	
	/**
	 * Return the model in ANML language. The domain modeling part is to be reused from the current manual implementation.
	 * @return string representation of the ANML problem intance
	 */
	public String getAnmlInstanceSingleGoal () {
		StringBuffer anml = new StringBuffer();
		
		// prepare the data necessary
		// set of buttons, doors, mapping (opens)
		
		// doors are edges
		Set<String> doors = new HashSet<>();
		Set<String> doorSideNames = new HashSet<>();
		for (Integer e : doorsGraph.edgeSet()) {
			doors.add(nameDoor(e));
			doorSideNames.add(nameDoorSide(e, true));
			doorSideNames.add(nameDoorSide(e, false));
		}
		
		// buttons are vertices
		Set<String> buttons = new HashSet<>();
		for (Vector<EFSMState> v : doorsGraph.vertexSet()){
			for (EFSMState b : v) {
				buttons.add(b.getId());
			}
		}
		
		// button opens door
		Map<String, Set<String>> door2Button = new HashMap<>();
		for (Entry<Integer, Set<EFSMState>> entry : doorButtonsMap.entrySet()) {
			Set<String> btns = new HashSet<String>();
			for (EFSMState btn : entry.getValue()) {
				btns.add(btn.getId());
			}
			door2Button.put(nameDoor(entry.getKey()), btns);
		}
		
		// some strings
		String buttonType = "Button";
		String doorType = "Door";
		String locationType = "Location";
		
		//////////////////////////////////////////////////////
		
		// buttons
		anml.append("\ninstance " + buttonType + " ");
		for (String bn : buttons) {
			anml.append(" " + bn+ ",");
		}
		// replace last , by ;
			replaceLastCommaBySemicolon(anml);
		
		
		// doors
//		Set<String> doorSideNames = new HashSet<String>();
		anml.append("\ninstance " + doorType + " ");
		for (String doorName : doors) {
			anml.append(" " + doorName + ",");
		}
		// replace last , by ;
		replaceLastCommaBySemicolon(anml);
		
		
		// button locations
		anml.append("\ninstance " +  locationType + " ");
		for (String bn : buttons) {
			anml.append(locationName (bn) + ",");
		}
		replaceLastCommaBySemicolon(anml);
		
		
		// door side locations
		anml.append("\ninstance " +  locationType + " ");
		for (String dsn : doorSideNames) {
			anml.append(locationName (dsn) + ",");
		}
		replaceLastCommaBySemicolon(anml);
		
		
		// initialize the constants
		for (String bn : buttons) {
			anml.append("\nbutton_location("+ bn +") := " + locationName (bn) + ";");
		}
		

		for (Entry<String, Set<String>> entry : door2Button.entrySet()) {
			for (String b : entry.getValue()) {
				anml.append("\nconnected("+ b  + "," + entry.getKey() +") := true;");
			}
		}
		anml.append("\nconnected(*) := false;");
		
		
		// add free (non-guarded) transitions/movements
		for (Object t : efsm.getTransitons()) {
			EFSMTransition trans = (EFSMTransition)t;
			if (!trans.isSelfTransition() && trans.getGuard() == null) {
				anml.append("\nreachable("+ locationName (trans.getSrc().getId()) + "," + locationName (trans.getTgt().getId()) +") := true;");
			}
		}
		anml.append("\nreachable(*) := false;");
		
		for (Integer d : doorButtonsMap.keySet()) {
			String s1 = locationName (nameDoorSide(d, true));
			String s2 = locationName (nameDoorSide(d, false));
			String dn = nameDoor (d);
			anml.append("\ndoor_connection("+ s1 + ", " + s2 +") := " + dn + ";");
			anml.append("\ndoor_connection("+ s2 + ", " + s1 +") := " + dn + ";");
		}
		anml.append("\ndoor_connection(*) := NONE;");
		
		
		
		// This is the initial state description (the agent is in b0 and all
		// doors are closed)
		String b0 = efsm.getInitialConfiguration().getState().getId(); // nameButton(0);
		anml.append("\n[start] position := " + locationName (b0) + ";");
		
		for (String d : door2Button.keySet()) {
			anml.append("\n[start] open(" + d + ") := false;");
		}
		anml.append("\n[start] open(NONE) := false;");
		
		// This is the goal of the planning problem: we want to eventually
		// reach location T
		
		// reach all states (buttons and doors)?
//		for (Object s : efsm.getStates()) {
//			String target = locationName (((EFSMState)s).getId());
//			anml.append("\n[end] position == " + target + ";");
//		}
		
		String target = locationName (nameDoorSide(doors.size()-1, true));
		anml.append("\n[end] position == " + target + ";");
		return anml.toString();
	}
	
	
	/**
	 * Return the model in ANML language. The domain modeling part is to be reused from the current manual implementation.
	 * @param criterion 
	 * @return string representation of the ANML problem intance
	 */
	public String getAnmlInstanceMultiGoal (ModelCriterion criterion) {
		StringBuffer anml = new StringBuffer();
		
		// prepare the data necessary
		// set of buttons, doors, mapping (opens)
		
		// doors are edges
		Set<String> doors = new HashSet<>();
		Set<String> doorSideNames = new HashSet<>();
		for (Integer e : doorsGraph.edgeSet()) {
			doors.add(nameDoor(e));
			doorSideNames.add(nameDoorSide(e, true));
			doorSideNames.add(nameDoorSide(e, false));
		}
		
		// buttons are vertices
		Set<String> buttons = new HashSet<>();
		for (Vector<EFSMState> v : doorsGraph.vertexSet()){
			for (EFSMState b : v) {
				buttons.add(b.getId());
			}
		}
		
		// button opens door
		Map<String, Set<String>> door2Button = new HashMap<>();
		for (Entry<Integer, Set<EFSMState>> entry : doorButtonsMap.entrySet()) {
			Set<String> btns = new HashSet<String>();
			for (EFSMState btn : entry.getValue()) {
				btns.add(btn.getId());
			}
			door2Button.put(nameDoor(entry.getKey()), btns);
		}
		
		// some strings
		String buttonType = "Button";
		String doorType = "Door";
		String locationType = "Location";
		
		//////////////////////////////////////////////////////
		
		// buttons
		anml.append("\ninstance " + buttonType + " ");
		for (String bn : buttons) {
			anml.append(" " + bn+ ",");
		}
		// replace last , by ;
			replaceLastCommaBySemicolon(anml);
		
		
		// doors
//		Set<String> doorSideNames = new HashSet<String>();
		anml.append("\ninstance " + doorType + " ");
		for (String doorName : doors) {
			anml.append(" " + doorName + ",");
		}
		// replace last , by ;
		replaceLastCommaBySemicolon(anml);
		
		
		// button locations
		anml.append("\ninstance " +  locationType + " ");
		for (String bn : buttons) {
			anml.append(locationName (bn) + ",");
		}
		replaceLastCommaBySemicolon(anml);
		
		
		// door side locations
		anml.append("\ninstance " +  locationType + " ");
		for (String dsn : doorSideNames) {
			anml.append(locationName (dsn) + ",");
		}
		replaceLastCommaBySemicolon(anml);
		
		
		// initialize the constants
		for (String bn : buttons) {
			anml.append("\nbutton_location("+ bn +") := " + locationName (bn) + ";");
		}
		

		for (Entry<String, Set<String>> entry : door2Button.entrySet()) {
			for (String b : entry.getValue()) {
				anml.append("\nconnected("+ b  + "," + entry.getKey() +") := true;");
			}
		}
		anml.append("\nconnected(*) := false;");
		
		
		// add free (non-guarded) transitions/movements
		for (Object t : efsm.getTransitons()) {
			EFSMTransition trans = (EFSMTransition)t;
			if (!trans.isSelfTransition() && trans.getGuard() == null) {
				anml.append("\nreachable("+ locationName (trans.getSrc().getId()) + "," + locationName (trans.getTgt().getId()) +") := true;");
			}
		}
		anml.append("\nreachable(*) := false;");
		
		for (Integer d : doorButtonsMap.keySet()) {
			String s1 = locationName (nameDoorSide(d, true));
			String s2 = locationName (nameDoorSide(d, false));
			String dn = nameDoor (d);
			anml.append("\ndoor_connection("+ s1 + ", " + s2 +") := " + dn + ";");
			anml.append("\ndoor_connection("+ s2 + ", " + s1 +") := " + dn + ";");
		}
		anml.append("\ndoor_connection(*) := NONE;");
		
		
		
		// This is the initial state description (the agent is in b0 and all
		// doors are closed)
		String b0 = efsm.getInitialConfiguration().getState().getId(); // nameButton(0);
		anml.append("\n[start] position := " + locationName (b0) + ";");
		
		anml.append("\n[start] forall(Location l) { location_visited(l) := false; };");
		anml.append("\n[start] forall(Location l1, Location l2) { edge_visited(l1, l2) := false; };");
		anml.append("\n[start] forall(Door d) { open(d) := false; };");
		
		
		if (criterion == ModelCriterion.STATE) {
			// This is the goal of the planning problem: we want to eventually
			// visit all the locations
			anml.append("\n[end] forall(Location l) { location_visited(l) == true; };");
		}else if (criterion == ModelCriterion.TRANSITION) {
			// This is the goal of the planning problem: we want to eventually
			// visit all the edges
			anml.append("\n[end] forall(Location l1, Location l2) { (not (reachable(l1, l2) or door_connection(l1, l2) != NONE)) or edge_visited(l1, l2) == true; };");
		}else {
			throw new RuntimeException("Unsupported criterion: " + criterion);
		}

		return anml.toString();
	}
	
	
	private String locationName (String entityName) {
		return "l_" + entityName;
	}
	
	/**
	 * Utility method
	 * @param anml
	 */
	private void replaceLastCommaBySemicolon(StringBuffer anml) {
		anml.setCharAt(anml.length() - 1, ';');
	}
	
	/*
	 * Auxiliary function to name a button
	 */
	private String nameButton(Integer i) {
		return ( "b"+Integer.toString(i));
	}
	
	
	/*
	 * Auxiliary function to name a goal flag
	 */
	private String nameGoalFlag(Integer i) {
		return ( "gf"+Integer.toString(i));
	}
	
	
	/*
	 * Auxiliary function to name a door
	 */
 	private String nameDoor(Integer i) {
		return ( "door"+Integer.toString(i));
	}
	
	/*
	 * Auxiliary function to name a door side
	 */
	private String nameDoorSide(Integer i, Boolean side) {
		if (side) {
			return("d"+i+"p");
		}else {
			return("d"+i+"m");
		}
	}
	

	public static StateType getStateType(EFSMState eState) {
		
		char firstChar = eState.getId().charAt(0);
		
		if (firstChar == 'b') {
			return StateType.Button;
		}
		
		if (firstChar == 'd') {
			return StateType.DoorSide;
		}
		
		if (firstChar == 'g') {
			return StateType.GoalFlag;
		}
		
		throw new RuntimeException("Cannot determine state type of state"+eState.getId());
	}
	
	
	/**
	 * Generate a room set accordingly with MBTProperty LR_random_mode
	 * @return A room set represented as a list of vectors. Each vector represents the buttons of a room.
	 */
	private List<Vector<EFSMState>> generateRoomSet(){
		switch (MBTProperties.LR_generation_mode) {
		case N_ROOMS_DEPENDENT:
			// n rooms is the dependent variable
			return generateRoomSetRoomDependent();
		case N_BUTTONS_DEPENDENT:
			return generateRoomSetButtonsDependent();
		default:
			// n rooms is the dependent variable
			return generateRoomSetRoomDependent();
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	private List<Vector<EFSMState>> generateRoomSetButtonsDependent(){
		// number of rooms that are not yet generated
		int availableRooms = nRooms;
		// buttons names id
		int currentButtonId = 0;
		// room set
		List<Vector<EFSMState>> roomSet = new ArrayList<Vector<EFSMState>>();
		// generate rooms
		while ( availableRooms > 0) {
			int nRoomButtons = (int)rndGenerator.nextPoisson(meanButtonsPerRoom) + 1;
			// generate a room 
			Vector<EFSMState> room = generateRoom(currentButtonId,nRoomButtons);
			roomSet.add(room);
			// update id for naming buttons
			currentButtonId = currentButtonId + nRoomButtons;	
			availableRooms = availableRooms - 1;
		}
		this.nButtons =currentButtonId+1;
		return roomSet;	
	}
	
	
	/**
	 * Generate a set of rooms given the number of buttons and the mean number buttons in a room. 
	 * @return a list of vectors of LabRectuitsState
	 */
	private List<Vector<EFSMState>> generateRoomSetRoomDependent(){		
		// buttons that need to be generated
		int availableButtons  = nButtons;
		// buttons names are in the form b_1, b_2, ...
		int currentButtonId = 0;
		// a room set is a list of rooms
		// a room is a Vector of LabRecruitsState
		List<Vector<EFSMState>> roomSet = new ArrayList<Vector<EFSMState>>();
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
			Vector<EFSMState> room = generateRoom(currentButtonId,nRoomButtons);
			roomSet.add(room);
			// update id for naming buttons
			currentButtonId = currentButtonId + nRoomButtons;			
		}		
		this.nRooms = roomSet.size();
		return roomSet;		
	}
	
	
	/**
	 * A room is a vector of buttons. Each button is named with an integer.
	 * @param currentButtonId
	 * @param nRoomButtons
	 * @return
	 */
	private Vector<EFSMState>  generateRoom(int currentButtonId, int nRoomButtons) {	
		Vector<EFSMState> room = new Vector<EFSMState>(nRoomButtons);	
		for (int i = 0; i < nRoomButtons; i++) {
			Integer buttonName =  currentButtonId + i;
			EFSMState button = new EFSMState( nameButton(buttonName));
			room.add(i,  button );
		}	
		return(room);		
	}
	
	/**
	 * Generate the door graph
	 * @param roomSet
	 * @return doors graph
	 */
	
	
	
	/*
	 * Generate the door graph trying to make it planar
	 * - no self loop
	 * - no multi-edges
	 * - at most 3 edges per node
	 * @param roomSet
	 * @return doors graph
	 */
	private Pseudograph<Vector<EFSMState>,Integer> generatePlanarDoorsGraph(List<Vector<EFSMState>> roomSet){
		
		Pseudograph<Vector<EFSMState>,Integer>  doorsGraph = new Pseudograph<>(Integer.class);
		Pseudograph<Vector<EFSMState>,Integer>  completeGraph = new Pseudograph<>(Integer.class);
		
		// add vertex, i.e., set of labrecruits states
		for (Vector<EFSMState> vector : roomSet) {
			doorsGraph.addVertex(vector);
		}
		
		
		
		// tmp values to store used and remaining rooms that need to be connected
		Set<Vector<EFSMState>> usedRooms =  new LinkedHashSet<>();
		// need to clone because vertexSet returns a view
		Set<Vector<EFSMState>> availableRooms = new LinkedHashSet<>(doorsGraph.vertexSet());
		
		// rooms and initial doors to make the graph connected is the same as generateDoorsGraph
		// need to reconcile 
		
		// pick first node and update used and available rooms
		//Vector<?> firstNode = (Vector<?>) availableRooms.toArray()[rndGenerator.nextInt(0, availableRooms.size()-1)];
		int firstNodeId = rndGenerator.nextInt(0, availableRooms.size()-1);
		Vector<EFSMState> firstNode = availableRooms.stream().skip(firstNodeId).iterator().next();
		availableRooms.remove(firstNode);
		usedRooms.add(firstNode);
		
		HashMap<Vector<EFSMState>, Integer> roomsDoorsCount = new HashMap<Vector<EFSMState>, Integer>();
		roomsDoorsCount.put(firstNode,0);
		
		// iterate over nDoors and add an edge each time selecting
		// one node from the used and from the available
		for (int i = 0; i < nDoors; i++) {
			// random pick an available and a used node
			int nextAvailableId = rndGenerator.nextInt(0, availableRooms.size()-1);
			Vector<EFSMState> unconnectedNode =  availableRooms.stream().skip(nextAvailableId).iterator().next();
			int nextUsedId = rndGenerator.nextInt(0, usedRooms.size()-1);
			Vector<EFSMState> connectedNode =  usedRooms.stream().skip(nextUsedId).iterator().next();
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
		for(Vector<EFSMState> src :  usedRooms) {
			for(Vector<EFSMState> tgt :  usedRooms) {
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
		for (Vector<EFSMState> room : availableRooms) {
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
	 * set which button activate which door to build the context. Starting from the
	 * door graph we build the context in such a way from a random starting room
	 * there is a path to each other rooms
	 */
	/*
	private HashMap<Integer, Set<EFSMState>> buttonDoorsMap() {

		HashMap<Integer, Set<EFSMState>> doorButtonsMap = new HashMap<Integer, Set<EFSMState>>();
		// iterator over doors graph vertex set (rooms)
		Iterator<Vector<EFSMState>> vertexIterator = doorsGraph.vertexSet().iterator();
		if (vertexIterator.hasNext()) {
			// pick initial room
			Vector<EFSMState> firstRoom = vertexIterator.next();
			// pick initial EFSM state from the first room
			EFSMState initialState = firstRoom.get(rndGenerator.nextInt(0, firstRoom.size() - 1));

			// iterate over door graph and store door-button pairs while traveling
			BFSShortestPath<Vector<EFSMState>, Integer> shortestPath = new BFSShortestPath<Vector<EFSMState>, Integer>(
					doorsGraph);

			// keep track of the connected doors
			Set<Integer> connectedDoors = new HashSet<Integer>();

			while (vertexIterator.hasNext()) {
				Vector<EFSMState> availableButtons = new Vector<EFSMState>(firstRoom);
				Vector<EFSMState> nextRoom = vertexIterator.next();

				// get the shortest path between initial room and the current room
				GraphPath<Vector<EFSMState>, Integer> path = shortestPath.getPath(firstRoom, nextRoom);
				ArrayList<Vector<EFSMState>> traversedRooms = (ArrayList<Vector<EFSMState>>) path.getVertexList();
				List<Integer> traversedDoors = path.getEdgeList();
				traversedRooms.remove(0);

				for (Integer currentDoor : traversedDoors) {
					connectedDoors.add(currentDoor);
					EFSMState currentButton = availableButtons
							.get(rndGenerator.nextInt(0, availableButtons.size() - 1));
					Set<EFSMState> newSet = new HashSet<EFSMState>();
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
			for (Integer missingDoor : missingDoors) {
				Vector<EFSMState> randomRoom = doorsGraph.vertexSet().stream()
						.skip(rndGenerator.nextLong(0, doorsGraph.vertexSet().size() - 1)).iterator().next();
				EFSMState randomButtom = randomRoom.get(rndGenerator.nextInt(0, randomRoom.size() - 1));

				Set<EFSMState> newSet = new HashSet<EFSMState>();
				if (doorButtonsMap.containsKey(missingDoor)) {
					newSet.addAll(doorButtonsMap.get(missingDoor));
				}
				newSet.add(randomButtom);
				doorButtonsMap.put(missingDoor, newSet);

			}

		}
		
		// now create a map between buttons and the set of doors they open
		HashMap<EFSMState, Set<Integer>> buttonDoorsMap = new HashMap<EFSMState, Set<Integer>>();
		
		return (doorButtonsMap);
	}
	 */
	
	/**
	 * Convert a doors graph to an EFSM. 
	 * For each node of the doors graph create a totally connected graph of buttons. For each edge of
	 * the  doors graph, create two connected door nodes in the EFMS. Each door node represents the side
	 * of a door, and has to be totally connected with the buttons in the respective room.
	 * @return an EFSM
	 */
	private EFSM doorsGraphToEFSM() {
		
		// create the EFSM builder
		EFSMBuilder	labRecruitsBuilder = new EFSMBuilder(EFSM.class);
		
		/*
		 * Input variables
		 * - need to be fixed to account for guard
		 */

		// toggle input parameter
		Var toggleVar = new Var<LRActions>("toggle", LRActions.TOGGLE);
		EFSMParameter inputParToggle = new EFSMParameter(toggleVar);		
		// toggle input parameter
		Var exploreVar = new Var<LRActions>("explore", LRActions.EXPLORE);
		EFSMParameter inputParExplore = new EFSMParameter(exploreVar);
		
		/*
		 * Context
		 */
		
		// Keep doors variables in an hashmap to use later for guards
		HashMap<Integer, EFSMGuard> doorsGuardMap = new HashMap<>();	
		// Create a trigger for each door
		HashMap<Integer,Assign> doorTriggerMap = new HashMap<>();
		// Keep a list with doors var to create the context
		List<Var<Boolean>> doorsVar = new ArrayList<>();
		// Iterate over doors graph edges
		Iterator<Integer> edgeInterator = doorsGraph.edgeSet().iterator();
		while(edgeInterator.hasNext()) {
			Integer edge = edgeInterator.next();
			String dname = nameDoor(edge);
			Var<Boolean> doorCheck = new Var<Boolean>(dname, false);
			doorsGuardMap.put(edge, new EFSMGuard(doorCheck) );
			doorTriggerMap.put(edge, new Assign(doorCheck, new BoolNot(doorCheck)));
			doorsVar.add(doorCheck);
			
		}
		EFSMContext context = new EFSMContext(doorsVar.toArray(new Var[doorsVar.size()]));
		
		
		/*
		 * Operations: need to map a button to the doors it open to use the appropriate operation
		 * when creating transitions. This step also identify initial state.
		 * Starting from the door graph we build the context in such a way from a random starting room
		 * there is a path to each other rooms
		 */
		//HashMap<Integer, Set<EFSMState>> doorButtonsMap = new HashMap<Integer, Set<EFSMState>>();
		doorButtonsMap = new HashMap<Integer, Set<EFSMState>>();
		EFSMState initialState = null;
		// iterator over doors graph vertex set (rooms)
		Iterator<Vector<EFSMState>> vertexIterator = doorsGraph.vertexSet().iterator();
		if (vertexIterator.hasNext()) {
			// pick initial room
			Vector<EFSMState> firstRoom = vertexIterator.next();
			// pick initial EFSM state from the first room
			initialState = firstRoom.get(rndGenerator.nextInt(0, firstRoom.size() - 1));

			// iterate over door graph and store door-button pairs while traveling
			BFSShortestPath<Vector<EFSMState>, Integer> shortestPath = new BFSShortestPath<Vector<EFSMState>, Integer>(
					doorsGraph);

			// keep track of the connected doors
			Set<Integer> connectedDoors = new HashSet<Integer>();

			while (vertexIterator.hasNext()) {
				Vector<EFSMState> availableButtons = new Vector<EFSMState>(firstRoom);
				Vector<EFSMState> nextRoom = vertexIterator.next();

				// get the shortest path between initial room and the current room
				GraphPath<Vector<EFSMState>, Integer> path = shortestPath.getPath(firstRoom, nextRoom);
				ArrayList<Vector<EFSMState>> traversedRooms = (ArrayList<Vector<EFSMState>>) path.getVertexList();
				List<Integer> traversedDoors = path.getEdgeList();
				traversedRooms.remove(0);

				for (Integer currentDoor : traversedDoors) {
					connectedDoors.add(currentDoor);
					// FIXME
					
					EFSMState currentButton = getRandomButton(availableButtons);
					
					Set<EFSMState> newSet = new HashSet<EFSMState>();
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
			for (Integer missingDoor : missingDoors) {
				Vector<EFSMState> randomRoom = doorsGraph.vertexSet().stream()
						.skip(rndGenerator.nextLong(0, doorsGraph.vertexSet().size() - 1)).iterator().next();
				//EFSMState randomButtom = randomRoom.get(rndGenerator.nextInt(0, randomRoom.size() - 1));
				EFSMState randomButton = getRandomButton(randomRoom);
				
				Set<EFSMState> newSet = new HashSet<EFSMState>();
				if (doorButtonsMap.containsKey(missingDoor)) {
					newSet.addAll(doorButtonsMap.get(missingDoor));
				}
				newSet.add(randomButton);
				doorButtonsMap.put(missingDoor, newSet);

			}

		}
		
		// now create a map between buttons and the set of doors they open
		HashMap<EFSMState, Set<Integer>> buttonDoorsMap = new HashMap<EFSMState, Set<Integer>>();
		for(Integer i: doorButtonsMap.keySet()) {
			Set<EFSMState> states = doorButtonsMap.get(i);
			for(EFSMState s : states) {
				if (buttonDoorsMap.keySet().contains(s)) {
					buttonDoorsMap.get(s).add(i);
					//buttonDoorsMap.put(s, buttonDoorsMap.get(s).add(i));
				}else {
					Set<Integer> doors = new HashSet<Integer>();
					doors.add(i);
					buttonDoorsMap.put(s, doors);
				}
			}
		}
		

		/*
		 * Start building the graph
		 */
		
		
		// For each vertex of the doors graph (that represent a room)
		// create complete subgraphs that connects each door and button
		vertexIterator = doorsGraph.vertexSet().iterator();
		while(vertexIterator.hasNext()) {
			// build a complete graph in the EFSM with 
			// free transition between different buttons and
			// toggle transition (self-loop) 
			Vector<EFSMState> room =  vertexIterator.next();
			for (int i = 0; i < room.size(); i++) {
				for (int j = 0; j < room.size(); j++) {
					if (room.get(i).equals(room.get(j)))  {
						if (getStateType(room.get(i)).equals(StateType.Button)) {
							EFSMTransition toggle = new EFSMTransition<>();
							toggle.setInParameter(inputParToggle);	
							if (buttonDoorsMap.containsKey(room.get(i))){
								List<Assign> doorTriggers = new ArrayList<>();
								for(Integer d: buttonDoorsMap.get(room.get(i))) {
									doorTriggers.add(doorTriggerMap.get(d));
								}
								EFSMOperation op = new EFSMOperation(doorTriggers.toArray(new Assign[doorTriggers.size()]));
								toggle.setOp(op);
							}
							labRecruitsBuilder.withTransition(room.get(i), room.get(i),toggle);
						}
					}else {
						EFSMTransition explore = new EFSMTransition<>();
						explore.setInParameter(inputParExplore);
						labRecruitsBuilder.withTransition(room.get(i), room.get(j), explore);
				//		labRecruitsBuilder.withTransition(room.get(i), room.get(j), new LabRecruitsFreeTravelTransition());
					}
				}
			}
			
			
		}
		
		
	
		
		// iterate over edges (doors) of the door graph and connect with buttons connected subgraph
		edgeInterator = doorsGraph.edgeSet().iterator();
		// keep track of the states that represent doors
		List<EFSMState> doorsList = new ArrayList<>();
		
		
		while(edgeInterator.hasNext()) {
			Integer edge = edgeInterator.next();
			// create door states (one for each side of the door)
			EFSMState d_m = new EFSMState(nameDoorSide(edge, false));
			EFSMState d_p = new EFSMState(nameDoorSide(edge, true));
			
			doorsList.add(d_m);
			doorsList.add(d_p);
			
			// add transition and guards connecting the doors 
			EFSMTransition explore_d_m_d_p = new EFSMTransition<>();
			explore_d_m_d_p.setGuard(doorsGuardMap.get(edge));
			explore_d_m_d_p.setInParameter(inputParExplore);
			labRecruitsBuilder.withTransition(d_m,d_p,explore_d_m_d_p);
			
			EFSMTransition explore_d_p_d_m = new EFSMTransition<>();
			explore_d_p_d_m.setGuard(doorsGuardMap.get(edge));
			explore_d_p_d_m.setInParameter(inputParExplore);
			labRecruitsBuilder.withTransition(d_p,d_m,explore_d_p_d_m);
	
			// get the source of the edge, i.e, all the buttons in the room
			Vector<EFSMState> sourceRoom = doorsGraph.getEdgeSource(edge);
			// connect d_m with each button in the source room
			for(int i =0; i < sourceRoom.size(); i++) {
				EFSMTransition explore1 = new EFSMTransition<>();
				explore1.setInParameter(inputParExplore);
				labRecruitsBuilder.withTransition(sourceRoom.get(i), d_m, explore1);
				EFSMTransition explore2 = new EFSMTransition<>();
				explore2.setInParameter(inputParExplore);
				labRecruitsBuilder.withTransition(d_m,sourceRoom.get(i), explore2);
			}
			// get the target of the edge
			Vector<EFSMState> targetRoom = doorsGraph.getEdgeTarget(edge);
			// check if target room is different to the source room
			if (!targetRoom.equals(sourceRoom)) {
				for(int i =0; i < targetRoom.size(); i++) {
					EFSMTransition explore1 = new EFSMTransition<>();
					explore1.setInParameter(inputParExplore);
					labRecruitsBuilder.withTransition(targetRoom.get(i), d_p, explore1);
					EFSMTransition explore2 = new EFSMTransition<>();
					explore2.setInParameter(inputParExplore);
					labRecruitsBuilder.withTransition(d_p,targetRoom.get(i), explore2);
				}
			}			
		}
		
		// doors within the same room need to be connected by a free travel transition
		vertexIterator = doorsGraph.vertexSet().iterator();
		while(vertexIterator.hasNext()) {
			Vector<EFSMState> room =  vertexIterator.next();

			// pick doors in the room
			List<EFSMState> localDoorList = new ArrayList<EFSMState>();
			
			// get in doors
			Set<EFSMTransition> inTransition = labRecruitsBuilder.incomingTransitionsOf(room.get(0));
			Iterator<EFSMTransition> tr = inTransition.iterator();
			while(tr.hasNext()) {
				EFSMState state = (EFSMState) tr.next().getSrc();
				if (doorsList.contains(state) & !localDoorList.contains(state)) {
					localDoorList.add(state);
				}
			}
				
			
			//Set<EFSMTransition> outTransition = labRecruitsBuilder.outgoingTransitionsOf(room.get(0));
			//tr = outTransition.iterator();
			//while (tr.hasNext()) {
			//	EFSMState state = (EFSMState) tr.next().getTgt();
			//	if (doorsList.contains(state) & !localDoorList.contains(state)) {
			//		localDoorList.add(state);
			//	}
			//}
			 


			// connect all the doors in doorList
			for (int i = 0; i < localDoorList.size(); i++)
				for (int j = 0; j < localDoorList.size(); j++) {
					if (i != j) {
						EFSMTransition explore1 = new EFSMTransition<>();
						explore1.setInParameter(inputParExplore);
						labRecruitsBuilder.withTransition(localDoorList.get(i), localDoorList.get(j), explore1);
						//EFSMTransition explore2 = new EFSMTransition<>();
						//explore2.setInParameter(inputParExplore);
						//labRecruitsBuilder.withTransition(localDoorList.get(j), localDoorList.get(i), explore2);
					}
				}
		}
		
	
		return(labRecruitsBuilder.build(initialState, context, new LRParameterGenerator()));
		
	}	
	
	// get random button from a room removing goal flg
	public EFSMState getRandomButton(Vector<EFSMState> room ) {
		
		EFSMState currentButton = room.get(rndGenerator.nextInt(0, room.size() - 1));
		while(!getStateType(currentButton).equals(StateType.Button)) {
			currentButton = room.get(rndGenerator.nextInt(0, room.size() - 1));
		}
		
		return currentButton;
	}
	
	/**
	 * Generate mutant levels removing and adding links between 
	 * doors and buttons
	 */
	/*public void generateMutants() {
		// initialize manger of LabRecruits csv
		LabRecruitMutationManager csvManager = new LabRecruitMutationManager(csvLevel);
		// extract the header where button-door map is defined
		csvManager.setButtonDoorsHeader();
		csvManager.fillButtonDoorsMap();
		csvManager.setLayout();
		csvManager.fillDoorSet();
		csvManager.fillButtonSet();
		csvManager.createRemoveMutations();
		removeMutations = csvManager.createRemoveMutations();
		addMutations = csvManager.createAddMutations(); 
	}*/
	
	/**
	 * Generate the level as a text file using Wishnu code
	 * @throws IOException 
	 */
	
	private String generateCSV() {
		
		
		List<Room> csvRooms = new LinkedList<>() ;			
		List<Button> buttonList = new LinkedList<>();
		List<GoalFlag> goalFlagList = new LinkedList<>();
		List<Door> doorList = new LinkedList<>();
		
		HashMap< Vector<EFSMState> , Integer> GraphRoomToCsvRoom = new HashMap<Vector<EFSMState>, Integer>();
		
		//EFSMContext doorButtonMap = efsm.getConfiguration().getContext();
		EFSMState efsmInitialState = efsm.getInitialConfiguration().getState();
		
		HashMap<EFSMState, Button> buttonDoorMap = new HashMap<EFSMState,Button>();
								
		//HashMap<Integer, String> buttonList = new  HashMap<Integer, String>();
		//HashMap<Integer, Integer> doorList = new  HashMap<Integer, Integer>();
		
		int roomId = 0;
		
		// iterate over doors graph vertex (rooms)
		Iterator<Vector<EFSMState>> vertexIterator = doorsGraph.vertexSet().iterator();
		while (vertexIterator.hasNext()) {
			Vector<EFSMState> doorsGraphState = vertexIterator.next();
			Room room = new Room(roomId);
			// add buttons
			for(EFSMState s : doorsGraphState ) {
				if (s.equals(efsmInitialState)) {
					room.placeAgent("Agent1");
				}
				if (getStateType(s).equals(StateType.Button)) {
					Button b = room.addButton(s.getId());
					buttonList.add(b);
					buttonDoorMap.put(s,b);
				}
				if (getStateType(s).equals(StateType.GoalFlag)) {
					GoalFlag g = room.addGoalFlag(s.getId());
					goalFlagList.add(g);
				}
			}
			csvRooms.add(room);
			GraphRoomToCsvRoom.put(doorsGraphState, roomId);
			roomId = roomId + 1;
			
		}
		
		// iterate over door graph edges (doors)
		Iterator<Integer> edgeIterator = doorsGraph.edgeSet().iterator();
		while (edgeIterator.hasNext()) {
			
			Integer doorsGraphEdge = edgeIterator.next();
			//LabRecruitsDoor doorsGraphDoor = doorButtonMap.getDoor("door"+doorsGraphEdge.toString());
			//HashSet<String> doorsGraphButtons = doorsGraphDoor.getButtons();
			Set<EFSMState> activatingButtons = doorButtonsMap.get(doorsGraphEdge);
			
			Vector<EFSMState> source = doorsGraph.getEdgeSource(doorsGraphEdge);
			Vector<EFSMState> dest = doorsGraph.getEdgeTarget(doorsGraphEdge);
			
			Corridor corridor = Corridor.connect( csvRooms.get(GraphRoomToCsvRoom.get(source)) , csvRooms.get(GraphRoomToCsvRoom.get(dest)) );
			Door door = corridor.guard("door"+doorsGraphEdge.toString());
						
			//Iterator<String> doorsGraphButtonsIterator = doorsGraphButtons.iterator();
			//while(doorsGraphButtonsIterator.hasNext()) {			
			//	door.operatedBy(buttonDoorMap.get(doorsGraphButtonsIterator.next()));
			//}
			for(EFSMState s : activatingButtons) {
				door.operatedBy(buttonDoorMap.get(s));
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

	public void saveDoorGraph(String scenarioId) throws IOException  {
		// JgraphT 1.5.0
		GraphMLExporter<Vector<EFSMState>, Integer> gExporter = new GraphMLExporter();
		gExporter.setExportEdgeWeights(true);
		gExporter.setExportEdgeLabels(true);
		gExporter.setExportVertexLabels(true);
		
		/*
		Function<Vector<EFSMState>,String> vertexLabeler = (vertex) -> {
			String out = "";
			for (EFSMState state : vertex) {
				out = out + " " + state.getId();
			}
			return (out);
		};
		
		Function<Integer, String> edgeLabeler = (edge) -> {
			return("d"+edge.toString());
		};
		
		/*
		GraphMLExporter<Vector<EFSMState>, Integer> gExporter = 
				new GraphMLExporter(
						new IntegerComponentNameProvider<>(),
						v -> vertexLabeler.apply( (Vector<LabRecruitsState>) v),
						new IntegerComponentNameProvider<>(),
						e -> edgeLabeler.apply((Integer) e));
		 */
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
			Function<EFSMState, String> stateLabeler = (state) -> { return(state.getId()); };
			Function<EFSMTransition, String> edgeLabeler = (edge) -> { return(""); };
			
			EFSMDotExporter dotExporter = new EFSMDotExporter(efsm,stateLabeler,edgeLabeler);
			
			
			Path outFile = Paths.get(scenarioID+"_efsm.dot");
			dotExporter.writeOut(outFile);
		}
	}
	
	/**
	 * Export efsm to a string in dot format
	 * 
	 * @throws IOException 
	 */
	public String getEFSMAsDot(){
		String dot = "";
		if (efsm != null) {
			Function<EFSMState, String> stateLabeler = (state) -> { return(state.getId()); };
			Function<EFSMTransition, String> edgeLabeler = (edge) -> { return(""); };
			
			EFSMDotExporter dotExporter = new EFSMDotExporter(efsm,stateLabeler,edgeLabeler);
			
			
			StringBuilderWriter stringWriter = new StringBuilderWriter();
			dotExporter.writeOut(stringWriter);
			dot = stringWriter.toString();
		}
		return dot;
	}
	
	/*
	 * Save the csv playable with Lab Recruits
	 */
	public void saveLabRecruitsLevel(String scenarioID){
		Path outFile = Paths.get(scenarioID+"_LR.csv");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.toString()));
			writer.write(this.csvLevel);
	        writer.close();
		} catch(IOException io){
			io.printStackTrace();
		}
		
		
	}
	
	
	
	/*
	 * private Pseudograph<Vector<EFSMState>,Integer>
	 * generateDoorsGraph(List<Vector<EFSMState>> roomSet){
	 * 
	 * doorsGraph = new Pseudograph<>(Integer.class);
	 * 
	 * // add vertex, i.e., set of labrecruits states for (Vector<EFSMState> vector
	 * : roomSet) { doorsGraph.addVertex(vector); }
	 * 
	 * 
	 * 
	 * // tmp values to store used and remaining rooms that need to be connected
	 * Set<Vector<EFSMState>> usedRooms = new LinkedHashSet<>(); // need to clone
	 * because vertexSet returns a view Set<Vector<EFSMState>> availableRooms = new
	 * LinkedHashSet<>(doorsGraph.vertexSet());
	 * 
	 * 
	 * // pick first node and update used and available rooms //Vector<?> firstNode
	 * = (Vector<?>) availableRooms.toArray()[rndGenerator.nextInt(0,
	 * availableRooms.size()-1)]; int firstNodeId = rndGenerator.nextInt(0,
	 * availableRooms.size()-1); Vector<EFSMState> firstNode =
	 * availableRooms.stream().skip(firstNodeId).iterator().next();
	 * availableRooms.remove(firstNode); usedRooms.add(firstNode);
	 * 
	 * // iterate over nDoors and add an edge each time selecting // one node from
	 * the used and from the available for (int i = 0; i < nDoors; i++) { // random
	 * pick an available and a used node int nextAvailableId =
	 * rndGenerator.nextInt(0, availableRooms.size()-1); Vector<EFSMState>
	 * unconnectedNode =
	 * availableRooms.stream().skip(nextAvailableId).iterator().next(); int
	 * nextUsedId = rndGenerator.nextInt(0, usedRooms.size()-1); Vector<EFSMState>
	 * connectedNode = usedRooms.stream().skip(nextUsedId).iterator().next(); // add
	 * a new edge to the graph doorsGraph.addEdge(connectedNode, unconnectedNode,i);
	 * // update available and used rooms availableRooms.remove(unconnectedNode);
	 * usedRooms.add(unconnectedNode); // no more rooms but still some door to
	 * create if (availableRooms.size() == 0) { break; } }
	 * 
	 * // 3 situations are possible
	 * 
	 * // all rooms are used but there are still some available door: random add
	 * doors between random rooms if (doorsGraph.edgeSet().size() < nDoors) { for
	 * (int j = doorsGraph.edgeSet().size(); j < nDoors; j++) { // random pick two
	 * rooms id (could be the same) int firstRoomId = rndGenerator.nextInt(0,
	 * usedRooms.size()-1); int secondRoomId = rndGenerator.nextInt(0,
	 * usedRooms.size()-1); Vector<EFSMState> firstRoom =
	 * usedRooms.stream().skip(firstRoomId).iterator().next(); if (firstRoomId !=
	 * secondRoomId) { Vector<EFSMState> secondRoom =
	 * usedRooms.stream().skip(secondRoomId).iterator().next();
	 * doorsGraph.addEdge(firstRoom, secondRoom,j); }else { // self loop are doors
	 * to empty rooms (solved when building the final EFSM)
	 * doorsGraph.addEdge(firstRoom, firstRoom, j); }
	 * 
	 * } }
	 * 
	 * // all doors are used but there are still some room: merge remaining rooms
	 * with used rooms // remove unused rooms for (Vector<EFSMState> room :
	 * availableRooms) { doorsGraph.removeVertex(room); } // add unused buttons to
	 * already connected rooms
	 * 
	 * for(Vector<LabRecruitsState> room : availableRooms) { int toMergeId =
	 * rndGenerator.nextInt(0, doorsGraph.vertexSet().size() - 1);
	 * doorsGraph.vertexSet().stream().skip(toMergeId).iterator().next().addAll(room
	 * ); }
	 * 
	 * // all rooms and doors are used: exit return(doorsGraph); }
	 * 
	 */	
}
 