package eu.fbk.iv4xr.mbt.efsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.jgrapht.GraphPath;

import com.google.common.collect.Lists;

/** @author Davide Prandi */
public class EFSMPath implements Iterable<EFSMTransition>, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2580497263468303058L;
	protected final LinkedList<EFSMTransition> transitions;

	protected EFSMPath() {
		transitions = new LinkedList<>();
	}

	protected EFSMPath(EFSMPath basePath) {
		this.transitions = new LinkedList<>(basePath.transitions);
	}

	public EFSMPath(GraphPath<EFSMState, EFSMTransition> basePath) {

		transitions = new LinkedList<>(basePath.getEdgeList());
	}

	protected EFSMPath(List<EFSMTransition> transitions) {
		this.transitions = new LinkedList<>(transitions);
	}

	protected EFSMPath(EFSMTransition... transitions) {
		this.transitions = new LinkedList<>(Arrays.asList(transitions));
	}

	public EFSMPath(EFSMPath basePath, EFSMTransition t) {
		this(basePath);
		transitions.add(t);
	}

	public void append(EFSMTransition t) {
		if (!transitions.isEmpty()) {
			EFSMTransition last = transitions.getLast();
			if (!last.getTgt().equals(t.getSrc())) {
				throw new IllegalArgumentException(
						"The given transition does not connect to the last transition of this path");
			}
		}

		transitions.addLast(t);
	}

	public void append(EFSMPath other) {
		append(other.transitions);
	}

	public void append(GraphPath<EFSMState, EFSMTransition> other) {
		append(new LinkedList<>(other.getEdgeList()));
	}

	public void append(LinkedList<EFSMTransition> other) {
		if (other.isEmpty()) {
			return;
		}

		ensureConnects(this.transitions, other);

		this.transitions.addAll(other);
	}

	public void prepend(EFSMTransition t) {
		if (!transitions.isEmpty()) {
			EFSMTransition first = transitions.getFirst();
			if (!first.getSrc().equals(t.getTgt())) {
				throw new IllegalArgumentException(
						"The given transition does not connect to the first transition of this path");
			}
		}

		transitions.addFirst(t);
	}

	public void prepend(EFSMPath other) {
		prepend(other.transitions);
	}

	public void prepend(GraphPath<EFSMState, EFSMTransition> other) {
		prepend(new LinkedList<>(other.getEdgeList()));
	}

	public void prepend(LinkedList<EFSMTransition> other) {
		if (other.isEmpty()) {
			return;
		}

		ensureConnects(other, this.transitions);

		this.transitions.addAll(0, other);
	}

	public void ensureConnects(LinkedList<EFSMTransition> head, LinkedList<EFSMTransition> tail) {
		if (!transitions.isEmpty()) {
			EFSMTransition last = head.getLast();
			EFSMTransition first = tail.getFirst();
			if (!last.getTgt().equals(first.getSrc())) {
				throw new IllegalArgumentException("The given paths do not connect");
			}
		}
	}

	public boolean isConnected() {
		boolean connected = true;
		EFSMTransition previous = this.transitions.getFirst();
		for (int i = 1; i < getLength(); i++) {
			EFSMTransition current = this.transitions.get(i);
			if (!previous.getTgt().equals(current.getSrc())) {
				connected = false;
				break;
			} else {
				previous = current;
			}
		}
		return connected;
	}

	public List<EFSMTransition> getTransitions() {
		if (transitions.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return Collections.unmodifiableList(transitions);
	}

	public List<EFSMTransition> getModfiableTransitions() {
		if (transitions.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return transitions;
	}

	public EFSMTransition getTransitionAt(int index) {
		return transitions.get(index);
	}

	public List<EFSMState> getStates() {
		if (transitions.isEmpty()) {
			return Collections.emptyList();
		}

		ArrayList<EFSMState> states = Lists.newArrayListWithCapacity(transitions.size() + 1);
		for (EFSMTransition transition : transitions) {
			states.add(transition.getSrc());
		}

		states.add(transitions.getLast().getTgt());

		return states;
	}

	public boolean contains(EFSMTransition t) {
		return transitions.contains(t);
	}

	public boolean isEmpty() {
		return transitions.isEmpty();
	}

	public EFSMState getSrc() {
		if (transitions.isEmpty()) {
			return null;
		}
		return transitions.getFirst().getSrc();
	}

	public EFSMState getTgt() {
		if (transitions.isEmpty()) {
			return null;
		}
		return transitions.getLast().getTgt();
	}

	public int getLength() {
		return transitions.size();
	}

	public EFSMPath subPath(int src, int tgt) {
		int size = transitions.size();
		if (src < 0 || src >= size || tgt < src || tgt > size) {
			throw new IndexOutOfBoundsException();
		}
		return new EFSMPath(transitions.subList(src, tgt));
	}

	@Override
	public Iterator<EFSMTransition> iterator() {
		return transitions.iterator();
	}

	@Override
	public void forEach(Consumer<? super EFSMTransition> action) {
		transitions.forEach(action);
	}

	@Override
	public Spliterator<EFSMTransition> spliterator() {
		return transitions.spliterator();
	}

	@Override
	public String toString() {
		return transitions.toString();
	}

	/**
	 * 
	 * @param kTransition
	 * @return
	 */
	public boolean isSubPath(EFSMPath kTransition) {
		int indexOfSubList = Collections.indexOfSubList(this.getTransitions(), kTransition.getTransitions());
		if (indexOfSubList == -1) {
			return false;
		} else {
			return true;
		}
	}

//	@Override
//	public int hashCode() {
//		return Objects.hash(transitions);
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transitions == null) ? 0 : transitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EFSMPath))
			return false;
		EFSMPath other = (EFSMPath) obj;
		if (transitions == null) {
			if (other.transitions != null)
				return false;
		} else if (!transitions.equals(other.transitions))
			return false;
		return true;
	}

}