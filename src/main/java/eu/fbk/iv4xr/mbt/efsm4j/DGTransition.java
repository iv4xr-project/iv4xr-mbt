package eu.fbk.iv4xr.mbt.efsm4j;

/**
 * gD transitions are those transitions that have a domain guard but not an input parameter guard,
 * gD ?= NIL and gPi = NIL.
 *
 * @author Manuel Benz created on 20.02.18
 */
public abstract class DGTransition<
		State extends EFSMState, 
		Parameter extends EFSMParameter, 
		Context extends IEFSMContext<Context>>
    extends Transition<State, Parameter, Context> {

  @Override
  protected boolean inputGuard(Parameter input) {
    return true;
  }

  @Override
  public boolean hasDomainGuard() {
    return true;
  }

  @Override
  public boolean hasParameterGuard() {
    return false;
  }
}
