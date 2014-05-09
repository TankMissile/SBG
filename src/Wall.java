import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;


public class Wall extends JButton {
	private static final long serialVersionUID = 1L;

	
	//integer values for each wall material
	public static final int
	NONE = 0,
	STONE = 1,
	DIRT = 2,
	WOOD = 3;

	@SuppressWarnings("unused")
	//integer values for each corner type
	private final int
	TOP = 0,
	MID = 1,
	BOT = 2,
	ROW = 3,
	LEFT = 0,
	RIGHT = 2,
	COLUMN = 3;

	public final int TILE_WIDTH = 40, TILE_HEIGHT = 40;
	
	private int type = NONE;



	public Wall(int x, int y, Color c){
		this.setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
		this.setVisible(true);
		this.setLocation(new Point(x,y));
		this.setBackground(c);
		this.setForeground(c.darker());
		this.repaint();
	}

	public Wall(int x, int y, int t){
		type = t;
		this.setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
		this.setVisible(true);
		this.setLocation(new Point(x*TILE_WIDTH,y*TILE_HEIGHT));
		loadTileBackground(1,1);
		this.setFocusable(false);
		this.setBorderPainted(false);
		this.repaint();
	}

	public Wall(int x, int y){
		this.setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
		this.setVisible(true);
		this.setLocation(new Point(x*TILE_WIDTH,y*TILE_HEIGHT));
		this.repaint();
		this.setBackground(new Color(0,0,0));
	}
	
	@Override
	public void setSize(int w, int h){
		super.setSize(w*TILE_WIDTH,h*TILE_HEIGHT);
	}

	
	private void loadTileBackground(int x, int y){
		
		//get the correct sprite zone for the material
		switch(type)
		{
		case NONE:
			return; //if no type, don't load image
		case STONE:
			break; //stone is top left, no movement necessary
		case WOOD:
			y += 4; //move to next vertical section from top
			break;
		case DIRT:
			x +=4; //move to next horizontal section from top
			break;
		default:
			System.err.println("Wall dun goofed.");
			break;
		}
		
		try {
			BufferedImage sprite = ImageIO.read(getClass().getResource("/img/wallsprite.png")).getSubimage(x*TILE_WIDTH, y*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
			this.setIcon(new ImageIcon(sprite.getScaledInstance(TILE_WIDTH, TILE_HEIGHT, Image.SCALE_SMOOTH)));
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + "/img/wallsprite.png"); }
	}

}
