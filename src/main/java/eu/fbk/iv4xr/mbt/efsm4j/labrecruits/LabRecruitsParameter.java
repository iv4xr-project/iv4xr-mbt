/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.utils.Randomness;

/**
 * @author Davide Prandi
 *
 * Sep 22, 2020
 */
public class LabRecruitsParameter extends EFSMParameter<LabRecruitsAction>{

	/**
	 * @param val
	 */
	public LabRecruitsParameter(LabRecruitsAction val) {
		super(val);
	}
	
	/**
	public LabRecruitsParameter() {
		super();
	}
	**/
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != LabRecruitsParameter.class) {
			return false;
		}else {
			LabRecruitsParameter tmp = (LabRecruitsParameter)obj;
			return (tmp.getValue() == this.getValue());
		}
	}
	
	/**
	 * To evaluate if it is better to have more deep copy
	 */
	@Override
	public LabRecruitsParameter clone() {
		return this.clone();
	}

	/**
	@Override
	public LabRecruitsAction getRandom() {
		if (Randomness.nextDouble() < 0.2) {
			return LabRecruitsAction.TOGGLE;
		}else {
			return LabRecruitsAction.EXPLORE;
		}
	}
	**/




}
