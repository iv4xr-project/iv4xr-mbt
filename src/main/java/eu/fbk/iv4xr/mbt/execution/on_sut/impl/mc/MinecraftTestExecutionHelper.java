package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.io.File;
import java.util.LinkedHashMap;

import eu.fbk.iv4xr.mbt.execution.on_sut.TestExecutionHelper;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;


public class MinecraftTestExecutionHelper extends TestExecutionHelper {

	public MinecraftTestExecutionHelper(String mineflayerTestDir, String mcLevelPath, String mcServerAddress, String testsDir, String agent, int x, int y, int z) {

		model = parseModel(testsDir);
		testExecutor = new MinecraftConcreteTestExecutor(model, mineflayerTestDir, mcLevelPath, testsDir, agent, mcServerAddress, x, y, z);
		testToFileMap = new LinkedHashMap<AbstractTestSequence,File>();
		testSuite = parseTests (testsDir);
		testsFolder = testsDir;
	}

	public String getDebugTableTable(){
		return "";
	}
	
	public String getStatsTable(){
		return "";
	}
	
}
