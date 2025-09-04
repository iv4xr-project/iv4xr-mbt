package eu.fbk.iv4xr.mbt.execution.on_sut;


import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

public class TestCaseExecutionReport {

	protected String response;
	protected EFSMTransition transition;
	
	public TestCaseExecutionReport() {

	}
	
	public void addReport(String r, EFSMTransition t) {
		response = r;
		transition = t;
	}
	
	public String getResponse() {
		return response;
	}
	
	public EFSMTransition getTransition() {
		return transition;
	}
	
	public String toString() {
		String out = "";
		out = out + transition.toString(); 
		out = out + " response "+response+"\n";
		return out;	 
	}
}