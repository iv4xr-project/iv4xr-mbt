package eu.fbk.iv4xr.mbt.efsm4j;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.jgrapht.GraphPath;

/** @author Manuel Benz created on 02.03.18 */
public class EFSMPath<
        State,
        Parameter,
        Context extends IEFSMContext<Context>,
        Transition extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, Parameter, Context>>
    implements Iterable<Transition> {

  protected final LinkedList<Transition> transitions;

  protected EFSMPath() {
    transitions = new LinkedList<>();
  }

  protected EFSMPath(EFSMPath<State, Parameter, Context, Transition> basePath) {
    this.transitions = new LinkedList<>(basePath.transitions);
  }

  public EFSMPath(GraphPath<State, Transition> basePath) {

    transitions = new LinkedList<>(basePath.getEdgeList());
  }

  protected EFSMPath(Collection<Transition> transitions) {
    this.transitions = new LinkedList<>(transitions);
  }

  protected EFSMPath(Transition... transitions) {
    this.transitions = new LinkedList<>(Arrays.asList(transitions));
  }

  public EFSMPath(EFSMPath<State, Parameter, Context, Transition> basePath, Transition t) {
    this(basePath);
    transitions.add(t);
  }

  protected void append(Transition t) {
    if (!transitions.isEmpty()) {
      Transition last = transitions.getLast();
      if (last.getTgt() != t.getSrc()) {
        throw new IllegalArgumentException(
            "The given transition does not connect to the last transition of this path");
      }
    }

    transitions.addLast(t);
  }

  protected void append(EFSMPath<State, Parameter, Context, Transition> other) {
    append(other.transitions);
  }

  protected void append(GraphPath<State, Transition> other) {
    append(new LinkedList<>(other.getEdgeList()));
  }

  private void append(LinkedList<Transition> other) {
    if (other.isEmpty()) {
      return;
    }

    ensureConnects(this.transitions, other);

    this.transitions.addAll(other);
  }

  protected void prepend(Transition t) {
    if (!transitions.isEmpty()) {
      Transition first = transitions.getFirst();
      if (first.getSrc() != t.getTgt()) {
        throw new IllegalArgumentException(
            "The given transition does not connect to the first transition of this path");
      }
    }

    transitions.addFirst(t);
  }

  protected void prepend(EFSMPath<State, Parameter, Context, Transition> other) {
    prepend(other.transitions);
  }

  protected void prepend(GraphPath<State, Transition> other) {
    prepend(new LinkedList<>(other.getEdgeList()));
  }

  private void prepend(LinkedList<Transition> other) {
    if (other.isEmpty()) {
      return;
    }

    ensureConnects(other, this.transitions);

    this.transitions.addAll(0, other);
  }

  private void ensureConnects(LinkedList<Transition> head, LinkedList<Transition> tail) {
    if (!transitions.isEmpty()) {
      Transition last = head.getLast();
      Transition first = tail.getFirst();
      if (last.getTgt() != first.getSrc()) {
        throw new IllegalArgumentException("The given paths do not connect");
      }
    }
  }

  public List<Transition> getTransitions() {
    if (transitions.isEmpty()) {
      return Collections.EMPTY_LIST;
    }

    return Collections.unmodifiableList(transitions);
  }

  public Transition getTransitionAt(int index) {
    return transitions.get(index);
  }

  public List<State> getStates() {
    if (transitions.isEmpty()) {
      return Collections.emptyList();
    }

    ArrayList<State> states = Lists.newArrayListWithCapacity(transitions.size() + 1);
    for (Transition transition : transitions) {
      states.add(transition.getSrc());
    }

    states.add(transitions.getLast().getTgt());

    return states;
  }

  public boolean contains(Transition t) {
    return transitions.contains(t);
  }

  public boolean isEmpty() {
    return transitions.isEmpty();
  }

  public State getSrc() {
    if (transitions.isEmpty()) {
      return null;
    }
    return transitions.getFirst().getSrc();
  }

  public State getTgt() {
    if (transitions.isEmpty()) {
      return null;
    }
    return transitions.getLast().getTgt();
  }

  public int getLength() {
    return transitions.size();
  }

  public EFSMPath<State, Parameter, Context, Transition> subPath(int src, int tgt) {
    int size = transitions.size();
    if (src < 0 || src >= size || tgt < src || tgt > size) {
      throw new IndexOutOfBoundsException();
    }
    return new EFSMPath(transitions.subList(src, tgt));
  }

  @Override
  public Iterator<Transition> iterator() {
    return transitions.iterator();
  }

  @Override
  public void forEach(Consumer<? super Transition> action) {
    transitions.forEach(action);
  }

  @Override
  public Spliterator<Transition> spliterator() {
    return transitions.spliterator();
  }

  @Override
  public String toString() {
    return transitions.toString();
  }
}
