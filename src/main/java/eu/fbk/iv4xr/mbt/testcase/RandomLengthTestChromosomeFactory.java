/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.efsm.EFSM;

//import de.upb.testify.efsm.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.IEFSMContext;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;


/**
 * @author kifetew
 *
 */
public class RandomLengthTestChromosomeFactory<T extends Chromosome> implements ChromosomeFactory<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171599793081703149L;
	
	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestChromosomeFactory.class);

	
	private TestFactory testFactory = null;
	
	private EFSM efsm = null;
	
	/**
	 * 
	 */
	public RandomLengthTestChromosomeFactory(TestFactory testFactory, EFSM efsm) {
		this.efsm = efsm;
		this.testFactory = testFactory;
	}

	@Override
	public T getChromosome() {
		T chromosome = (T) new MBTChromosome(efsm);
		Testcase testcase = testFactory.getTestcase();
		((MBTChromosome)chromosome).setTestcase(testcase);
		return chromosome;
	}

}
