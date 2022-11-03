package eu.fbk.iv4xr.mbt.efsm.cps;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMProvider;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.enumerator.EnumEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

/**
 * Model that support 4 direction moves. This version support only discrete movements
 * of 1 meter.
 * @author prandi
 *
 */
public class OneState implements EFSMProvider {
	
	/*
	 * Model parameters
	 */
	public Integer step = 1;
	
	public Integer initial_x_coord = 0;
	public Integer initial_y_coord = 0;
	
	public Integer min_x_coord = 0;
	public Integer min_y_coord = 0;
	public Integer max_x_coord = 199;
	public Integer max_y_coord = 199;
	
	/*
	 * Constant
	 */
	public Const<Integer> c_min_x_coord = new Const<Integer>(min_x_coord);
	public Const<Integer> c_min_y_coord = new Const<Integer>(min_y_coord);
	public Const<Integer> c_max_x_coord = new Const<Integer>(max_x_coord);
	public Const<Integer> c_max_y_coord = new Const<Integer>(max_y_coord);
	
	/*
	 * States
	 */
//	public EFSMState start = new EFSMState("Start");
//	public EFSMState north = new EFSMState("North");
//	public EFSMState south = new EFSMState("South");
//	public EFSMState east = new EFSMState("East");
//	public EFSMState west = new EFSMState("West");
	public EFSMState move = new EFSMState("Move");
	
	/*
	 * Variables are x and y positions
	 */
	public Var<Integer> pos_x = new Var<Integer>("pos_x", initial_x_coord);
	public Var<Integer> pos_y = new Var<Integer>("pos_y", initial_x_coord);
	//public Var<Enum> dir = new Var<Enum>("dir", Direction.NORTH);
	
	/*
	 * Input parameters
	 */
	//public Var direction = new Var<Direction>("dir", Direction.NORTH);
	//public EFSMParameter parDirection = new EFSMParameter(direction);
	
	public EFSMParameter parNorthDirection = new EFSMParameter(new Var<Direction>("dir", Direction.NORTH));
	public EFSMParameter parSouthDirection = new EFSMParameter(new Var<Direction>("dir", Direction.SOUTH));
	public EFSMParameter parEastDirection = new EFSMParameter(new Var<Direction>("dir", Direction.EAST));
	public EFSMParameter parWestDirection = new EFSMParameter(new Var<Direction>("dir", Direction.WEST));
	
	/*
	 * Constants
	 */
	public Const<Enum> c_north = new Const<Enum>(Direction.NORTH);
	public Const<Enum> c_south = new Const<Enum>(Direction.SOUTH);
	public Const<Enum> c_east = new Const<Enum>(Direction.EAST);
	public Const<Enum> c_west = new Const<Enum>(Direction.WEST);
	
	
	/*
	 * Not correct: need all compare operators to avoid using 199
	 */
	public Exp<Boolean> pox_x_gt_min_x = new IntGreat(pos_x, c_min_x_coord);
	public Exp<Boolean> pox_x_lt_max_x = new BoolNot(new IntGreat(pos_x, c_max_x_coord));
	public Exp<Boolean> pox_y_gt_min_y = new IntGreat(pos_y, c_min_y_coord);
	public Exp<Boolean> pox_y_lt_max_y = new BoolNot(new IntGreat(pos_y, c_max_y_coord));
	
	public Exp<Boolean> valid_coords = new BoolAnd(new BoolAnd(pox_x_gt_min_x, pox_x_lt_max_x), 
													new BoolAnd(pox_y_gt_min_y, pox_y_lt_max_y));
	

	//public Exp<Boolean> is_north = new EnumEq(dir, c_north);
	//public Exp<Boolean> is_south = new EnumEq(dir, c_south);
	//public Exp<Boolean> is_west = new EnumEq(dir, c_west);
	//public Exp<Boolean> is_east = new EnumEq(dir, c_east);
	
	
	
