package eu.fbk.iv4xr.mbt.execution.on_sut;


import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

public class AplibTestCaseExecutionReport extends TestCaseExecutionReport {

	private GoalStructure goals;
	private String goalStatus;
	
	public AplibTestCaseExecutionReport() {

	}
	
	public void addReport(GoalStructure g, String r, EFSMTransition t, String s) {
		goals = g;
		response = r;
		transition = t;
		goalStatus = s;	
	}
	
	public GoalStructure getGoal() {
		return goals;
	}
	
	public String getGoalStatus() {
		return goalStatus;
	}

	@Override
	public String toString() {
		String out = "";
		out = out + transition.toString(); 
		out = out + " response "+response+"\n";
		out = out + " goal status is\n  "+goalStatus+"\n";
		return out;	 
	}
}