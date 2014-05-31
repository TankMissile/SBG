import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;



public class Level extends JLayeredPane{
	private static final long serialVersionUID = 1L;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers
	public static final String WALL_CODE = "wa",
			PASSABLE_WALL_CODE = "pw",
			BACKGROUND_CODE = "bg",
			STONE_CODE = "st",
			DIRT_CODE = "dr",
			WOOD_CODE = "wd",
			EYE_CODE = "ey",
			PLATFORM_CODE = "pt",
			PLAYER_CODE = "pl",
			ENTITY_CODE = "en",
			SPIKE_CODE = "sp",
			WATER_CODE = "wt",
			MUSIC_CODE = "mu";

	private Wall[][] wall; //Stores wall tiles (interact with player)
	private Wall[][] background; //Stores background tiles (don't interact with player)
	private ArrayList<Entity> entity = new ArrayList<Entity>(); //Stores all entities other than fluids
	private ArrayList<Entity> fluids = new ArrayList<Entity>(); //Stores all fluids
	private Player player; //The player
	private HealthBar healthbar; //The player's health bar
	private GameMenu gamemenu; //The pause menu (opened with esc)
	private ClientWindow container;

	//Depths for each component of the level (lower depths are covered by higher depths)
	public static final Integer BACKGROUND_DEPTH = new Integer(-10), BGWALL_DEPTH = new Integer(-5), PASSABLE_WALL_DEPTH = new Integer(-3), ENTITY_DEPTH = new Integer(0), PARTICLE_DEPTH = new Integer(3), FLUID_DEPTH = new Integer(5),  WALL_DEPTH = new Integer(10), MENU_DEPTH = new Integer(200);

