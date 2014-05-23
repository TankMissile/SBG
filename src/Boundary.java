
public class Boundary {
	public int value; //Integer value of the boundary (either x or y axis)
	public boolean slidable = true; //Can the player interact with this boundary?
	public boolean hangable = true; //Can the player hang from the top of this boundary?
	public boolean dropthrough = false; //Can the player drop through this boundary by holding the crouch button?
	
	//Basic Constructor
	public Boundary(int v){
		value = v;
	}
	
	//Variable-based constructor
	public Boundary(int v, boolean s, boolean h, boolean d){
		value = v;
		slidable = s;
		hangable = h;
		dropthrough = d;
	}
}
