/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j;


/**
 * @author Davide Prandi
 *
 * Sep 18, 2020
 */
public abstract class EFSMState implements Comparable<EFSMState>, Cloneable {

	
	
	public abstract String getId();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object obj);
	

}
