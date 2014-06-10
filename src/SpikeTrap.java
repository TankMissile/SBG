
public class SpikeTrap extends Entity {
	private static final long serialVersionUID = 1L;
	
	public int rotation;
	
	public SpikeTrap(int x, int y, int rotation){
		super();
		this.rotation = rotation;
		img_path = "/img/spike.png";
		this.w = Wall.TILE_WIDTH;
		this.h = Wall.TILE_HEIGHT;
		this.setBounds(x*Wall.TILE_WIDTH, y*Wall.TILE_HEIGHT, w, h);
		this.loadImage(img_path, w, h);
		this.damage = -2;
		entity_code = Level.SPIKE_CODE;
		//this.invincible = true;
	}

}