package eu.fbk.iv4xr.mbt.efsm4j;

import com.google.common.base.Objects;

/** @author Manuel Benz created on 24.02.18 */
public class Configuration<State, Context> {
  private final State curState;
  private final Context context;

  public Configuration(State curState, Context context) {
    this.curState = curState;
    this.context = context;
  }

  public State getState() {
    return curState;
  }

  public Context getContext() {
    return context;
  }

  @Override
  public String toString() {
    return curState + " | " + context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Configuration that = (Configuration) o;
    return Objects.equal(curState, that.curState) && Objects.equal(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(curState, context);
  }
}
