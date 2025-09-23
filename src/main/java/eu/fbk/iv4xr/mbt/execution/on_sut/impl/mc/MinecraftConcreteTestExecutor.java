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
import eu.fbk.iv4xr.mbt.execution.on_sut.ConcreteTestExecutor;
import eu.fbk.iv4xr.mbt.execution.on_sut.TestCaseExecutionReport;
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
	protected EFSM model;
	private ArrayNode testCases;
	private ObjectNode meta;

	private String mineflayerTestDir;
	private Path jsonFilePath;
	private Path outputCsv;

	private TestConcretizer testConcretizer;
	protected long startTime = 0;

	private String debugTable = "";

	private TestSuiteExecutionReport reporter;
	protected int failures = 0;
	private HashMap<String, AbstractTestSequence> testCaseMap = new HashMap<>();

	public MinecraftConcreteTestExecutor(EFSM model, String mineflayerTestDir, String levelPath, String testsDir,
			String agent,
			String mcServerAddress, int x, int y, int z) {
		this.model = model;
		this.startTime = System.currentTimeMillis();
		this.testCases = mapper.createArrayNode();
		this.meta = mapper.createObjectNode();
		this.mineflayerTestDir = mineflayerTestDir;

		this.testConcretizer = new MinecraftTestConcretizer(model);

		this.reporter = new TestSuiteExecutionReport();

		this.jsonFilePath = Paths.get(testsDir, "concrete_test.json");
		this.outputCsv = Paths.get(testsDir, "execution_log.csv");

		meta.put("id", MBTProperties.SUT_EFSM);
		meta.put("time", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(OffsetDateTime.now()));
		meta.put("level_csv", Paths.get(levelPath).toAbsolutePath().toString());
		meta.put("output_csv", outputCsv.toAbsolutePath().toString());
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
		ObjectNode jsonTestcase = concreteTestcase.getJsonTestCase(caseName);
		testCases.add(jsonTestcase);

		testCaseMap.put(caseName, testcase);

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

	public String getDebugTable() {
		return debugTable;
	}

	private void reportExecution() {
		String currentTestCase = null;
		boolean prevRes = true;
		long startTime = this.startTime;
		HashMap<String, Integer> header = null;

		try (BufferedReader br = new BufferedReader(new FileReader(outputCsv.toFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				// parse quoted commas correctly
				String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

				if (header == null) {
					header = new HashMap<>();
					for (int i = 0; i < values.length; i++) {
						header.put(values[i].trim(), i);
					}
					continue;
				}

				String testCase = values[header.get("test_case")];
				String version = values[header.get("game_version")];
				long time = Long.parseLong(values[header.get("time")]);
				String result = values[header.get("result")];
				String actionDetails = values[header.get("action_details")];
				boolean passed = values[header.get("passed")].equals("true");

				if (!passed) {
					failures++;
				}

				if (currentTestCase != null && !testCase.equals(currentTestCase)) {
					List<TestCaseExecutionReport> caseReport = new ArrayList<>();
					caseReport.add(new TestCaseExecutionReport());
					reporter.addTestCaseReport(testCaseMap.get(testCase), caseReport, prevRes, time - startTime);
					startTime = time;
				}

				String status = null;

				if (result.equals("")) {
					status = "SUCCESS. Action returned no value";
				} else {
					status = (passed ? "SUCCESS" : "FAILURE") + ". Action retuned " + result + " on " + version;
				}

				debugTable += startTime + "," +
						testCase + "," +
						passed + "," +
						actionDetails + ","
						+ (passed ? "Passed" : "Failed") + ","
						+ ","
						+ status + "\n";

				currentTestCase = testCase;
				prevRes = passed;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		reporter.addTestCaseReport(null, null, null, null);
	}
}