	//Constructor
	public Level(ClientWindow cw){
		this.setPreferredSize(new Dimension(ClientWindow.WIDTH,ClientWindow.HEIGHT));
		this.setLayout(null);
		this.setVisible(true);
		this.container = cw;
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll(); //Clear everything from the level

		String bgm = null; //Stores the filename of the background music for the level

		try {
			//Find the file for reading
			InputStream istream = getClass().getResourceAsStream("/level/"+path+".lvl");

			//Open the file to read
			BufferedReader br = new BufferedReader(new InputStreamReader(istream));
			String readline = null; //reads a line
			String[] splitline; //Splits the line by spaces

			int x, y, t; //X and Y positions for the component loaded, and the wall material if applicable

			Entity newEntity = null; //Stores a new entity to be loaded

			//Initialize the wall and background arrays to the size of the level
			wall = new Wall[ClientWindow.WIDTH/Wall.TILE_WIDTH][ClientWindow.HEIGHT/Wall.TILE_HEIGHT];
			background = new Wall[ClientWindow.WIDTH/Wall.TILE_WIDTH][ClientWindow.HEIGHT/Wall.TILE_HEIGHT];

			//Begin actual loading
			//System.out.println("Loading Level...");
			container.preloader.updateCurrentStatus("Reading Level File");
			while((readline = br.readLine()) != null){
				splitline = readline.split(" ");

				//Non-Passable walls
				if(splitline[0].equals(WALL_CODE)){
					x = Integer.parseInt(splitline[2]);
					y = Integer.parseInt(splitline[3]);
					t = Wall.NONE;
					if(splitline[1].equals(STONE_CODE)){
						t = Wall.STONE;
					}
					else if(splitline[1].equals(DIRT_CODE)){
						t = Wall.DIRT;
					}
					else if(splitline[1].equals(WOOD_CODE)){
						t = Wall.WOOD;
					}
					else if(splitline[1].equals(EYE_CODE)){
						t = Wall.EYE;
					}
					container.preloader.updateCurrentStatus("Adding Wall at " + x + " " + y);
					wall[x][y] = new Wall(x, y, t);
					this.add(wall[x][y], WALL_DEPTH);
				}
				//Passable Walls
				else if(splitline[0].equals(PASSABLE_WALL_CODE)){
					x = Integer.parseInt(splitline[2]);
					y = Integer.parseInt(splitline[3]);
					t = Wall.NONE;
					if(splitline[1].equals(PLATFORM_CODE)){
						t = Wall.PLATFORM;
					}
					wall[x][y] = new Wall(x, y, t);
					this.add(wall[x][y], PASSABLE_WALL_DEPTH);
					container.preloader.updateCurrentStatus("Adding Platform at " + x + " " + y);
				}
				//Backgrounds
				else if(splitline[0].equals(BACKGROUND_CODE)){
					x = Integer.parseInt(splitline[2]);
					y = Integer.parseInt(splitline[3]);
					t = Wall.NONE;
					if(splitline[1].equals(STONE_CODE)){
						t = Wall.STONE;
					}
					else if(splitline[1].equals(DIRT_CODE)){
						t = Wall.DIRT;
					}
					else if(splitline[1].equals(WOOD_CODE)){
						t = Wall.WOOD;
					}
					else if(splitline[1].equals(EYE_CODE)){
						t = Wall.EYE;
					}
					else if(splitline[1].equals(PLATFORM_CODE)){
						t = Wall.PLATFORM;
					}
					background[x][y] = new Wall(x, y, t, Wall.BACKGROUND_WALL);
					this.add(background[x][y], BGWALL_DEPTH);
					container.preloader.updateCurrentStatus("Adding Background Tile at " + x + " " + y);
				}
				//The Player
				else if(splitline[0].equals(PLAYER_CODE)){
					//System.out.println("Adding Player...");
					x = Integer.parseInt(splitline[1])*Wall.TILE_WIDTH + (Wall.TILE_WIDTH-Player.NORMALWIDTH)/2;
					y = Integer.parseInt(splitline[2])*Wall.TILE_HEIGHT;

					player = new Player(new Point(x, y), this);
					this.add(player, ENTITY_DEPTH, 0);
					container.preloader.updateCurrentStatus("Adding Player");
				}
				//Non-player Entities
				else if(splitline[0].equals(ENTITY_CODE)){
					//System.out.println("Adding entity: " + splitline[1]);
					container.preloader.updateCurrentStatus("Adding Entity: " + splitline[1]);

					if(splitline[1].equals(SPIKE_CODE)){
						newEntity = new SpikeTrap( Integer.parseInt(splitline[2]), Integer.parseInt(splitline[3]), Integer.parseInt(splitline[4]) );
						entity.add(newEntity);
						this.add(newEntity, ENTITY_DEPTH, 1 );
					}
					else if(splitline[1].equals(WATER_CODE)){
						newEntity = new Water( Integer.parseInt(splitline[2]), Integer.parseInt(splitline[3]), Integer.parseInt(splitline[4]), Integer.parseInt(splitline[5]));
						fluids.add(newEntity);
						this.add(newEntity, FLUID_DEPTH, 0);
					}
				}
				//Background Music
				else if(splitline[0].equals(MUSIC_CODE)){
					bgm = splitline[1];
				}
			}

			//Draw the background
			//System.out.println("Drawing background...");
			container.preloader.updateOverview("Drawing Background");
			drawBackground();
			//System.out.println("Complete.");

			//Draw walls
			//System.out.println("Drawing Textures...");
			container.preloader.updateOverview("Drawing Walls");
			blendWalls();
			//System.out.println("Complete.");

			//System.out.println("Level Loaded.");

		} 
		catch (UnsupportedEncodingException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }

		//Create Pause menu
		//System.out.println("Creating Pause Menu...");
		container.preloader.updateOverview("Finalizing");
		container.preloader.updateCurrentStatus("Creating pause menu...");
		gamemenu = new GameMenu();
		this.add(gamemenu, MENU_DEPTH);
		//System.out.println("Complete.");

		//Add static background color
		//System.out.println("Adding Background Color...");
		container.preloader.updateCurrentStatus("Coloring Background");
		JPanel bgColor = new JPanel();
		bgColor.setBackground(new Color(200, 220, 255));
		bgColor.setSize(ClientWindow.WIDTH, ClientWindow.HEIGHT);
		this.add(bgColor, BACKGROUND_DEPTH);
		//System.out.println("Complete.");

		//saveLevel(path);

		//Create the health bar and add it to screen
		container.preloader.updateCurrentStatus("Adding HUD");
		if(healthbar == null)
			healthbar = new HealthBar();
		this.add(healthbar, MENU_DEPTH, 0);


		//Tell the player what his health bar is
		player.linkHealthBar(healthbar);

		//Unpause the player
		container.preloader.updateOverview("Starting Game");
		container.preloader.updateCurrentStatus("");

		if(bgm != null)
			Sound.music(bgm);

		player.pause(false);
	}

	//Remove an Entity
	public void removeEntity(Entity e){
		entity.remove(e);
		this.remove(e);
	}

