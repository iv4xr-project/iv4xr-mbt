/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tools.ant.types.Path;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.MBTProperties.Parameter;
import eu.fbk.iv4xr.mbt.MBTProperties.SecondaryObjective;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitMutationManager;
import eu.fbk.iv4xr.mbt.efsm.sbst2022.TestToPoints;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestExecutionHelper;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteExecutor;
import eu.fbk.iv4xr.mbt.execution.labrecruits.LabRecruitsTestSuiteReporter;
import eu.fbk.iv4xr.mbt.strategy.CoverageTracker;
import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.PlanningBasedStrategy;
import eu.fbk.iv4xr.mbt.strategy.RandomTestStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

/**
 * @author kifetew
 *
 * Main entry point to the MBT
 */
public class SBSTMain {

	protected static final Logger logger = LoggerFactory.getLogger(SBSTMain.class);
	SuiteChromosome solution;
	int index = 0;
	
	
	
	
	/**
	 * 
	 * @param searchBudget time (in milliseconds) for generating tests
	 * @param model the name of the model to use
	 */
	public SBSTMain(int searchBudget, String model,
			double beamng_min_x_coord,
			double beamng_min_y_coord,
			double beamng_max_x_coord,
			double beamng_max_y_coord,
			double beamng_initial_x_coord,
			double beamng_initial_y_coord,
			int beamng_n_directions,
			double beamng_max_angle,
			int beamng_min_street_length,
			int beamng_max_street_length,
			int beamng_beamng_street_chunck_length) {
		
		MBTProperties.SessionId = "" + System.currentTimeMillis();
		MBTProperties.SUT_EFSM = model;  //sbst2022.nine_states
		MBTProperties.SEARCH_BUDGET = searchBudget;
		MBTProperties.SHOW_PROGRESS = false;
		MBTProperties.SECONDARY_OBJECTIVE = new SecondaryObjective[] {  };
		
		// Set model parameters
		MBTProperties.beamng_min_x_coord = beamng_min_x_coord;
		MBTProperties.beamng_min_y_coord = beamng_min_y_coord;
		MBTProperties.beamng_max_x_coord = beamng_max_x_coord;
		MBTProperties.beamng_max_y_coord = beamng_max_y_coord;
		MBTProperties.beamng_initial_x_coord = beamng_initial_x_coord;
		MBTProperties.beamng_initial_y_coord = beamng_initial_y_coord;
		MBTProperties.beamng_n_directions = beamng_n_directions;
		MBTProperties.beamng_max_angle = beamng_max_angle;
		MBTProperties.beamng_min_street_length = beamng_min_street_length;
		MBTProperties.beamng_max_street_length = beamng_max_street_length;
		MBTProperties.beamng_street_chunck_length = beamng_beamng_street_chunck_length;
		
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		solution = generationStrategy.generateTests();
		
	}
	
	
	public SBSTMain(int searchBudget, String model) {
		
		MBTProperties.SessionId = "" + System.currentTimeMillis();
		MBTProperties.SUT_EFSM = model;  //sbst2022.nine_states
		MBTProperties.SEARCH_BUDGET = searchBudget;
		MBTProperties.SHOW_PROGRESS = false;
		
	
		GenerationStrategy generationStrategy = new SearchBasedStrategy<MBTChromosome>();
		solution = generationStrategy.generateTests();
		
	}
	

	public int totalTests () {
		if (solution != null) {
			return solution.size();
		}else {
			return 0;
		}
	}
	
	
	public boolean hasMoreTests () {
		if (solution == null || solution.size() == 0 || index >= solution.size()) {
			return false;
		}else {
			return true;
		}
	}
	
	//public List<Pair<Integer, Integer>> getNextTest(){
	public List<Pair<Double, Double>> getNextTest(){
		if (solution == null || solution.size() == 0 || index >= solution.size()) {
			return new ArrayList<>();
		}else {
			MBTChromosome mbtChromosome = solution.getTestChromosome(index++);
			AbstractTestSequence abstractTestSequence = (AbstractTestSequence)mbtChromosome.getTestcase();
			//List<Pair<Integer, Integer>> points = TestToPoints.getInstance().testcaseToPoints(abstractTestSequence);
			List<Pair<Double, Double>> points = TestToPoints.getInstance().testcaseToPoints(abstractTestSequence);
			return points;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SBSTMain sbstMain = new SBSTMain(60, "sbst2022.nine_states");
//		SBSTMain sbstMain = new SBSTMain(60, "beamng_model", 
//				10,10,190,190, // field
//				20,20, // initial position
//				36, // correspond to 2 degree min rotation
//				20, // max angle
//				10, // min street length
//				15 //max street length
//				);
		while (sbstMain.hasMoreTests()) {
			//List<Pair<Integer,Integer>> test = sbstMain.getNextTest();
			List<Pair<Double,Double>> test = sbstMain.getNextTest();
			System.out.println(test.toString());
		}
		System.exit(0);
	}

}
