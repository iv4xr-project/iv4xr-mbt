package eu.fbk.iv4xr.mbt.efsm.cps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Precision;
import org.jgrapht.alg.util.Pair;

import eu.fbk.iv4xr.mbt.MBTProperties;
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
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleComputeRadius;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleLess;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleSum;

/**
 * This class updates model generator of class BeamNgModelGenerator adding a
 * guard that checks that the radius of the circle passing through the last 3
 * visited points. This class is used in the SBFT 2023 Cyber-physical systems
 * (CPS) testing competition
 * 
 * @author Davide Prandi
 *
 */

public class BeamNgModelGenerator2 implements EFSMProvider {

	// Field definition

	// all parameters are double and are rounded to n_digits to avoid rounding
	// problems
	private int n_digits = MBTProperties.beamng_n_digitis;

	// field size
	private Double min_x_coord = MBTProperties.beamng_min_x_coord;
	private Double min_y_coord = MBTProperties.beamng_min_y_coord;
	private Double max_x_coord = MBTProperties.beamng_max_x_coord;
	private Double max_y_coord = MBTProperties.beamng_max_y_coord;
	private Const<Double> c_min_x_coord = new Const<Double>(min_x_coord);
	private Const<Double> c_min_y_coord = new Const<Double>(min_y_coord);
	private Const<Double> c_max_x_coord = new Const<Double>(max_x_coord);
	private Const<Double> c_max_y_coord = new Const<Double>(max_y_coord);

	// initial position
	private Double initial_x_coord = MBTProperties.beamng_initial_x_coord;
	private Double initial_y_coord = MBTProperties.beamng_initial_y_coord;

	// current position
	private Var<Double> pos_x = new Var<Double>("pos_x", initial_x_coord);
	private Var<Double> pos_y = new Var<Double>("pos_y", initial_y_coord);
	// previous position: initialize at the same value of current position
	private Var<Double> prev_x = new Var<Double>("prev_x", initial_x_coord);
	private Var<Double> prev_y = new Var<Double>("prev_y", initial_y_coord);

	// context account for current and previous position
	public EFSMContext context = new EFSMContext(pos_x, pos_y, prev_x, prev_y);

	// number of slices to divide 360 angle
	private double nDirections = MBTProperties.beamng_n_directions;
	// maximum permitted angle
	private double maxAngle = MBTProperties.beamng_max_angle;

	// street length
	private double minStreetLength = MBTProperties.beamng_min_street_length;
	private double maxStreetLength = MBTProperties.beamng_max_street_length;
	private double streetChunckLength = MBTProperties.beamng_street_chunck_length;

	// minimum radius
	private double minRadius = MBTProperties.beamng_min_radius;
	private Const<Double> minRadiusConst = new Const<Double>(minRadius);
	
	
	// fields that are computed from MBTProperties values

	// vector of possible street lenghts
	private double[] streetLengthSteps;
	// max possible turn considering maxAngle
	private int maxRotation;
	// min rotation angle
	private double minAngle;

	//// EFSM Model and its builder
	private EFSM model;
	EFSMBuilder modelBuilder = new EFSMBuilder(EFSM.class);

	//// Temporary fields used for support
	// List of EFSM states
	private List<EFSMState> modelStates;
	// name of the initial state
	private String initialStateName = "start";
	// Empty parameters generator (not used)
	EFSMParameterGenerator parGenerator = new DirectionGenerator();
	// Map that stores the position of the states. Used to compute valid next states
	Map<EFSMState, Integer> stateIndex = new HashMap<EFSMState, Integer>();

	/**
	 * Constructor compute the valid set of street lengths and the maximum angle
	 * rotations
	 */
	public BeamNgModelGenerator2() {

		if (minStreetLength >= maxStreetLength) {
			streetLengthSteps = new double[1];
			streetLengthSteps[0] = minStreetLength;
		} else {

			int nSteps = (int) Math.floor((maxStreetLength - minStreetLength) / streetChunckLength);
			streetLengthSteps = new double[1 + nSteps];
			double currentLenght = minStreetLength;
			for (int i = 0; i < streetLengthSteps.length; i++) {
				streetLengthSteps[i] = currentLenght;
				currentLenght = currentLenght + streetChunckLength;
			}
		}

		// compute max possible rotation index
		minAngle = Precision.round(360d / nDirections, n_digits);
		if (minAngle >= maxAngle) {
			maxRotation = 1;
		} else {
			double currentAngle = minAngle;
			int rotation = 0;
			while (currentAngle < maxAngle) {
				rotation++;
				currentAngle += minAngle;
			}
			maxRotation = rotation;
		}

		// compute the set of states of the EFSM
		initStates();
		// build the transitions
		buildEFSM();
	}