	//Check entity collision
	public Entity checkCollision(Entity e1){
		Rectangle e1rect = e1.getVisibleRect();
		e1rect.setLocation(e1.getLocation());
		Rectangle e2rect;
		for(Entity e2: entity){
			if(e1 == e2) continue; //don't check if it hit itself

			e2rect = e2.getVisibleRect();
			e2rect.setLocation(e2.getLocation());

			if(e1rect.intersects(e2rect)){
				//System.out.println("" + e1rect + "\nCollided with: " + e2rect + "\n");
				return e2;
			}
		}

		return null;
	}

	//Check fluid collision
	public Entity checkFluidCollision(Entity e1){
		Rectangle e1rect = e1.getVisibleRect();
		e1rect.setLocation(e1.getLocation());
		Rectangle e2rect;
		for(Entity e2: fluids){
			if(e1 == e2) continue; //don't check if it hit itself

			e2rect = e2.getVisibleRect();
			e2rect.setLocation(e2.getLocation());

			if(e1rect.intersects(e2rect)){
				//System.out.println("" + e1rect + "\nCollided with: " + e2rect + "\n");
				return e2;
			}
		}

		return null;
	}

	//Make the walls blend together
	private void blendWalls(){
		boolean up = false, down = false, left = false, right = false;
		int x = Wall.COLUMN, y = Wall.ROW;
		int xpos, ypos;

		for(int i = 0; i < wall.length; i++){
			for(int j = 0; j < wall[i].length; j++){
				Wall w = wall[i][j];
				if(w == null) {
					//System.out.println("Tile blank - ignoring...");
					continue; //ignore blank spaces
				}

				String printline = "Drawing Tile " + i + " " + j + " : ";
				switch(w.type){
				case Wall.STONE:
					printline += "Stone";
					break;
				case Wall.DIRT:
					printline += "Dirt";
					break;
				case Wall.WOOD:
					printline += "Wood";
					break;
				case Wall.EYE:
					printline += "Eye";
					break;
				case Wall.PLATFORM:
					printline += "Platform";
					break;
				default:
					printline += "Material not recognized, using ";
				case Wall.NONE:
					printline += "None";
					break;
				}
				//System.out.println(printline);
				container.preloader.updateCurrentStatus(printline);

				up = down = left = right = false; //whether there's a tile in each direction

				//position in tile array, default to unsurrounded tile
				x = Wall.COLUMN;
				y = Wall.ROW;

				//get position of the tile in the level
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

				//Check for a tile on the left
				if(i - 1 >= 0 && wall[i-1][j] != null){ //If there's a tile there
					if((w.type != Wall.DIRT && wall[i-1][j].type != Wall.DIRT) || wall[i-1][j].type == w.type) //If neither tile is dirt or the tiles are both dirt
						if((w.type != Wall.PLATFORM && wall[i-1][j].type != Wall.PLATFORM) || wall[i-1][j].type == w.type) //If neither tile is a platform or both are platforms
							left = true;
				}

				//Check for a tile on the right
				if(i + 1 < wall.length && wall[i+1][j] != null/* && (w.type != Wall.DIRT || wall[i+1][j].type == w.type)*/){ //if there's a tile there
					if((w.type != Wall.DIRT && wall[i+1][j].type != Wall.DIRT) || wall[i+1][j].type == w.type) //If neither tile is dirt or the tiles are both dirt
						if((w.type != Wall.PLATFORM && wall[i+1][j].type != Wall.PLATFORM) || wall[i+1][j].type == w.type)
							right = true;
				}

				//check for a tile above
				if(j - 1 >= 0 && wall[i][j-1] != null && ( w.type != Wall.DIRT || wall[i][j-1].type == w.type)){ //If there's a tile there and the current tile is not dirt or they're both dirt
					if(w.type != Wall.EYE || wall[i][j-1].type == Wall.EYE) //If the current tile is not eye or they're both eye
						up = true;
				}

				//check for a tile below
				if(j + 1 < wall[i].length && wall[i][j+1] != null){ //If there's a tile there
					if(w.type != Wall.EYE || wall[i][j+1].type == Wall.EYE) //If the current tile is not eye or they're both eye
						down = true;
				}


				//Set Vertical type
				if(w.type == Wall.PLATFORM){
					y = Wall.TOP;
				}
				else if(up && down){
					y = Wall.MID;
				}
				else if(up){
					y = Wall.BOT;
				}
				else if(down){
					w.hangable = true;
					y = Wall.TOP;
				}
				else {
					w.hangable = true;
				}

				//Set horizontal type
				if(left && right){
					x = Wall.MID;
				}
				else if(left){
					x = Wall.RIGHT;
				}
				else if(right){
					x = Wall.LEFT;
				}

				//Load the background for the tile
				w.loadTileBackground(x, y);
			}
		}
	}

