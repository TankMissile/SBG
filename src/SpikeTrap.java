
public class SpikeTrap extends Entity {
	private static final long serialVersionUID = 1L;

	public SpikeTrap(int x, int y, int rotation){
		super();
		img_path = "/img/spike.png";
		this.setBounds(x* Wall.TILE_WIDTH, y * Wall.TILE_HEIGHT, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		this.loadImage(img_path, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		
		this.invincible = true;
	}
	
}
