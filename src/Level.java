import java.awt.Color;
import java.awt.Point;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;



public class Level extends JLayeredPane{
	private static final long serialVersionUID = 1L;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	private Wall[] wall;
	private Player player;
	private GameMenu gamemenu;

	public static final int BACKGROUND_DEPTH = 10000, WALL_DEPTH = 200, ENTITY_DEPTH = 100, PARTICLE_DEPTH = 1, MENU_DEPTH = 0;

	public Level(){
		super();
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll();
		
		wall = new Wall[55];
		wall[0] = new Wall(0, 10, Wall.STONE);
		wall[1] = new Wall(1, 3, Wall.STONE);
		wall[2] = new Wall(2, 6, Wall.STONE);
		wall[3] = new Wall(3, 15, Wall.STONE);
		wall[4] = new Wall(4, 13, Wall.STONE);
		wall[5] = new Wall(5, 10, Wall.STONE);
		wall[6] = new Wall(5, 11, Wall.STONE);
		wall[7] = new Wall(5, 12, Wall.STONE);
		wall[8] = new Wall(5, 13, Wall.STONE);
		wall[9] = new Wall(3, 5, Wall.STONE);
		wall[10] = new Wall(3, 6, Wall.STONE);
		wall[11] = new Wall(3, 7, Wall.STONE);
		wall[12] = new Wall(4, 15, Wall.STONE);
		wall[13] = new Wall(5, 15, Wall.STONE);
		wall[14] = new Wall(2, 15, Wall.STONE);
		wall[15] = new Wall(5, 16, Wall.STONE);
		wall[16] = new Wall(5, 17, Wall.DIRT);
		wall[17] = new Wall(11, 5, Wall.STONE);
		wall[18] = new Wall(10, 5, Wall.STONE);
		wall[19] = new Wall(8, 17, Wall.DIRT);
		wall[20] = new Wall(8, 16, Wall.STONE);
		wall[21] = new Wall(8, 15, Wall.STONE);
		wall[22] = new Wall(8, 14, Wall.STONE);
		wall[23] = new Wall(8, 13, Wall.STONE);
		wall[24] = new Wall(8, 12, Wall.STONE);
		wall[25] = new Wall(8, 11, Wall.STONE);
		wall[26] = new Wall(8, 10, Wall.STONE);
		wall[27] = new Wall(9, 15, Wall.STONE);
		wall[28] = new Wall(0, 17, Wall.DIRT);
		wall[29] = new Wall(1, 17, Wall.DIRT);
		wall[30] = new Wall(2, 17, Wall.DIRT);
		wall[31] = new Wall(3, 17, Wall.DIRT);
		wall[32] = new Wall(4, 17, Wall.DIRT);
		wall[33] = new Wall(5, 17, Wall.DIRT);
		wall[34] = new Wall(6, 17, Wall.DIRT);
		wall[35] = new Wall(7, 17, Wall.DIRT);
		wall[36] = new Wall(8, 17, Wall.DIRT);
		wall[37] = new Wall(9, 17, Wall.DIRT);
		wall[38] = new Wall(10, 17, Wall.DIRT);
		wall[39] = new Wall(11, 17, Wall.DIRT);
		wall[40] = new Wall(12, 17, Wall.DIRT);
		wall[41] = new Wall(13, 17, Wall.DIRT);
		wall[42] = new Wall(14, 17, Wall.DIRT);
		wall[43] = new Wall(15, 17, Wall.DIRT);
		wall[44] = new Wall(16, 17, Wall.DIRT);
		wall[45] = new Wall(17, 17, Wall.DIRT);
		wall[46] = new Wall(18, 17, Wall.DIRT);
		wall[47] = new Wall(19, 17, Wall.DIRT);
		wall[48] = new Wall(20, 17, Wall.DIRT);
		wall[49] = new Wall(21, 17, Wall.DIRT);
		wall[50] = new Wall(22, 17, Wall.DIRT);
		wall[51] = new Wall(23, 17, Wall.DIRT);
		wall[52] = new Wall(24, 17, Wall.DIRT);
		wall[53] = new Wall(25, 17, Wall.DIRT);
		wall[54] = new Wall(26, 17, Wall.DIRT);
		for(Wall p : wall)
			this.add(p, WALL_DEPTH);
		blendWalls();


		player = new Player(new Point(400, 150), this);
		this.add(player, ENTITY_DEPTH);

		gamemenu = new GameMenu();
		//gamemenu.setLocation(new Point(ClientWindow.WIDTH/2-150,ClientWindow.HEIGHT/2-200));
		this.add(gamemenu, MENU_DEPTH);
		
		JPanel bgColor = new JPanel();
		bgColor.setBackground(new Color(200, 220, 255));
		bgColor.setSize(ClientWindow.WIDTH, ClientWindow.HEIGHT);
		this.add(bgColor, BACKGROUND_DEPTH);

		player.pause(false);
	}
	//Make the walls blend together
	private void blendWalls(){
		boolean up = false, down = false, left = false, right = false;
		int x = Wall.COLUMN, y = Wall.ROW;
		int xpos, ypos;
		int n_xpos, n_ypos;
		
		for(Wall w: wall){
			up = down = left = right = false;
			x = Wall.COLUMN;
			y = Wall.ROW;
			xpos = w.getLocation().x;
			ypos = w.getLocation().y;
			
			if(ypos + Wall.TILE_HEIGHT >= ClientWindow.HEIGHT)
				down = true;
			if(xpos <= 0)
				left = true;
			if(xpos + Wall.TILE_WIDTH >= ClientWindow.WIDTH)
				right = true;
			
			for(Wall n: wall){
				if(w == n) continue;
				n_xpos = n.getLocation().x;
				n_ypos = n.getLocation().y;
				
				if((w.type == n.type) && xpos - Wall.TILE_WIDTH == n_xpos && ypos == n_ypos){
					left = true;
				}
				else if((w.type == n.type) && xpos + Wall.TILE_WIDTH == n_xpos && ypos == n_ypos){
					right = true;
				}
				else if((w.type == n.type) && ypos - Wall.TILE_WIDTH == n_ypos && xpos == n_xpos){
					up = true;
				}
				else if(ypos + Wall.TILE_WIDTH == n_ypos && xpos == n_xpos){
					down = true;
				}
			}
			
			if(up && down)
				y = Wall.MID;
			else if(up)
				y = Wall.BOT;
			else if(down)
				y = Wall.TOP;
			
			if(left && right)
				x = Wall.MID;
			else if(left)
				x = Wall.RIGHT;
			else if(right)
				x = Wall.LEFT;
			
			
			w.loadTileBackground(x, y);
		}
	}

	//Open or close the in-game menu
	public void openGameMenu(boolean b){
		if(b){
			player.pause(true);
			gamemenu.open(true);
			this.revalidate();
		}
		else {
			gamemenu.open(false);
			player.pause(false);
			this.revalidate();
		}
	}
	
	//Add a particle with a given effect to the scene
	public void addParticle(int effect, int x, int y){
		Particle p = new Particle(effect, x, y, this);
		p.setSize(Particle.TILE_WIDTH, Particle.TILE_WIDTH);
		this.add(p, PARTICLE_DEPTH);
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
			bound = this.getHeight();
			break;
		case LEFT:
			bound = 0;
			break;
		case RIGHT:
			bound = this.getWidth();
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
		int oue, ode, ole, ore; //player edges
		int pue, pde, ple, pre; //wall edges
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
			if(oue > pue //wall is above
					&& ore > ple  //is not to the right
					&& ole < pre  //is not to the left
					) 
				return true;
			break;
		case DOWN:
			if(ode < pde //wall is below
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
}
