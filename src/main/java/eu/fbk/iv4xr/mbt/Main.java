/**
 * 
 */
package eu.fbk.iv4xr.mbt;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import org.evosuite.Properties;
import org.evosuite.executionmode.Continuous;
import org.evosuite.executionmode.Help;
import org.evosuite.executionmode.ListClasses;
import org.evosuite.executionmode.ListParameters;
import org.evosuite.executionmode.MeasureCoverage;
import org.evosuite.executionmode.PrintStats;
import org.evosuite.executionmode.Setup;
import org.evosuite.executionmode.TestGeneration;
import org.evosuite.executionmode.WriteDependencies;
import org.evosuite.ga.Chromosome;
import org.evosuite.utils.LoggingUtils;

import ch.qos.logback.classic.Logger;
import eu.fbk.iv4xr.mbt.execution.EFSMTestExecutor;
import eu.fbk.iv4xr.mbt.execution.ExecutionResult;

//import eu.fbk.iv4xr.mbt.efsm4j.EFSMParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.EFSMState;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsContext;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsParameter;
//import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.LabRecruitsState;

import eu.fbk.iv4xr.mbt.strategy.GenerationStrategy;
import eu.fbk.iv4xr.mbt.strategy.SearchBasedStrategy;
import eu.fbk.iv4xr.mbt.testcase.AbstractTestSequence;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;

/**
 * @author kifetew
 *
 * Main entry point to the MBT
 */
public class Main {

	private GenerationStrategy generationStrategy;
	
	/**
	 * 
	 */
	public Main() {
		generationStrategy = new 
				SearchBasedStrategy<Chromosome>();
	}

	
	private void run () {
		SuiteChromosome solution = generationStrategy.generateTests();
		//solution.
		for (int i = 0; i < solution.size(); i++) {
			AbstractTestSequence testcase = (AbstractTestSequence)solution.getTestChromosome(i).getTestcase();
			System.out.println("Valid: " + testcase.isValid());
			System.out.println(testcase.toDot());
			System.out.println(testcase.toString());
			if (!testcase.isValid()) {
				// re-execute for debugging
				executeForDebug (testcase);
			}
		}
	}
	
	private void executeForDebug(AbstractTestSequence testcase) {
//		TestExecutor executor = new EFSMTestExecutor<>();
		ExecutionResult executionResult = EFSMTestExecutor.getInstance().executeTestcase(testcase);
		if (!executionResult.isSuccess()) {
			System.err.println("INVALID: " + testcase.toDot());
		}
	}

	/**
	 * TODO should no longer be necessary, options can now be passed as commandline arguments -Doption=value
	 */
	private void setProperties () {
		MBTProperties.SEARCH_BUDGET = 500;
		MBTProperties.LR_mean_buttons = 1;
		MBTProperties.LR_n_buttons = 20;
		MBTProperties.LR_n_doors = 10 ;
		MBTProperties.SUT_EFSM = "labrecruits.random_default";
		MBTProperties.LR_seed = 370327;
	}
	

	
	/**
	 * TODO add proper optios here
	 * @return
	 */
	public static Options getCommandLineOptions() {
		Options options = new Options();

		Option help = new Option("help", "print this message");


//		Option property = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription("use value for given property").create("D");
		
		Option property   = Option.builder("D")
				.numberOfArgs(2)
				.argName("property=value")
				.valueSeparator('=')
				.required(false)
				.optionalArg(false)
				.type(String.class)
				.desc("use value for given property")
				.build();

		
		options.addOption(help);
		options.addOption(property);
		return options;
	}
	
	public void parseCommandLine(String[] args) {
		Options options = getCommandLineOptions();
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(options, args);
			
			// TODO deal with arguments here ..
			
			setGlobalProperties (line);
		} catch (ParseException e) {
			System.err.println("Failed to parse commandline arguments.");
		}
		
	}


	/**
	 * Method adapted from the EvoSuite project
	 * Read commandline arguments and update the global properties classes (both MBT and EvoSuite)
	 * @param line
	 */
	private void setGlobalProperties(CommandLine line) {
		Properties properties = line.getOptionProperties("D");
		Set<String> propertyNames = new HashSet<>(MBTProperties.getParameters());

        for (String propertyName : properties.stringPropertyNames()) {

            if (!propertyNames.contains(propertyName)) {
				LoggingUtils.getEvoLogger().error("* Unknown property: " + propertyName);
				throw new Error("Unknown property: " + propertyName);
			}

            String propertyValue = properties.getProperty(propertyName);

            try {
				MBTProperties.getInstance().setValue(propertyName, propertyValue);
				
			} catch (Exception e) {
				throw new Error("Invalid value for property " + propertyName+": "+propertyValue+". Exception "+e.getMessage(),e);
			}
            try {
            	//Do this also for Evosuite global properties, if they exsits
            	org.evosuite.Properties.getInstance().setValue(propertyName, propertyValue);
            }catch (Exception e) {
            	System.err.println("Unable to set Evosuite global property: " + propertyName);
            }
		}
		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		//main.setProperties();
		main.parseCommandLine(args);
		main.run();
		System.exit(0);
	}

}
