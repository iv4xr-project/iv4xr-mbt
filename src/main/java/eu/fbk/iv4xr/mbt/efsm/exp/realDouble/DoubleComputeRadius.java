package eu.fbk.iv4xr.mbt.efsm.exp.realDouble;

import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Exp;
import eu.fbk.iv4xr.mbt.efsm.exp.VarSet;


/**
 * Compute the radius of the circle given 3 points
 * (x1, y!), (x2, y2), and (x3,y3)
 * 
 * @author Davide Prandi
 *
 * @param <Double>
 */
public class DoubleComputeRadius implements Exp<Double>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6544275016164000806L;
	
	private Exp<Double> x1;
	private Exp<Double> y1;
	private Exp<Double> x2;
	private Exp<Double> y2;
	private Exp<Double> x3;
	private Exp<Double> y3;
	
	public DoubleComputeRadius(Exp<Double> x1, Exp<Double> y1, Exp<Double> x2, Exp<Double> y2, Exp<Double> x3, Exp<Double> y3  ) {
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		
	}
	
	
	
	
	@Override
	public Const<Double> eval() {
		
		Double x1v = (Double) this.x1.eval().getVal();
		Double y1v = (Double) this.y1.eval().getVal();
		Double x2v = (Double) this.x2.eval().getVal();
		Double y2v = (Double) this.y2.eval().getVal();
		Double x3v = (Double) this.x3.eval().getVal();
		Double y3v = (Double) this.y3.eval().getVal();
		

		
		Double x12 = x1v - x2v;
		Double x13 = x1v - x3v;
	     
		Double y12 = y1v - y2v;
		Double y13 = y1v - y3v;
	 
		Double y31 = y3v - y1v;
		Double y21 = y2v - y1v;
	 
		Double x31 = x3v - x1v;
		Double x21 = x2v - x1v;

		Double sx13 = (Double)(Math.pow(x1v, 2) -
                				Math.pow(x3v, 2));
		Double sy13 = (Double)(Math.pow(y1v, 2) -
				Math.pow(y3v, 2));

		Double sx21 = (Double)(Math.pow(x2v, 2) -
				Math.pow(x1v, 2));
		
		Double sy21 = (Double)(Math.pow(y2v, 2) -
				Math.pow(y1v, 2));
		
		Double f = ((sx13) * (x12)
	            + (sy13) * (x12)
	            + (sx21) * (x13)
	            + (sy21) * (x13))
	            / (2 * ((y31) * (x12) - (y21) * (x13)));
		Double g = ((sx13) * (y12)
	            + (sy13) * (y12)
	            + (sx21) * (y13)
	            + (sy21) * (y13))
	            / (2 * ((x31) * (y12) - (x21) * (y13)));
	    
		Double c = -(Double)Math.pow(x1v, 2) - (Double)Math.pow(y1v, 2) -
                2 * g * x1v - 2 * f * y1v;
		
		Double h = -g;
		Double k = -f;
		Double sqr_of_r = h * h + k * k - c;
		Double r = Math.sqrt(sqr_of_r);
		
		if (Double.isNaN(r) || Double.isInfinite(r)) {
			// it has to be a number because of the branch distance
			r = Double.MAX_VALUE;
		}
		
		Const<Double> val = new Const<Double>(r);
		
		return val;
	}

	@Override
	public VarSet<?> getVariables() {
		VarSet out = new VarSet();
		out.add(x1.getVariables());
		out.add(y1.getVariables());
		out.add(x2.getVariables());
		out.add(y2.getVariables());
		out.add(x3.getVariables());
		out.add(y3.getVariables());
		return out;
	}

	@Override
	public void update(VarSet<?> varSet) {
		x1.update(varSet);
		y1.update(varSet);
		x2.update(varSet);
		y2.update(varSet);
		x3.update(varSet);
		y3.update(varSet);
	}

	@Override
	public boolean equalsUpToValue(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof DoubleComputeRadius) {
			DoubleComputeRadius ist = (DoubleComputeRadius) o;
			if (ist.getX1().equalsUpToValue(x1) &&
				ist.getX2().equalsUpToValue(x2) &&
				ist.getX3().equalsUpToValue(x3) &&
				ist.getY1().equalsUpToValue(y1) &&
				ist.getY2().equalsUpToValue(y2) &&
				ist.getY3().equalsUpToValue(y3)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toDebugString() {	
		return "radius(("+x1.toDebugString()+","+y1.toDebugString()+"),"+
						"("+x2.toDebugString()+","+y2.toDebugString()+")," +
						"("+x3.toDebugString()+","+y3.toDebugString()+"))";
	}




	public Exp<Double> getX1() {
		return x1;
	}




	public Exp<Double> getY1() {
		return y1;
	}




	public Exp<Double> getX2() {
		return x2;
	}




	public Exp<Double> getY2() {
		return y2;
	}




	public Exp<Double> getX3() {
		return x3;
	}




	public Exp<Double> getY3() {
		return y3;
	}

}
