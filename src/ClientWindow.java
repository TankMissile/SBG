import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel gamebox;
	private Player player;
	
	private final int WIDTH = 1080;
	private final int HEIGHT = 720;

	private Wall[] wall;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	public ClientWindow(){
		this.setResizable(false);
		this.setTitle("QVTN");
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon("img/gameicon.png")).getImage());
		this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		Configs.init();

		gamebox = new JPanel();
		gamebox.setBackground(Color.white);
		gamebox.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		gamebox.setLayout(null);
		gamebox.setVisible(true);
		this.setContentPane(gamebox);

		wall = new Wall[6];
		wall[0] = new Wall(0, 460, 640, 20, new Color(0,0,0));
		wall[0].setSize(640,20);
		wall[1] = new Wall(300,300);
		wall[1].setSize(100,120);
		wall[2] = new Wall(0,300);
		wall[2].setSize(150,300);
		wall[3] = new Wall(400,0);
		wall[3].setSize(20,430);
		wall[4] = new Wall(470,50);
		wall[4].setSize(20,370);
		wall[5] = new Wall(0,0);
		wall[5].setSize(20,60);
		for(Wall p : wall)
			gamebox.add(p);

		player = new Player(new Point(50, 200), this);
		gamebox.add(player);

		this.revalidate();
		this.pack();

		player.pause(false);
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
		int[] bounds = new int[4];

		bounds[UP] = checkBoundsInDirection(UP, o);
		bounds[DOWN] = checkBoundsInDirection(DOWN, o);
		bounds[LEFT] = checkBoundsInDirection(LEFT, o);
		bounds[RIGHT] = checkBoundsInDirection(RIGHT, o);
		
		//System.out.println("up: " + bounds[0] + " down: " + bounds[1] + " left: " + bounds[2] + " right:  " + bounds[3]);
		return bounds;
	}
	private int checkBoundsInDirection(int d, Player o){
		//set initial bound
		int bound = 0;
		switch(d){
		case UP:
			bound = 0;
			break;
		case DOWN:
			bound = gamebox.getHeight();
			break;
		case LEFT:
			bound = 0;
			break;
		case RIGHT:
			bound = gamebox.getWidth();
			break;
		default: 
			System.err.println("Ya dun goofed.");
			break;
		}
		
		//if there are no walls, exit
		if(wall == null)
			return bound;
		
		//Find valid walls and check their positions
		for(Wall w : wall){
			int pue, pde, ple, pre; //edges of wall
			pue = w.getLocation().y;
			pde = pue + w.getHeight();
			ple = w.getLocation().x;
			pre = ple + w.getWidth();
			
			switch(d){
			case UP:
				if(pde > bound && checkDesiredDirection(UP, o, w)) //if in correct direction and is lowest edge encountered
					bound = pde;
				break;
			case DOWN:
				if(pue < bound && checkDesiredDirection(DOWN, o, w)) //if highest edge encountered
					bound = pue;
				break;
			case LEFT:
				if(pre > bound && checkDesiredDirection(LEFT, o, w)) //if rightmost edge encountered
					bound = pre;
				break;
			case RIGHT:
				if(ple < bound && checkDesiredDirection(RIGHT, o, w)) //if leftmost edge encountered
					bound = ple;
				break;
			default: 
				System.err.println("checkBoundsInDirection() goofed.");
				break;
			}
		}

		return bound;
	}
	private boolean checkDesiredDirection(int d, Player o, Wall p){
		int oue, ode, ole, ore;
		int pue, pde, ple, pre;
		oue = o.getLocation().y;
		ode = oue + o.getHeight();
		ole = o.getLocation().x;
		ore = ole + o.getWidth();
		pue = p.getLocation().y;
		pde = pue + p.getHeight();
		ple = p.getLocation().x;
		pre = ple + p.getWidth();
		
		switch(d){
		case UP:
			if(oue > pde - 3 //wall is above
					&& ore > ple  //is not to the right
					&& ole < pre  //is not to the left
					) 
				return true;
			break;
		case DOWN:
			if(ode < pue + 3 //wall is below
					&& ore > ple  //is not to the right
					&& ole < pre  //is not to the left
					) 
				return true;
			break;
		case LEFT:
			if(ole > pre - 3 //wall is to the left
					&& oue < pde  //is not above
					&& ode > pue  //is not below
					)
				return true;
			break;
		case RIGHT:
			if(ore < ple + 3 //wall is to the right
					&& oue < pde  //is not above
					&& ode > pue  //is not to the right
					)
				return true;
			break;
		default:
			System.err.println("checkDesiredDirection() goof.");
			break;
		}
		
		
		return false;
	}

	public static void main(String[] args) {
		new ClientWindow();
	}

}
