package eu.fbk.iv4xr.mbt.execution.on_sut.impl.se;

import java.io.File;
import java.util.LinkedHashMap;

import eu.fbk.iv4xr.mbt.execution.on_sut.TestExecutionHelper;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.lr.LabRecruitsConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

public class SpaceEngineersTestExecutionHelper extends TestExecutionHelper {

	
	
	public SpaceEngineersTestExecutionHelper(String seExecutableDir, String seGameSavePath, String testsDir, Integer maxCyclePerGoal) {
		
		testExecutor = new SpaceEngineersConcreteTestExecutor(seExecutableDir, seGameSavePath, maxCyclePerGoal);
		testToFileMap = new LinkedHashMap<AbstractTestSequence,File>();
		testSuite = parseTests (testsDir);
		testsFolder = testsDir;
		
	}
	
}