	public EFSM getModel(){
		
		/*
		 * Parameters: directions 
		 */
//		Var northDirection = new Var<Direction>("dir",Direction.NORTH);
//		EFSMParameter parNorth = new EFSMParameter(northDirection);
//		
//		Var southDirection = new Var<Direction>("dir",Direction.SOUTH);
//		EFSMParameter parSouth = new EFSMParameter(southDirection);
//		
//		Var eastDirection = new Var<Direction>("dir",Direction.EAST);
//		EFSMParameter parEast = new EFSMParameter(eastDirection);
//		
//		Var westDirection = new Var<Direction>("dir",Direction.WEST);
//		EFSMParameter parWest = new EFSMParameter(westDirection);

	
		
		
		/*
		 * Context
		 */
		// EFSMContext fd1Context = new EFSMContext(pos_x,pos_y, dir);
		EFSMContext fd1Context = new EFSMContext(pos_x,pos_y);
		
		/*
		 * Guards: check that pox_x and pos_y are within boundaries
		 */
//		EFSMGuard is_valid_coords_north = new EFSMGuard(new BoolAnd(pox_y_lt_max_y, is_north));
//		EFSMGuard is_valid_coords_south = new EFSMGuard(new BoolAnd(pox_y_gt_min_y, is_south));
//		EFSMGuard is_valid_coords_east = new EFSMGuard(new BoolAnd(pox_x_lt_max_x, is_east));
//		EFSMGuard is_valid_coords_west = new EFSMGuard(new BoolAnd(pox_x_gt_min_x, is_west));
		EFSMGuard is_valid_coords_north = new EFSMGuard(pox_y_lt_max_y);
		EFSMGuard is_valid_coords_south = new EFSMGuard(pox_y_gt_min_y);
		EFSMGuard is_valid_coords_east = new EFSMGuard(pox_x_lt_max_x);
		EFSMGuard is_valid_coords_west = new EFSMGuard(pox_x_gt_min_x);
		
		/*
		 * Assignments: increase/decrease by step
		 */
		Assign<Integer> inc_pos_x = new Assign<>(pos_x, new IntSum(pos_x, new Const(step)));
		Assign<Integer> dec_pos_x = new Assign<>(pos_x, new IntSubt(pos_x, new Const(step)));
		Assign<Integer> inc_pos_y = new Assign<>(pos_y, new IntSum(pos_y, new Const(step)));
		Assign<Integer> dec_pos_y = new Assign<>(pos_y, new IntSubt(pos_y, new Const(step)));
		
		/*
		 * Operations
		 */
		EFSMOperation move_north = new EFSMOperation(inc_pos_y);
		EFSMOperation move_south = new EFSMOperation(dec_pos_y);
		EFSMOperation move_east = new EFSMOperation(inc_pos_x);
		EFSMOperation move_west = new EFSMOperation(dec_pos_x);
		
		
		/*
		 * Transitions
		 */
		
		// from start
		EFSMTransition t_north = new EFSMTransition();
		t_north.setInParameter(parNorthDirection);
		t_north.setGuard(is_valid_coords_north);
		t_north.setOp(move_north);
		
		EFSMTransition t_south = new EFSMTransition();
		t_south.setInParameter(parSouthDirection);
		t_south.setGuard(is_valid_coords_south);
		t_south.setOp(move_south);
		
		EFSMTransition t_east = new EFSMTransition();
		t_east.setInParameter(parEastDirection);
		t_east.setGuard(is_valid_coords_east);
		t_east.setOp(move_east);
		
		EFSMTransition t_west = new EFSMTransition();
		t_west.setInParameter(parWestDirection);
		t_west.setGuard(is_valid_coords_west);
		t_west.setOp(move_west);
		
		/*
		 * EFSM declaration
		 */
		EFSM sbst;
	
		/*
		 * parameter generator
		 */
		DirectionGenerator parGenerator = new DirectionGenerator();
	
	
	    EFSMBuilder sbstBuilder = new EFSMBuilder(EFSM.class);
		
	    sbst = sbstBuilder
	    		.withTransition(move, move, t_north)
	    		.withTransition(move, move, t_south)
	    		.withTransition(move, move, t_east)
	    		.withTransition(move, move, t_west)	
	    		.build(move, fd1Context, parGenerator);
	    
		return sbst;
		
	}
	
	
	
}
