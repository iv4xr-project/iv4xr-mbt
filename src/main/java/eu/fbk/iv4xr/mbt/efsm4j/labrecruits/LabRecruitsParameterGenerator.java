/**
 * 
 */
package eu.fbk.iv4xr.mbt.efsm4j.labrecruits;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.ParameterGenerator;
import eu.fbk.iv4xr.mbt.utils.Randomness;

/**
 * @author Davide Prandi
 *
 * Sep 24, 2020
 */
public class LabRecruitsParameterGenerator extends ParameterGenerator {

	@Override
	public LabRecruitsParameter getRandom() {
		if (Randomness.nextDouble() < 0.1) {
			return new LabRecruitsParameter(LabRecruitsAction.TOGGLE);
		}else {
			return new LabRecruitsParameter(LabRecruitsAction.EXPLORE);
		}
	}

}
