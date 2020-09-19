package eu.fbk.iv4xr.mbt.efsm4j;

/**
 * gPi transitions are those transitions that have input parameter guard but not a domain guard, gPi
 * =? NIL and gD = NIL.
 *
 * @author Manuel Benz created on 20.02.18
 */
public abstract class PGTransition<State, Parameter, Context>
    extends Transition<State, Parameter, Context> {

  @Override
  protected boolean domainGuard(Context context) {
    return true;
  }

  @Override
  public boolean hasDomainGuard() {
    return false;
  }

  @Override
  public boolean hasParameterGuard() {
    return true;
  }
}
