package eu.fbk.iv4xr.mbt.labrecruitsexecutor;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import agents.LabRecruitsTestAgent;
import agents.tactics.GoalLib;
import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import game.LabRecruitsTestServer;
import game.Platform;

import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import world.BeliefState;

public class ServerTest {

	// from iv4xrDemo
	@Test
	public void binExistsTest() {

		String labRecruitesExeRootDir = System.getProperty("user.dir");
		System.out.println(labRecruitesExeRootDir);
		LabRecruitsTestServer testServer = new LabRecruitsTestServer(false,
				Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));

		// should be false because server is not started yet
		Assertions.assertTrue(testServer.isRunning());

		testServer.close();
	}

	// generate a random level and the open it
	@Test
	public void createAndLoadLevelTest() throws InterruptedException, IOException {
		
		
		// basic configurations of LR
		String levelName = "testLRexecutor";
		String levelPath = Paths.get(Platform.LEVEL_PATH, levelName).toString();
		String agentName = "Agent1";
		String levelFileName = levelName + "_LR";
		String labRecruitesExeRootDir = System.getProperty("user.dir");

		// MBT configurations
		MBTProperties.LR_generation_mode = MBTProperties.LR_random_mode.N_BUTTONS_DEPENDENT;
		MBTProperties.LR_n_rooms = 5;
		MBTProperties.LR_n_doors = 4;
		MBTProperties.SEARCH_BUDGET = 60;
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		
		// create random level and save it
		EFSMFactory factory = EFSMFactory.getInstance(true);
		EFSM efsm = factory.getEFSM();
		// try to save the csv. If the csv is not generated the test fails
		if (efsm.getEFSMString() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
			fail();
		} else {
			// save the level
			BufferedWriter writer = new BufferedWriter(new FileWriter(levelPath+"_LR.csv"));
		    writer.write(efsm.getEFSMString()); 
		    writer.close();
		}

		// compute a solution with MOSA
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		SuiteChromosome solution = generationStrategy.generateTests();
			
		// create the executor
        LabRecruitsTestSuiteExecutor lrExecutor = new LabRecruitsTestSuiteExecutor(labRecruitesExeRootDir, levelFileName, agentName);
        
        // execute the test suite
        LinkedHashMap<AbstractTestSequence, Boolean> response = lrExecutor.executeTestSuite(solution);
        
        // check the results
        System.out.println("Performed "+response.size()+" test");
        for(AbstractTestSequence test : response.keySet()) {     	
        	if (response.get(test)) {
        		//System.out.println("PASS");
        		//System.out.println(test.toString());
        	}else {
        		System.out.println("FAIL");
        		System.out.println(test.toString());
        		//fail();
        	}
        }

		

	}
	

	
}