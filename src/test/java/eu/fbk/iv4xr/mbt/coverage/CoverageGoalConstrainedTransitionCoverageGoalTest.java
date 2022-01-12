package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.evosuite.Properties.NoSuchParameterException;
import org.evosuite.utils.Randomness;
import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.MBTProperties.TestFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * Test to execute CoverageGoalConstrainedTransitionCoverageGoal
 * @author prandi
 *
 */
public class CoverageGoalConstrainedTransitionCoverageGoalTest {
	
	@Test
	public void test1() {
		
		/*
		 * Set properties
		 */
		
		// Define a labrectuits random model
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		
		// Set parameters of the model
		MBTProperties.LR_seed = 65327;
		MBTProperties.LR_mean_buttons = 1;
		MBTProperties.LR_n_buttons = 20;
		MBTProperties.LR_n_doors = 20;
		MBTProperties.LR_n_goalFlags = 3;
		
		// Set criterion
		MBTProperties.MODELCRITERION = new ModelCriterion[] {
				ModelCriterion.TRANSITION_FIX_END_STATE 
		};
		MBTProperties.TEST_FACTORY = MBTProperties.TestFactory.RANDOM_LENGTH_FIX_TARGET;
		
		// Set Target state
		String targetState = "b12";
		MBTProperties.STATE_TARGET = targetState;
		
		// Search budget in seconds
		MBTProperties.SEARCH_BUDGET = 30;
		
		// set seed
//        try {
//        	//Do this also for Evosuite global properties, if they exsits
//        	org.evosuite.Properties.getInstance().setValue("random_seed", "32817");
//        }catch (Exception e) {
//        }
		
  
		// Optionally set output folder
		// MBTProperties.OUTPUT_DIR = "outdir";
		
		// check that target state exists
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		assertTrue(efsm.getStates().contains(new EFSMState(targetState)));
		
		// Generate result
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		SuiteChromosome solution = generationStrategy.generateTests();
		
		// Output folder
		String testFolder = MBTProperties.TESTS_DIR() + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + System.currentTimeMillis();
		File testsFolder = new File (testFolder);
		testsFolder.mkdirs();
		
		int count = 1;
		for (MBTChromosome testCase : solution.getTestChromosomes()) {
			System.out.println(count);
			AbstractTestSequence testSequence = (AbstractTestSequence)testCase.getTestcase();
			// check that the last state is target state
			EFSMTransition lastTranstion = testSequence.getPath().getTransitionAt(testSequence.getPath().getLength()-1);
			assertTrue(lastTranstion.getTgt().equals(new EFSMState(targetState)));
			// check that the target state appears only once in the solution
			assertTrue(Collections.frequency(testSequence.getPath().getStates(), new EFSMState(targetState)) == 1);
			
			String txtFileName = testFolder + File.separator + "test_" + count + ".txt";
			File txtFile = new File (txtFileName);
			try {
				FileUtils.writeStringToFile(txtFile, testCase.getTestcase().toString(), Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		
	}

}
