package eu.fbk.iv4xr.mbt.efsm.sbst2022;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameter;
import eu.fbk.iv4xr.mbt.efsm.EFSMParameterGenerator;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolAnd;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

public class NineStates {
	
	/*
	 * Model parameters
	 */
	public Integer step = 20;
	
	public Integer initial_x_coord = 20;
	public Integer initial_y_coord = 20;
	
	public Integer min_x_coord = 20;
	public Integer min_y_coord = 20;
	public Integer max_x_coord = 180;
	public Integer max_y_coord = 180;
	
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
	public EFSMState start = new EFSMState("Start");
	public EFSMState north = new EFSMState("North");
	public EFSMState south = new EFSMState("South");
	public EFSMState east = new EFSMState("East");	
	public EFSMState west = new EFSMState("West");
	public EFSMState north_east = new EFSMState("NorthEast");
	public EFSMState south_east = new EFSMState("SouthEest");
	public EFSMState north_west = new EFSMState("NorthWest");	
	public EFSMState south_west = new EFSMState("SouthWest");
	
	/*
	 * Variables are x and y positions
	 */
	public Var<Integer> pos_x = new Var<Integer>("pos_x", initial_x_coord);
	public Var<Integer> pos_y = new Var<Integer>("pos_y", initial_x_coord);
	
	/*
	 * Context
	 */
	public EFSMContext context = new EFSMContext(pos_x,pos_y);
	
	/*
	 * Input parameters are delta_x and delta_y
	 */
	public EFSMParameter parGoNorth = new EFSMParameter(new Var<Integer>("delta_x", 0), new Var<Integer>("delta_y", step));
	public EFSMParameter parGoSouth = new EFSMParameter(new Var<Integer>("delta_x", 0), new Var<Integer>("delta_y", -step));
	public EFSMParameter parGoEast = new EFSMParameter(new Var<Integer>("delta_x", step), new Var<Integer>("delta_y", 0));
	public EFSMParameter parGoWest = new EFSMParameter(new Var<Integer>("delta_x", -step), new Var<Integer>("delta_y", 0));
	
	// TODO: compute correct delta x and y for diagonal movements
	public EFSMParameter parGoNorthEast = new EFSMParameter(new Var<Integer>("delta_x", step), new Var<Integer>("delta_y", step));
	public EFSMParameter parGoSouthEast = new EFSMParameter(new Var<Integer>("delta_x", step), new Var<Integer>("delta_y", -step));
	public EFSMParameter parGoNorthWest = new EFSMParameter(new Var<Integer>("delta_x", -step), new Var<Integer>("delta_y", step));
	public EFSMParameter parGoSouthWest = new EFSMParameter(new Var<Integer>("delta_x", -step), new Var<Integer>("delta_y", -step));
	
	/*
	 * Guards
	 */
	// delta variables for guards
	public Var<Integer> guardDelta_x = new Var<Integer>("delta_x", 0);
	public Var<Integer> guardDelta_y = new Var<Integer>("delta_y", 0);
	
	public Exp<Boolean> pos_x_gt_min_x = new IntGreat(new IntSum(pos_x, guardDelta_x), c_min_x_coord);
	public Exp<Boolean> pos_x_lt_max_x = new IntLess(new IntSum(pos_x, guardDelta_x), c_max_x_coord);
	public Exp<Boolean> pos_y_gt_min_y = new IntGreat(new IntSum(pos_y, guardDelta_y), c_min_y_coord);
	public Exp<Boolean> pos_y_lt_max_y = new IntLess(new IntSum(pos_y, guardDelta_y), c_max_y_coord);
	
	EFSMGuard can_move_north = new EFSMGuard(pos_y_lt_max_y);
	EFSMGuard can_move_south = new EFSMGuard(pos_y_gt_min_y);
	EFSMGuard can_move_east = new EFSMGuard(pos_x_lt_max_x);
	EFSMGuard can_move_west = new EFSMGuard(pos_x_gt_min_x);
	EFSMGuard can_move_north_east = new EFSMGuard(new BoolAnd(pos_y_lt_max_y, pos_x_lt_max_x));
	EFSMGuard can_move_south_east = new EFSMGuard(new BoolAnd(pos_y_gt_min_y, pos_x_lt_max_x));
	EFSMGuard can_move_north_west = new EFSMGuard(new BoolAnd(pos_y_lt_max_y, pos_x_gt_min_x));
	EFSMGuard can_move_south_west = new EFSMGuard(new BoolAnd(pos_y_gt_min_y, pos_x_gt_min_x));
	
	
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
	EFSMOperation move_north_east = new EFSMOperation(inc_pos_y, inc_pos_x);
	EFSMOperation move_south_east = new EFSMOperation(dec_pos_y, inc_pos_x);
	EFSMOperation move_north_west = new EFSMOperation(inc_pos_y, dec_pos_x);
	EFSMOperation move_south_west = new EFSMOperation(dec_pos_y, dec_pos_x);
	

