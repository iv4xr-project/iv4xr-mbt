package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import eu.fbk.iv4xr.mbt.execution.on_sut.AplibTestExecutionHelper;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;

/**
 * A class that loads tests from disk and executs them on a given LabRecruits binary
 * @author kifetew
 *
 */
public class MinecraftAplibTestExecutionHelper extends AplibTestExecutionHelper {
	public MinecraftAplibTestExecutionHelper(String mineflayerTestURL, String minecraftServerURL, String mcLevelPath,
			String testsDir, String agent, int x, int y, int z) throws IOException {
		model = parseModel(testsDir);
		testExecutor = new MinecraftAplibConcreteTestExecutor(model, mineflayerTestURL, minecraftServerURL, mcLevelPath, agent, x, y, z);
		testToFileMap = new LinkedHashMap<AbstractTestSequence,File>();
		testSuite = parseTests (testsDir);
		testsFolder = testsDir;
	}
}
