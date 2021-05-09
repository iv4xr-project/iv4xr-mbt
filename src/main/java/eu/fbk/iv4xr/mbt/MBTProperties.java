/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties.DoubleValue;
import org.evosuite.Properties.Parameter;
import org.evosuite.Properties.RankingType;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.utils.FileIOUtils;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kifetew
 *
 */
public class MBTProperties {

	private final static Logger logger = LoggerFactory.getLogger(MBTProperties.class);

	
	/**
	 * Parameters are fields of the Properties class, annotated with this
	 * annotation. The key parameter is used to identify values in property
	 * files or on the command line, the group is used in the config file or
	 * input plugins to organize parameters, and the description is also
	 * displayed there.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Parameter {
		String key();

		String group() default "Experimental";

		String description();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface IntValue {
		int min() default Integer.MIN_VALUE;

		int max() default Integer.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface LongValue {
		long min() default Long.MIN_VALUE;

		long max() default Long.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface DoubleValue {
		double min() default -(Double.MAX_VALUE - 1); // FIXXME: Check

		double max() default Double.MAX_VALUE;
	}
	
	/*
	 * Public parameters, follow definitions in Properties.java in Evosuite
	 */

	
	@Parameter(key = "show_progress", group = "Output", description = "Show progress bar on console")
	public static boolean SHOW_PROGRESS = true;
	
	@Parameter(key = "random_seed", group = "Search Algorithm", description = "Random number seed for MBT")
	public static long RANDOM_SEED = 123456778;
	
	@Parameter(key = "sut_efsm", group = "Search Algorithm", description = "ID of the EFSM for the current SUT")
	public static String SUT_EFSM = "labrecruits.random_default"; // "labrecruits.buttons_doors_fire"; // 
	
	@Parameter(key = "population", group = "Search Algorithm", description = "Population size of genetic algorithm")
	@IntValue(min = 1)
	public static int POPULATION = 50;

	@Parameter(key = "max_path_length", group = "Search Algorithm", description = "Maximum length of a path derived from EFSM")
	@IntValue(min = 1)
	public static int MAX_PATH_LENGTH = 100;
	
	public enum TestFactory{
		RANDOM_LENGTH, RANDOM_LENGTH_PARAMETER;
	}
	
	@Parameter(key = "test_factory", group = "Search Algorithm", description = "Test factory")
	public static TestFactory TEST_FACTORY = TestFactory.RANDOM_LENGTH;
	
	public enum PopulationLimit {
		INDIVIDUALS, TESTS, STATEMENTS;
	}

	@Parameter(key = "population_limit", group = "Search Algorithm", description = "What to use as limit for the population size")
	public static PopulationLimit POPULATION_LIMIT = PopulationLimit.INDIVIDUALS;
	
	@Parameter(key = "search_budget", group = "Search Algorithm", description = "Maximum search duration")
	@LongValue(min = 1)
	public static long SEARCH_BUDGET = 60;

	@Parameter(key = "OUTPUT_DIR", group = "Runtime", description = "Directory in which to put generated files")
	public static String OUTPUT_DIR = "mbt-files";

	public static String PROPERTIES_FILE = OUTPUT_DIR + File.separator + "evosuite.properties";

	
	public static String TESTS_DIR = OUTPUT_DIR + File.separator + "tests";
	
	
	public static String STATISTICS_DIR = OUTPUT_DIR + File.separator + "statistics";
	
	
	public static String STATISTICS_FILE = STATISTICS_DIR + File.separator + "statistics.csv";
	
	public static String EXECUTIONSTATISTICS_FILE = STATISTICS_DIR + File.separator + "execution_statistics.csv";
	
	public static String EXECUTIONDEBUG_FILE = STATISTICS_DIR + File.separator + "execution_debug.csv";
	
	@Parameter(key = "statistics_interval", group = "SUT execution", description = "Duration of statistics snapshot (in seconds, should be less than search budget)")
	@LongValue(min = 1)
	public static long STATISTICS_INTERVAL = 10;
	
	public static String MUTATION_ANALYSIS_FOLDER = OUTPUT_DIR + File.separator + "mutations";
	
