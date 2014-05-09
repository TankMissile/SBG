import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private Level level;
	private Player player;

	private final int WIDTH = 1080;
	private final int HEIGHT = 720;

	public ClientWindow(){
		this.setResizable(false);
		this.setTitle("QVTN");
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon(getClass().getResource("/img/gameicon.png"))).getImage());
		this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		Configs.init();

		level = new Level();
		level.setBackground(Color.white);
		level.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		level.setLayout(null);
		level.setVisible(true);
		this.setContentPane(level);

		loadLevel("test");

		player = new Player(new Point(50, 10), this);
		level.add(player);

		this.revalidate();
		this.pack();

		player.pause(false);
	}

	//Load a game level
	public void loadLevel(String str){
		level.loadLevel(str);

		return;
	}

	//Open or close the in-game menu
	public void openGameMenu(boolean b){
		if(b){

		}
		else {

		}
	}

	//Find the movement boundaries for the player by checking each wall
	public int[] findBoundaries(Player o){
		//System.out.println("up: " + bounds[0] + " down: " + bounds[1] + " left: " + bounds[2] + " right:  " + bounds[3]);
		return level.findBoundaries(o);
	}
	

	public static void main(String[] args) {
		new ClientWindow();
	}

}
