/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j;


/**
 * @author Davide Prandi
 *
 * Sep 23, 2020
 */
public abstract class ParameterGenerator<T extends EFSMParameter> {

	public ParameterGenerator() {		
	}
	
	public abstract T getRandom();
	
}
 