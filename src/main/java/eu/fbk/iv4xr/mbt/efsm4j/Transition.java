package eu.fbk.iv4xr.mbt.efsm4j;

import java.util.Collections;
import java.util.Set;

/** @author Manuel Benz created on 20.02.18 */
public abstract class Transition<
	State extends EFSMState, 
	Parameter extends EFSMParameter, 
	Context extends IEFSMContext<Context>> {

  private State src;
  private State tgt;

  public boolean isFeasible(Parameter input, Context context) {
    return inputGuard(input) && domainGuard(context);
  }

  /**
   * Assumes {@link Transition#isFeasible(Parameter, Context)} was called and returned true to work
   * properly
   *
   * @param input
   * @param context
   * @return A set of output values
   */
  public Set<Parameter> take(Parameter input, Context context) {
    Set<Parameter> apply = operation(input, context);

    if (apply == null) {
      return Collections.emptySet();
    }

    return apply;
  }

  /**
   * Tries to take the transition by calling {@link Transition#isFeasible(Parameter, Context)}
   * itself and returning null if the transition cannot be taken.
   *
   * @param input
   * @param context
   * @return An (potentially empty) set of output values or null if the transition is infeasible.
   */
  public Set<Parameter> tryTake(Parameter input, Context context) {
    if (isFeasible(input, context)) {
      return take(input, context);
    } else {
      return null;
    }
  }

  public State getSrc() {
    return src;
  }

  protected void setSrc(State src) {
    this.src = src;
  }

  public State getTgt() {
    return tgt;
  }

  protected void setTgt(State tgt) {
    this.tgt = tgt;
  }

  /**
   * Has to handle the empty input (null)
   *
   * @param input Null as empty input or a Parameter
   * @return
   */
  protected abstract boolean inputGuard(Parameter input);

  protected abstract boolean domainGuard(Context context);

  protected abstract Set<Parameter> operation(Parameter input, Context context);

  public abstract boolean hasOperation();

  public abstract boolean hasDomainGuard();

  public abstract boolean hasParameterGuard();

  public boolean isEpsilonTransition() {
    return isSimpleTransition() && !hasOperation();
  }

  public boolean isDGTransition() {
    return hasDomainGuard() && !hasParameterGuard();
  }

  public boolean isPGTransition() {
    return hasParameterGuard() && !hasDomainGuard();
  }

  public boolean isPGDGTransition() {
    return hasParameterGuard() && hasDomainGuard();
  }

  public boolean isSimpleTransition() {
    return !hasDomainGuard() && !hasParameterGuard();
  }
}
