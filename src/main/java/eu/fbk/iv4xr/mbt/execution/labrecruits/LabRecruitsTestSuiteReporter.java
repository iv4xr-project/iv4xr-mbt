package eu.fbk.iv4xr.mbt.execution.labrecruits;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;
import nl.uu.cs.aplib.mainConcepts.GoalStructure.PrimitiveGoal;

public class LabRecruitsTestSuiteReporter {
	
	private LinkedHashMap<AbstractTestSequence, List<LabRecruitsTestCaseReporter>> testCasesReporter;
	private LinkedHashMap<AbstractTestSequence, Boolean> testCasesStatus;
	private LinkedHashMap<AbstractTestSequence, Long> testCasesTime;
	
	public LabRecruitsTestSuiteReporter() {
		testCasesReporter = new LinkedHashMap<AbstractTestSequence, List<LabRecruitsTestCaseReporter>>();
		testCasesStatus = new LinkedHashMap<AbstractTestSequence, Boolean>();
		testCasesTime = new LinkedHashMap<AbstractTestSequence, Long>();
	}

	public void addTestCaseReport(AbstractTestSequence testcase, List<LabRecruitsTestCaseReporter> goalReport, Boolean status, Long duration ) {
		if (testCasesReporter.containsKey(testcase)) {
			throw new RuntimeException("Test case " + testcase.toString() + " already present");
		}else {
			testCasesReporter.put(testcase, goalReport);
			testCasesStatus.put(testcase, status);
			testCasesTime.put(testcase, duration);
		}
	}
	
	public Set<AbstractTestSequence> getTestCases(){
		return testCasesReporter.keySet();
	}
	
	public List<LabRecruitsTestCaseReporter> getTestCaseReport(AbstractTestSequence testCase) {
		if (testCasesReporter.containsKey(testCase)) {
			return testCasesReporter.get(testCase);
		}else {
			throw new RuntimeException("Test case " + testCase.toString() + " not present");
			
		}
	}
	
	public Boolean getTestCaseStatus(AbstractTestSequence testCase) {
		if (testCasesStatus.containsKey(testCase)) {
			return testCasesStatus.get(testCase);
		}else {
			throw new RuntimeException("Test case " + testCase.toString() + " not present");
		}
	}
	
	public Integer getNumberOfTestCases() {
		return testCasesReporter.size();
	}
	
	public Integer getNumberOfPassedTestCases() {
		Integer nPassed = 0;
		for(AbstractTestSequence test : testCasesStatus.keySet()) {
			if (testCasesStatus.get(test)) {
				nPassed ++;
			}
		}
		return nPassed;
	}
	
	public Long getTestSuiteTime() {
		Long time = 0L;
		for(AbstractTestSequence test : testCasesTime.keySet()) {
			time = time + (testCasesTime.get(test) / 1000);
		}
		return time;
	}
	
	/*
	 * Write debug information
	 */
	public String toString() {
		String out = "";
		
		out = out + "N tests performed: "+getNumberOfTestCases()+ "\n";
		out = out + "N tests passed: "+getNumberOfPassedTestCases()+ "\n\n";
		
		for(AbstractTestSequence test : testCasesReporter.keySet()) {
			List<LabRecruitsTestCaseReporter> caseReport = testCasesReporter.get(test);
			out = out + "################\nTest pass: " + testCasesStatus.get(test) + "\n";
			out = out + test.toString() + "\n";
			for(LabRecruitsTestCaseReporter rep : caseReport) {
				out = out + rep.toString();
			}
			out = out + "\n";
		
		}
		return(out);
	}
	
}
