/**
 * @author kifetew
 */
package eu.fbk.iv4xr.mbt.execution.on_sut;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.mbt.utils.TestSerializationUtils;

/**
 * 
 */
public abstract class TestExecutionHelper {

	protected ConcreteTestExecutor testExecutor;
	protected SuiteChromosome testSuite;
	
	protected String debugHeader = "run_id,testCase,testCaseStatus,transition,transitionReponse\n" ;
	
	protected String statHeader = "run_id,folder,n_test,n_test_passed,time\n";
	
	// save the map between the file and the test case
	protected LinkedHashMap<AbstractTestSequence, File > testToFileMap;
	
	// test folder
	protected String testsFolder;
	
	// id of the run
	protected String run_id;
	
	
	public File getTestCaseFile(AbstractTestSequence testSequence) {
		if (testToFileMap.containsKey(testSequence)) {
			return testToFileMap.get(testSequence);
		}else {
			throw new RuntimeException(testSequence.toString()+" not present");
		}
	}
	
	
	// setter and getter
	public String getStatHeader() {
		return statHeader;
	}
	
	public String getDebugHeader() {
		return debugHeader;
	}

	/**
	 * Load serialized tests into a SuiteChromosome object
	 * @param testsDir
	 * @return
	 */
	protected  SuiteChromosome parseTests(String testsDir) {
		SuiteChromosome suite = new SuiteChromosome();
		try {
			WildcardFileFilter fileFilter = WildcardFileFilter.builder().setWildcards("*.ser").get();
			Collection<File> files = FileUtils.listFiles(new File(testsDir), fileFilter, null);
			for (File file : files) {
				AbstractTestSequence test = TestSerializationUtils.loadTestSequence(file.getAbsolutePath());
				MBTChromosome chromosome = new MBTChromosome();
				chromosome.setTestcase(test);
				suite.addTest(chromosome ); 
				// save file to test map
				testToFileMap.put(test,file);
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
			this.run_id = String.valueOf(System.currentTimeMillis());
			success = testExecutor.executeTestSuite(testSuite);
		} catch (InterruptedException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	


	/*
	 * Create statistics table
	 */
	public abstract String getDebugTableTable();
	
	public abstract String getStatsTable();
	
	
	
	public TestSuiteExecutionReport getExecutionReport() {
		
		return testExecutor.getReport();
	}
}
