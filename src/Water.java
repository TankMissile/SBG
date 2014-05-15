import javax.swing.JLabel;


public class Water extends Entity{
	private static final long serialVersionUID = 1L;
	
	public static final int FRAMERATE = 1000/30;
	public static final int TILE_WIDTH = 40, TILE_HEIGHT = 40;
	private static final int TOP_BORDER = 14;
	
	JLabel[][] images;
	
	
	public Water(int x, int y, int w, int h){
		this.w = w;
		this.h = h - TOP_BORDER;
		this.setBounds(x*Wall.TILE_WIDTH, y*Wall.TILE_HEIGHT + TOP_BORDER, w*Wall.TILE_WIDTH, h*Wall.TILE_HEIGHT);
		
		speed_modifier = 0.5;
		speed_cap_modifier = 0.5;
		
		entity_code = Level.WATER_CODE;
		img_path = "/img/fluidsprite.png";
		
		images = new JLabel[w][h];
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				System.out.println("Adding water tile: " + i + " " + j);
				images[i][j] = loadTileImage(img_path, w, h, 0, 0);
				if(images[i][j] != null){
					images[i][j].setLocation(i * Wall.TILE_WIDTH, j * Wall.TILE_HEIGHT);
					this.add(images[i][j]);
				}
				else
					System.err.println("Image could not be created at: " + i + " " + j);
			}
		}
	}
	
}
