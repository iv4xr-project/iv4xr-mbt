package eu.fbk.iv4xr.mbt.efsm.cps;

import org.evosuite.utils.Randomness;

import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;

// TODO Not used but need to be defined for serialization
public class DirectionGenerator extends EFSMParameterGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8907507725852026985L;

	@Override
	public EFSMParameter getRandom() {
		Double f =  Randomness.nextDouble();
		if (f < 0.25) {
			return new EFSMParameter(new Var("dir", Direction.NORTH));
		}
		if (f >= 0.25 && f < 0.5) {
			return new EFSMParameter(new Var("dir", Direction.SOUTH));
		}
		if (f >= 0.5 && f < 0.75) {
			return new EFSMParameter(new Var("dir", Direction.EAST));
		}
		if (f >= 0.75) {
			return new EFSMParameter(new Var("dir", Direction.EAST));
		}
		
		throw new RuntimeException("Unexpected generated direction");
	}

}