	/**
	 * Populate the list of states. There is one state for each possible direction
	 * plus the start state
	 * 
	 * @return
	 */
	private boolean initStates() {
		boolean result = true;

		modelStates = new ArrayList<EFSMState>();

		double totalAngle = minAngle;
		Integer idx = 0;
		// start from minAngle and continue adding minAngle until 360 degree
		while (totalAngle <= 360d) {
			EFSMState state = new EFSMState(Double.toString(totalAngle));
			stateIndex.put(state, idx);
			idx++;
			modelStates.add(state);
			totalAngle = Precision.round(totalAngle + minAngle, n_digits);
		}

		return result;
	}

	/**
	 * Build the model
	 * 
	 * @return
	 */
	private boolean buildEFSM() {
		boolean result = true;

		// from start state it is possible to take any direction
		// not check for the radius as only two points are available
		EFSMState initialState = new EFSMState(initialStateName);
		for (EFSMState s : modelStates) {
			// get angle
			double angle = Double.parseDouble(s.getId());
			// for each street lenght
			for (double step : streetLengthSteps) {
				// create a transition
				EFSMTransition t_start_s = new EFSMTransition();
				// get the guard
				EFSMGuard is_inside_map = isInsideMap(angle, step);
				t_start_s.setGuard(is_inside_map);
				// get the parameter
				EFSMParameter moveParameter = moveParameter(angle, step);
				t_start_s.setInParameter(moveParameter);
				// set operation
				EFSMOperation moveAction = moveActionFromStart(angle, step);
				t_start_s.setOp(moveAction);
				// add to the builder
				modelBuilder.withTransition(initialState, s, t_start_s);
			}
		}

		// iterate over the states and compute valid transitions
		for (EFSMState s : modelStates) {
			// get angle
			//double angle = Double.parseDouble(s.getId());
			// Iterate over the valid reachable states
			List<EFSMState> validStates = getValidStates(s);
			for (EFSMState targetState : validStates) {				
				// get the angle that corresponds to the target state
				Double a = Double.parseDouble(targetState.getId());
				for (double step : streetLengthSteps) {
					// create a transition
					EFSMTransition t_s_targetState = new EFSMTransition();
					// get the guard
					EFSMGuard canMove = canMove(a, step);
					t_s_targetState.setGuard(canMove);
					// get the parameter
					EFSMParameter moveParameter = moveParameter(a, step);
					t_s_targetState.setInParameter(moveParameter);
					// set operation
					EFSMOperation moveAction = moveAction(a, step);
					t_s_targetState.setOp(moveAction);
					// add to the builder
					modelBuilder.withTransition(s, targetState, t_s_targetState);
				}
			}
		}

		model = modelBuilder.build(initialState, context, parGenerator);
		return result;
	}

