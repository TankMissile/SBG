
public class Boundary {
	public int value;
	public boolean slidable = true;
	public boolean hangable = true;
	public boolean dropthrough = false;
	
	public Boundary(int v){
		value = v;
	}
	
	public Boundary(int v, boolean s, boolean h, boolean d){
		value = v;
		slidable = s;
		hangable = h;
		dropthrough = d;
	}
}
