package eu.fbk.iv4xr.mbt.execution.labrecruits;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.shared.utils.io.FileUtils;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

/**
 * A class that loads tests from disk and executs them on a given LabRecruits binary
 * @author kifetew
 *
 */
public class LabRecruitsTestExecutionHelper {

	LabRecruitsTestSuiteExecutor lrExecutor;
	SuiteChromosome testSuite;
	public LabRecruitsTestExecutionHelper(String lrExecutableDir, String lrLevelPath, String agentName, String testsDir, Integer maxCyclePerGoal) {
		lrExecutor = new LabRecruitsTestSuiteExecutor(lrExecutableDir, lrLevelPath, agentName, maxCyclePerGoal);
		testSuite = parseTests (testsDir);
	}

	/**
	 * Load serialized tests into a SuiteChromosome object
	 * @param testsDir
	 * @return
	 */
	private static SuiteChromosome parseTests(String testsDir) {
		SuiteChromosome suite = new SuiteChromosome();
		try {
			List<File> files = FileUtils.getFiles(new File(testsDir), "*.ser", "");
			for (File file : files) {
				AbstractTestSequence test = TestSerializationUtils.loadTestSequence(file.getAbsolutePath());
				MBTChromosome chromosome = new MBTChromosome<>();
				chromosome.setTestcase(test);
				suite.addTest(chromosome );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suite;
	}

	public boolean execute() {
		boolean success = true;
		try {
			lrExecutor.executeTestSuite(testSuite);
		} catch (InterruptedException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	public static void main(String[] args) {
		String level_file = "/Users/prandi/Google Drive/iv4XR/github/iv4xr-project/iv4xr-mbt/devel_ff/mbt-files/tests/labrecruits.random_default/MOSA/1619810829804/Model/LabRecruits_level";
		String agentName = "Agent1";
		String execDir = "/Users/prandi/Google Drive/iv4XR/github/iv4xr-project/iv4xr-mbt/devel_ff/";
		String testsDir = "/Users/prandi/Google Drive/iv4XR/github/iv4xr-project/iv4xr-mbt/devel_ff/mbt-files/tests/labrecruits.random_default/MOSA/1619810829804/";
		Integer maxCyclePerGoal = 500;
		//SuiteChromosome suite = parseTests(testsDir);
//		System.out.println(suite.size());
//		for (MBTChromosome t : suite.getTestChromosomes()) {
//			System.out.println(t.toString());
//		}
		LabRecruitsTestExecutionHelper helper = new LabRecruitsTestExecutionHelper(execDir, level_file, agentName, testsDir, maxCyclePerGoal);
		helper.execute();
	}

}
