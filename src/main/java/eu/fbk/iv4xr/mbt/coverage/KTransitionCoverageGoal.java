/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;

/**
 * @author kifetew
 *
 * Represents a sequence of K transitions, where K >= 2
 */
public class KTransitionCoverageGoal<
State extends EFSMState,
Parameter extends EFSMParameter,
Context extends IEFSMContext<Context>,
Trans extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>> extends CoverageGoal<State, Parameter, Context, Trans> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6341416548064403719L;
	private int k = 2;
	
	/**
	 * 
	 */
	public KTransitionCoverageGoal() {
		// TODO Auto-generated constructor stub
	}

	
	public KTransitionCoverageGoal(int k){
		this.k = k;
	}
	


	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}


	/**
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}


	@Override
	public double getFitness(Chromosome individual) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

}