	/**
	 * Compute the guard from the angle and the street length and the previous
	 * positions.
	 * 
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMGuard isInsideMap(double angle, double streetLength) {

		Pair<Double, Double> delta_xy = getXY(angle, streetLength);

		Const<Double> delta_x = new Const<Double>(delta_xy.getFirst());
		Const<Double> delta_y = new Const<Double>(delta_xy.getSecond());

		Exp<Boolean> pos_y_lt_max_y = new DoubleLess(new DoubleSum(pos_y, delta_y), c_max_y_coord);
		Exp<Boolean> pos_y_gt_min_y = new DoubleGreat(new DoubleSum(pos_y, delta_y), c_min_y_coord);

		Exp<Boolean> pos_x_lt_max_x = new DoubleLess(new DoubleSum(pos_x, delta_x), c_max_x_coord);
		Exp<Boolean> pos_x_gt_min_x = new DoubleGreat(new DoubleSum(pos_x, delta_x), c_min_x_coord);

		Exp<Boolean> check = new BoolAnd(new BoolAnd(pos_x_lt_max_x, pos_x_gt_min_x),
				new BoolAnd(pos_y_lt_max_y, pos_y_gt_min_y));

		EFSMGuard can_move = new EFSMGuard(check);

		return can_move;

	}

	

	/**
	 * Check if the next position is inside the map and if
	 * the radius of the circle intersecting the last 3 position has
	 * radius at least minRadius
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMGuard canMove(double angle, double streetLength) {
	
		// get guard from isInsideMap
		EFSMGuard insideMap = isInsideMap(angle, streetLength);
		
		Pair<Double, Double> next_pos = getXY(angle, streetLength);
		
		Var<Double> next_x = new Var<Double>("next_x", next_pos.getFirst());
		Var<Double> next_y = new Var<Double>("next_y", next_pos.getSecond());
		
		DoubleComputeRadius rad = new DoubleComputeRadius(
				prev_x, prev_y,
				pos_x, pos_y,  
				new DoubleSum(pos_x, new Const( next_pos.getFirst())),
				new DoubleSum(pos_y, new Const( next_pos.getSecond())));
		
		DoubleGreat isLargerThanMinRadius = new DoubleGreat(rad, minRadiusConst);
		
		BoolAnd isInsideAndLarger = new BoolAnd(insideMap.getGuard(), isLargerThanMinRadius);
		
		EFSMGuard canMove = new EFSMGuard(isInsideAndLarger);
		
		
		return canMove;
	}
	
	
	/**
	 * Compute x and y coordinates given the length of the street and the angle
	 * 
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private Pair<Double, Double> getXY(double angle, double streetLength) {

		double randiants = Math.toRadians(angle);
		double x = Precision.round((double) streetLength * Math.cos(randiants), n_digits);
		double y = Precision.round((double) streetLength * Math.sin(randiants), n_digits);

		return new Pair<Double, Double>(x, y);

	}

	@Override
	public EFSM getModel() {
		return model;
	}

	/**
	 * Compute parameter of a transition
	 * 
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMParameter moveParameter(double angle, double streetLength) {

		Pair<Double, Double> delta_xy = getXY(angle, streetLength);

		EFSMParameter par = new EFSMParameter(new Var<Double>("delta_x", delta_xy.getFirst()),
				new Var<Double>("delta_y", delta_xy.getSecond()));

		return par;

	}

	/**
	 * Compute operation of a transition from start
	 * 
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMOperation moveActionFromStart(double angle, double streetLength) {

		Pair<Double, Double> delta_xy = getXY(angle, streetLength);

		Assign<Double> update_x = new Assign<>(pos_x, new DoubleSum(pos_x, new Const(delta_xy.getFirst())));
		Assign<Double> update_y = new Assign<>(pos_y, new DoubleSum(pos_y, new Const(delta_xy.getSecond())));

		EFSMOperation move = new EFSMOperation(update_x, update_y);

		return move;
	}

	
	/**
	 * Compute operation of a transition from any state that is not start
	 * 
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMOperation moveAction(double angle, double streetLength) {

		Pair<Double, Double> delta_xy = getXY(angle, streetLength);

		Assign<Double> update_prev_x = new Assign<>(prev_x, pos_x);
		Assign<Double> update_prev_y = new Assign<>(prev_y, pos_y);
		
		Assign<Double> update_x = new Assign<>(pos_x, new DoubleSum(pos_x, new Const<Double>(delta_xy.getFirst())));
		Assign<Double> update_y = new Assign<>(pos_y, new DoubleSum(pos_y, new Const<Double>(delta_xy.getSecond())));

		EFSMOperation move = new EFSMOperation(update_prev_x, update_prev_y, update_x, update_y);

		return move;
	}
	
	/**
	 * Given the current state that represents an angle, return the list of the valid 
	 * next states accordingly with the maximum allowed rotation
	 * @param s
	 * @return
	 */
	private List<EFSMState> getValidStates(EFSMState s){
		List<EFSMState> validAngles = new ArrayList<EFSMState>();

		// always allow to take the same direction
		validAngles.add(s);
		
		Integer sIdx = stateIndex.get(s);
		Integer maxIdx = stateIndex.size();
		
		for (int i = 1; i <= maxRotation; i++) {
			
			int posIdx = (sIdx + i) % (maxIdx);
			int negIdx = (sIdx  + maxIdx - i) % (maxIdx );
			
			EFSMState posAngle = modelStates.get(posIdx);
			EFSMState negAngle = modelStates.get(negIdx);
			
			validAngles.add(posAngle);
			validAngles.add(negAngle);
		}
		
		return validAngles;
	}
	
}
