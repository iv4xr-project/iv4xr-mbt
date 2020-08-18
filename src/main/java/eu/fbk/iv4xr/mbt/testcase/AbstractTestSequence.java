/**
 * 
 */
package eu.fbk.iv4xr.mbt.testcase;

/**
 * @author kifetew
 *
 */
public class AbstractTestSequence implements Testcase {

	private Path path;
	
	/**
	 * 
	 */
	public AbstractTestSequence() {
		
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(Path path) {
		this.path = path;
	}

}
