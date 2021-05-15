package eu.fbk.iv4xr.mbt.efsm.labRecruits;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Generate mutated version of a LabRecruits csv level. 
 * Two types of mutations are considered: <br>
 * - remove a link between a button and a door
 * - add a link between a button and a door
 * @author prandi
 *
 */
public class LabRecruitMutationManager {

	static final String startLayerCode = "|" ;
	
	private String csvLevel;
	
	// Store the doors each button can open
	private LinkedHashMap<String, LinkedHashSet<String>> buttonDoorsMap;
	
	// Store all buttons
	private LinkedHashSet<String> buttonSet;
	
	// Store all doors
	private LinkedHashSet<String> doorSet;
	
	// Store original csv header
	private String originalHeader;
	
	// Store original csv body
	private String originalBody;
	
	// Remove mutations
	private List<String> removeMutations;
	
	// Add mutations
	private List<String> addMutations;
	
	
	public LabRecruitMutationManager(String csvLevel) {
		this.csvLevel = csvLevel;
		buttonDoorsMap = new LinkedHashMap<String, LinkedHashSet<String>>();
		buttonSet = new LinkedHashSet<String>();
		doorSet = new LinkedHashSet<String>();
	}
	
	// extract the header of the csv 
	public void setButtonDoorsHeader() {
		String buttonsDoorsMap = "";
		// Search within the header for 
		if (!csvLevel.startsWith(startLayerCode)  ) {
			// get the position of the first character of floor definition
			Integer floorStart = csvLevel.indexOf(startLayerCode);
			buttonsDoorsMap = csvLevel.substring(0, floorStart); 
		}
		originalHeader = buttonsDoorsMap;
	}
	
	// extract the level layout from the csv
	public void setLayout() {
		// get the position of the first character of floor definition
		Integer floorStart = csvLevel.indexOf(startLayerCode);
		// take the rest of the csv
		String levelLayout = csvLevel.substring(floorStart);
		originalBody = levelLayout;
	}
	
	// fill buttonDoorsMap using the header
	public void fillButtonDoorsMap() {
		// header not yet parsed
		String remainingToParse = originalHeader;
		// cycle over return character
		Integer returnPos = remainingToParse.indexOf(System.lineSeparator());
		// temporary store the string of a single assignment
		String singleMap = "";
		
		while(returnPos > -1) {
			// get the current assignment
			singleMap = remainingToParse.substring(0, returnPos);
			
			// split by comma
			String[] rawAssign = singleMap.split(",");
			for (int i = 1; i < rawAssign.length; i++) {
				if (buttonDoorsMap.containsKey(rawAssign[0])) {
					buttonDoorsMap.get(rawAssign[0]).add(rawAssign[i]);
				}else {
					buttonDoorsMap.put(rawAssign[0], new LinkedHashSet<>());
					buttonDoorsMap.get(rawAssign[0]).add(rawAssign[i]);
				}
			}	
			// prepare for next assigment
			remainingToParse = remainingToParse.substring(returnPos+1, remainingToParse.length());
			returnPos = remainingToParse.indexOf(System.lineSeparator());
		}
		
	}
	
	
	// https://stackoverflow.com/questions/5705111/how-to-get-all-substring-for-a-given-regex
	private static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
	
	// set all doors defined in a level into doorSet
	// doors are f:d>X^NAME where X is the direction and NAME an id of the door
	public void fillDoorSet() {
		// define door pattern string
		String patternString = "f:d>.\\^\\w+";
		// get all doors in the level
		List<String> parsedDoors = getAllMatches(originalBody, patternString);
		// Insert doors in doorSet
		for(String d : parsedDoors) {
			// extract door id
			Integer start = d.indexOf("^");
			String dName = d.substring(start+1);
			doorSet.add(dName);
		}
	}
	
	
	// set all buttons in a level into buttonSet
	// buttons are f:b^NAME where NAME is the id of the button
	public void fillButtonSet() {
		// define button pattern
		String patternString = "f:b\\^\\w+";
		// get all buttons
		List<String> parseButtons = getAllMatches(originalBody, patternString);
		// insert buttons in buttonSet
		for(String b : parseButtons) {
			// extract button id
			Integer start = b.indexOf("^");
			String bName = b.substring(start+1);
			buttonSet.add(bName);
		}	
	}
	
	
	/*
	 * convert a button-doors map to a string
	 */
	public String buttonDoorsMapToString(LinkedHashMap<String, LinkedHashSet<String>> newMap) {
		String outString = "";
		for(String b : newMap.keySet()) {
			if (newMap.get(b).size() > 0) {
				outString = outString + b + ",";
			}
			for(String d : newMap.get(b)) {
				outString = outString + d + ",";
			}
			if (outString.length() > 0) {
				// remove last ,
				outString = outString.substring(0, outString.length()-1);
				// add retunr
				outString = outString + System.lineSeparator();
			}
		}
		return outString;
	}
	
	/*
	 * Hard clone button door map
	 */
	public LinkedHashMap<String, LinkedHashSet<String>> cloneButtonDoorsMap(){
		// deep clone map
		LinkedHashMap<String, LinkedHashSet<String>> newMap = new LinkedHashMap<String, LinkedHashSet<String>>();
		for(String s: buttonDoorsMap.keySet()) {
			LinkedHashSet<String> tmpDoors = new LinkedHashSet<String>(buttonDoorsMap.get(s));
			newMap.put(new String(s), tmpDoors);
		}
		return newMap;
	}
	
	
	/* 
	 * iterate over buttonDoorMap and remove a door
	 * each iteration
	 */
	public void createRemoveMutations(){
		List<String> mutations = new LinkedList<String>();
		for(String b : buttonDoorsMap.keySet()) {
			LinkedHashSet<String> doorSet = buttonDoorsMap.get(b);
			for(String d : doorSet) {
				LinkedHashMap<String, LinkedHashSet<String>> newMap = cloneButtonDoorsMap();
				newMap.get(b).remove(d);
				// convert to a string
				String mutant = buttonDoorsMapToString(newMap) + originalBody;
				mutations.add(mutant);
			}
	 
		}
		removeMutations = mutations;
	}
	
	/*
	 * iterate over all defined buttons and doors
	 * if the link is not present create it and return a new level
	 */
	public void createAddMutations(){
		List<String> mutations = new LinkedList<>();
		for(String b : buttonSet) {
			for(String d: doorSet) {
				LinkedHashMap<String, LinkedHashSet<String>> newMap = cloneButtonDoorsMap();
				// check if link b-d is in the original
				if (buttonDoorsMap.keySet().contains(b)) {
					if(buttonDoorsMap.get(b).contains(d)) {
						continue;
					}
				}else {
					newMap.put(b, new LinkedHashSet<String>());
				}
				// b-d is not in the button doors map
				// clone the map and add the new link
				newMap.get(b).add(d);
				// convert to a string
				String mutant = buttonDoorsMapToString(newMap) + originalBody;
				mutations.add(mutant);
			}
		}
		
		addMutations = mutations;
	}
	
	/*
	 * generate mutants
	 */
	private void generateMutations() {
		setButtonDoorsHeader();
		fillButtonDoorsMap();
		setLayout();
		fillDoorSet();
		fillButtonSet();
		createRemoveMutations();
		//createAddMutations(); 
	}
	
	/*
	 * return mutants
	 */
	public List<String> getMutations(){
		List<String> mutations = new ArrayList<String>();
		generateMutations();
		if (removeMutations != null) {
			mutations.addAll(removeMutations);
		}
		
		if (addMutations != null) {
			mutations.addAll(addMutations);
		}
		
		return mutations;
		
	}
}
