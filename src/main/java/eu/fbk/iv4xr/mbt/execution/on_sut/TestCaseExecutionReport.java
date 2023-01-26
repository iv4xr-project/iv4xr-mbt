package eu.fbk.iv4xr.mbt.execution.on_sut;


import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

public class TestCaseExecutionReport {

	private GoalStructure goals;
	private String response;
	private EFSMTransition transition;
	private String goalStatus;
	
	public TestCaseExecutionReport() {

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
	
	public String getResponse() {
		return response;
	}
	
	public EFSMTransition getTransition() {
		return transition;
	}
	
	public String getGoalStatus() {
		return goalStatus;
	}

	public String toString() {
		String out = "";
		out = out + transition.toString(); 
		out = out + " response "+response+"\n";
		out = out + " goal status is\n  "+goalStatus+"\n";
		return out;	 
	}
}