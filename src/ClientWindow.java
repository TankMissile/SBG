import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel gamebox;
	private Player player;

	private Platform[] platform;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	public ClientWindow(){
		this.setResizable(false);
		this.setTitle("QVTN");
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon("img/gameicon.png")).getImage());
		this.getContentPane().setPreferredSize(new Dimension(640, 480));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		Configs.init();

		gamebox = new JPanel();
		gamebox.setBackground(Color.white);
		gamebox.setPreferredSize(new Dimension(640,480));
		gamebox.setLayout(null);
		gamebox.setVisible(true);
		this.setContentPane(gamebox);

		platform = new Platform[4];
		platform[0] = new Platform(0, 460, 640, 20, new Color(0,0,0));
		platform[0].setSize(640,20);
		platform[1] = new Platform(300,300);
		platform[1].setSize(100,120);
		platform[2] = new Platform(0,300);
		platform[2].setSize(150,300);
		platform[3] = new Platform(400,0);
		platform[3].setSize(20,420);
		for(Platform p : platform)
			gamebox.add(p);

		player = new Player(new Point(50, 200), this);
		gamebox.add(player);

		this.revalidate();
		this.pack();

		player.pause(false);
	}

	//Find the movement boundaries for the player by checking each platform
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
		
		//Find valid platforms and check their positions
		for(Platform p : platform){
			int pue, pde, ple, pre; //edges of platform
			pue = p.getLocation().y;
			pde = pue + p.getHeight();
			ple = p.getLocation().x;
			pre = ple + p.getWidth();
			
			switch(d){
			case UP:
				if(pde > bound && checkDesiredDirection(UP, o, p)) //if in correct direction and is lowest edge encountered
					bound = pde;
				break;
			case DOWN:
				if(pue < bound && checkDesiredDirection(DOWN, o, p)) //if highest edge encountered
					bound = pue;
				break;
			case LEFT:
				if(pre > bound && checkDesiredDirection(LEFT, o, p)) //if rightmost edge encountered
					bound = pre;
				break;
			case RIGHT:
				if(ple < bound && checkDesiredDirection(RIGHT, o, p)) //if leftmost edge encountered
					bound = ple;
				break;
			default: 
				System.err.println("checkBoundsInDirection() goofed.");
				break;
			}
		}

		return bound;
	}
	private boolean checkDesiredDirection(int d, Player o, Platform p){
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
			if(oue > pde - 3 //platform is above
					&& ore > ple  //is not to the right
					&& ole < pre  //is not to the left
					) 
				return true;
			break;
		case DOWN:
			if(ode < pue + 3 //platform is below
					&& ore > ple  //is not to the right
					&& ole < pre  //is not to the left
					) 
				return true;
			break;
		case LEFT:
			if(ole > pre - 3 //platform is to the left
					&& oue < pde  //is not above
					&& ode > pue  //is not below
					)
				return true;
			break;
		case RIGHT:
			if(ore < ple + 3 //platform is to the right
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