	//Draw the background
	private void drawBackground(){
		//whether there's a tile in each direction
		boolean up = false, down = false, left = false, right = false;

		//Position in tile array
		int x = Wall.COLUMN, y = Wall.ROW;

		//Position in level
		int xpos, ypos;

		//For each tile in the level
		for(int i = 0; i < background.length; i++){
			for(int j = 0; j < background[i].length; j++){
				Wall w = background[i][j];
				if(w == null) {
					//System.out.println("Tile blank - ignoring...");
					continue; //ignore blank spaces
				}

				String printline = "Drawing Background Tile " + i + " " + j + " : ";
				switch(w.type){
				case Wall.STONE:
					printline += "Stone";
					break;
				case Wall.DIRT:
					printline += "Dirt";
					break;
				case Wall.WOOD:
					printline += "Wood";
					break;
				case Wall.EYE:
					printline += "Eye";
					break;
				case Wall.PLATFORM:
					printline += "Platform";
					break;
				default:
					printline += "Material not recognized, using ";
				case Wall.NONE:
					printline += "None";
					break;
				}
				//System.out.println(printline);
				container.preloader.updateCurrentStatus(printline);

				//whether there's a tile in each direction
				up = down = left = right = false;

				//position in tile array, default to unsurrounded tile
				x = Wall.COLUMN;
				y = Wall.ROW;

				//get position of the tile in the level
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

				//check background walls
				//Check left
				if(i - 1 >= 0 && background[i-1][j] != null){ //tile exists
					if((w.type != Wall.DIRT && background[i-1][j].type != Wall.DIRT) || background[i-1][j].type == w.type) //not dirt or both dirt
						if((w.type != Wall.PLATFORM && background[i-1][j].type != Wall.PLATFORM) || background[i-1][j].type == w.type) //not platform or both platform
							left = true;
				}
				//Check right
				if(i + 1 < background.length && background[i+1][j] != null/* && (w.type != Wall.DIRT || background[i+1][j].type == w.type)*/){ //tile exists
					if((w.type != Wall.DIRT && background[i+1][j].type != Wall.DIRT) || background[i+1][j].type == w.type) //not dirt or both dirt
						if((w.type != Wall.PLATFORM && background[i+1][j].type != Wall.PLATFORM) || background[i+1][j].type == w.type) //not platform or both platform
							right = true;
				}
				//Check up
				if(j - 1 >= 0 && background[i][j-1] != null && ( w.type != Wall.DIRT || background[i][j-1].type == w.type)){ // exists and not dirt or both dirt
					if(w.type != Wall.EYE || background[i][j-1].type == Wall.EYE) //not eye or both eye
						up = true;
				}
				//Check down
				if(j + 1 < background[i].length && background[i][j+1] != null){ //exists
					if(w.type != Wall.EYE || background[i][j+1].type == Wall.EYE) //not eye or both eye
						down = true;
				}

				//check normal walls
				//Check left
				if(i - 1 >= 0 && wall[i-1][j] != null){ //exists
					if((w.type != Wall.DIRT && wall[i-1][j].type != Wall.DIRT) || wall[i-1][j].type == w.type) //not dirt or both dirt
						if((w.type != Wall.PLATFORM && wall[i-1][j].type != Wall.PLATFORM) || wall[i-1][j].type == w.type) //not platform or both platform
							left = true;
				}
				//Check right
				if(i + 1 < wall.length && wall[i+1][j] != null && (w.type != Wall.DIRT || wall[i+1][j].type == w.type)){ //exists and not dirt or both dirt
					if((w.type != Wall.PLATFORM && wall[i+1][j].type != Wall.PLATFORM) || wall[i+1][j].type == w.type)  //not platform or both platform
						right = true;
				}
				//Check up
				if(j - 1 >= 0 && wall[i][j-1] != null && ( w.type != Wall.DIRT || wall[i][j-1].type == w.type)){ //exists and not dirt or both dirt
					if(w.type != Wall.EYE || wall[i][j-1].type == Wall.EYE) //not eye or both eye
						up = true;
				}
				if(j + 1 < wall[i].length && wall[i][j+1] != null){ //exists
					if(w.type != Wall.EYE || wall[i][j+1].type == Wall.EYE) //not eye or both eye
						down = true;
				}

				//Set vertical type
				if(w.type == Wall.PLATFORM){
					y = Wall.TOP;
				}
				else if(up && down){
					y = Wall.MID;
				}
				else if(up){
					y = Wall.BOT;
				}
				else if(down){
					w.hangable = true;
					y = Wall.TOP;
				}
				else {
					w.hangable = true;
				}

				//Set horizontal type
				if(left && right){
					x = Wall.MID;
				}
				else if(left){
					x = Wall.RIGHT;
				}
				else if(right){
					x = Wall.LEFT;
				}

				//Load the tile's background
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
		this.add(p, PARTICLE_DEPTH, 0);
	}

	//Find the movement boundaries for the player by checking each wall
	public Boundary[] findBoundaries(Player o){
		Boundary[] bounds = new Boundary[4];
		bounds[UP] = new Boundary(0);
		bounds[DOWN] = new Boundary(ClientWindow.HEIGHT);
		bounds[LEFT] = new Boundary(0);
		bounds[RIGHT] = new Boundary(ClientWindow.WIDTH);

		//Get points of each corner of player, in actual coordinates
		Point topleft, topright, botleft, botright;
		topleft = o.getLocation();
		topright = new Point(topleft.x + o.w-1, topleft.y);
		botleft = new Point(topleft.x, topleft.y + o.h-1);
		botright = new Point(topleft.x + o.w-1, topleft.y + o.h-1);

		//Convert points to tiled coordinates
		topleft = new Point((topleft.x - topleft.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (topleft.y - topleft.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		topright = new Point((topright.x - topright.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (topright.y - topright.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		botleft = new Point((botleft.x - botleft.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (botleft.y - botleft.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);
		botright = new Point((botright.x - botright.x % Wall.TILE_WIDTH) / Wall.TILE_WIDTH, (botright.y - botright.y % Wall.TILE_HEIGHT) / Wall.TILE_HEIGHT);

		//System.out.println("topleft: " + topleft + "\n topright: " + topright + "\n botleft: " + botleft + "\n botright: " + botright);

		//get upper boundary
		for(int i = topleft.y; i >= 0; i--){
			if(wall[topleft.x][i] != null && wall[topleft.x][i].type != Wall.PLATFORM){
				bounds[UP].value = wall[topleft.x][i].getLocation().y + Wall.TILE_HEIGHT;
				break;
			}
			if(wall[topright.x][i] != null && wall[topright.x][i].type != Wall.PLATFORM){
				bounds[UP].value = wall[topright.x][i].getLocation().y + Wall.TILE_HEIGHT;
				break;
			}
		}

		//get lower boundary
		for(int i = botleft.y; i < wall[botleft.x].length; i++){
			if((wall[botleft.x][i] != null && wall[botleft.x][i].type == Wall.PLATFORM) && (wall[botright.x][i] != null && wall[botright.x][i].type == Wall.PLATFORM))
			{
				if(o.crouching && o.frames_to_dropthrough == 0)
					continue;
				bounds[DOWN].dropthrough = true;
			}
			else
				bounds[DOWN].dropthrough = false;

			if(wall[botleft.x][i] != null && wall[botleft.x][i].type != Wall.PLATFORM){
				if(!(i == botleft.y && wall[botleft.x][i].type == Wall.PLATFORM)){
					bounds[DOWN].value = wall[botleft.x][i].getLocation().y;
					break;
				}
			}
			else if(wall[botright.x][i] != null){
				if(!(i == botleft.y && wall[botright.x][i].type == Wall.PLATFORM)){
					bounds[DOWN].value = wall[botright.x][i].getLocation().y;
					break;
				}
			}
		}

		//get left boundary
		for(int i = topleft.x; i >= 0; i--){

			//Eyes don't like to be grabbed
			if((wall[i][topleft.y] != null && wall[i][topleft.y].type == Wall.EYE) && (wall[i][botleft.y] != null && wall[i][botleft.y].type == Wall.EYE))
			{
				bounds[LEFT].slidable = false;
			}
			else
				bounds[LEFT].slidable = true;

			if(topleft.y >= 0 && wall[i][topleft.y]!= null && wall[i][topleft.y].type != Wall.PLATFORM){
				bounds[LEFT].value = wall[i][topleft.y].getLocation().x + Wall.TILE_WIDTH;
				break;
			}
			else if(botleft.y < wall[i].length && wall[i][botleft.y]!= null && wall[i][botleft.y].type != Wall.PLATFORM){
				bounds[LEFT].value = wall[i][botleft.y].getLocation().x + Wall.TILE_WIDTH;
				break;
			}
		}
		if(bounds[LEFT].value == 0) bounds[LEFT].slidable = false;

		//get right boundary
		for(int i = topright.x; i < wall.length; i++){

			//Eyes don't like to be grabbed
			if((wall[i][topright.y] != null && wall[i][topright.y].type == Wall.EYE) && (wall[i][botright.y] != null && wall[i][botright.y].type == Wall.EYE))
			{
				bounds[RIGHT].slidable = false;
			}
			else
				bounds[RIGHT].slidable = true;

			if(topright.y >= 0 && wall[i][topright.y] != null && wall[i][topright.y].type != Wall.PLATFORM){
				bounds[RIGHT].value = wall[i][topright.y].getLocation().x;
				break;
			}
			else if(botright.y >= 0 && wall[i][botright.y] != null && wall[i][botright.y].type != Wall.PLATFORM){
				bounds[RIGHT].value = wall[i][botright.y].getLocation().x;
				break;
			}
		}
		if(bounds[RIGHT].value == ClientWindow.WIDTH) bounds[RIGHT].slidable = false;

		//System.out.println("up: " + bounds[0] + " down: " + bounds[1] + " left: " + bounds[2] + " right:  " + bounds[3]);
		return bounds;
	}

	//Save all tiles of the level
	@SuppressWarnings("unused")
	private void saveLevel(String name){
		try {
			File file = new File("res/level/" + name + ".lvl");
			if(!file.exists()){
				file.createNewFile();
			}

			FileWriter fw = null;
			fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			String writeline = null;
			for(int i = 0; i < wall.length; i++ ){
				for(int j = 0; j < wall[i].length; j++){
					if(wall[i][j] == null) continue; //skip empty spaces

					writeline = WALL_CODE;

					switch(wall[i][j].type){
					case Wall.STONE:
						writeline += " " + STONE_CODE;
						break;
					case Wall.DIRT:
						writeline += " " + DIRT_CODE;
						break;
					case Wall.WOOD:
						writeline += " " + WOOD_CODE;
						break;
					case Wall.EYE:
						writeline += " " + EYE_CODE;
						break;
					case Wall.PLATFORM:
						writeline = PASSABLE_WALL_CODE + " " + PLATFORM_CODE;
						break;
					default:
						System.err.println("Invalid tile at: " + i + ", " + j);
						continue;
					}
					writeline += " " + i + " " + j;
					bw.write(writeline);
					bw.newLine();
				}
			}

			for(int i = 0; i < background.length; i++ ){
				for(int j = 0; j < background[i].length; j++){
					if(background[i][j] == null) continue; //skip empty spaces

					writeline = BACKGROUND_CODE;

					switch(background[i][j].type){
					case Wall.STONE:
						writeline += " " + STONE_CODE;
						break;
					case Wall.DIRT:
						writeline += " " + DIRT_CODE;
						break;
					case Wall.WOOD:
						writeline += " " + WOOD_CODE;
						break;
					case Wall.EYE:
						writeline += " " + EYE_CODE;
						break;
					case Wall.PLATFORM:
						writeline += " " + PLATFORM_CODE;
						break;
					default:
						System.err.println("Invalid tile at: " + i + ", " + j);
						continue;
					}
					writeline += " " + i + " " + j;
					bw.write(writeline);
					bw.newLine();
				}
			}

			for(Entity e : entity){
				writeline = ENTITY_CODE + " " + e.entity_code + " " + ((e.getLocation().x - e.getLocation().x%Wall.TILE_WIDTH)/Wall.TILE_WIDTH) + " " + ((e.getLocation().y - e.getLocation().y%Wall.TILE_HEIGHT)/Wall.TILE_HEIGHT);
				if(e.entity_code == WATER_CODE)
					writeline += " " + e.w + " " + e.h;
				bw.write(writeline);
			}

			writeline = PLAYER_CODE + " " + ((player.getLocation().x - player.getLocation().x%Wall.TILE_WIDTH)/Wall.TILE_WIDTH) + " " + ((player.getLocation().y - player.getLocation().y%Wall.TILE_HEIGHT)/Wall.TILE_HEIGHT);
			bw.write(writeline);


			bw.flush();
			bw.close();
		} catch (IOException e) { e.printStackTrace(); }

	}
}
