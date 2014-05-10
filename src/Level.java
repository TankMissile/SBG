import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLayeredPane;



public class Level extends JLayeredPane{
	private static final long serialVersionUID = 1L;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	private Wall[] wall;
	private Player player;
	private GameMenu gamemenu;

	public static final int WALL_DEPTH = 3, ENTITY_DEPTH = 2, PARTICLE_DEPTH = 1, MENU_DEPTH = 0;

	public Level(){
		super();
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll();
		wall = new Wall[28];
		wall[0] = new Wall(0, 10, Wall.STONE);
		wall[0].setSize(1,1);
		wall[1] = new Wall(1, 3, Wall.STONE);
		wall[1].setSize(1,1);
		wall[2] = new Wall(2, 6, Wall.STONE);
		wall[2].setSize(1,1);
		wall[3] = new Wall(3, 15, Wall.STONE);
		wall[3].setSize(1,1);
		wall[4] = new Wall(4, 13, Wall.STONE);
		wall[4].setSize(1,1);
		wall[5] = new Wall(5, 10, Wall.STONE);
		wall[5].setSize(1,1);
		wall[6] = new Wall(5, 11, Wall.STONE);
		wall[6].setSize(1,1);
		wall[7] = new Wall(5, 12, Wall.STONE);
		wall[7].setSize(1,1);
		wall[8] = new Wall(5, 13, Wall.STONE);
		wall[8].setSize(1,1);
		wall[9] = new Wall(3, 5, Wall.STONE);
		wall[9].setSize(1,1);
		wall[10] = new Wall(3, 6, Wall.STONE);
		wall[10].setSize(1,1);
		wall[11] = new Wall(3, 7, Wall.STONE);
		wall[11].setSize(1,1);
		wall[12] = new Wall(4, 15, Wall.STONE);
		wall[12].setSize(1,1);
		wall[13] = new Wall(5, 15, Wall.STONE);
		wall[13].setSize(1,1);
		wall[14] = new Wall(2, 15, Wall.STONE);
		wall[14].setSize(1,1);
		wall[15] = new Wall(5, 16, Wall.STONE);
		wall[15].setSize(1,1);
		wall[16] = new Wall(5, 17, Wall.STONE);
		wall[16].setSize(1,1);
		wall[17] = new Wall(11, 5, Wall.STONE);
		wall[17].setSize(1,1);
		wall[18] = new Wall(10, 5, Wall.STONE);
		wall[18].setSize(1,1);
		wall[19] = new Wall(8, 17, Wall.STONE);
		wall[19].setSize(1,1);
		wall[20] = new Wall(8, 16, Wall.STONE);
		wall[20].setSize(1,1);
		wall[21] = new Wall(8, 15, Wall.STONE);
		wall[21].setSize(1,1);
		wall[22] = new Wall(8, 14, Wall.STONE);
		wall[22].setSize(1,1);
		wall[23] = new Wall(8, 13, Wall.STONE);
		wall[23].setSize(1,1);
		wall[24] = new Wall(8, 12, Wall.STONE);
		wall[24].setSize(1,1);
		wall[25] = new Wall(8, 11, Wall.STONE);
		wall[25].setSize(1,1);
		wall[26] = new Wall(8, 10, Wall.STONE);
		wall[26].setSize(1,1);
		wall[27] = new Wall(9, 15, Wall.STONE);
		wall[27].setSize(1,1);
		for(Wall p : wall)
			this.add(p, WALL_DEPTH);
		blendWalls();


		player = new Player(new Point(50, 10), this);
		this.add(player, ENTITY_DEPTH);

		gamemenu = new GameMenu();
		gamemenu.setLocation(WIDTH/2-150,HEIGHT/2-200);
		this.add(gamemenu, MENU_DEPTH);

		player.pause(false);
	}
	//Make the walls blend together
	private void blendWalls(){
		boolean up = false, down = false, left = false, right = false;
		int x = Wall.COLUMN, y = Wall.ROW;
		
		for(Wall w: wall){
			up = down = left = right = false;
			x = Wall.COLUMN;
			y = Wall.ROW;
			for(Wall n: wall){
				if(w == n) continue;
				
				if(w.getLocation().x - Wall.TILE_WIDTH == n.getLocation().x && w.getLocation().y == n.getLocation().y){
					left = true;
				}
				else if(w.getLocation().x + Wall.TILE_WIDTH == n.getLocation().x && w.getLocation().y == n.getLocation().y){
					right = true;
				}
				else if(w.getLocation().y - Wall.TILE_WIDTH == n.getLocation().y && w.getLocation().x == n.getLocation().x){
					up = true;
				}
				else if(w.getLocation().y + Wall.TILE_WIDTH == n.getLocation().y && w.getLocation().x == n.getLocation().x){
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
			//gamemenu.open(true);
			this.revalidate();
		}
		else {
			//gamemenu.open(false);
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
