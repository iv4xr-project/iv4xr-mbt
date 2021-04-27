/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.FileUtils;
import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.Algorithm;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testsuite.MBTSuiteChromosome;
import eu.fbk.iv4xr.mbt.testsuite.SuiteChromosome;
import eu.fbk.jtamer.jtamer;
import eu.fbk.jtamer.tamer_action;
import eu.fbk.jtamer.tamer_env;
import eu.fbk.jtamer.tamer_expr;
import eu.fbk.jtamer.tamer_problem;
import eu.fbk.jtamer.tamer_ttplan;
import eu.fbk.jtamer.tamer_ttplan_step;
import eu.fbk.jtamer.tamer_ttplan_step_iter;

/**
 * Iteratively generate random tests. If adding the random test
 * leads to improved fitness, keep it, otherwise drop it again.
 * 
 * Class adapted from EvoSuite
 * 
 * @author kifetew
 *
 */
public class PlanningBasedStrategy<T extends Chromosome> extends GenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(PlanningBasedStrategy.class);
	
	private final String outputDir = "data/tamer/out/";
	
	/**
	 * Set some Evosuite global parameters that control properties of the search algorithms
	 */
	private static void configureSettings () {
		
		Properties.LOG_LEVEL = "WARN";
		Properties.SEARCH_BUDGET = MBTProperties.SEARCH_BUDGET;
		
//		MBTProperties.SUT_EFSM = "labrecruits.random_simple";
	}
	
	@Override
	public SuiteChromosome generateTests() {
		
		configureSettings();
		
		LoggingUtils.getEvoLogger().info("* Using planning-based test generation");

		AlgorithmFactory<T> algorithmFactory = new AlgorithmFactory<T>();
		
		List<?> goals = algorithmFactory.getCoverageGoals();

		// TODO this part is currently meaningless, added for the sake of completeness
		GeneticAlgorithm<T> searchAlgorithm = algorithmFactory.getSearchAlgorithm();
		coverageTracker = new CoverageTracker(goals);
		searchAlgorithm.addListener(getCoverageTracker());
		searchAlgorithm.addStoppingCondition(getCoverageTracker());
		
		
		EFSM efsm = EFSMFactory.getInstance().getEFSM();
		String anml = efsm.getAnmlString();
		
		String dot = efsm.getDotString();
		String anmlFilePath = prepareAnmlFile (anml);
		
		// useful for debugging
		saveDotFile (dot, anmlFilePath);
		
		callPlanner(anmlFilePath);
		
		// TODO take output of planner and populate the test suite
		// TODO calculate coverage
		SuiteChromosome suite = new MBTSuiteChromosome(); 
		return suite;
	}
	
	private void saveDotFile(String dot, String anmlFilePath) {
		String dotFilePath = anmlFilePath + ".dot";
		try {
			FileUtils.fileWrite(dotFilePath, dot);
			System.out.println("Saved efsm as dot: " + dotFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * create a temporary file, save the model in anml and return file path
	 * @return
	 */
	private String prepareAnmlFile (String strAnml) {
		File parentDir = new File(outputDir);
		parentDir.mkdirs();
		File tempFile = FileUtils.createTempFile(MBTProperties.SUT_EFSM + "_" + System.currentTimeMillis(), ".anml", parentDir);
		try {
			FileUtils.fileWrite(tempFile, "utf8", strAnml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempFile.getAbsolutePath();
	}
	
	
	private void callPlanner (String anmlFilePath) {
		tamer_env env = jtamer.tamer_env_new();

        // create the problem parsing an ANML file
        tamer_problem problem = jtamer.tamer_parse_anml(env, anmlFilePath);

        // solve the problem
        jtamer.tamer_env_set_boolean_option(env, "tsimple-goals-serialization", 1);
        jtamer.tamer_env_set_string_option(env, "tsimple-heuristic", "blind");
        tamer_ttplan plan = jtamer.tamer_ttplan_from_potplan(jtamer.tamer_do_tsimple_planning(problem));

        // read the plan
        tamer_ttplan_step_iter steps = jtamer.tamer_ttplan_get_steps(plan);
        for (int i=0; i<jtamer.tamer_ttplan_get_size(plan); i++) {
                tamer_ttplan_step step = jtamer.tamer_ttplan_step_iter_next(steps);
                tamer_action action = jtamer.tamer_ttplan_step_get_action(step);
                String action_name = jtamer.tamer_action_get_name(action);
                List<String> action_params = new ArrayList<String>();
                for (int j=0; j<jtamer.tamer_ttplan_step_get_num_parameters(step); j++) {
                        tamer_expr param = jtamer.tamer_ttplan_step_get_parameter(step, j);
                        action_params.add(jtamer.tamer_expr_get_anml(param));
                }
                double duration = parse(jtamer.tamer_ttplan_step_get_duration(step));
                double start_time = parse(jtamer.tamer_ttplan_step_get_start_time(step));
                System.out.println(start_time + " : " + action_name + "(" + String.join(", ", action_params) + ") [" + duration + "]");
        }
	}
	
	private void __callPlanner (String anmlFilePath) {
		tamer_env env = jtamer.tamer_env_new();
		
		
		// =================================================================
				
		// create the problem parsing an ANML file
		tamer_problem problem = jtamer.tamer_parse_anml(env, anmlFilePath); //"lab_recruits_random_1.anml");
		System.out.println("Loaded problem file: " + anmlFilePath);
		// solve the problem
		tamer_ttplan plan = jtamer.tamer_ttplan_from_potplan(jtamer.tamer_do_iw_planning(problem));
		
		// read the plan
		tamer_ttplan_step_iter steps = jtamer.tamer_ttplan_get_steps(plan);
		for (int i=0; i<jtamer.tamer_ttplan_get_size(plan); i++) {
			tamer_ttplan_step step = jtamer.tamer_ttplan_step_iter_next(steps);
			tamer_action action = jtamer.tamer_ttplan_step_get_action(step);
			String action_name = jtamer.tamer_action_get_name(action);
			List<String> action_params = new ArrayList<String>();
			for (int j=0; j<jtamer.tamer_ttplan_step_get_num_parameters(step); j++) {
				tamer_expr param = jtamer.tamer_ttplan_step_get_parameter(step, j);
				action_params.add(jtamer.tamer_expr_get_anml(param));
			}
			double duration = parse(jtamer.tamer_ttplan_step_get_duration(step));
			double start_time = parse(jtamer.tamer_ttplan_step_get_start_time(step));
			System.out.println(start_time + " :: " + action_name + "(" + String.join(", ", action_params) + ") [" + duration + "]");
		}
	}
	
	private double parse(String ratio) {
	    if (ratio.contains("/")) {
	        String[] rat = ratio.split("/");
	        return Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);
	    } else {
	        return Double.parseDouble(ratio);
	    }
	}
	
}
