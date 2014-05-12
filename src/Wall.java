import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Wall extends JPanel {
	private static final long serialVersionUID = 1L;

	
	//integer values for each wall material
	public static final int
	NONE = 0,
	STONE = 1,
	DIRT = 2,
	WOOD = 3,
	EYE = 4,
	PLATFORM = 5;

	//integer values for each corner type
	public static final int
	TOP = 0,
	MID = 1,
	BOT = 2,
	ROW = 3,
	LEFT = 0,
	RIGHT = 2,
	COLUMN = 3;

	public static final int TILE_WIDTH = 40, TILE_HEIGHT = 40;
	
	public int type = NONE;
	public boolean slidable = false;
	public boolean hangable = false;
	public boolean dropthrough = false;

	public Wall(int x, int y, int t){
		type = t;
		this.setVisible(true);
		this.setBounds(x*TILE_WIDTH,y*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
		//loadTileBackground(3,3);
		this.setFocusable(false);
		this.setLayout(null);
	}

	
	public void loadTileBackground(int x, int y){
		this.removeAll();
		this.setOpaque(false);
		
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
		case EYE:
			if(x == LEFT)
				x = 8;
			else if(x == RIGHT)
				x = 9;
			slidable = false;
			break;
		case PLATFORM:
			y +=8;
			slidable = false;
			dropthrough = true;
			break;
		default:
			System.err.println("Wall dun goofed.");
			break;
		}
		
		try {
			BufferedImage sprite = ImageIO.read(getClass().getResource("/img/wallsprite2.png")).getSubimage(x*TILE_WIDTH, y*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
			JLabel image = new JLabel(new ImageIcon(sprite.getScaledInstance(TILE_WIDTH, TILE_HEIGHT, Image.SCALE_SMOOTH)));
			image.setSize(TILE_WIDTH, TILE_HEIGHT);
			this.add(image);
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + "/img/wallsprite.png"); }
	}

}
