import java.awt.Dimension;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private Level level;
	public Preloader preloader;

	public static final int WIDTH = 1080;
	public static final int HEIGHT = 720;

	public ClientWindow(){
		this.setTitle("SBG");
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon(getClass().getResource("/img/gameicon.png"))).getImage());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		//Create Preloader
		preloader = new Preloader();
		this.setContentPane(preloader);
		this.pack();

		//Set the default controls or load from a file (not implemented)
		Configs.init();

		//Create a new instance of level and set it as the content pane
		preloader.updateOverview("Creating Level");
		level = new Level(this);
		//this.setContentPane(level);
		//this.pack();

		//Load the level from a file
		loadLevel("test");
		
		//Make everything visible and resize the window to fit the frame
		this.setContentPane(level);
		this.revalidate();
		this.pack();
	}

	//Load a game level
	public void loadLevel(String str){
		level.loadLevel(str);

		return;
	}

	public static void main(String[] args) {
		new ClientWindow();
	}

}
