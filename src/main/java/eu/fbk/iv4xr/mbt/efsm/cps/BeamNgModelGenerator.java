package eu.fbk.iv4xr.mbt.efsm.cps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntLess;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSubt;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleLess;
import eu.fbk.iv4xr.mbt.efsm.exp.realDouble.DoubleSum;

/**
 * This class generate a model for the SBST 2022 Cyber-physical systems (CPS) testing competition.
 * The model extends NineStates static model. The user can specify several parameters: <br>
 * - the number of slices to divide 360 degree. This gives the minimum turn angle <br>
 * - the maximum turn angle <br>
 * - the minimum length of a piece of the road <br>
 * - the maximum length of a piece of the road <br>
 * - the number of steps between min and max road length
 * 
 * 
 * @author prandi
 *
 */
public class BeamNgModelGenerator implements EFSMProvider {
	
	// Field definition
	
	private int n_digits = MBTProperties.beamng_n_digitis;
	
	// field size
	private Double min_x_coord = Precision.round(MBTProperties.beamng_min_x_coord, n_digits);
	private Double min_y_coord = Precision.round(MBTProperties.beamng_min_y_coord, n_digits);
	private Double max_x_coord = Precision.round(MBTProperties.beamng_max_x_coord, n_digits);
	private Double max_y_coord = Precision.round(MBTProperties.beamng_max_y_coord, n_digits);
	private Const<Double> c_min_x_coord = new Const<Double>(min_x_coord);
	private Const<Double> c_min_y_coord = new Const<Double>(min_y_coord);
	private Const<Double> c_max_x_coord = new Const<Double>(max_x_coord);
	private Const<Double> c_max_y_coord = new Const<Double>(max_y_coord);
	
	
	// initial position
	private Double initial_x_coord = Precision.round(MBTProperties.beamng_initial_x_coord, n_digits);
	private Double initial_y_coord = Precision.round(MBTProperties.beamng_initial_y_coord, n_digits);
	private Var<Double> pos_x = new Var<Double>("pos_x", initial_x_coord);
	private Var<Double> pos_y = new Var<Double>("pos_y", initial_y_coord);
	
	public EFSMContext context = new EFSMContext(pos_x,pos_y);

	
	// number of slices to divide 360 angle
	private double nDirections = Precision.round(MBTProperties.beamng_n_directions, n_digits);
	// maximum permitted angle
	private double maxAngle = Precision.round(MBTProperties.beamng_max_angle, n_digits);
	
	
	// street length
	private double minStreetLength = Precision.round(MBTProperties.beamng_min_street_length, n_digits);
	private double maxStreetLength = Precision.round(MBTProperties.beamng_max_street_length, n_digits);
	private double streetChunckLength = Precision.round(MBTProperties.beamng_street_chunck_length, n_digits);
	
	
	
	
	// dependent values
	
	// vector of possible street lenghts
	private double[] streetLengthSteps;
	// max possible turn considering maxAngle
	private int maxRotation;
	// min rotation angle
	private double minAngle;
		
	// Model
	private EFSM model;
	
	// Tmp values
	private List<EFSMState> modelStates;
	private String initialStateName = "start";

	
	// Empty parameters generator (not used)
	EFSMParameterGenerator parGenerator = new DirectionGenerator();
	
	// EFSM builder
	EFSMBuilder modelBuilder = new EFSMBuilder(EFSM.class);
	
	// tmp map that store the position of the states
	Map<EFSMState, Integer> stateIndex = new HashMap<EFSMState, Integer>();
	
	public BeamNgModelGenerator() {
		
		// init street Length Steps
//		streetLengthSteps = new int[ maxStreetLength - minStreetLength + 1 ];
//		for (int i = 0; i < streetLengthSteps.length; i++) {
//			streetLengthSteps[i] = minStreetLength + i;
//			
//		}
		
		if (minStreetLength >= maxStreetLength) {
			streetLengthSteps = new double[1];
			streetLengthSteps[0] = minStreetLength;
		}else {
			
			int nSteps = (int) Math.floor( (maxStreetLength - minStreetLength) / streetChunckLength );
			streetLengthSteps = new double[ 1 + nSteps ];
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
		}else {
			double currentAngle = minAngle;
			int rotation = 0;
			while (currentAngle < maxAngle) {
				rotation++;
				currentAngle+=minAngle;
			}
			maxRotation = rotation;
		}
		
		initStates();
		buildEFSM();
				
	}
	
