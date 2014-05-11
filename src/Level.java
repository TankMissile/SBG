import java.awt.Color;
import java.awt.Point;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;



public class Level extends JLayeredPane{
	private static final long serialVersionUID = 1L;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	private Wall[][] wall;
	private Player player;
	private GameMenu gamemenu;

	public static final int BACKGROUND_DEPTH = 10000, WALL_DEPTH = 200, ENTITY_DEPTH = 100, PARTICLE_DEPTH = 1, MENU_DEPTH = 0;

	public Level(){
		super();
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll();

		wall = new Wall[ClientWindow.WIDTH/Wall.TILE_WIDTH][ClientWindow.HEIGHT/Wall.TILE_HEIGHT];
		wall[0][10] = new Wall(0, 10, Wall.STONE);
		wall[1][3] = new Wall(1, 3, Wall.STONE);
		wall[2][6] = new Wall(2, 6, Wall.STONE);
		wall[3][15] = new Wall(3, 15, Wall.STONE);
		wall[4][13] = new Wall(4, 13, Wall.STONE);
		wall[5][10] = new Wall(5, 10, Wall.STONE);
		wall[5][11] = new Wall(5, 11, Wall.STONE);
		wall[5][12] = new Wall(5, 12, Wall.STONE);
		wall[5][13] = new Wall(5, 13, Wall.STONE);
		wall[3][5] = new Wall(3, 5, Wall.STONE);
		wall[3][6] = new Wall(3, 6, Wall.STONE);
		wall[3][7] = new Wall(3, 7, Wall.STONE);
		wall[4][15] = new Wall(4, 15, Wall.STONE);
		wall[5][15] = new Wall(5, 15, Wall.STONE);
		wall[2][15] = new Wall(2, 15, Wall.STONE);
		wall[5][16] = new Wall(5, 16, Wall.STONE);
		wall[11][5] = new Wall(11, 5, Wall.STONE);
		wall[10][5] = new Wall(10, 5, Wall.STONE);
		wall[8][16] = new Wall(8, 16, Wall.STONE);
		wall[8][15] = new Wall(8, 15, Wall.STONE);
		wall[8][14] = new Wall(8, 14, Wall.STONE);
		wall[8][13] = new Wall(8, 13, Wall.STONE);
		wall[8][12] = new Wall(8, 12, Wall.STONE);
		wall[8][11] = new Wall(8, 11, Wall.STONE);
		wall[8][10] = new Wall(8, 10, Wall.STONE);
		wall[9][15] = new Wall(9, 15, Wall.STONE);
		for(int i = 0; i < wall.length; i++){ // add the ground
			wall[i][17] = new Wall(i, 17, Wall.DIRT);
		}
		for(Wall[] r : wall)
			for(Wall w : r)
				if(w != null)
					this.add(w, WALL_DEPTH);
		blendWalls();


		player = new Player(new Point(400, 150), this);
		this.add(player, ENTITY_DEPTH);

		gamemenu = new GameMenu();
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

		for(int i = 0; i < wall.length; i++){
			for(int j = 0; j < wall[i].length; j++){
				Wall w = wall[i][j];
				if(w == null) continue; //ignore blank spaces

				up = down = left = right = false;
				x = Wall.COLUMN;
				y = Wall.ROW;
				xpos = w.getLocation().x;
				ypos = w.getLocation().y;
				
				//set blend for tiles on the level's border
				if(ypos <= 0)
					up = true;
				if(ypos + Wall.TILE_HEIGHT >= ClientWindow.HEIGHT)
					down = true;
				if(xpos <= 0)
					left = true;
				if(xpos + Wall.TILE_WIDTH >= ClientWindow.WIDTH)
					right = true;

				if(i - 1 >= 0 && wall[i-1][j] != null && wall[i-1][j].type == w.type){
					left = true;
				}
				if(i + 1 < wall.length && wall[i+1][j] != null && wall[i+1][j].type == w.type){
					right = true;
				}
				if(j - 1 >= 0 && wall[i][j-1] != null && wall[i][j-1].type == w.type){
					up = true;
				}
				if(j + 1 < wall[i].length && wall[i][j+1] != null){ //down shouldn't have a border if there's any tile
					down = true;
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
		bounds[UP] = 0;
		bounds[DOWN] = ClientWindow.HEIGHT;
		bounds[LEFT] = 0;
		bounds[RIGHT] = ClientWindow.WIDTH;

		Point topleft, topright, botleft, botright;
		topleft = o.getLocation();
		topright = new Point(topleft.x + o.w-1, topleft.y);
		botleft = new Point(topleft.x, topleft.y + o.h-1);
		botright = new Point(topleft.x + o.w-1, topleft.y + o.h-1);

		topleft = new Point((topleft.x - topleft.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (topleft.y - topleft.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		topright = new Point((topright.x - topright.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (topright.y - topright.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		botleft = new Point((botleft.x - botleft.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (botleft.y - botleft.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		botright = new Point((botright.x - botright.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (botright.y - botright.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);

		//System.out.println("topleft: " + topleft + "\n topright: " + topright + "\n botleft: " + botleft + "\n botright: " + botright);

		//get upper boundary
		for(int i = topleft.y; i >= 0; i--){
			if(wall[topleft.x][i] != null){
				bounds[UP] = wall[topleft.x][i].getLocation().y + Wall.TILE_HEIGHT;
				break;
			}
			else if(wall[topright.x][i] != null){
				bounds[UP] = wall[topright.x][i].getLocation().y + Wall.TILE_HEIGHT;
				break;
			}
		}

		//get lower boundary
		for(int i = botleft.y; i < wall[botleft.x].length; i++){
			if(wall[botleft.x][i] != null){
				bounds[DOWN] = wall[botleft.x][i].getLocation().y;
				break;
			}
			else if(wall[botright.x][i] != null){
				bounds[DOWN] = wall[botright.x][i].getLocation().y;
				break;
			}
		}

		//get left boundary
		for(int i = topleft.x; i >= 0; i--){
			if(topleft.y >= 0 && wall[i][topleft.y]!= null ){
				bounds[LEFT] = wall[i][topleft.y].getLocation().x + Wall.TILE_WIDTH;
				break;
			}
			else if(botleft.y < wall[i].length && wall[i][botleft.y]!= null ){
				bounds[LEFT] = wall[i][botleft.y].getLocation().x + Wall.TILE_WIDTH;
				break;
			}
		}

		//get right boundary
		for(int i = topright.x; i < wall.length; i++){
			if(topright.y >= 0 && wall[i][topright.y] != null){
				bounds[RIGHT] = wall[i][topright.y].getLocation().x;
				break;
			}
			else if(botright.y >= 0 && wall[i][botright.y] != null){
				bounds[RIGHT] = wall[i][botright.y].getLocation().x;
				break;
			}
		}

		//System.out.println("up: " + bounds[0] + " down: " + bounds[1] + " left: " + bounds[2] + " right:  " + bounds[3]);
		return bounds;
	}
}
