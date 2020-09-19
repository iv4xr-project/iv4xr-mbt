package eu.fbk.iv4xr.mbt.efsm4j;

/**
 * gPi-gD transitions are those transitions that have both an input parameter guard and a domain
 * guard, gPi =? NIL and gD ?= NIL
 *
 * @author Manuel Benz created on 20.02.18
 */
public abstract class PGDGTransition<State, Parameter, Context>
    extends Transition<State, Parameter, Context> {

  @Override
  public boolean hasDomainGuard() {
    return true;
  }

  @Override
  public boolean hasParameterGuard() {
    return true;
  }
}
