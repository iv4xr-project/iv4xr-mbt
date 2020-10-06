package eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator;

/*
 * @author wish
 */
/*
 * changes are made to complain Java 8 for using evosuite 1.0.6
 */
public class Door {
	String ID ;
	boolean initialState = false ; // by default closed
	public Door(String id) { this.ID = id ; }
	
	public void operatedBy(Button ... buttons) {
		//for(var B : buttons) {
		for(Button B : buttons) {
			B.associatedDoors.add(this) ;
		}
	}
}
