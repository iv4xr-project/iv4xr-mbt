package eu.fbk.iv4xr.mbt.efsm4j;

/** @author Manuel Benz created on 26.02.18 */
public interface IEFSMContext<Context extends IEFSMContext> {

  /**
   * Creates a hard copy of this context which can live and change independently
   *
   * @return
   */
  Context snapshot();
}
