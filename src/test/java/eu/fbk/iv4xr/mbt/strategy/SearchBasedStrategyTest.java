package eu.fbk.iv4xr.mbt.strategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.cps.TestToPoints;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutionListener;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * 
 * @author prandi
 *
 */
public class SearchBasedStrategyTest {

	@Test
	public void testGenerateConstrainedTests() {
		// Model criterion should be STATE
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
				ModelCriterion.STATE 
		};
	
		MBTProperties.LR_n_goalFlags = 2;
		
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		
		EFSMState gf0 = new EFSMState("gf0");
		assertTrue(efsm.getStates().contains(gf0));
		
		Set<String> targetSet = new HashSet<>();
		targetSet.add("gf0");
		
		EFSMTestExecutor.getInstance().resetEFSM();
		
		SearchBasedStrategy sbStrategy = new SearchBasedStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests(targetSet);
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		
		System.out.println("\nGenerated "+testChromosomes.size()+" test cases");
		for(MBTChromosome chr : testChromosomes) {
			AbstractTestSequence testcase = (AbstractTestSequence) chr.getTestcase();
			System.out.println("Fitness: "+testcase.getFitness());
			System.out.println(testcase.toString());
			assertTrue(testcase.getPath().getStates().contains(gf0));
		}
	}
	
	@Test
	public void testGenerateTestsBeamNg() {
		// Model criterion should be STATE
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
				ModelCriterion.TRANSITION
		};
		
		MBTProperties.SUT_EFSM = "cps.beamng_custom_model";
		
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		
		EFSMTestExecutor.getInstance().resetEFSM();
		
		SearchBasedStrategy sbStrategy = new SearchBasedStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests();
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		
		System.out.println("\nGenerated "+testChromosomes.size()+" test cases");
		TestToPoints testToPoints = TestToPoints.getInstance();
		for(MBTChromosome chr : testChromosomes) {
			AbstractTestSequence testcase = (AbstractTestSequence) chr.getTestcase();
			List testcaseToPoints = testToPoints.testcaseToPoints(testcase);
			System.out.println("Fitness: "+testcase.getFitness());
			System.out.println("Test Case");
			System.out.println(testcase.toString());
			System.out.println("Points");
			System.out.println(testcaseToPoints.toString());
			
		}
	}
	
	@Test
	public void testGenerateTests() {
		MBTProperties.SUT_EFSM = "labrecruits.random_large";
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
				ModelCriterion.TRANSITION 
		};
		
		SearchBasedStrategy strategy = new SearchBasedStrategy<>();
		SuiteChromosome suite = strategy.generateTests();
		List<MBTChromosome> generatedTests = suite.getTestChromosomes();
		System.out.println("\nGenerated "+generatedTests.size()+" test cases");
		for(MBTChromosome chr : generatedTests) {
			AbstractTestSequence testcase = (AbstractTestSequence) chr.getTestcase();
			System.out.println("Fitness: "+testcase.getFitness());
			System.out.println(testcase.toString());
		}
	}
	
	@Test
	public void trafficLightSearchTest() {
		// select SUT
		MBTProperties.SUT_EFSM = "examples.traffic_light";
		// Select coverage criterion
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
			ModelCriterion.TRANSITION
		};
		// Fix time budget to 120s
		MBTProperties.SEARCH_BUDGET = 120l;
		// select search algorithm to NSGAII
		MBTProperties.ALGORITHM = Algorithm.NSGAII;
		// set seed for repeatibility
		MBTProperties.RANDOM_SEED = 4328213l;
		
		// call the test factory
		EFSMFactory mFactory = EFSMFactory.getInstance(true);
		assertNotNull(mFactory);
		EFSM efsm = mFactory.getEFSM();
		assertNotNull (efsm);
		
		// clean EFSM abstract test executor
		EFSMTestExecutor.getInstance().resetEFSM();
		
		// generate test cases
		SearchBasedStrategy sbStrategy = new SearchBasedStrategy<>();
		SuiteChromosome generatedTests = sbStrategy.generateTests();
		List<MBTChromosome> testChromosomes = generatedTests.getTestChromosomes();
		
		// plot test cases
		System.out.println("\nGenerated "+testChromosomes.size()+" test cases");
		for(MBTChromosome chr : testChromosomes) {
			AbstractTestSequence testcase = (AbstractTestSequence) chr.getTestcase();
			System.out.println("Fitness: "+testcase.getFitness());
			System.out.println("Test Case");
			System.out.println(testcase.toString());
		}
		
	}
}
