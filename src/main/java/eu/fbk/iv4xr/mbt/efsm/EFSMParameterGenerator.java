package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;

public abstract class EFSMParameterGenerator<T extends EFSMParameter> implements Cloneable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4073688259536579614L;

	public EFSMParameterGenerator() {		
	}
	
	public abstract T getRandom();

}