	public static String MUTATION_STATISTIC_FILE = STATISTICS_DIR + File.separator + "mutation_statistics.csv";
	
	@Parameter(key = "max_mutations", group = "SUT execution", description = "Maximum number of mutations to execute")
	@IntValue(min = 1)
	public static int MAX_NUMBER_MUTATIONS = 10;

	
	public enum StoppingCondition {
		MAXSTATEMENTS, MAXTESTS,
        /** Max time in seconds */ MAXTIME,
        MAXGENERATIONS, MAXFITNESSEVALUATIONS, TIMEDELTA
	}


	@Parameter(key = "stopping_condition", group = "Search Algorithm", description = "What condition should be checked to end the search")
	public static StoppingCondition STOPPING_CONDITION = StoppingCondition.MAXTIME;

	public enum CrossoverFunction {
		SINGLEPOINTRELATIVE, SINGLEPOINTFIXED, SINGLEPOINT, COVERAGE, UNIFORM, EXTENDEDSINGLEPOINTRELATIVE
	}

	@Parameter(key = "crossover_function", group = "Search Algorithm", description = "Crossover function during search")
	public static CrossoverFunction CROSSOVER_FUNCTION = CrossoverFunction.SINGLEPOINTRELATIVE;
	
	
	@Parameter(key = "crossover_rate", group = "Search Algorithm", description = "Probability of crossover")
	@DoubleValue(min = 0.0, max = 1.0)
	public static double CROSSOVER_RATE = 0.75;

	@Parameter(key = "headless_chicken_test", group = "Search Algorithm", description = "Activate headless chicken test")
	public static boolean HEADLESS_CHICKEN_TEST = false;

//	@Parameter(key = "mutation_rate", group = "Search Algorithm", description = "Probability of mutation")
//	@DoubleValue(min = 0.0, max = 1.0)
//	public static double MUTATION_RATE = 0.75;
	
	
	public enum SecondaryObjective {
		AVG_LENGTH, MAX_LENGTH, TOTAL_LENGTH, SIZE, EXCEPTIONS, IBRANCH, RHO
	}

	@Parameter(key = "secondary_objectives", group = "Search Algorithm", description = "Secondary objective during search")
	public static SecondaryObjective[] SECONDARY_OBJECTIVE = new SecondaryObjective[] { SecondaryObjective.TOTAL_LENGTH };

	
	@Parameter(key = "bloat_factor", group = "Search Algorithm", description = "Maximum relative increase in length")
	public static int BLOAT_FACTOR = 2;

	@Parameter(key = "stop_zero", group = "Search Algorithm", description = "Stop optimization once goal is covered")
	public static boolean STOP_ZERO = false;

	@Parameter(key = "dynamic_limit", group = "Search Algorithm", description = "Multiply search budget by number of test goals")
	public static boolean DYNAMIC_LIMIT = false;

	@Parameter(key = "global_timeout", group = "Search Algorithm", description = "Maximum seconds allowed for entire search when not using time as stopping criterion")
	@IntValue(min = 0)
	public static int GLOBAL_TIMEOUT = 120;

	@Parameter(key = "minimization_timeout", group = "Search Algorithm", description = "Seconds allowed for minimization at the end")
	@IntValue(min = 0)
	public static int MINIMIZATION_TIMEOUT = 60;

    @Parameter(key = "assertion_timeout", group = "Search Algorithm", description = "Seconds allowed for assertion generation at the end")
    @IntValue(min = 0)
    public static int ASSERTION_TIMEOUT = 60;
    
    @Parameter(key = "max_length", group = "Test Creation", description = "Maximum length of test suites (0 = no check)")
	public static int MAX_LENGTH = 0;
    
	@Parameter(key = "breeder_truncation", group = "Search Algorithm", description = "Percentage of population to use for breeding in breeder GA")
	@DoubleValue(min = 0.01, max = 1.0)
	public static double TRUNCATION_RATE = 0.5;

	@Parameter(key = "number_of_mutations", group = "Search Algorithm", description = "Number of single mutations applied on an individual when a mutation event occurs")
	public static int NUMBER_OF_MUTATIONS = 1;

	@Parameter(key = "p_test_insertion", group = "Search Algorithm", description = "Initial probability of inserting a new test in a test suite")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_TEST_INSERTION = 0.1;

