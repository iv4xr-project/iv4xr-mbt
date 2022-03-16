/**
 * 
 */
package eu.fbk.iv4xr.mbt.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;

/**
 * @author kifetew
 *
 */
public class ExecutionTrace implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3139864863859510093L;
	private Collection<EFSMState> coveredStates = new HashSet<EFSMState>();
	private Collection<EFSMTransition> coveredTransitions = new HashSet<EFSMTransition>();
	
	private double pathBranchDistance;
	private double pathApproachLevel;
	
	private boolean success;
	private int passedTransitions = -1;
	private int branchPointPassedTransitions = -1;
	private List<EFSMContext> contexts = new ArrayList<EFSMContext>();
	
	/**
	 * 
	 */
	public ExecutionTrace() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the coveredStates
	 */
	public Collection<EFSMState> getCoveredStates() {
		return coveredStates;
	}

	/**
	 * @param coveredStates the coveredStates to set
	 */
	public void setCoveredStates(Collection<EFSMState> coveredStates) {
		this.coveredStates = coveredStates;
	}

	/**
	 * @return the coveredTransitions
	 */
	public Collection<EFSMTransition> getCoveredTransitions() {
		return coveredTransitions;
	}

	/**
	 * @param coveredTransitions the coveredTransitions to set
	 */
	public void setCoveredTransitions(Collection<EFSMTransition> coveredTransitions) {
		this.coveredTransitions = coveredTransitions;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the pathBranchDistance
	 */
	public double getPathBranchDistance() {
		return pathBranchDistance;
	}

	/**
	 * @param pathBranchDistance the pathBranchDistance to set
	 */
	public void setPathBranchDistance(double pathBranchDistance) {
		this.pathBranchDistance = pathBranchDistance;
	}

	/**
	 * @return the pathApproachLevel
	 */
	public double getPathApproachLevel() {
		return pathApproachLevel;
	}

	/**
	 * @param pathApproachLevel the pathApproachLevel to set
	 */
	public void setPathApproachLevel(double pathApproachLevel) {
		this.pathApproachLevel = pathApproachLevel;
	}

	public void setPassedTransitions(int passedTransitions) {
		this.passedTransitions = passedTransitions;
	}

	public int getPassedTransitions() {
		return this.passedTransitions;
	}
	
	public int getBranchPointPassedTransitions() {
		return branchPointPassedTransitions;
	}

	public void setBranchPointPassedTransitions(int branchPointPassedTransitions) {
		this.branchPointPassedTransitions = branchPointPassedTransitions;
	}

	@Override
	protected ExecutionTrace clone() {
		ExecutionTrace copy = new ExecutionTrace();
		if (coveredStates != null) {
			for (EFSMState state : coveredStates) {
				copy.getCoveredStates().add(state.clone());
			}
		}
		if (coveredTransitions != null) {
			for (EFSMTransition transition : coveredTransitions) {
				copy.getCoveredTransitions().add(transition.clone());
			}
		}
		copy.setPathBranchDistance(pathBranchDistance);
		copy.setPathApproachLevel(pathApproachLevel);
		copy.setSuccess(success);
		copy.setPassedTransitions(passedTransitions);
		if (contexts != null) {
			for (EFSMContext context : contexts) {
				copy.getContexts().add(context.clone());
			}
		}
		return copy;
	}

	/**
	 * @return the context
	 */
	public List<EFSMContext> getContexts() {
		return contexts;
	}

	/**
	 * @param context the context to set
	 */
	public void setContexts(List<EFSMContext> contexts) {
		this.contexts = contexts;
	}
}
