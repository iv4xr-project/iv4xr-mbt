package eu.fbk.iv4xr.mbt.labrecruitsexecutor;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.evosuite.shaded.org.apache.commons.io.FileUtils;
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
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestCaseReporter;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteReporter;
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
	
	private static String getExecDir() {
		return Paths.get(System.getProperty("user.dir"), "suts").toAbsolutePath().toString();
	}
	
	// from iv4xrDemo
	@Test
	public void binExistsTest() {

		String labRecruitesExeRootDir = getExecDir();
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
		String labRecruitesExeRootDir = getExecDir();
		Integer maxCycles = 200;
		String reportFileName = levelPath + "_report.txt";
		String statsFileName = levelPath + "_stats.csv";
		
		
		// MBT configurations
		MBTProperties.LR_generation_mode = MBTProperties.LR_random_mode.N_BUTTONS_DEPENDENT;
		MBTProperties.LR_n_rooms = 5;
		MBTProperties.LR_n_doors = 4;
		MBTProperties.SEARCH_BUDGET = 10;
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		
		// create random level and save it
		EFSMFactory factory = EFSMFactory.getInstance(true);
		EFSM efsm = factory.getEFSM();
		// try to save the csv. If the csv is not generated the test fails
		if (efsm.getEFSMString() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
			fail();
		} else {
			// TODO fix this part
			// save the level
			File resourceFolder = new File (Platform.LEVEL_PATH);
			if (!resourceFolder.exists()) {
				resourceFolder.mkdirs();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(levelPath+"_LR.csv"));
		    writer.write(efsm.getEFSMString()); 
		    writer.close();
		}

		// compute a solution with MOSA
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		SuiteChromosome solution = generationStrategy.generateTests();
			
		// create the executor
        LabRecruitsTestSuiteExecutor lrExecutor = new LabRecruitsTestSuiteExecutor(labRecruitesExeRootDir, Platform.LEVEL_PATH+"/"+levelFileName, agentName, maxCycles);
        //lrExecutor.setMaxCycle(maxCycles);
        
        // execute the test suite
        lrExecutor.executeTestSuite(solution);
        
        LabRecruitsTestSuiteReporter executionReport = lrExecutor.getReport();
     
        File reportFile = new File(reportFileName);
        FileUtils.writeStringToFile(reportFile, executionReport.toString(),  Charset.defaultCharset());
        
	}
	

	
}
