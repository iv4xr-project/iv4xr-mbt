package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

/**
 * 
 * The context is made of the status of the doors in the environment. The context is implemented as an hashmap
 * that uses door id as key.
 * 
 * @author Davide Prandi
 *
 */


import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;

import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

public class LabRecruitsContext extends HashMap<String, LabRecruitsDoor> implements IEFSMContext<LabRecruitsContext> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 13L;

	/**
	 * Take a series of doors and build the hashmap using door id as key
	 * @param lrd
	 */
	public LabRecruitsContext(LabRecruitsDoor... lrd) {
		super();
		for(LabRecruitsDoor door : lrd ) {
			put(door.getId(),door);
		}	
	}
	
	/**
	 *  Require to create an hard copy.
	 */	
	@Override
	public LabRecruitsContext snapshot() {
        // create a new LabRectuitsContext
		LabRecruitsContext lrc = (LabRecruitsContext) SerializationUtils.clone(this);
		return lrc;
	}

	/**
	 * Return a door with a given id.
	 * @param id
	 * @return
	 */
	public LabRecruitsDoor getDoor(String id) {
		if (containsKey(id)) {
			return get(id);
		}else {
			System.err.println("Door " + id + " not found");
		}
		// to check for consistency
		return null;
	}
	
	/**
	 * Return the status of a door with a give id.
	 * @param id
	 * @return
	 */
	public Boolean getDoorStatus(String id) {		
		LabRecruitsDoor lrd = getDoor(id);
		if (lrd != null) {
			return(lrd.getStatus());
		}
		return null;				
	}
	
	
}
 
