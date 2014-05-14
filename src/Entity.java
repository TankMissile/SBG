import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Entity extends JPanel{
	private static final long serialVersionUID = 1L;
	
	//Framerate
	public int framerate = 30;
	public long lastUpdate = 0;
	
	//Stats
	protected int maxhealth, health;
	protected int damage;
	protected boolean invincible = false;
	
	
	//Position and Size
	protected int x = 0;
	protected int y = 0;
	public int w;
	public int h;
	
	//Boundaries for movement
	protected Boundary boundary[] = new Boundary[4]; //0 up 1 down 2 left 3 right
	protected final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	
	//Action variables
	protected boolean pause = true;
	protected boolean airborne = true;
	protected int vert_velocity = 0;
	protected int horiz_velocity = 0;
	
	//Image
	protected JLabel image;
	protected String img_path;
	
	
	//Constructor
	public Entity(){
		this.setOpaque(false);
		this.setVisible(true);
		this.setLayout(null);
		
	}
	
	//Default animation
	public boolean animate(){
		
		return true;
	}
	
	//check if the given entity is in contact with the current entity (Override individually in each class)
	public boolean checkCollision(Entity e){
		if( e.getVisibleRect().intersects(this.getVisibleRect())){
			return true;
		}
		
		return false;
	}

	//Image Handling
	protected void loadImage(String path, int w, int h){
		this.removeAll();
		java.net.URL imgURL = getClass().getResource(path);

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			
			if(bi == null){
				System.err.println("Image not found: " + path);
			}
			
			image = new JLabel(new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
			image.setSize(w, h);
			this.add(image);
			this.revalidate();
			this.repaint();
			if(w != this.getWidth() || h != this.getHeight()){
				System.out.println("Redraw failed: " + w + " " + h + " Entity size: " + this.getWidth() + " " + this.getHeight());
			}
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
	}

}
