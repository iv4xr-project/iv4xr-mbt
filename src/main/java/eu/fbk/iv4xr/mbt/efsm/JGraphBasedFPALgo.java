package eu.fbk.iv4xr.mbt.efsm;

import java.util.List;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DirectedPseudograph;

/** @author Manuel Benz created on 02.03.18 */
public abstract class JGraphBasedFPALgo implements IFeasiblePathAlgo {

  protected final DirectedPseudograph<EFSMState, EFSMTransition> baseGraph;
  protected final EFSM efsm;

  public JGraphBasedFPALgo(EFSM efsm) {
    this.efsm = efsm;
    baseGraph = efsm.getBaseGraph();
  }

  @Override
  public EFSMPath getPath(EFSMState tgt) {
    return getPath(efsm.getConfiguration(), tgt);
  }

  @Override
  public List<EFSMPath> getPaths(EFSMState tgt) {
    return getPaths(efsm.getConfiguration(), tgt);
  }

  @Override
  public boolean pathExists(EFSMState tgt) {
    return pathExists(efsm.getConfiguration(), tgt);
  }
}
