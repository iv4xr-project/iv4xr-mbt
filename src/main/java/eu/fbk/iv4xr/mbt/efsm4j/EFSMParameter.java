/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j;

import java.util.HashMap;


/**
 * @author Davide Prandi
 *
 * Sep 21, 2020
 * @param <T>
 */
public abstract class EFSMParameter<T> implements Cloneable {
	
	private T value;
	
	public EFSMParameter(T val){
		this.value = val;
	}
	
	public EFSMParameter(){
		this.value = null;
	}
	
	/**
	public EFSMParameter(){
		this.value = getRandom();
	}
	**/
	
	public T getValue() {
		return(this.value);
	}
	
	public Class<? extends Object> getType() {
		return(this.value.getClass());
	}
	
	public abstract boolean equals(Object obj);
	
	public abstract EFSMParameter<T> clone();
	
	@Override
	public String toString() {
		if (this.value == null) {
			return("NIL");
		}else {
			return(this.value.toString());
		}
	}
	
	//public abstract T getRandom();
	
	
}
