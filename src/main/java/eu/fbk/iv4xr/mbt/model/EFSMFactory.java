package eu.fbk.iv4xr.mbt.model;

import de.upb.testify.efsm.EFSM;

/**
 * 
 * @author kifetew
 * Interface to be implemented by specific EFSM implementations from different SUT
 */

public interface EFSMFactory {
	EFSM getEFSM (String scenarioId);
}
