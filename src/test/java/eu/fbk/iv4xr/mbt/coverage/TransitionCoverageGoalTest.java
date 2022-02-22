/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
//import de.upb.testify.efsm.EFSM;
//import de.upb.testify.efsm.Transition;
import eu.fbk.iv4xr.mbt.MBTProperties;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSM;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.Transition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsEFSMFactory;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsFreeTravelTransition;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;

import org.evosuite.Properties;
import org.evosuite.utils.Randomness;

/**
 * @author kifetew
 *
 */
class TransitionCoverageGoalTest {

	
	EFSMFactory mFactory;
	EFSM model;
	TestFactory testFactory;
	RandomLengthTestChromosomeFactory<MBTChromosome> cFactory;
	
	double PENALITY1 = EFSMTestExecutionListener.PENALITY1;
	double PENALITY2 = EFSMTestExecutionListener.PENALITY2;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		Properties.RANDOM_SEED = 1234L;
		Randomness.getInstance();
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";		
		mFactory = EFSMFactory.getInstance(true);
		model = mFactory.getEFSM();
		testFactory = new RandomLengthTestFactory(model);
		assertNotNull(testFactory);
		cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		assertNotNull(cFactory);
		
		
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoal#TransitionCoverageGoal(eu.fbk.se.labrecruits.LabRecruitsState)}.
	 */
	@Test
	void testTransitionCoverageGoal() {
		EFSMTransition transition = model.getTransition("t_0");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(transition);
		assertNotNull(goal);
	}
	
	@Test
	void testGetFitnessInvalidPathTargetPresent() {
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		path.append(model.getTransition("t_14"));  // dtm -> dtp
		t.setPath(path);
		
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		EFSMTransition trans = model.getTransition("t_14");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(trans);
		double fitness = goal.getFitness(c);
		double expectedFitness = 0.5d;
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 
	}
	
	@Test
	void testGetFitnessInvalidPathTargetNotPresent() {
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		path.append(model.getTransition("t_14"));  // dtm -> dtp
		t.setPath(path);
		
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		EFSMTransition trans = model.getTransition("t_30");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(trans);
		double fitness = goal.getFitness(c);
		double expectedFitness = 0.5d + (PENALITY2 + (PENALITY2/(PENALITY2+1)));
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 
	}
	
	@Test
	void testGetFitnessValidPathTargetPresent() {
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		t.setPath(path);
		
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		EFSMTransition trans = model.getTransition("t_4");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(trans);
		double fitness = goal.getFitness(c);
		double expectedFitness = 0d;
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 
	}

	@Test
	void testGetFitnessValidPathTargetNotPresent() {
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		t.setPath(path);
		
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		EFSMTransition trans = model.getTransition("t_30");
		TransitionCoverageGoal goal = new TransitionCoverageGoal(trans);
		double fitness = goal.getFitness(c);
		double expectedFitness = PENALITY1 + (PENALITY1/(PENALITY1+1));
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 
	}
	
}
