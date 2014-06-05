import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	public static Level level;
	public static LevelEditor levelEditor;
	public static Preloader preloader;
	private static int xoffset = 0, yoffset = 0; //how far to move the level so that the player is visible?
	private static final int playerPadding = 50; //how close to any given edge of the screen can the player be?
	public static ClientWindow activeWindow;
	public static MainMenu mainMenu;
	private JPanel transition = new JPanel();

	public static final int WIDTH = 1080;
	public static final int HEIGHT = 720;

	public void start(){
		this.setTitle("SBG");
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon(getClass().getResource("/img/gameicon.png"))).getImage());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.pack();

		mainMenu = new MainMenu();
		mainMenu.setBackground(Color.black);
		this.setContentPane(mainMenu);

		//Set the default controls or load from a file (not implemented)
		Configs.init();
	}

	//Load a game level
	public void loadLevel(String str){	
		//Create Preloader
		preloader = new Preloader();
		this.setContentPane(preloader);
		//this.revalidate();
		this.repaint();

		//Create a new instance of level and set it as the content pane
		preloader.updateOverview("Creating Level");
		level = new Level();

		//Load the level from a file
		level.loadLevel(str);

		//Make everything visible and resize the window to fit the frame
		this.getContentPane().removeAll();
		this.getContentPane().add(level);
		//this.revalidate();
	}
	
	//Load a game level in the editor
	public void loadLevelEditor(String str){	
		//Create Preloader
		preloader = new Preloader();
		this.setContentPane(preloader);
		//this.revalidate();
		this.repaint();

		//Create a new instance of level and set it as the content pane
		preloader.updateOverview("Creating Level");
		levelEditor= new LevelEditor(this);

		//Load the level from a file
		levelEditor.loadLevel(str);

		//Make everything visible and resize the window to fit the frame
		this.getContentPane().removeAll();
		this.getContentPane().add(levelEditor);
		//this.revalidate();
		
		levelEditor.requestFocusInWindow();
	}

	public static void main(String[] args) {
		ClientWindow.activeWindow = new ClientWindow();
		activeWindow.start();
	}

	public void transition(String s){
		if(s.equals("fade_out_black")){
			transition.setBackground(new Color(0,0,0,0));
			this.add(transition);
			for(int i = 0; i < 255; i++){
				transition.setBackground(new Color(0,0,0,i));
				try {
					Thread.sleep(1000/30);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		else if(s.equals("fade_in_black)")){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) { e.printStackTrace(); }
			for(int i = 255; i > 0; i--){
				transition.setBackground(new Color(0,0,0,i));
				try {
					Thread.sleep(1000/30);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			this.remove(transition);
		}
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
			xChange *= 2.5; //if the player is too far to the side, double camera speed
		}


		//Check the y direction
		if(playerCoords.y < HEIGHT/2 && yoffset < 0){ //go up
			yChange = (HEIGHT/2 - playerCoords.y) / playerPadding;
		}
		else if(playerCoords.y > HEIGHT/2 && yoffset > -1 * level.height){ //go down
			yChange = -1 * (playerCoords.y - HEIGHT/2) / playerPadding;
		}


		//Move the level
		xoffset = level.getLocation().x + xChange;
		yoffset = level.getLocation().y + yChange;

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
