package eu.fbk.iv4xr.mbt.coverage;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.evosuite.Properties;
import org.evosuite.utils.Randomness;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;


import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMPath;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;
 


/**
 * Derived from eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoalTest
 * @author prandi
 *
 */
public class KTransitionCoverageGoalTest {
	

	EFSMFactory mFactory;
	EFSM model;
	TestFactory testFactory;
	RandomLengthTestChromosomeFactory<MBTChromosome> cFactory;
	
	double PENALITY1 = EFSMTestExecutionListener.PENALITY1;
	double PENALITY2 = EFSMTestExecutionListener.PENALITY2;
	

	@BeforeEach
	void setUp() throws Exception {
		Properties.RANDOM_SEED = 1234L;
		Randomness.getInstance();
		MBTProperties.K_TRANSITION_SIZE = 2;
		Randomness.getInstance();
		MBTProperties.SUT_EFSM = "labrecruits.buttons_doors_1";		
		mFactory = EFSMFactory.getInstance(true);
		model = mFactory.getEFSM();
		testFactory = new RandomLengthTestFactory(model);
		assertNotNull(testFactory);
		cFactory = new RandomLengthTestChromosomeFactory<MBTChromosome>(testFactory);
		assertNotNull(cFactory);
	}
	
	
	@Test
	public void testGetFitnessInvalidPathTargetPresent() {
		
		// define test case
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		path.append(model.getTransition("t_14"));  // dtm -> dtp
		t.setPath(path);
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		
		// define goal
		List<EFSMTransition> targetTransitions = new ArrayList<EFSMTransition>();
		targetTransitions.add(model.getTransition("t_7"));
		targetTransitions.add(model.getTransition("t_4"));	
		GraphPath graphTarget = new GraphWalk(model.getBaseGraph(), new EFSMState("b1"), new EFSMState("d3m"), targetTransitions, 1 );
		EFSMPath pathTarget = new EFSMPath(graphTarget);
		KTransitionCoverageGoal goal = new KTransitionCoverageGoal(pathTarget);
		
		// compute fitness
		double fitness = goal.getFitness(c);
		double expectedFitness = 0.5d;
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 
	}
	
	
	@Test
	void testGetFitnessInvalidPathTargetNotPresent() {
		// define test case: path invalid because d_1 is closed (b_1 is not pressed)
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0")); // b0 -> b1
		path.append(model.getTransition("t_6")); // b1 -> d_1_m
		path.append(model.getTransition("t_10")); // d_1_m -> d_1_p
		path.append(model.getTransition("t_17")); // d_1_p -> b_2
		path.append(model.getTransition("t_20")); // b_2 -> b_2
		t.setPath(path);
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);

		// define goal: go from b1 to d1m and then to b0
		List<EFSMTransition> targetTransitions = new ArrayList<EFSMTransition>();
		targetTransitions.add(model.getTransition("t_6")); // b1 -> d_1_m
		targetTransitions.add(model.getTransition("t_8")); // d_1_m -> b_0
		GraphPath graphTarget = new GraphWalk(model.getBaseGraph(), new EFSMState("b1"), new EFSMState("b0"),
				targetTransitions, 1);
		EFSMPath pathTarget = new EFSMPath(graphTarget);
		KTransitionCoverageGoal goal = new KTransitionCoverageGoal(pathTarget);

		// compute fitness
		double fitness = goal.getFitness(c);
		double expectedFitness = 1003.499000999001d;
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness);
	}
	
	@Test
	void testGetFitnessValidPathTargetPresent() {
		// define test case
		AbstractTestSequence t = new AbstractTestSequence();
		Path path = new Path();
		path.append(model.getTransition("t_0"));  // b0 -> b1
		path.append(model.getTransition("t_7"));  // b1 -> b1
		path.append(model.getTransition("t_4"));  // b1 -> dtm
		path.append(model.getTransition("t_12"));  // dtm -> d1m
		t.setPath(path);
		MBTChromosome c = new MBTChromosome();
		c.setTestcase(t);
		
		// define goal
		List<EFSMTransition> targetTransitions = new ArrayList<EFSMTransition>();
		targetTransitions.add(model.getTransition("t_7"));
		targetTransitions.add(model.getTransition("t_4"));	
		GraphPath graphTarget = new GraphWalk(model.getBaseGraph(), new EFSMState("b1"), new EFSMState("d3m"), targetTransitions, 1 );
		EFSMPath pathTarget = new EFSMPath(graphTarget);
		KTransitionCoverageGoal goal = new KTransitionCoverageGoal(pathTarget);
		
		// compute fitness
		double fitness = goal.getFitness(c);
		double expectedFitness = 0d;
		System.out.println("Actual fitness: " + fitness);
		System.out.println("Expected fitness: " + expectedFitness);
		assertTrue(expectedFitness == fitness); 

	}
}
