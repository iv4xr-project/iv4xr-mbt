/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties.SecondaryObjective;
import eu.fbk.iv4xr.mbt.efsm.cps.TestToPoints;
import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

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

	public SuiteChromosome getTestSuite() {
		return solution;
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
	 * format the list of Pairs to csv entries and return them as string
	 * @param points
	 * @return
	 */
	public static String pointsToCsv(List<Pair<Integer, Integer>> points) {
		StringBuffer buffer = new StringBuffer();
		for (Pair<Integer, Integer> point : points) {
			buffer.append(point.toString("%1$s,%2$s") + "\n");
		}
		return buffer.toString();
	}
	
	public static void writeTests(SuiteChromosome solution, String testFolder) {
		// make sure tests folder exists
//		String testFolder = MBTProperties.TESTS_DIR() + File.separator + MBTProperties.SUT_EFSM + File.separator + MBTProperties.ALGORITHM + File.separator + MBTProperties.SessionId;
		File testsFolder = new File (testFolder);
		if (testsFolder.exists()) {
			try {
				FileUtils.deleteDirectory(testsFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		testsFolder.mkdirs();
		
		int count = 1;
		for (MBTChromosome testCase : solution.getTestChromosomes()) {
//			String dotFileName = testFolder + File.separator + "test_" + count + ".dot";
//			String txtFileName = testFolder + File.separator + "test_" + count + ".txt";
//			String serFileName = testFolder + File.separator + "test_" + count + ".ser";
			String csvFileName = testFolder + File.separator + "test_" + count + ".csv";
//			File dotFile = new File (dotFileName);
//			File txtFile = new File (txtFileName);
			File csvFile = new File (csvFileName);
			AbstractTestSequence abstractTestSequence = (AbstractTestSequence)testCase.getTestcase();
			try {
//				FileUtils.writeStringToFile(dotFile, abstractTestSequence.toDot(), Charset.defaultCharset());
//				FileUtils.writeStringToFile(txtFile, abstractTestSequence.toString(), Charset.defaultCharset());
//				TestSerializationUtils.saveTestSequence(abstractTestSequence, serFileName);
				// SBST competition specific
				List<Pair<Integer, Integer>> points = TestToPoints.getInstance().testcaseToPoints(abstractTestSequence);
				String pointsCsv = pointsToCsv(points);
				FileUtils.writeStringToFile(csvFile, pointsCsv, Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int mbt_generation_budget = Integer.parseInt(args[0]);
		
		int map_border_size = 10;
		double min_x = map_border_size;
		double min_y = map_border_size;
		double max_x = Integer.parseInt(args[1]) - map_border_size;
		double max_y = Integer.parseInt(args[1]) - map_border_size;
		double initial_x = 40;
		double initial_y = 40;
		int n_rotation = 12;
		double max_rotation_angle = 45;
		int min_street_length = 10;
		int max_street_length = 40;
		int street_length_step = 10;
		SBSTMain sbstMain = new SBSTMain(mbt_generation_budget, "beamng_model", min_x, min_y, max_x, max_y, initial_x, initial_y,
                n_rotation, max_rotation_angle, min_street_length, max_street_length, street_length_step);
		writeTests(sbstMain.getTestSuite(), args[2]);
//		while (sbstMain.hasMoreTests()) {
//			//List<Pair<Integer,Integer>> test = sbstMain.getNextTest();
//			List<Pair<Double,Double>> test = sbstMain.getNextTest();
//			System.out.println(test.toString());
//		}
		System.exit(0);
	}

}
