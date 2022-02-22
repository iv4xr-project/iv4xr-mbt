package eu.fbk.iv4xr.mbt.efsm;

import java.util.List;

/** @author Manuel Benz created on 02.03.18 */
public interface IFeasiblePathAlgo {
  /**
   * Returns a feasible path between the current state of the efsm and the given target state, or
   * null if no path exists. If a path is feasible depends on the semantics of the underlying efsm
   * implementation. The algorithm will assume the current configuration of the efsm as start
   * configuration.
   *
   * <p>Note: The implementation of this interface has to decide if any feasible path or the
   * shortest feasible path will be returned.
   *
   * @param tgt
   * @return A feasible path or null if non exists
   */
	EFSMPath getPath(EFSMState tgt);

  /**
   * Returns a feasible path between the given state of the efsm and the given target state, or null
   * if no path exists. If a path is feasible depends on the semantics of the underlying efsm
   * implementation. The algorithm will assume given configuration of the efsm as start
   * configuration.
   *
   * <p>Note: The implementation of this interface has to decide if any feasible path or the
   * shortest feasible path will be returned.
   *
   * @param tgt
   * @return A feasible path or null if non exists
   * @config The configuration from which a path should be calculated
   */
  EFSMPath getPath(EFSMConfiguration config, EFSMState tgt);

  /**
   * Returns a set feasible path between the given state of the efsm and the given target state, or
   * null if no path exists. If a path is feasible depends on the semantics of the underlying efsm
   * implementation. The algorithm will assume the current configuration of the efsm as start
   * configuration.
   *
   * <p>Note: The implementation of this interface has to decide which paths to returen, e.g., all
   * feasible path or a subset.
   *
   * @param tgt
   * @return A set of feasible path (not necessarily all) or null if non exists
   */
  List<EFSMPath> getPaths(EFSMState tgt);

  /**
   * Returns a set feasible path between the given state of the efsm and the given target state, or
   * null if no path exists. If a path is feasible depends on the semantics of the underlying efsm
   * implementation. The algorithm will assume given configuration of the efsm as start
   * configuration.
   *
   * <p>Note: The implementation of this interface has to decide which paths to returen, e.g., all
   * feasible path or a subset
   *
   * @param tgt
   * @return A set of feasible path (not necessarily all) or null if non exists
   * @config The configuration from which a path should be calculated
   */
  List<EFSMPath> getPaths(EFSMConfiguration config, EFSMState tgt);

  boolean pathExists(EFSMState tgt);

  boolean pathExists(EFSMConfiguration config, EFSMState tgt);

  interface SingleSourceShortestPath {

    /**
     * Returns a set feasible path between the source state of this {@link SingleSourceShortestPath}
     * instance and the given target state, or null if no path exists. If a path is feasible depends
     * on the semantics of the underlying efsm implementation.
     *
     * <p>Note: The implementation of this interface has to decide which paths to returen, e.g., all
     * feasible path or a subset.
     *
     * @param tgt
     * @return A set of feasible path (not necessarily all) or null if non exists
     */
    List<EFSMPath> getPaths(EFSMState tgt);

    /**
     * Returns a feasible path between the source state of this {@link SingleSourceShortestPath}
     * instance and the given target state, or null if no path exists. If a path is feasible depends
     * on the semantics of the underlying efsm implementation.
     *
     * <p>Note: The implementation of this interface has to decide if any feasible path or the
     * shortest feasible path will be returned.
     *
     * <p>The implementation of this method should correspond to {@link
     * SingleSourceShortestPath#getLength(Object)}, i.e. should return the path for which {@link
     * SingleSourceShortestPath#getLength(Object)} would return the its length.
     *
     * @param tgt
     * @return A feasible path or null if non exists
     */
     EFSMPath getPath(EFSMState tgt);

    /**
     * Returns the length of a feasible path between the source state of this {@link
     * SingleSourceShortestPath} instance and the given target state, or -1 if no path exists. If a
     * path is feasible depends on the semantics of the underlying efsm implementation.
     *
     * <p>Note: The implementation of this interface has to decide if the lenght of any feasible
     * path or the shortest feasible path will be returned.
     *
     * <p>The implementation of this method should correspond to {@link
     * SingleSourceShortestPath#getPath(Object)}, i.e. should return the length of the path that
     * would be returned by {@link SingleSourceShortestPath#getPath(Object)}.
     *
     * @param tgt
     * @return A feasible path or null if non exists
     */
    int getLength(EFSMState tgt);

    /** @return The source configuration of this {@link SingleSourceShortestPath} instance */
    EFSMConfiguration getSource();
  }
}
