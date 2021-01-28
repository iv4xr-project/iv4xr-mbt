package eu.fbk.iv4xr.mbt.utils;

public class EqualsWithNulls {
	public static final boolean test(Object a, Object b) {
		if (a==b) return true;
		if ((a==null) || (b==null)) return false;
		return a.equals(b);
		
	}
}
