package eu.fbk.iv4xr.mbt.efsm;

import java.util.HashMap;

/**
 * This class provide an hash for fast checking if an EFSMTransition is already in the 
 * base graph of an EFSM class. The hash of an EFSMTransition is define by the concatenation of
 * the source and target EFSMState id. This could create some collisions, but they will be quite rare.
 * 
 * @author prandi
 *
 */

public class EFSMTransitionMapper<T extends EFSMTransition> {
	
	private HashMap<String, T> transitionsMap;
	
	// symbol used to connect src and tgt ids
	private String connector = "+";
	
	public EFSMTransitionMapper() {
		this.transitionsMap = new HashMap<>();	
	}
	
	
	/*
	 * compute a string from and EFSMTranstion pasting src and tgt id
	 */
	private String hash(T t) {
		if (t == null) {
			throw new RuntimeException("EFSMTransitionMapper does not accept null EFSMTransitions");
		}else {
			return t.getSrc().getId() + connector + t.getTgt().getId();
		}
	}
	
	/*
	 * insert an EFSMTransition in the map
	 */
	public void put(T t) {
		String key = hash(t);
		transitionsMap.put(key, t);
	}
	
	public T get(T t) {
		
		String key = hash(t);
		if (transitionsMap.containsKey(key)) {
			return transitionsMap.get(key);
		}
		throw new RuntimeException("Transition not found in model: " +  t);
	}
	
}
