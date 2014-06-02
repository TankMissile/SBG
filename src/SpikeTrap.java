
public class SpikeTrap extends Entity {
	private static final long serialVersionUID = 1L;
	
	public int rotation;
	
	public SpikeTrap(int x, int y, int rotation){
		super();
		this.rotation = rotation;
		img_path = "/img/spike.png";
		this.setBounds(x* Wall.TILE_WIDTH, y * Wall.TILE_HEIGHT, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		this.loadImage(img_path, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		this.damage = -2;
		entity_code = Level.SPIKE_CODE;
		//this.invincible = true;
	}

}