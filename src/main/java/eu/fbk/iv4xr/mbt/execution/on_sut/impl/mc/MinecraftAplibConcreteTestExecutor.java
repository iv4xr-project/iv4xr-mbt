package eu.fbk.iv4xr.mbt.execution.on_sut.impl.mc;

import java.nio.file.Paths;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.fbk.iv4xr.mbt.concretization.TestConcretizer;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftConcreteTestCase;
import eu.fbk.iv4xr.mbt.concretization.impl.MinecraftTestConcretizer;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.execution.on_sut.AplibConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestSuiteExecutionReport;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.iv4xr.minecraftlib.MinecraftEnv;

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
public class MinecraftAplibConcreteTestExecutor implements AplibConcreteTestExecutor {
	private static ObjectMapper mapper = new ObjectMapper();
	protected EFSM model;
	private ArrayNode testCases;
	private ObjectNode meta;


	private MinecraftEnv environment;


	private TestConcretizer testConcretizer;
	protected long startTime = 0;

	private String debugTable = "";

	private TestSuiteExecutionReport reporter;
	protected int failures = 0;
	private HashMap<String, AbstractTestSequence> testCaseMap = new HashMap<>();

	public MinecraftAplibConcreteTestExecutor(EFSM model, String mineflayerTestURL, String levelPath,
			String agent,
			String mcServerAddress, int x, int y, int z) {
		this.model = model;
		this.startTime = System.currentTimeMillis();
		this.testCases = mapper.createArrayNode();
		this.meta = mapper.createObjectNode();

		environment = new MinecraftEnv(mineflayerTestURL);

		environment.buildLevel(levelPath, x, y, z);

		this.testConcretizer = new MinecraftTestConcretizer(model);

		this.reporter = new TestSuiteExecutionReport();


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
		return reporter;
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

		int res = runMineflayer();
		reportExecution();
		return res == 0;
	}

	// run a test case
	public boolean executeTestCase(AbstractTestSequence testcase) {
		MinecraftConcreteTestCase concreteTestcase = (MinecraftConcreteTestCase) testConcretizer
				.concretizeTestCase(testcase);

		String caseName = "test_" + (testCases.size() + 1);
		ObjectNode jsonTestcase = concreteTestcase.toJsonTestCase(caseName);
		testCases.add(jsonTestcase);

		testCaseMap.put(caseName, testcase);

		return true;
	}


	public String getDebugTable() {
		return debugTable;
	}

	private void reportExecution() {

	}

	@Override
	public void setMaxCyclePerGoal(int max) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setMaxCyclePerGoal'");
	}

	@Override
	public int getMaxCylcePerGoal() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getMaxCylcePerGoal'");
	}
}
