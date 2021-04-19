package eu.fbk.iv4xr.mbt.labrecruitsexecutor;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
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
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.Path;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.Testcase;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
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
		
		
		// basic configurations
		String levelName = "testLRexecutor";
		String levelPath = Paths.get(Platform.LEVEL_PATH, levelName).toString();
		String agentName = "Agent1";
		String levelFileName = levelName + "_LR";

		// MBT configurations
		MBTProperties.LR_generation_mode = MBTProperties.LR_random_mode.N_BUTTONS_DEPENDENT;
		MBTProperties.LR_n_rooms = 5;
		MBTProperties.LR_n_doors = 4;
		
		
		// use the factory to create a level
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		EFSMFactory factory = EFSMFactory.getInstance(true);
		
		// create random level with default parameters
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

		// open the level with LabRectuits
		String labRecruitesExeRootDir = System.getProperty("user.dir");
		System.out.println(labRecruitesExeRootDir);
		LabRecruitsTestServer testServer = new LabRecruitsTestServer(false,
				Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));

		LabRecruitsConfig lrCfg = new LabRecruitsConfig(levelFileName);
		var environment = new LabRecruitsEnvironment(lrCfg);

		LabRecruitsTestAgent agent = new LabRecruitsTestAgent(agentName).attachState(new BeliefState())
				.attachEnvironment(environment);

		// get a feasible test case
		Boolean feasible = false;
		Testcase testcase = null;
		while( ! feasible) {
			RandomLengthTestFactory testFactory = new RandomLengthTestFactory(efsm);
			testcase = testFactory.getTestcase();
			ExecutionResult result = EFSMTestExecutor.getInstance().executeTestcase(testcase);
			if (result.isSuccess()) {
				feasible = true;
			}
		}
		
		// convert the test case ot a goal structure
		GoalStructure goals = convertTestCaseToGoalStructure(agent,(AbstractTestSequence) testcase);
		
		if (goals.getSubgoals().size() > 0) {
			agent.setGoal(goals);

	        // press play in Unity
	        if (! environment.startSimulation())
	            throw new InterruptedException("Unity refuses to start the Simulation!");

	        int i = 0 ;
	        while (goals.getStatus().inProgress()) {
	            agent.update();
	            System.out.println("*** " + i + "/" 
	               + agent.getState().worldmodel.timestamp + ", "
	               + agent.getState().id + " @" + agent.getState().worldmodel.position) ;
	            Thread.sleep(5);
	            i++ ;
	            if (i>200) {
	            	break ;
	            }
	        }
	        
	        goals.printGoalStructureStatus();
		}else {
			System.out.println("Cannot build a goal structure from test case ");
			System.out.println(toString());
			fail();
		}
	
		

		// close the server
		if (testServer != null)
			testServer.close();

	}
	
	
	// sample code for translating EFSM test case into a goal structure
	private GoalStructure convertTestCaseToGoalStructure(TestAgent agent, AbstractTestSequence tc) {
		Path path = tc.getPath();
		List<EFSMTransition> listTransitions = path.getTransitions();
		List<GoalStructure> subGoals = new LinkedList<GoalStructure>() ;
		for(EFSMTransition t : listTransitions) {
			LinkedList<GoalStructure> transitionGoals = convertEFMSTransitionToGoal(agent, t);
			for(GoalStructure g : transitionGoals) {
				subGoals.add(g);
			}			
		}
		
		GoalStructure testingTask = SEQ(subGoals.toArray(new GoalStructure[0])) ;
		return testingTask;
	}
	
	
	private LinkedList<GoalStructure> convertEFMSTransitionToGoal(TestAgent agent, EFSMTransition t ) {
		LinkedList<GoalStructure> subGoals = new LinkedList<GoalStructure>() ;
		// start refreshing the origin state
		//subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getSrc())));
		
		// look at src and tgt state to understand the type of transition
		if (t.getSrc().equals(t.getTgt())) {
			// if self loop we are pressing a button
			subGoals.add(GoalLib.entityInteracted(t.getTgt().getId())) ;
		}else if (oppositeDoorSides(t.getSrc(),t.getTgt())) {
			// to optimize
			String doorName = convertDoorSideToDoorName(t.getTgt().getId());
			
			subGoals.add(GoalLib.entityStateRefreshed(doorName));
  			//subGoals.add(GoalLib.entityInvariantChecked(agent, doorName, doorName+" should be open", (WorldEntity e) -> e.getBooleanProperty("isOpen"))) ;
  		}else {
  			subGoals.add(GoalLib.entityStateRefreshed(convertStateToString(t.getTgt()))) ;
  		}
		
		return subGoals;
	}
	
	private String convertStateToString(EFSMState s) {
		if (isDoor(s)) {
			return convertDoorSideToDoorName(s.getId());
		}else {
			return s.getId();
		}
	}
	
	// take a string in the form d1p/d1m and return door1
	private String convertDoorSideToDoorName(String doorSide) {
		String tmp = doorSide.substring( 1, doorSide.length() - 1 );
		return "door"+tmp;
	}
	
	private Boolean isDoor(EFSMState s) {
		String name = s.getId();
		String b = name.substring(0, 1);
		if (name.substring(0, 1).equals("b")) {
			return false;
		}else {
			return true;
		}
	}
	
	private Boolean oppositeDoorSides(EFSMState s, EFSMState t) {
		if (!isDoor(s) || !isDoor(t)) {
			// one of the state is not a door
			return false;
		}else if (convertDoorSideToDoorName(s.toString()).equals(convertDoorSideToDoorName(t.toString())) &&
				s.toString() != t.toString()){
			return true;
		}else {
			return false;
		}
	}
	
}
