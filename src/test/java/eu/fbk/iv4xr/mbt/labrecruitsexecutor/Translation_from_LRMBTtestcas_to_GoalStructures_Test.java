package eu.fbk.iv4xr.mbt.labrecruitsexecutor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import agents.LabRecruitsTestAgent;
import agents.TestSettings;
import agents.tactics.GoalLib;
import nl.uu.cs.aplib.mainConcepts.*;
import static nl.uu.cs.aplib.AplibEDSL.* ;
import static org.junit.Assert.assertTrue;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import eu.iv4xr.framework.spatial.Vec3;
import game.LabRecruitsTestServer;
import game.Platform;
import world.BeliefState;

public class Translation_from_LRMBTtestcas_to_GoalStructures_Test {
	
	private static LabRecruitsTestServer labRecruitsTestServer;

    @BeforeAll
    static void start() {
    	// The default is to let Java launch LR. But if you prefer not to do that (so 
    	// an LR instance is already running, then uncomment this:
    	TestSettings.USE_SERVER_FOR_TEST = true ;
    	// Uncomment this to make the game's graphic visible:
    	// TestSettings.USE_GRAPHICS = true ;
    	String labRecruitesExeRootDir = System.getProperty("user.dir") ;
    	labRecruitsTestServer = TestSettings.start_LabRecruitsTestServer(labRecruitesExeRootDir) ;
    }

    @AfterAll
    static void close() { 
    	// close the game if it was launched by Java. Else this will leave it open.
    	if(labRecruitsTestServer!=null) 
    		labRecruitsTestServer.close(); 
    }
    
    /**
     * A simple setup to test the translation from EFSMtransitions to LabRecruits GoalStructures.
     */
    @Test
    public void test1_translations_from_EFSMtransitions_to_GoalStructures() throws Exception {
    	
    	TestSettings.youCanRepositionWindow();
    	
    	// set the level to play; this mini level:
    	var config = new LabRecruitsConfig("visibilitytest") ;
    	// create a test agent
        var agent = new LabRecruitsTestAgent("agent0") 
    		    . attachState(new BeliefState())
    		    . attachEnvironment(new LabRecruitsEnvironment(config))
    		    . setTestDataCollector(new TestDataCollector());
        
        
        //agent.getState().env().startSimulation();
        
        // Setting up some states and transitions on this example level, here we will
        // set only one button, one door, and one goal-flag for the agent to travel to:
        var start = new EFSMState("start") ;
        var button0 = new EFSMState("button0") ;
        var d0m = new EFSMState("d0m");
        var d0p = new EFSMState("d0p");
        var goalFlag = new EFSMState("goalFLAG") ;  
        // transitions:
        var tr0 = new EFSMTransition("tr0") ;
        var toggle_b0 = new EFSMTransition("toggle_b0") ;
        var to_d0 = new EFSMTransition("to_d0") ;
        var to_otherside_of_d0 = new EFSMTransition("to_otherside_of_d0") ;
        var to_goalflag =  new EFSMTransition("to_goalflag") ;
        // a dummy EFSM-builder; we just borrow it to build the transitions:
        var builder = new EFSMBuilder(null) ;
        builder
           .withTransition(start, button0, tr0)
           .withTransition(button0, button0, toggle_b0)
           .withTransition(button0, d0m, to_d0)
           .withTransition(d0m, d0p, to_otherside_of_d0)
           .withTransition(d0p, goalFlag, to_goalflag)
        ;
        
        // the testcase to translate:
        EFSMTransition[] testcase = { tr0, toggle_b0, to_d0, to_otherside_of_d0, to_goalflag } ;
        var converter = new LabRecruitsTestSuiteExecutor() ;
        // the resulting goal-structure:
        var goals = converter.convertTestCaseToGoalStructure(agent, Arrays.asList(testcase)) ;
        GoalStructure H = SEQ(goals.toArray(new GoalStructure[0])) ;
        
        // now let's run H:
        agent.setGoal(H) ;        
        int i = 0 ;
        // keep updating the agent
        while (H.getStatus().inProgress()) {
        	System.out.println("*** " + i + ", " + agent.getState().id + " @" + agent.getState().worldmodel.position) ;
            Thread.sleep(50);
            i++ ;
            agent.update();
        	if (i>50) {
        		break ;
        	}
        }
        assertTrue(H.getStatus().success()) ;
        assertTrue(agent.getTestDataCollector().getNumberOfFailVerdictsSeen() == 0) ;
   	
    }

    
    /**
     * Same test of test1, but goals are exectuted independently and not as a single goal seq
     */
    @Test
    public void test2_translations_from_EFSMtransitions_to_GoalStructures() throws Exception {
    	
    	TestSettings.youCanRepositionWindow();
    	
    	// set the level to play; this mini level:
    	var config = new LabRecruitsConfig("visibilitytest") ;
    	// create a test agent
        var agent = new LabRecruitsTestAgent("agent0") 
    		    . attachState(new BeliefState())
    		    . attachEnvironment(new LabRecruitsEnvironment(config))
    		    . setTestDataCollector(new TestDataCollector());
        
        
        //agent.getState().env().startSimulation();
        
        // Setting up some states and transitions on this example level, here we will
        // set only one button, one door, and one goal-flag for the agent to travel to:
        var start = new EFSMState("start") ;
        var button0 = new EFSMState("button0") ;
        var d0m = new EFSMState("d0m");
        var d0p = new EFSMState("d0p");
        var goalFlag = new EFSMState("goalFLAG") ;  
        // transitions:
        var tr0 = new EFSMTransition("tr0") ;
        var toggle_b0 = new EFSMTransition("toggle_b0") ;
        var to_d0 = new EFSMTransition("to_d0") ;
        var to_otherside_of_d0 = new EFSMTransition("to_otherside_of_d0") ;
        var to_goalflag =  new EFSMTransition("to_goalflag") ;
        // a dummy EFSM-builder; we just borrow it to build the transitions:
        var builder = new EFSMBuilder(null) ;
        builder
           .withTransition(start, button0, tr0)
           .withTransition(button0, button0, toggle_b0)
           .withTransition(button0, d0m, to_d0)
           .withTransition(d0m, d0p, to_otherside_of_d0)
           .withTransition(d0p, goalFlag, to_goalflag)
        ;
        
        // the testcase to translate:
        EFSMTransition[] testcase = { tr0, toggle_b0, to_d0, to_otherside_of_d0, to_goalflag } ;
        var converter = new LabRecruitsTestSuiteExecutor() ;
        // the resulting goal-structure:
        var goals = converter.convertTestCaseToGoalStructure(agent, Arrays.asList(testcase)) ;
        //GoalStructure H = SEQ(goals.toArray(new GoalStructure[0])) ;
        
        
        for(GoalStructure g : goals) {
        	 // now let's run H:
            agent.setGoal(g) ;        
            int i = 0 ;
            // keep updating the agent
            while (g.getStatus().inProgress()) {
            	System.out.println("*** " + i + ", " + agent.getState().id + " @" + agent.getState().worldmodel.position) ;
                Thread.sleep(50);
                i++ ;
                agent.update();
            	if (i>50) {
            		break ;
            	}
            }
            assertTrue(g.getStatus().success()) ;
            assertTrue(agent.getTestDataCollector().getNumberOfFailVerdictsSeen() == 0) ;
        }
       
   	
    }
    
}