	@Parameter(key = "p_statement_insertion", group = "Search Algorithm", description = "Initial probability of inserting a new statement in a test case")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_STATEMENT_INSERTION = 0.5;

	@Parameter(key = "p_change_parameter", group = "Search Algorithm", description = "Probability of replacing parameters when mutating a method or constructor statementa in a test case")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_CHANGE_PARAMETER = 0.1;

	@Parameter(key = "p_test_delete", group = "Search Algorithm", description = "Probability of deleting statements during mutation")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_TEST_DELETE = 1d / 3d;

	@Parameter(key = "p_test_change", group = "Search Algorithm", description = "Probability of changing statements during mutation")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_TEST_CHANGE = 1d / 3d;

	@Parameter(key = "p_test_insert", group = "Search Algorithm", description = "Probability of inserting new statements during mutation")
    @DoubleValue(min = 0.0, max = 1.0)
	public static double P_TEST_INSERT = 1d / 3d;

	@Parameter(key = "kincompensation", group = "Search Algorithm", description = "Penalty for duplicate individuals")
	@DoubleValue(min = 0.0, max = 1.0)
	public static double KINCOMPENSATION = 1.0;

	@Parameter(key = "elite", group = "Search Algorithm", description = "Elite size for search algorithm")
	public static int ELITE = 1;  
    
    // evosuite 1.0.7
    public enum Strategy {
	    SUITE, DYNAMOSA, GA, RANDOM, RANDOM_FIXED, NOVELTY, MAP_ELITES, MODEL_CHECKING
	}
    
	@Parameter(key = "strategy", group = "Runtime", description = "Which mode to use")
	public static Strategy STRATEGY = Strategy.GA;
	
	
	public enum Algorithm {
		// random
		RANDOM_SEARCH,
		// GAs
		STANDARD_GA, MONOTONIC_GA, STEADY_STATE_GA, BREEDER_GA, CELLULAR_GA, STANDARD_CHEMICAL_REACTION, MAP_ELITES,
		// mu-lambda
		ONE_PLUS_LAMBDA_LAMBDA_GA, ONE_PLUS_ONE_EA, MU_PLUS_LAMBDA_EA, MU_LAMBDA_EA,
		// many-objective algorithms
		MOSA, MIO,
		// multiple-objective optimisation algorithms
		NSGAII, SPEA2
	}
	
	@Parameter(key = "algorithm", group = "Search Algorithm", description = "Search algorithm")
	public static Algorithm ALGORITHM = Algorithm.MOSA; // .MONOTONIC_GA;
	
	
	public enum ModelCriterion {
		STATE, TRANSITION, KTRANSITION, PATH
	}
	
	@Parameter(key = "modelcriterion", group = "Search Algorithm", description = "Model coverage criterion")
	public static ModelCriterion[] MODELCRITERION = new ModelCriterion[] {
		ModelCriterion.TRANSITION //, ModelCriterion.STATE
	};
	
	// MOSA PROPERTIES
	public enum RankingType {
		// Preference sorting is the ranking strategy proposed in
		PREFERENCE_SORTING, 
		FAST_NON_DOMINATED_SORTING
	}

	@Parameter(key = "ranking_type", group = "Runtime", description = "type of ranking to use in MOSA")
	public static RankingType RANKING_TYPE = RankingType.PREFERENCE_SORTING;
	
    @Parameter(key = "PROJECT_PREFIX", group = "Runtime", description = "Package name of target package")
	public static String PROJECT_PREFIX = "";

	@Parameter(key = "PROJECT_DIR", group = "Runtime", description = "Directory name of target package")
	public static String PROJECT_DIR = null;
	
	
	/**
	 * Random Lab Recruits level generation parameters
	 */
	@Parameter(key = "LR_seed", group = "Lab Recruits", description = "Seed for Lab Recruits random level generation")
	public static long LR_seed = 123;
	
	@Parameter(key = "LR_n_buttons", group = "Lab Recruits", description = "Total number of buttons in the level")
	public static int LR_n_buttons = 5;
	
	@Parameter(key = "LR_n_doors", group = "Lab Recruits", description = "Total number of doors in the level")
	public static int LR_n_doors = 4;
	
