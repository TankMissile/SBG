import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Entity extends JPanel{
	private static final long serialVersionUID = 1L;
	
	//Combat
	protected int maxhealth, health;
	protected int invincibility = 0;
	protected int damage; //How much damage do I deal per hit?
	protected boolean hurt = false;
	protected int flash_timer = 0; //How long until next damage flash?
	protected final int FLASH_DELAY = 10; //frames between damage flashes
	
	//Modifiers
	protected double speed_modifier = 1; //Movement speed modifier (incoming for entities, outgoing for fluids)
	protected double speed_cap_modifier = 1; //Speed cap modifier (incoming for entities, outgoing for fluids)
	protected double vert_accel_modifier = 1;  //Vertical speed cap modifier (incoming for entities, outgoing for fluids)
	protected boolean in_fluid = false;
	
	//Location and size
	protected int x = 0;
	protected int y = 0;
	public int w;
	public int h;
	
	//~2 characters labeling what type of entity (spike, player, etc)
	public String entity_code;
	
	//Boundaries for movement
	protected Boundary boundary[] = new Boundary[4]; //0 up 1 down 2 left 3 right
	protected final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	
	//Action Variables
	protected boolean airborne = true;
	protected int vert_velocity = 0;
	protected int horiz_velocity = 0;
	
	//Pause this entity?
	protected boolean pause = true;
	
	//Image Handling variables
	private JLabel image;
	protected String img_path;
	
	//Constructor
	public Entity(){
		this.setPreferredSize(new Dimension(w, h));
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(null);
	}
	
	//Is this in contact with the given entity?
	public Entity checkCollision(Entity e){
		if(e.getVisibleRect().intersects(this.getVisibleRect()))
			return this;
		
		return null;
	}
	
	//Thread
	public void animate(){
		
	}

	//Image Handling
	protected void loadImage(String path, int w, int h){
		this.removeAll();
		java.net.URL imgURL = getClass().getResource(path);

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			image = new JLabel(new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
			image.setSize(w, h);
			this.add(image);
			this.revalidate();
			this.repaint();
			if(w != this.getWidth() || h != this.getHeight()) System.out.println("Redraw failed: " + w + " " + h + " Entity size: " + this.getWidth() + " " + this.getHeight());
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
	}
	protected void loadImage(String path, int w, int h, int x, int y){
		try {
			this.removeAll();
			BufferedImage sprite = ImageIO.read(getClass().getResource(path)).getSubimage((x /*+ frame*/) * Wall.TILE_WIDTH, y*Wall.TILE_HEIGHT, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
			JLabel image = new JLabel(new ImageIcon(sprite.getScaledInstance(Wall.TILE_WIDTH, Wall.TILE_HEIGHT, Image.SCALE_SMOOTH)));
			image.setSize(Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
			this.add(image);
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
		return;
	}
	protected JLabel loadTileImage(String path, int w, int h, int x, int y){
		try {
			BufferedImage sprite = ImageIO.read(getClass().getResource(path)).getSubimage((x /*+ frame*/) * Wall.TILE_WIDTH, y*Wall.TILE_HEIGHT, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
			JLabel image = new JLabel(new ImageIcon(sprite.getScaledInstance(Wall.TILE_WIDTH, Wall.TILE_HEIGHT, Image.SCALE_SMOOTH)));
			image.setSize(Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
			return image;
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
		return null;
	}
}
