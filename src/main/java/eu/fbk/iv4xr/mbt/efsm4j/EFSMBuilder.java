package eu.fbk.iv4xr.mbt.efsm4j;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Manuel Benz created on 20.02.18 */
public class EFSMBuilder<
    State,
    Parameter,
    Context extends IEFSMContext<Context>,
    Transition extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>,
    EFSM extends eu.fbk.iv4xr.mbt.efsm4j.EFSM<State, Parameter, Context, Transition>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EFSMBuilder.class);
  protected final Graph<State, Transition> base;
  private final Class<EFSM> efsmTypeClass;

  public EFSMBuilder(Class<EFSM> efsmTypeClass) {
    this(efsmTypeClass, new DirectedPseudograph<>(null));
  }

  /**
   * Creates a builder operating on the given EFSM, effectively rendering the given EFSMS mutable.
   *
   * @param efsmTypeClass
   * @param base
   */
  public EFSMBuilder(Class<EFSM> efsmTypeClass, EFSM base) {
    this(efsmTypeClass, base.getBaseGraph());
  }

  private EFSMBuilder(Class<EFSM> efsmTypeClass, Graph<State, Transition> base) {
    this.efsmTypeClass = efsmTypeClass;
    this.base = base;
  }

  public EFSMBuilder<State, Parameter, Context, Transition, EFSM> withEFSM(EFSM s) {
    Preconditions.checkNotNull(s);
    Graphs.addGraph(base, s.getBaseGraph());
    return this;
  }

  public EFSMBuilder<State, Parameter, Context, Transition, EFSM> withState(State... s) {
    Preconditions.checkNotNull(s);
    Graphs.addAllVertices(base, Arrays.asList(s));
    return this;
  }

  public EFSMBuilder<State, Parameter, Context, Transition, EFSM> withTransition(
      State src, State tgt, Transition t) {
    t.setSrc(src);
    t.setTgt(tgt);

    // ignore duplicate transitions
    if (!base.containsEdge(t)) {
      base.addVertex(src);
      base.addVertex(tgt);

      base.addEdge(src, tgt, t);
    } else {
      LOGGER.trace("Duplicate edge from {} to {}: {}", src, tgt, t);
    }

    return this;
  }

  public EFSMBuilder<State, Parameter, Context, Transition, EFSM> replaceTransition(
      Transition old, Transition newT) {
    Preconditions.checkArgument(
        base.containsEdge(old), "Transition to replace does not exist in EFSM");

    base.removeEdge(old);

    final State src = old.getSrc();
    final State tgt = old.getTgt();

    newT.setSrc(src);
    newT.setTgt(tgt);

    base.addEdge(src, tgt, newT);
    return this;
  }

  public EFSM build(State initialState, Context initialContext) {
    Preconditions.checkNotNull(initialState);
    Preconditions.checkNotNull(initialContext);

    try {
      Constructor<EFSM> constructor = getConstructor(initialState, initialContext);
      if (constructor == null) {
        throw new RuntimeException("No constructor found");
      }
      constructor.setAccessible(true);
      return constructor.newInstance(base, initialState, initialContext);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Constructor<EFSM> getConstructor(State initialState, Context initialContext) {
    for (Constructor<?> constructor : efsmTypeClass.getDeclaredConstructors()) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      if (parameterTypes.length != 3) {
        continue;
      }

      final TypeVariable<Class<EFSM>>[] typeParameters = efsmTypeClass.getTypeParameters();

      if (parameterTypes[0].isAssignableFrom(base.getClass())
          && parameterTypes[1].isAssignableFrom(initialState.getClass())
          && parameterTypes[2].isAssignableFrom(initialContext.getClass())) {
        return (Constructor<EFSM>) constructor;
      }
    }
    return null;
  }

  public Set<Transition> incomingTransitionsOf(State s) {
    return base.incomingEdgesOf(s);
  }

  public Set<Transition> outgoingTransitionsOf(State s) {
    return base.outgoingEdgesOf(s);
  }
}
