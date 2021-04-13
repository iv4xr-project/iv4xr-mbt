package eu.fbk.iv4xr.mbt.utils;

import java.util.ArrayList;
import java.util.List;

import eu.fbk.jtamer.jtamer;
import eu.fbk.jtamer.tamer_action;
import eu.fbk.jtamer.tamer_constant;
import eu.fbk.jtamer.tamer_env;
import eu.fbk.jtamer.tamer_expr;
import eu.fbk.jtamer.tamer_fluent;
import eu.fbk.jtamer.tamer_instance;
import eu.fbk.jtamer.tamer_param;
import eu.fbk.jtamer.tamer_problem;
import eu.fbk.jtamer.tamer_ttplan;
import eu.fbk.jtamer.tamer_ttplan_step;
import eu.fbk.jtamer.tamer_ttplan_step_iter;
import eu.fbk.jtamer.tamer_type;

public class EFSMToTamer {
	public static double parse(String ratio) {
	    if (ratio.contains("/")) {
	        String[] rat = ratio.split("/");
	        return Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);
	    } else {
	        return Double.parseDouble(ratio);
	    }
	}
		
	public static void main(String[] args) {
		tamer_env env = jtamer.tamer_env_new();
		
		
		// =================================================================
				
		// create the problem parsing an ANML file
		tamer_problem problem = jtamer.tamer_parse_anml(env, "lab_recruits_random_1.anml");
		System.out.println("Loaded problem file");
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

}
