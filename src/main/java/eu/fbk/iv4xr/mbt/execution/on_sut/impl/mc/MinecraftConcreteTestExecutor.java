package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.IOException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import eu.fbk.iv4xr.mbt.concretization.GenericTestConcretizer;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftTestConcretizer;
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class transforms a test suite generated from an EFMS model of a
 * Minecraft test level into a MineflayerTestbed json file and run it.
 * 
 * @author guss-alberto
 *
 */
public class MinecraftConcreteTestExecutor implements ConcreteTestExecutor {
	private static ObjectMapper mapper = new ObjectMapper();

	private ArrayNode testCases;
	private ObjectNode meta;

	private String mineflayerTestDir;
	private Path jsonFilePath;

	private GenericTestConcretizer testConcretizer;

	public MinecraftConcreteTestExecutor(String mineflayerTestDir, String levelPath, String testsDir, String agent,
			String mcServerAddress, int x, int y, int z) {
		this.testCases = mapper.createArrayNode();
		this.meta = mapper.createObjectNode();
		this.mineflayerTestDir = mineflayerTestDir;

		this.testConcretizer = new MinecraftTestConcretizer();

		this.jsonFilePath = Paths.get(testsDir, "concrete_test.json");

		meta.put("id", MBTProperties.SUT_EFSM);
		meta.put("time", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(OffsetDateTime.now()));
		meta.put("level_csv", Paths.get(levelPath).toAbsolutePath().toString());
		meta.put("username", agent);
		meta.put("address", mcServerAddress);
		meta.put("x", x);
		meta.put("y", y);
		meta.put("z", z);

	}

	public TestSuiteExecutionReport getReport() {
		return new TestSuiteExecutionReport();
	}

	public boolean executeTestSuite(SuiteChromosome solution) {
		// cycle over the test cases
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence) solution.getTestChromosome(i).getTestcase();
			executeTestCase(testcase);
		}

		ObjectNode json = mapper.createObjectNode();
		json.set("meta", meta);
		json.set("test_cases", testCases);

		try {
			mapper.writeValue(jsonFilePath.toFile(), json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return runMineflayer() == 0;
	}

	// run a test case
	public boolean executeTestCase(AbstractTestSequence testcase) {
		MinecraftConcreteTestCase concreteTestcase = (MinecraftConcreteTestCase) testConcretizer
				.concretizeTestCase(testcase);
		ObjectNode jsonTestcase = concreteTestcase.getJsonTestCase("test_" + (testCases.size() + 1));
		testCases.add(jsonTestcase);

		return true;
	}

	private int runMineflayer() {
		ProcessBuilder processBuilder = new ProcessBuilder("npm", "start", "test=" + jsonFilePath.toAbsolutePath());
		processBuilder.directory(new java.io.File(mineflayerTestDir));

		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();
			return exitCode;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 255;
	}
}
