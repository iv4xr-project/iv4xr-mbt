package eu.fbk.iv4xr.mbt.efsm;

import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import eu.fbk.iv4xr.mbt.efsm.EFSM;



/**
 * 
 * This class is derived from from the EFMS4J project created by
 * Manuel Benz. @see <a href="https://github.com/mbenz89/EFSM4J">EFSM4J</a>
 * 
 * 
 * 
 * @author prandi
 *
 */

public class EFSMBuilder
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EFSMBuilder.class);
	protected final DirectedPseudograph<EFSMState, EFSMTransition> base;
	private final Class<EFSM> efsmTypeClass;
	
	public EFSMBuilder(Class<EFSM> efsmTypeClass) {
		
		
		this(efsmTypeClass, new DirectedPseudograph<EFSMState, EFSMTransition>(EFSMTransition.class));
		
	    //this(efsmTypeClass, new DirectedPseudograph<>(EFSMTransition.class));
		//this(efsmTypeClass, new DirectedPseudograph<>(null));
	}

	/**
	 * Creates a builder operating on the given EFSM, effectively rendering the
	 * given EFSMS mutable.
	 *
	 * @param efsmTypeClass
	 * @param base
	 */
	public EFSMBuilder(Class<EFSM> efsmTypeClass, EFSM base) {
		this(efsmTypeClass, base.getBaseGraph());
	}
	
	private EFSMBuilder(Class<EFSM> efsmTypeClass, DirectedPseudograph<EFSMState, EFSMTransition> base) {
		this.efsmTypeClass = efsmTypeClass;
		this.base = base;
	}

	public EFSMBuilder withEFSM(EFSM s) {
		Preconditions.checkNotNull(s);
		Graphs.addGraph(base, s.getBaseGraph());
		return this;
	}

	public EFSMBuilder withState(EFSMState... s) {
		Preconditions.checkNotNull(s);
		Graphs.addAllVertices(base, Arrays.asList(s));
		return this;
	}
	
	public EFSMBuilder withTransition(EFSMState src, EFSMState tgt, EFSMTransition t) {
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
	
	public EFSMBuilder replaceTransition(EFSMTransition old, EFSMTransition newT) {
		Preconditions.checkArgument(base.containsEdge(old), "Transition to replace does not exist in EFSM");

		base.removeEdge(old);

		final EFSMState src = old.getSrc();
		final EFSMState tgt = old.getTgt();

		newT.setSrc(src);
		newT.setTgt(tgt);

		base.addEdge(src, tgt, newT);
		return this;
	}
	
	public EFSM build(
			EFSMState initialState, 
			EFSMContext initialContext,
			EFSMParameterGenerator parameterGenerator) {
		Preconditions.checkNotNull(initialState);
		Preconditions.checkNotNull(initialContext);
		Preconditions.checkNotNull(parameterGenerator);
		
		return new EFSM(base, initialState, initialContext, parameterGenerator);

	}
	
	
//	public EFSM _build(EFSMState initialState, EFSMContext initialContext,
//			EFSMParameterGenerator<EFSMParameter> parameterGenerator) {
//		Preconditions.checkNotNull(initialState);
//		Preconditions.checkNotNull(initialContext);
//		try {
//			Constructor<EFSM> constructor = getConstructor(initialState, initialContext, parameterGenerator);
//			if (constructor == null) {
//				throw new RuntimeException("No constructor found");
//			}
//			constructor.setAccessible(true);
//			return constructor.newInstance(base, initialState, initialContext, parameterGenerator);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}

//	private Constructor<EFSM> getConstructor(EFSMState initialState, EFSMContext initialContext,
//			EFSMParameterGenerator<InParameter> parameterGenerator) {
//		for (Constructor<?> constructor : efsmTypeClass.getDeclaredConstructors()) {
//			Class<?>[] parameterTypes = constructor.getParameterTypes();
//			if (parameterTypes.length != 4) {
//				continue;
//			}
//
//			final TypeVariable<Class<EFSM>>[] typeParameters = efsmTypeClass.getTypeParameters();
//
//
//			if (parameterTypes[0].isAssignableFrom(base.getClass())
//					&& parameterTypes[1].isAssignableFrom(initialState.getClass())
//					&& parameterTypes[2].isAssignableFrom(initialContext.getClass())
//					&& parameterTypes[3].isAssignableFrom(parameterGenerator.getClass())) {
//				return (Constructor<EFSM>) constructor;
//			}
//
//		}
//		return null;
//	}
	
	public Set<EFSMTransition> incomingTransitionsOf(EFSMState s) {
		return base.incomingEdgesOf(s);
	}

	public Set<EFSMTransition> outgoingTransitionsOf(EFSMState s) {
		return base.outgoingEdgesOf(s);
	}
	
	
}
