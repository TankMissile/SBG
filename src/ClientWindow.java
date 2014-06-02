import java.awt.Dimension;
import java.awt.Point;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	public static Level level;
	public static Preloader preloader;
	private static int xoffset = 0, yoffset = 0; //how far to move the level so that the player is visible?
	private static final int playerPadding = 50; //how close to any given edge of the screen can the player be?
	public static ClientWindow activeWindow;

	public static final int WIDTH = 1080;
	public static final int HEIGHT = 720;

	public void start(){
		this.setTitle("SBG");
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon(getClass().getResource("/img/gameicon.png"))).getImage());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.setContentPane(new JScrollPane());
		this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.pack();

		//Set the default controls or load from a file (not implemented)
		Configs.init();

		loadLevel("test");
	}

	//Load a game level
	public void loadLevel(String str){
		this.getContentPane().removeAll();
		//Create Preloader
		preloader = new Preloader();
		this.setContentPane(preloader);
		//this.pack();

		//Create a new instance of level and set it as the content pane
		preloader.updateOverview("Creating Level");
		level = new Level(this);
		//this.setContentPane(level);
		//this.pack();

		//Load the level from a file
		level.loadLevel(str);

		//Make everything visible and resize the window to fit the frame
		this.getContentPane().removeAll();
		this.getContentPane().add(level);
		//camera.add(level);
		this.revalidate();
		//this.pack();
	}

	public static void main(String[] args) {
		ClientWindow.activeWindow = new ClientWindow();
		activeWindow.start();
	}

	public static void moveCameraTowardPlayer(){
		//Get player coordinates relative to level, then convert to relative to visibe frame
		Point playerCoords = level.getPlayerCoords();
		playerCoords.x = playerCoords.x + xoffset;
		playerCoords.y = playerCoords.y + yoffset;

		int xChange = 0, yChange = 0; //stores how far to move the camera in each direction

		//Check the x direction
		if(playerCoords.x < WIDTH/2 && xoffset < 0){ //go left
			xChange = (WIDTH/2 - playerCoords.x) / playerPadding;
		}
		else if(playerCoords.x > WIDTH/2 && xoffset > -1 * level.width){ //go right
			xChange = -1 * (playerCoords.x - WIDTH/2) / playerPadding;
		}
		if(playerCoords.x < playerPadding || playerCoords.x > WIDTH-playerPadding) {
			xChange *= 2; //if the player is too far to the side, double camera speed
		}
		
		
		//Check the y direction
		if(playerCoords.y < HEIGHT/2 && yoffset < 0){ //go up
			yChange = (HEIGHT/2 - playerCoords.y) / playerPadding;
		}
		else if(playerCoords.y > HEIGHT/2 && yoffset > -1 * level.height){ //go down
			yChange = -1 * (playerCoords.y - HEIGHT/2) / playerPadding;
		}
		if(playerCoords.y < playerPadding || playerCoords.y > HEIGHT-playerPadding){
			yChange *= 2; //if the player is too far to the side, double camera speed
		}


		//Move the level
		xoffset = level.getLocation().x + xChange;
		yoffset = level.getLocation().y + yChange;

		if(level.isPlayerAirDrop() && playerCoords.y > HEIGHT){
			yChange *= 6; //if the player is air dropping, oh boy do we have a problem.
		}
		
		//Make sure you don't go off the edge of the level
		if(xoffset > 0) xoffset = 0;
		else if(xoffset < -1 * (level.width - WIDTH)) xoffset = -1 * (level.width - WIDTH);
		if(yoffset > 0) yoffset = 0;
		else if(yoffset < -1 * (level.height - HEIGHT)) yoffset = -1 * (level.height - HEIGHT);
		
		level.setLocation(xoffset, yoffset);
		level.healthbar.setLocation(-xoffset + 10, -yoffset + 10);
		activeWindow.repaint();
	}

}
