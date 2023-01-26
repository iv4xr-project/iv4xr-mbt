/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm;

/**
 * @author kifetew
 *
 */
public interface EFSMProvider {
	
	/**
	 * Every implementation must return a valid EFSM model.
	 * @return
	 */
	public EFSM getModel ();

}