	@Parameter(key = "LR_mean_buttons", group = "Lab Recruits", description = "Expected number of buttons in a room")
	public static double LR_mean_buttons = 1;
	
	@Parameter(key = "LR_n_rooms", group = "Lab Recruits", description = "Number of rooms")
	public static int LR_n_rooms = 3;
	
	/**
	 * Random generation has four parameters:
	 * - number of buttons
	 * - number of doors
	 * - number of rooms
	 * - mean number of buttons per room
	 * Random generation mode fixed on parameter has dependent.
	 */
	public enum LR_random_mode {
		// the number of rooms depends on the number of buttons and the mean buttons per room
		N_ROOMS_DEPENDENT, 
		// the number of buttons depends on the number of rooms and the mean number of buttons
		N_BUTTONS_DEPENDENT,
		// the user select the number of rooms and buttons and the buttons are uniformly distributed in the rooms
		UNIFORM_BUTTON_DISTRIBUTION
	}
	
	@Parameter(key = "LR_generation_mode", group = "Lab Recruits", description = "Number of rooms")
	public static LR_random_mode LR_generation_mode = LR_random_mode.N_ROOMS_DEPENDENT;
	
	
	/**
	 * Number of maximum tries to generate a randoma level
	 * 
	 */
	@Parameter(key = "LR_n_try_generation", group = "Lab Recruits", description = "Number of tries to generate a LR level")
	public static int LR_n_try_generation = 3;
	
	/**
	 * Session id to identify an experiment
	 * 
	 */
	@Parameter(key = "SessionId", group = "Runtime", description = "String that identify an experiment")
	public static String SessionId = "default_session";
	
	
	/**
	 * Get all parameters that are available
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public static Set<String> getParameters() {
		return parameterMap.keySet();
	}

	/**
	 * Determine fields that are declared as parameters
	 */
	private static void reflectMap() {
		for (Field f : MBTProperties.class.getFields()) {
			if (f.isAnnotationPresent(Parameter.class)) {
				Parameter p = f.getAnnotation(Parameter.class);
				parameterMap.put(p.key(), f);
				try {
					defaultMap.put(f, f.get(null));
				} catch (Exception e) {
					logger.error("Exception: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Initialize properties from property file or command line parameters
	 */
	private void initializeProperties() throws IllegalStateException{
		for (String parameter : parameterMap.keySet()) {
			try {
				String property = System.getProperty(parameter);
				if (property == null) {
					property = properties.getProperty(parameter);
				}
				if (property != null) {
					setValue(parameter, property);
				}
			} catch (Exception e) {
                throw new IllegalStateException("Wrong parameter settings for '" + parameter + "': " + e.getMessage());
            }
		}
		if (POPULATION_LIMIT == PopulationLimit.STATEMENTS) {
			if (MAX_LENGTH < POPULATION) {
				MAX_LENGTH = POPULATION;
			}
		}
	}

	/**
	 * Load and initialize a properties file from the default path
	 */
	public void loadProperties(boolean silent) {
		loadPropertiesFile(System.getProperty(PROPERTIES_FILE,
				"evosuite-files/evosuite.properties"), silent);
		initializeProperties();
	}

	/**
	 * Load and initialize a properties file from a given path
	 *
	 * @param propertiesPath
	 *            a {@link java.lang.String} object.
	 */
	public void loadProperties(String propertiesPath, boolean silent) {
		loadPropertiesFile(propertiesPath, silent);
		initializeProperties();
	}

	/**
	 * Load a properties file
	 *
	 * @param propertiesPath
	 *            a {@link java.lang.String} object.
	 */
	public void loadPropertiesFile(String propertiesPath, boolean silent) {
		properties = new java.util.Properties();
		try {
			InputStream in = null;
			File propertiesFile = new File(propertiesPath);
			if (propertiesFile.exists()) {
				in = new FileInputStream(propertiesPath);
				properties.load(in);

				if (!silent)
					LoggingUtils.getEvoLogger().info(
							"* Properties loaded from "
									+ propertiesFile.getAbsolutePath());
			} else {
				propertiesPath = "evosuite.properties";
				in = this.getClass().getClassLoader()
						.getResourceAsStream(propertiesPath);
				if (in != null) {
					properties.load(in);
					if (!silent)
						LoggingUtils.getEvoLogger().info(
								"* Properties loaded from "
										+ this.getClass().getClassLoader()
												.getResource(propertiesPath)
												.getPath());
				}
				// logger.info("* Properties loaded from default configuration file.");
			}
		} catch (FileNotFoundException e) {
			logger.warn("- Error: Could not find configuration file "
					+ propertiesPath);
		} catch (IOException e) {
			logger.warn("- Error: Could not find configuration file "
					+ propertiesPath);
		} catch (Exception e) {
			logger.warn("- Error: Could not find configuration file "
					+ propertiesPath);
		}
	}

	/** All fields representing values, inserted via reflection */
	private static Map<String, Field> parameterMap = new HashMap<String, Field>();

	/** All fields representing values, inserted via reflection */
	private static Map<Field, Object> defaultMap = new HashMap<Field, Object>();

	static {
		// need to do it once, to capture all the default values
		reflectMap();
	}

	/**
	 * Keep track of which fields have been changed from their defaults during
	 * loading
	 */
	private static Set<String> changedFields = new HashSet<String>();

	/**
	 * Get class of parameter
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link java.lang.Class} object.
	 */
	public static Class<?> getType(String key) throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		return f.getType();
	}

	/**
	 * Get description string of parameter
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getDescription(String key)
			throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		Parameter p = f.getAnnotation(Parameter.class);
		return p.description();
	}

	/**
	 * Get group name of parameter
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getGroup(String key) throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		Parameter p = f.getAnnotation(Parameter.class);
		return p.group();
	}

	/**
	 * Get integer boundaries
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link org.evosuite.Properties.IntValue} object.
	 */
	public static IntValue getIntLimits(String key)
			throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		return f.getAnnotation(IntValue.class);
	}

	/**
	 * Get long boundaries
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link org.evosuite.Properties.LongValue} object.
	 */
	public static LongValue getLongLimits(String key)
			throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		return f.getAnnotation(LongValue.class);
	}

	/**
	 * Get double boundaries
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @return a {@link org.evosuite.Properties.DoubleValue} object.
	 */
	public static DoubleValue getDoubleLimits(String key)
			throws NoSuchParameterException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		return f.getAnnotation(DoubleValue.class);
	}

	/**
	 * Get an integer parameter value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @return a int.
	 */
	public static int getIntegerValue(String key)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		return parameterMap.get(key).getInt(null);
	}

