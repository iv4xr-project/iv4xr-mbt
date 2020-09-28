/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

/**
 * @author kifetew
 *
 */
public interface Testcase extends Comparable<Testcase> {
	
	/**
	 * Return the length of the test case (e.g., as number of transitions)
	 * @return length 
	 */
	public int getLength ();
	
	
	public double getFitness ();


	public Testcase clone() throws CloneNotSupportedException;
	
	public boolean isValid ();
	
	public void setValid (boolean valid);


	public void crossOver(Testcase other, int position1, int position2);


	public void mutate();
}