	public EFSM getModel(){
		
		// From start
		EFSMTransition t_start_north = new EFSMTransition<>();
		t_start_north.setInParameter(parGoNorth);
		t_start_north.setGuard(can_move_north);
		t_start_north.setOp(move_north);
		
		EFSMTransition t_start_south = new EFSMTransition<>();
		t_start_south.setInParameter(parGoSouth);
		t_start_south.setGuard(can_move_south);
		t_start_south.setOp(move_south);
		
		EFSMTransition t_start_east = new EFSMTransition<>();
		t_start_east.setInParameter(parGoEast);
		t_start_east.setGuard(can_move_east);
		t_start_east.setOp(move_east);
		
		EFSMTransition t_start_west = new EFSMTransition<>();
		t_start_west.setInParameter(parGoWest);
		t_start_west.setGuard(can_move_west);
		t_start_west.setOp(move_west);
		
		EFSMTransition t_start_ne = new EFSMTransition<>();
		t_start_ne.setInParameter(parGoNorthEast);
		t_start_ne.setGuard(can_move_north_east);
		t_start_ne.setOp(move_north_east);
		
		EFSMTransition t_start_se = new EFSMTransition<>();
		t_start_se.setInParameter(parGoSouthEast);
		t_start_se.setGuard(can_move_south_east);
		t_start_se.setOp(move_south_east);
		
		EFSMTransition t_start_nw = new EFSMTransition<>();
		t_start_nw.setInParameter(parGoNorthWest);
		t_start_nw.setGuard(can_move_north_west);
		t_start_nw.setOp(move_north_west);
		
		EFSMTransition t_start_sw = new EFSMTransition<>();
		t_start_sw.setInParameter(parGoSouthWest);
		t_start_sw.setGuard(can_move_south_west);
		t_start_sw.setOp(move_south_west);
		
		
		// from north can go to N, NE, NW
		EFSMTransition t_north_north = new EFSMTransition<>();
		t_north_north.setInParameter(parGoNorth);
		t_north_north.setGuard(can_move_north);
		t_north_north.setOp(move_north);
		
		EFSMTransition t_north_ne = new EFSMTransition<>();
		t_north_ne.setInParameter(parGoNorthEast);
		t_north_ne.setGuard(can_move_north_east);
		t_north_ne.setOp(move_north_east);
		
		EFSMTransition t_north_nw = new EFSMTransition<>();
		t_north_nw.setInParameter(parGoNorthWest);
		t_north_nw.setGuard(can_move_north_west);
		t_north_nw.setOp(move_north_west);
		
		
		// from south can go to S, SE, SW
		EFSMTransition t_south_south = new EFSMTransition<>();
		t_south_south.setInParameter(parGoSouth);
		t_south_south.setGuard(can_move_south);
		t_south_south.setOp(move_south);
		
		EFSMTransition t_south_se = new EFSMTransition<>();
		t_south_se.setInParameter(parGoSouthEast);
		t_south_se.setGuard(can_move_south_east);
		t_south_se.setOp(move_south_east);
		
		EFSMTransition t_south_sw = new EFSMTransition<>();
		t_south_sw.setInParameter(parGoSouthWest);
		t_south_sw.setGuard(can_move_south_west);
		t_south_sw.setOp(move_south_west);
		
		// from east can go to E, NE, SE
		EFSMTransition t_east_east = new EFSMTransition<>();
		t_east_east.setInParameter(parGoEast);
		t_east_east.setGuard(can_move_east);
		t_east_east.setOp(move_east);
		
		EFSMTransition t_east_ne = new EFSMTransition<>();
		t_east_ne.setInParameter(parGoNorthEast);
		t_east_ne.setGuard(can_move_north_east);
		t_east_ne.setOp(move_north_east);
		
		EFSMTransition t_east_se = new EFSMTransition<>();
		t_east_se.setInParameter(parGoSouthEast);
		t_east_se.setGuard(can_move_south_east);
		t_east_se.setOp(move_south_east);
		
		// from west can go to W, NW, SW
		EFSMTransition t_west_west = new EFSMTransition<>();
		t_west_west.setInParameter(parGoWest);
		t_west_west.setGuard(can_move_west);
		t_west_west.setOp(move_west);
		
		EFSMTransition t_west_nw = new EFSMTransition<>();
		t_west_nw.setInParameter(parGoNorthWest);
		t_west_nw.setGuard(can_move_north_west);
		t_west_nw.setOp(move_north_west);
		
		EFSMTransition t_west_sw = new EFSMTransition<>();
		t_west_sw.setInParameter(parGoSouthWest);
		t_west_sw.setGuard(can_move_south_west);
		t_west_sw.setOp(move_south_west);
		
		
		// from north east can do NE, N, E
		EFSMTransition t_ne_ne = new EFSMTransition<>();
		t_ne_ne.setInParameter(parGoNorthEast);
		t_ne_ne.setGuard(can_move_north_east);
		t_ne_ne.setOp(move_north_east);
		
		EFSMTransition t_ne_north = new EFSMTransition<>();
		t_ne_north.setInParameter(parGoNorth);
		t_ne_north.setGuard(can_move_north);
		t_ne_north.setOp(move_north);
		
		EFSMTransition t_ne_east = new EFSMTransition<>();
		t_ne_east.setInParameter(parGoEast);
		t_ne_east.setGuard(can_move_east);
		t_ne_east.setOp(move_east);
		
		// from north west can do NW, N, W
		EFSMTransition t_nw_nw = new EFSMTransition<>();
		t_nw_nw.setInParameter(parGoNorthWest);
		t_nw_nw.setGuard(can_move_north_west);
		t_nw_nw.setOp(move_north_west);
		
		EFSMTransition t_nw_north = new EFSMTransition<>();
		t_nw_north.setInParameter(parGoNorth);
		t_nw_north.setGuard(can_move_north);
		t_nw_north.setOp(move_north);
		
		EFSMTransition t_nw_west = new EFSMTransition<>();
		t_nw_west.setInParameter(parGoWest);
		t_nw_west.setGuard(can_move_west);
		t_nw_west.setOp(move_west);
		
		// from south east can do SE, S, E
		EFSMTransition t_se_se = new EFSMTransition<>();
		t_se_se.setInParameter(parGoSouthEast);
		t_se_se.setGuard(can_move_south_east);
		t_se_se.setOp(move_south_east);
		
		EFSMTransition t_se_south = new EFSMTransition<>();
		t_se_south.setInParameter(parGoSouth);
		t_se_south.setGuard(can_move_south);
		t_se_south.setOp(move_south);
		
		EFSMTransition t_se_east = new EFSMTransition<>();
		t_se_east.setInParameter(parGoEast);
		t_se_east.setGuard(can_move_east);
		t_se_east.setOp(move_east);
		
		// from south west can do SW, S, W
		EFSMTransition t_sw_sw = new EFSMTransition<>();
		t_sw_sw.setInParameter(parGoSouthWest);
		t_sw_sw.setGuard(can_move_south_west);
		t_sw_sw.setOp(move_south_west);
		
		EFSMTransition t_sw_south = new EFSMTransition<>();
		t_sw_south.setInParameter(parGoSouth);
		t_sw_south.setGuard(can_move_south);
		t_sw_south.setOp(move_south);
		
		EFSMTransition t_sw_west = new EFSMTransition<>();
		t_sw_west.setInParameter(parGoWest);
		t_sw_west.setGuard(can_move_west);
		t_sw_west.setOp(move_west);
		
		/*
		 * EFSM declaration
		 */
		EFSM sbst;
	
		
		/*
		 * Empty parameters generator
		 */
		EFSMParameterGenerator parGenerator = new DirectionGenerator();
		
		/*
		 * EFSM builder
		 */
		EFSMBuilder sbstBuilder = new EFSMBuilder(EFSM.class);
		
		sbst = sbstBuilder
			// from Start
			.withTransition(start, north, t_start_north)
			.withTransition(start, south, t_start_south)
			.withTransition(start, east, t_start_east)
			.withTransition(start, west, t_start_west)
			.withTransition(start, north_east, t_start_ne)
			.withTransition(start, north_west, t_start_nw)
			.withTransition(start, south_east, t_start_se)
			.withTransition(start, south_west, t_start_sw)
			// from North
			.withTransition(north, north, t_north_north)
			.withTransition(north, north_east, t_north_ne)
			.withTransition(north, north_west, t_north_nw)
			// from South
			.withTransition(south, south, t_south_south)
			.withTransition(south, south_east, t_south_se)
			.withTransition(south, south_west, t_south_sw)
			// from East
			.withTransition(east, east, t_east_east)
			.withTransition(east, north_east, t_east_ne)
			.withTransition(east, south_east, t_east_se)
			// from West
			.withTransition(west, west, t_west_west)
			.withTransition(west, north_west, t_west_nw)
			.withTransition(west, south_west, t_west_sw)
			// from North East
			.withTransition(north_east, north_east, t_ne_ne)
			.withTransition(north_east, north, t_ne_north)
			.withTransition(north_east, east, t_ne_east)
			// from North West
			.withTransition(north_west, north_west, t_nw_nw)
			.withTransition(north_west, north, t_nw_north)
			.withTransition(north_west, west, t_nw_west)
			// from South East
			.withTransition(south_east, south_east, t_se_se)
			.withTransition(south_east, south, t_se_south)
			.withTransition(south_east, east, t_se_east)
			// from South West
			.withTransition(south_west, south_west, t_sw_sw)
			.withTransition(south_west, south, t_sw_south)
			.withTransition(south_west, west, t_sw_west)
			.build(start, context, parGenerator);
		
		return sbst;
	}
	
	
	
}
