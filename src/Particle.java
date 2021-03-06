import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Particle extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private int framerate = 20;

	public final static int DUST_POOF = 0,
			JUMP_POOF = 1;

	public static final int TILE_WIDTH = 40, TILE_HEIGHT = 40;

	private int frame = 0;
	private Point start;
	private int lastframe = 0;
	private int loop = 0;
	private int loopframe = 0;

	private String filename = "effectsprite2";
	
	private Level container;

	public Particle(int effect, int x, int y, Level l){
		container = l;
		this.setSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
		this.setLocation(x, y);
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(null);
		switch(effect)
		{
		case DUST_POOF:
			start = new Point(0,0);
			framerate = 20;
			//loop = -1;
			lastframe = 11;
			break;
		case JUMP_POOF:
			start = new Point(0,1);
			//loop = 5;
			lastframe = 12;
			framerate = 45;
			break;
		default:
			System.err.println("Particle created with non-particle value");
			return;
		}

		new Thread(new Runnable(){
			@Override public void run() { animate(); } }).start();
	}

	private void animate(){
		boolean kill = false;
		while(!kill){
			loadImage();
			this.repaint();

			if(frame < lastframe){
				frame += 1;
			}
			else if(loop > 1){
				frame = loopframe;
				loop--;
			}
			else if(loop == -1)
			{
				frame = loopframe;
			}
			else{
				kill = true;
			}

			try {
				Thread.sleep(1000/framerate);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		container.remove(this);
	}

	private void loadImage(){
		try {
			this.removeAll();
			BufferedImage sprite = ImageIO.read(getClass().getResource("/img/" + filename + ".png")).getSubimage((start.x + frame) * TILE_WIDTH, start.y*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
			JLabel image = new JLabel(new ImageIcon(sprite.getScaledInstance(TILE_WIDTH, TILE_HEIGHT, Image.SCALE_SMOOTH)));
			image.setSize(TILE_WIDTH, TILE_HEIGHT);
			this.add(image);
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + "/img/" + filename + ".png"); }
		return;
	}
}