	/**
	 * Get an integer parameter value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @return a long.
	 */
	public static long getLongValue(String key)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		return parameterMap.get(key).getLong(null);
	}

	/**
	 * Get a boolean parameter value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @return a boolean.
	 */
	public static boolean getBooleanValue(String key)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		return parameterMap.get(key).getBoolean(null);
	}

	/**
	 * Get a double parameter value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @return a double.
	 */
	public static double getDoubleValue(String key)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		return parameterMap.get(key).getDouble(null);
	}

	/**
	 * Get parameter value as string (works for all types)
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getStringValue(String key)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		StringBuffer sb = new StringBuffer();
		Object val = parameterMap.get(key).get(null);
		if (val != null && val.getClass().isArray()) {
			int len = Array.getLength(val);
			for (int i = 0; i < len; i++) {
				if (i > 0)
					sb.append(";");

				sb.append(Array.get(val, i));
			}
		} else {
			sb.append(val);
		}
		return sb.toString();
	}

	/**
	 * Check if there exist any parameter with given name
	 *
	 * @param parameterName
	 * @return
	 */
	public static boolean hasParameter(String parameterName) {
		return parameterMap.containsKey(parameterName);
	}

	/**
	 * Set parameter to new integer value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            a int.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 */
	public void setValue(String key, int value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);

		if (f.isAnnotationPresent(IntValue.class)) {
			IntValue i = f.getAnnotation(IntValue.class);
			if (value < i.min() || value > i.max())
				throw new IllegalArgumentException();
		}

		f.setInt(this, value);
	}

	/**
	 * Set parameter to new long value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            a long.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 */
	public void setValue(String key, long value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);

		if (f.isAnnotationPresent(LongValue.class)) {
			LongValue i = f.getAnnotation(LongValue.class);
			if (value < i.min() || value > i.max())
				throw new IllegalArgumentException();
		}

		f.setLong(this, value);
	}

	/**
	 * Set parameter to new boolean value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            a boolean.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 */
	public void setValue(String key, boolean value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		f.setBoolean(this, value);
	}

	/**
	 * Set parameter to new double value
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            a double.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 */
	public void setValue(String key, double value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key))
			throw new NoSuchParameterException(key);

		Field f = parameterMap.get(key);
		if (f.isAnnotationPresent(DoubleValue.class)) {
			DoubleValue i = f.getAnnotation(DoubleValue.class);
			if (value < i.min() || value > i.max())
				throw new IllegalArgumentException();
		}
		f.setDouble(this, value);
	}

	/**
	 * Set parameter to new value from String
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            a {@link java.lang.String} object.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(String key, String value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key)) {
			throw new NoSuchParameterException(key);
		}

		Field f = parameterMap.get(key);
		changedFields.add(key);

		//Enum
		if (f.getType().isEnum()) {
			f.set(null, Enum.valueOf((Class<Enum>) f.getType(),
					value.toUpperCase()));
		}
		//Integers
		else if (f.getType().equals(int.class)) {
			setValue(key, Integer.parseInt(value));
		} else if (f.getType().equals(Integer.class)) {
			setValue(key, (Integer) Integer.parseInt(value));
		}
		//Long
		else if (f.getType().equals(long.class)) {
			setValue(key, Long.parseLong(value));
		} else if (f.getType().equals(Long.class)) {
			setValue(key, (Long) Long.parseLong(value));
		}
		//Boolean
		else if (f.getType().equals(boolean.class)) {
			setValue(key, strictParseBoolean(value));
		} else if (f.getType().equals(Boolean.class)) {
			setValue(key, (Boolean) strictParseBoolean(value));
		}
		//Double
		else if (f.getType().equals(double.class)) {
			setValue(key, Double.parseDouble(value));
		} else if (f.getType().equals(Double.class)) {
			setValue(key, (Double) Double.parseDouble(value));
		}
		//Array
		else if (f.getType().isArray()) {
			if (f.getType().isAssignableFrom(String[].class)) {
				setValue(key, value.split(":"));
			} else if (f.getType().getComponentType().equals(ModelCriterion.class)) {
				String[] values = value.split(":");
				ModelCriterion[] criteria = new ModelCriterion[values.length];

				int pos = 0;
				for (String stringValue : values) {
					criteria[pos++] = Enum.valueOf(ModelCriterion.class,
							stringValue.toUpperCase());
				}

				f.set(this, criteria);
			}
		} else {
			f.set(null, value);
		}
	}

	/**
	 * we need this strict function because Boolean.parseBoolean silently
	 * ignores malformed strings
	 *
	 * @param s
	 * @return
	 */
	protected boolean strictParseBoolean(String s) {
		if (s == null || s.isEmpty()) {
			throw new IllegalArgumentException(
					"empty string does not represent a valid boolean");
		}

		if (s.equalsIgnoreCase("true")) {
			return true;
		}

		if (s.equalsIgnoreCase("false")) {
			return false;
		}

		throw new IllegalArgumentException(
				"Invalid string representing a boolean: " + s);
	}

	/**
	 * <p>
	 * setValue
	 * </p>
	 *
	 * @param key
	 *            a {@link java.lang.String} object.
	 * @param value
	 *            an array of {@link java.lang.String} objects.
	 * @throws org.evosuite.Properties.NoSuchParameterException
	 *             if any.
	 * @throws java.lang.IllegalArgumentException
	 *             if any.
	 * @throws java.lang.IllegalAccessException
	 *             if any.
	 */
	public void setValue(String key, String[] value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key)) {
			throw new NoSuchParameterException(key);
		}

		Field f = parameterMap.get(key);

		f.set(this, value);
	}

	/**
	 * Set the given <code>key</code> variable to the given input Object
	 * <code>value</code>
	 *
	 * @param key
	 * @param value
	 * @throws NoSuchParameterException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void setValue(String key, Object value)
			throws NoSuchParameterException, IllegalArgumentException,
			IllegalAccessException {
		if (!parameterMap.containsKey(key)) {
			throw new NoSuchParameterException(key);
		}

		Field f = parameterMap.get(key);

		f.set(this, value);
	}

	/** Singleton instance */
	private static MBTProperties instance = null;

	/** Internal properties hashmap */
	private java.util.Properties properties;

	/** Constructor */
	private MBTProperties(boolean loadProperties, boolean silent) {
		if (loadProperties)
			loadProperties(silent);
	}

	/**
	 * Singleton accessor
	 *
	 * @return a {@link org.evosuite.Properties} object.
	 */
	public static MBTProperties getInstance() {
		if (instance == null)
			instance = new MBTProperties(true, false);
		return instance;
	}

	/**
	 * Singleton accessor
	 *
	 * @return a {@link org.evosuite.Properties} object.
	 */
	public static MBTProperties getInstanceSilent() {
		if (instance == null)
			instance = new MBTProperties(true, true);
		return instance;
	}

	/**
	 * This exception is used when a non-existent parameter is accessed
	 *
	 *
	 */
	public static class NoSuchParameterException extends Exception {

		private static final long serialVersionUID = 9074828392047742535L;

		public NoSuchParameterException(String key) {
			super("No such property defined: " + key);
		}
	}

	
	

	

	

	/**
	 * Update the evosuite.properties file with the current setting
	 */
	public void writeConfiguration() {
		URL fileURL = this.getClass().getClassLoader()
				.getResource("evosuite.properties");
		String name = fileURL.getFile();
		writeConfiguration(name);
	}

	/**
	 * Update the evosuite.properties file with the current setting
	 *
	 * @param fileName
	 *            a {@link java.lang.String} object.
	 */
	public void writeConfiguration(String fileName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CP=");
		// Replace backslashes with forwardslashes, as backslashes are dropped during reading
		// TODO: What if there are weird characters in the code? Need regex
		buffer.append(ClassPathHandler.getInstance()
				.getTargetProjectClasspath().replace("\\", "/"));
		buffer.append("\nPROJECT_PREFIX=");
		if (MBTProperties.PROJECT_PREFIX != null)
			buffer.append(MBTProperties.PROJECT_PREFIX);
		buffer.append("\n");

		Map<String, Set<Parameter>> fieldMap = new HashMap<String, Set<Parameter>>();
		for (Field f : MBTProperties.class.getFields()) {
			if (f.isAnnotationPresent(Parameter.class)) {
				Parameter p = f.getAnnotation(Parameter.class);
				if (!fieldMap.containsKey(p.group()))
					fieldMap.put(p.group(), new HashSet<Parameter>());

				fieldMap.get(p.group()).add(p);
			}
		}

		for (String group : fieldMap.keySet()) {
			if (group.equals("Runtime"))
				continue;

			buffer.append("#--------------------------------------\n");
			buffer.append("# ");
			buffer.append(group);
			buffer.append("\n#--------------------------------------\n\n");
			for (Parameter p : fieldMap.get(group)) {
				buffer.append("# ");
				buffer.append(p.description());
				buffer.append("\n");
				if (!changedFields.contains(p.key()))
					buffer.append("#");
				buffer.append(p.key());
				buffer.append("=");
				try {
					buffer.append(getStringValue(p.key()));
				} catch (Exception e) {
					logger.error("Exception " + e.getMessage(), e);
				}
				buffer.append("\n\n");
			}
		}
		FileIOUtils.writeFile(buffer.toString(), fileName);
	}

	/**
	 * <p>
	 * resetToDefaults
	 * </p>
	 */
	public void resetToDefaults() {
		MBTProperties.instance = new MBTProperties(false, true);
		for (Field f : MBTProperties.class.getFields()) {
			if (f.isAnnotationPresent(Parameter.class)) {
				if (defaultMap.containsKey(f)) {
					try {
						f.set(null, defaultMap.get(f));
					} catch (Exception e) {
						logger.error("Failed to init property field " + f
								+ " , " + e.getMessage(), e);
					}
				}
			}
		}
	}

}
