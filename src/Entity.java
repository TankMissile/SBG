import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Entity extends JPanel{
	private static final long serialVersionUID = 1L;
	
	protected int maxhealth, health;
	
	protected int x = 0;
	protected int y = 0;
	public int w;
	public int h;
	
	//Boundaries for movement
	protected Boundary boundary[] = new Boundary[4]; //0 up 1 down 2 left 3 right
	protected final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	

	protected boolean airborne = true;

	protected int vert_velocity = 0;
	protected int horiz_velocity = 0;
	
	protected boolean pause = true;
	
	protected int damage;
	private JLabel image;
	
	public Entity(){
	}

	//Image Handling
	protected void loadImage(String path, int w, int h){
		this.removeAll();
		java.net.URL imgURL = getClass().getResource(path);
		System.out.println("Attempting image redraw...");

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			image = new JLabel(new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
			image.setSize(w, h);
			this.add(image);
			this.revalidate();
			this.repaint();
			System.out.println("Redraw succeeded: " + w + " " + h + " Entity size: " + this.getWidth() + " " + this.getHeight());
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
	}

}
