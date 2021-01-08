package eu.fbk.iv4xr.mbt.efsm;

import java.util.List;
import org.jgrapht.ListenableGraph;

/** @author Manuel Benz created on 02.03.18 */
public abstract class JGraphBasedFPALgo<
	State extends EFSMState,
	InParameter extends EFSMParameter,
	OutParameter extends EFSMParameter,
	Context extends EFSMContext,
	Operation extends EFSMOperation,
	Guard extends EFSMGuard,
	Transition extends EFSMTransition<State, InParameter, OutParameter, Context, Operation, Guard>>
    	implements IFeasiblePathAlgo<State, InParameter, OutParameter, Context, Operation, Guard, Transition> {

  protected final ListenableGraph<State, Transition> baseGraph;
  protected final EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm;

  public JGraphBasedFPALgo(EFSM<State, InParameter, OutParameter, Context, Operation, Guard, Transition> efsm) {
    this.efsm = efsm;
    baseGraph = efsm.getBaseGraph();
  }

  @Override
  public EFSMPath<State, InParameter, OutParameter, Context, Operation, Guard, Transition> getPath(State tgt) {
    return getPath(efsm.getConfiguration(), tgt);
  }

  @Override
  public List<? extends EFSMPath<State, InParameter, OutParameter, Context, Operation, Guard, Transition>> getPaths(State tgt) {
    return getPaths(efsm.getConfiguration(), tgt);
  }

  @Override
  public boolean pathExists(State tgt) {
    return pathExists(efsm.getConfiguration(), tgt);
  }
}