	/**
	 * Compute x and y coordinates given the length of the street and the angle
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private Pair<Double,Double> getXY(double angle, double streetLength) {
		
		double randiants = Math.toRadians(angle);
		double x = Precision.round((double)streetLength * Math.cos(randiants),n_digits);
		double y = Precision.round((double)streetLength * Math.sin(randiants),n_digits);
		
		return new Pair<Double, Double>(x, y);

	}
	
	/**
	 * Populate the list of states. There is one state for each possible direction
	 * plus the start state
	 * @return
	 */
	private boolean initStates() {
		boolean result = true;
		
		modelStates = new ArrayList<EFSMState>();
		
		// start state
		//EFSMState start = new EFSMState(initialStateName);
		//modelStates.add(start);
		
		// double totalAngle = 0f;
		double totalAngle = minAngle;
		Integer idx = 0;
		while(totalAngle <= 360d) {
			EFSMState state = new EFSMState(Double.toString(totalAngle));
			stateIndex.put(state, idx);
			idx++;
			modelStates.add(state);
			totalAngle = Precision.round(totalAngle + minAngle, n_digits);
		}
		
		return result;
	}
	
	
	/**
	 * Compute the guard from the angle and the street length
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMGuard canMove(double angle, double streetLength) {
		
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
	 * Compute parameter of a transition
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMParameter moveParameter(double angle, double streetLength) {
		
		Pair<Double, Double> delta_xy = getXY(angle, streetLength);
		
		EFSMParameter par = new EFSMParameter(new Var<Double>("delta_x", delta_xy.getFirst()), new Var<Double>("delta_y", delta_xy.getSecond()));
		
		return par;
	
	}
	
	/**
	 * Compute operation of a transition
	 * @param angle
	 * @param streetLength
	 * @return
	 */
	private EFSMOperation moveAction(double angle, double streetLength) {
		
		Pair<Double, Double> delta_xy = getXY(angle, streetLength);
		
		Assign<Double> update_x = new Assign<>(pos_x, new DoubleSum(pos_x, new Const(delta_xy.getFirst())));
		Assign<Double> update_y = new Assign<>(pos_y, new DoubleSum(pos_y, new Const(delta_xy.getSecond())));

		EFSMOperation move = new EFSMOperation(update_x, update_y);
		
		return move;
	}
	
	private List<Double> getValidAngles(double angle) {
		List<Double> validAngles = new ArrayList<Double>();
		validAngles.add(angle);
		for (int i = 1; i <= maxRotation; i++) {
			double posAngle = Precision.round(angle + i * minAngle, n_digits);
			double negAngle = Precision.round(angle - i * minAngle, n_digits);
			Precision.round(angle + nDirections * minAngle , n_digits);
			
			if (negAngle < 0f) {
				negAngle = Precision.round(360f + negAngle, n_digits);
			}
			if (posAngle >= 360f) {
				posAngle = Precision.round(posAngle - 360f, n_digits);
			}
			validAngles.add(posAngle);
			validAngles.add(negAngle);
		}

	
		return validAngles;
	}
	
	private List<Double> getValidAngles(EFSMState s){
		List<Double> validAngles = new ArrayList<Double>();
		validAngles.add(Double.parseDouble(s.getId()));
		
		Integer sIdx = stateIndex.get(s);
		Integer maxIdx = stateIndex.size();
		
		for (int i = 1; i <= maxRotation; i++) {
			
			int posIdx = (sIdx + i) % (maxIdx);
			int negIdx = (sIdx  + maxIdx - i) % (maxIdx );
			
			double posAngle = Double.parseDouble(modelStates.get(posIdx).getId());
			double negAngle = Double.parseDouble(modelStates.get(negIdx).getId());
			
			validAngles.add(posAngle);
			validAngles.add(negAngle);
		}
		
		return validAngles;
	}
	
	/**
	 * Build the model
	 * @return
	 */
	private boolean buildEFSM() {
		boolean result = true;

		// from start state it is possible to take any direction
		EFSMState initialState = new EFSMState(initialStateName);
		for (EFSMState s : modelStates) {
			// get angle
			double angle = Double.parseDouble(s.getId());
			// for each street lenght
			for (double step : streetLengthSteps) {
				// create a transition
				EFSMTransition t_start_s = new EFSMTransition();
				// get the guard
				EFSMGuard canMove = canMove(angle, step);
				t_start_s.setGuard(canMove);
				// get the parameter
				EFSMParameter moveParameter = moveParameter(angle, step);
				t_start_s.setInParameter(moveParameter);
				// set operation
				EFSMOperation moveAction = moveAction(angle, step);
				t_start_s.setOp(moveAction);
				// add to the builder
				modelBuilder.withTransition(initialState, s, t_start_s);
			}
		}
		
		// iterate over the states and compute valid transitions
		for (EFSMState s : modelStates) {
			// get angle
			double angle = Double.parseDouble(s.getId());
			//List<Double> validAngles = getValidAngles(angle);
			List<Double> validAngles = getValidAngles(s);
			for(Double a : validAngles) {
				EFSMState targetState = new EFSMState(Double.toString(a));
				if (!modelStates.contains(targetState)) {
					throw new RuntimeException();
				}
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
	
	
	public EFSM getModel() {
		return model;
	}
}
