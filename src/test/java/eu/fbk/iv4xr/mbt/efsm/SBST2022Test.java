package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.sbst2022.Direction;
import eu.fbk.iv4xr.mbt.efsm.sbst2022.NineStates;
import eu.fbk.iv4xr.mbt.efsm.sbst2022.OneState;

public class SBST2022Test {

//	@Test
//	public void test_SBST2022_FourDirection1() {
//
//		OneState fourDirection1 = new OneState();
//		EFSM model = fourDirection1.getModel();
//
//		assertTrue(model.curState.equals(fourDirection1.move));
//
//		// go north
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.NORTH)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 0);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 1);
//
//		// go south
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.SOUTH)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 0);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 0);
//
//		// go east
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.EAST)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 1);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 0);
//
//		// go west
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.WEST)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 0);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 0);
//
//		// illegal moves
//		// go west
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.WEST)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 0);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 0);
//
//		// go south
//		model.transition(new EFSMParameter(new Var<Direction>("dir", Direction.SOUTH)), fourDirection1.move);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_x").getValue() == 0);
//		assertTrue((Integer) model.curContext.getContext().getVariable("pos_y").getValue() == 0);
//
//	}

	@Test
	public void testNineStates() {

		NineStates nineStates = new NineStates();
		EFSM model = nineStates.getModel();

		assertTrue(model.curState.equals(nineStates.start));

		// get available transitions from Start
		EFSMConfiguration initialConfiguration = model.getInitialConfiguration();
		EFSMState state = initialConfiguration.getState();
		Set transitionsOutOf = model.transitionsOutOf(state);
		List<EFSMTransition> listInitialTransition = new ArrayList<EFSMTransition>();
		listInitialTransition.addAll(transitionsOutOf);

		// go south: should fail
		model.transition(listInitialTransition.get(1));
		EFSMConfiguration currentConfiguration = model.getConfiguration();
		EFSMState currentState = currentConfiguration.getState();
		EFSMContext currentContext = currentConfiguration.getContext();
		assertTrue(currentState.getId().equalsIgnoreCase("Start"));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_x").getValue())
				.equals(nineStates.initial_x_coord));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_y").getValue())
				.equals(nineStates.initial_y_coord));

		// go west: should fail
		model.transition(listInitialTransition.get(3));
		currentConfiguration = model.getConfiguration();
		currentState = currentConfiguration.getState();
		currentContext = currentConfiguration.getContext();
		assertTrue(currentState.getId().equalsIgnoreCase("Start"));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_x").getValue())
				.equals(nineStates.initial_x_coord));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_y").getValue())
				.equals(nineStates.initial_y_coord));

		// go north
		model.transition(listInitialTransition.get(0));
		currentConfiguration = model.getConfiguration();
		currentState = currentConfiguration.getState();
		currentContext = currentConfiguration.getContext();
		assertTrue(currentState.getId().equalsIgnoreCase("North"));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_x").getValue())
				.equals(nineStates.initial_x_coord));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_y").getValue())
				.equals(nineStates.initial_y_coord + nineStates.step));

		// get available transitions from North
		Set currentTransitions = model.transitionsOutOf(model.curState);
		List<EFSMTransition> listCurrentTransition = new ArrayList<EFSMTransition>();
		listCurrentTransition.addAll(currentTransitions);

		// go north west: should fail
		model.transition(listCurrentTransition.get(2));
		currentConfiguration = model.getConfiguration();
		currentState = currentConfiguration.getState();
		currentContext = currentConfiguration.getContext();
		assertTrue(currentState.getId().equalsIgnoreCase("North"));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_x").getValue())
				.equals(nineStates.initial_x_coord));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_y").getValue())
				.equals(nineStates.initial_y_coord + nineStates.step));
		
		// go north east
		model.transition(listCurrentTransition.get(1));
		currentConfiguration = model.getConfiguration();
		currentState = currentConfiguration.getState();
		currentContext = currentConfiguration.getContext();
		assertTrue(currentState.getId().equalsIgnoreCase("NorthEast"));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_x").getValue())
				.equals(nineStates.initial_x_coord + nineStates.step));
		assertTrue(((Integer) currentContext.getContext().getVariable("pos_y").getValue())
				.equals(nineStates.initial_y_coord + 2 * nineStates.step));

	}

}
