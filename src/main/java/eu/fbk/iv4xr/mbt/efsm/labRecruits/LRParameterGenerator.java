package eu.fbk.iv4xr.mbt.efsm.labRecruits;

import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;

import org.evosuite.utils.Randomness;

public class LRParameterGenerator extends EFSMParameterGenerator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5183081712879509399L;

	@Override
	public EFSMParameter getRandom() {
		Float f =  Randomness.nextFloat();
		if (f > 0.7) {
			return new EFSMParameter(new Var("tmp",LRActions.EXPLORE));
		}else {
			return new EFSMParameter(new Var("tmp",LRActions.TOGGLE));
		}
	}

}
