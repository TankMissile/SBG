import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;



public class LevelEditor extends JLayeredPane implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
	private static final long serialVersionUID = 1L;
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
			MUSIC_CODE = "mu",
			DIMENSION_CODE = "di";

	private String currentType = WALL_CODE;
	private int currentMaterial = 0;
	private String wallMaterials[] = { DIRT_CODE, WOOD_CODE, STONE_CODE, EYE_CODE, PLATFORM_CODE };
	
	private boolean drawing = false;
	private boolean erasing = false;
	private JLabel ghostImage;
	private Point currCoords = new Point(0, 0);

	private boolean move_up = false, move_down = false, move_left = false, move_right = false;
	private final int move_speed = 10;

	private boolean menuOpen = false;

	public int width, height;
	private Wall[][] wall; //Stores wall tiles (interact with player)
	private Wall[][] background; //Stores background tiles (don't interact with player)
	private ArrayList<Entity> entity = new ArrayList<Entity>(); //Stores all entities other than fluids
	private ArrayList<Entity> fluids = new ArrayList<Entity>(); //Stores all fluids
	private JLabel player; //The player
	public HealthBar healthbar; //The player's health bar
	private GameMenu gamemenu; //The pause menu (opened with esc)
	@SuppressWarnings("unused")
	private ClientWindow container;

	//Depths for each component of the level (lower depths are covered by higher depths)
	public static final Integer BACKGROUND_DEPTH = new Integer(-10), BGWALL_DEPTH = new Integer(-5), PASSABLE_WALL_DEPTH = new Integer(-3), ENTITY_DEPTH = new Integer(0), PARTICLE_DEPTH = new Integer(3), FLUID_DEPTH = new Integer(5),  WALL_DEPTH = new Integer(10), MENU_DEPTH = new Integer(200);

	//Constructor
	public LevelEditor(ClientWindow cw){
		this.setLayout(null);
		this.setVisible(true);
		this.container = cw;
		ghostImage = new JLabel();

		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll(); //Clear everything from the level

		String bgm = "Dungeon"; //Stores the filename of the background music for the level

		try {
			//Find the file for reading
			InputStream istream = getClass().getResourceAsStream("/level/"+path+".lvl");

			//Open the file to read
			BufferedReader br = new BufferedReader(new InputStreamReader(istream));
			String readline = null; //reads a line
			String[] splitline; //Splits the line by spaces

			int x, y, t; //X and Y positions for the component loaded, and the wall material if applicable

			Entity newEntity = null; //Stores a new entity to be loaded

			//Find the dimensions of the level according to the file
			while((readline = br.readLine()) != null){

				splitline = readline.split(" ");
				if(splitline[0].equals(DIMENSION_CODE)){
					width = Integer.parseInt(splitline[1]);
					height = Integer.parseInt(splitline[2]);
					this.setSize(new Dimension(width, height));
					break;
				}
			}
			if(width == 0 || height == 0){
				System.err.println("Level dimensions could not be loaded correctly, aborting...");
				return;
			}
			istream = getClass().getResourceAsStream("/level/"+path+".lvl");
			br = new BufferedReader(new InputStreamReader(istream));

			//Initialize the wall and background arrays to the size of the level
			wall = new Wall[width/Wall.TILE_WIDTH][height/Wall.TILE_HEIGHT];
			background = new Wall[width/Wall.TILE_WIDTH][height/Wall.TILE_HEIGHT];

			//Begin actual loading
			ClientWindow.preloader.updateCurrentStatus("Reading Level File");
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
					ClientWindow.preloader.updateCurrentStatus("Adding Wall at " + x + " " + y);
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
					ClientWindow.preloader.updateCurrentStatus("Adding Platform at " + x + " " + y);
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
					ClientWindow.preloader.updateCurrentStatus("Adding Background Tile at " + x + " " + y);
				}
				//The Player
				else if(splitline[0].equals(PLAYER_CODE)){
					//System.out.println("Adding Player...");
					x = Integer.parseInt(splitline[1])*Wall.TILE_WIDTH + (Wall.TILE_WIDTH-Player.NORMALWIDTH)/2;
					y = Integer.parseInt(splitline[2])*Wall.TILE_HEIGHT;

					player = new JLabel("p");
					player.setSize(new Dimension(32, 32));
					player.setLocation(x, y);
					player.setOpaque(true);
					player.setVisible(true);;
					player.setBackground(new Color(120,150,255));
					this.add(player, ENTITY_DEPTH, 0);
					ClientWindow.preloader.updateCurrentStatus("Adding Player");
				}
				//Non-player Entities
				else if(splitline[0].equals(ENTITY_CODE)){
					//System.out.println("Adding entity: " + splitline[1]);
					ClientWindow.preloader.updateCurrentStatus("Adding Entity: " + splitline[1]);

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
				/*//Background Music
				else if(splitline[0].equals(MUSIC_CODE)){
					bgm = splitline[1];
				}*/
			}

			//Draw the background
			ClientWindow.preloader.updateOverview("Drawing Background");
			drawBackground();

			//Draw walls
			ClientWindow.preloader.updateOverview("Drawing Walls");
			blendWalls();
		} 
		catch (UnsupportedEncodingException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }

		//Create Pause menu
		ClientWindow.preloader.updateOverview("Finalizing");
		ClientWindow.preloader.updateCurrentStatus("Creating pause menu...");
		gamemenu = new GameMenu();
		this.add(gamemenu, MENU_DEPTH);

		//Add static background color
		//System.out.println("Adding Background Color...");
		ClientWindow.preloader.updateCurrentStatus("Coloring Background");
		JPanel bgColor = new JPanel();
		bgColor.setBackground(new Color(200, 220, 255));
		bgColor.setSize(width, height);
		this.add(bgColor, BACKGROUND_DEPTH);

		//Unpause the player
		ClientWindow.preloader.updateOverview("Starting Editor");
		ClientWindow.preloader.updateCurrentStatus("");

		if(bgm != null)
			Sound.music(bgm);

		saveLevel("testCopy");

		new Thread(new Runnable(){ @Override public void run() { panCamera();  } }).start();
	}

	//Remove an Entity
	public void removeEntity(Entity e){
		entity.remove(e);
		this.remove(e);
	}

	//Make the walls blend together
	private void blendWalls(){
		//For each wall tile
		for(int i = 0; i < wall.length; i++){
			for(int j = 0; j < wall[i].length; j++){
				drawSingleWall(i, j);
			}
		}
	}

	//Draw the background
	private void drawBackground(){
		//For each tile in the level
		for(int i = 0; i < background.length; i++){
			for(int j = 0; j < background[i].length; j++){
				drawSingleBackground(i, j);
			}
		}
	}

	private void drawSingleWall(int i, int j){
		boolean up = false, down = false, left = false, right = false;
		int x = Wall.COLUMN, y = Wall.ROW;
		int xpos, ypos;

		//make sure the array index isn't out of bounds
		if(i > wall.length-1 || i < 0 || j > wall[i].length-1 || j < 0) return;

		Wall w = wall[i][j];
		if(w == null) {
			return; //ignore blank spaces
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
		ClientWindow.preloader.updateCurrentStatus(printline);

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
		if(ypos + Wall.TILE_HEIGHT >= height)
			down = true;
		if(xpos <= 0)
			left = true;
		if(xpos + Wall.TILE_WIDTH >= width)
			right = true;

		//Check for a tile on the left
		if(i - 1 >= 0 && wall[i-1][j] != null){ //If there's a tile there
			if((w.type != Wall.DIRT && wall[i-1][j].type != Wall.DIRT) || wall[i-1][j].type == w.type) //If neither tile is dirt or the tiles are both dirt
				if((w.type != Wall.PLATFORM && wall[i-1][j].type != Wall.PLATFORM) || wall[i-1][j].type == w.type) //If neither tile is a platform or both are platforms
					left = true;
		}

		//Check for a tile on the right
		if(i + 1 < wall.length && wall[i+1][j] != null && (w.type != Wall.DIRT || wall[i+1][j].type == w.type)){ //if there's a tile there
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

	private void drawSingleBackground(int i, int j){
		//whether there's a tile in each direction
		boolean up = false, down = false, left = false, right = false;

		//Position in tile array
		int x = Wall.COLUMN, y = Wall.ROW;

		//Position in level
		int xpos, ypos;

		//Get the correct tile type and location
		Wall w = background[i][j];


		if(w == null) {
			return; //ignore blank spaces
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
		ClientWindow.preloader.updateCurrentStatus(printline);

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
		if(ypos + Wall.TILE_HEIGHT >= height)
			down = true;
		if(xpos <= 0)
			left = true;
		if(xpos + Wall.TILE_WIDTH >= width)
			right = true;

		//check background walls
		//Check left
		if(i - 1 >= 0 && background[i-1][j] != null){ //tile exists
			if((w.type != Wall.DIRT && background[i-1][j].type != Wall.DIRT) || background[i-1][j].type == w.type) //not dirt or both dirt
				if((w.type != Wall.PLATFORM && background[i-1][j].type != Wall.PLATFORM) || background[i-1][j].type == w.type) //not platform or both platform
					left = true;
		}
		//Check right
		if(i + 1 < background.length && background[i+1][j] != null && (w.type != Wall.DIRT || background[i+1][j].type == w.type)){ //tile exists
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

	//Save all tiles of the level
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

			writeline = DIMENSION_CODE + " " + width + " " + height;
			bw.write(writeline);
			bw.newLine();

			writeline = PLAYER_CODE + " " + player.getLocation().x/Wall.TILE_WIDTH + " " + player.getLocation().y/Wall.TILE_HEIGHT;
			bw.write(writeline);
			bw.newLine();

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
				if(e.entity_code.equals(SPIKE_CODE)){
					writeline += " " + ((SpikeTrap)e).rotation;
				}
				bw.write(writeline);
				bw.newLine();
			}

			for(Entity e: fluids){
				writeline = ENTITY_CODE + " " + e.entity_code + " " + ((e.getLocation().x - e.getLocation().x%Wall.TILE_WIDTH)/Wall.TILE_WIDTH) + " " + ((e.getLocation().y - e.getLocation().y%Wall.TILE_HEIGHT)/Wall.TILE_HEIGHT);
				if(e.entity_code == WATER_CODE)
					writeline += " " + e.w + " " + e.h;
				bw.write(writeline);
				bw.newLine();
			}

			//writeline = PLAYER_CODE + " " + ((player.getLocation().x - player.getLocation().x%Wall.TILE_WIDTH)/Wall.TILE_WIDTH) + " " + ((player.getLocation().y - player.getLocation().y%Wall.TILE_HEIGHT)/Wall.TILE_HEIGHT);
			//bw.write(writeline);


			bw.flush();
			bw.close();
		} catch (IOException e) { e.printStackTrace(); }

	}

	private void drawTempLocalWalls(Point p, Wall w){

	}

	private void drawLocalWalls(Point p, Wall w){
		drawSingleWall(p.x, p.y);
		drawSingleWall(p.x-1, p.y);
		drawSingleWall(p.x+1,p.y);
		drawSingleWall(p.x, p.y-1);
		drawSingleWall(p.x, p.y+1);
	}

	private void getGhostImage(){
		if(currentType == WALL_CODE){
			this.remove(ghostImage);
			if(wallMaterials[currentMaterial] == DIRT_CODE){
				ghostImage = loadSingleTile(7, 3, Wall.wall);
			}
			if(wallMaterials[currentMaterial] == STONE_CODE){
				ghostImage = loadSingleTile(3, 3, Wall.wall);
			}
			if(wallMaterials[currentMaterial] == WOOD_CODE){
				ghostImage = loadSingleTile(3, 7, Wall.wall);
			}
			if(wallMaterials[currentMaterial] == EYE_CODE){
				ghostImage = loadSingleTile(8, 3, Wall.wall);
			}
			if(wallMaterials[currentMaterial] == PLATFORM_CODE){
				ghostImage = loadSingleTile(3, 8, Wall.wall);
			}
			
			ghostImage.setSize(new Dimension(Wall.TILE_WIDTH, Wall.TILE_HEIGHT));
			this.add(ghostImage, new Integer(MENU_DEPTH - 1));
			this.repaint();
		}
	}

	private JLabel loadSingleTile(int x, int y, BufferedImage spritesheet){

		BufferedImage sprite = spritesheet.getSubimage(x*Wall.TILE_WIDTH, y*Wall.TILE_HEIGHT, Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		JLabel image = new JLabel(new ImageIcon(sprite.getScaledInstance(Wall.TILE_WIDTH, Wall.TILE_HEIGHT, Image.SCALE_SMOOTH)));
		image.setSize(Wall.TILE_WIDTH, Wall.TILE_HEIGHT);
		return image;
	}

	private void drawCurrentItem(){
		//Cancel the action if the mouse is out of bounds
		if(currCoords.x < 0 || currCoords.x > wall.length || currCoords.y < 0 || currCoords.y > wall[currCoords.x].length)
			return;

		//Add wall if selected
		if(currentType == WALL_CODE){
			//Get the integer representation of the material
			int type = Wall.NONE;
			if(wallMaterials[currentMaterial] == DIRT_CODE){
				type = Wall.DIRT;
			}
			else if(wallMaterials[currentMaterial] == STONE_CODE){
				type = Wall.STONE;
			}
			else if(wallMaterials[currentMaterial] == WOOD_CODE){
				type = Wall.WOOD;
			}
			else if(wallMaterials[currentMaterial] == EYE_CODE){
				type = Wall.EYE;
			}
			else if(wallMaterials[currentMaterial] == PLATFORM_CODE){
				type = Wall.PLATFORM;
			}

			//If there's a wall there, check that it's not the same as the wall being placed
			if(wall[currCoords.x][currCoords.y] != null){
				//dirt
				if(wall[currCoords.x][currCoords.y].type != type){
					this.remove(wall[currCoords.x][currCoords.y]);
					wall[currCoords.x][currCoords.y] = new Wall(currCoords.x, currCoords.y, type); 
					this.add(wall[currCoords.x][currCoords.y], WALL_DEPTH);
				}
			}
			else{
				if(currentType == PLATFORM_CODE){
					wall[currCoords.x][currCoords.y] = new Wall(currCoords.x, currCoords.y, Wall.PLATFORM); 
					this.add(wall[currCoords.x][currCoords.y], PASSABLE_WALL_DEPTH);
				}
				else {
					wall[currCoords.x][currCoords.y] = new Wall(currCoords.x, currCoords.y, type); 
					this.add(wall[currCoords.x][currCoords.y], WALL_DEPTH);
				}
			}
			drawLocalWalls(currCoords, wall[currCoords.x][currCoords.y]);
		}
	}

	private void eraseCurrentTile(){
		//Cancel the action if the mouse is out of bounds
		if(currCoords.x < 0 || currCoords.x > wall.length || currCoords.y < 0 || currCoords.y > wall[currCoords.x].length)
			return;
		

		if(currentType == WALL_CODE){
			//if there's no wall here, cancel the action
			if(wall[currCoords.x][currCoords.y] == null) return;
			this.remove(wall[currCoords.x][currCoords.y]);
			wall[currCoords.x][currCoords.y] = null;
			drawLocalWalls(currCoords, wall[currCoords.x][currCoords.y]);
		}
	}

	private void panCamera(){
		boolean kill = false;
		int hmov = this.getLocation().x, vmov = this.getLocation().y; //stores where to move in each direction
		Point startPoint = this.getLocation();
		while(!kill){
			if(move_left && !move_right){
				hmov = this.getLocation().x + move_speed;
			}
			else if(move_right && !move_left){
				hmov = this.getLocation().x - move_speed;
			}

			if(move_up && !move_down){
				vmov = this.getLocation().y + move_speed;
			}
			else if(move_down && !move_up){
				vmov = this.getLocation().y - move_speed;
			}

			if (hmov > 0) {
				hmov = 0;
			}
			else if(hmov < -1 * this.getWidth() + ClientWindow.WIDTH) hmov = -1 * this.getWidth() + ClientWindow.WIDTH;

			if(vmov > 0) vmov = 0;
			else if(vmov < -1 * this.getHeight() + ClientWindow.HEIGHT) vmov = -1 * this.getHeight() + ClientWindow.HEIGHT;

			this.setLocation(hmov, vmov);
			if(this.getLocation() != startPoint) moveGhost();

			try {
				Thread.sleep(1000/30);
			} catch (InterruptedException e) { }
		}
	}

	private void moveGhost(){
		Point mouseCoords = MouseInfo.getPointerInfo().getLocation();
		Point objCoords = this.getLocationOnScreen();
		Point newCoords = new Point(mouseCoords.x - objCoords.x, mouseCoords.y - objCoords.y);
		newCoords.x = newCoords.x / Wall.TILE_WIDTH;
		newCoords.y = newCoords.y / Wall.TILE_HEIGHT;

		if(newCoords.x == currCoords.x && newCoords.y == currCoords.y) return;

		currCoords = newCoords;
		getGhostImage();
		ghostImage.setLocation(newCoords.x * Wall.TILE_WIDTH, newCoords.y * Wall.TILE_HEIGHT);

		//System.out.println(currCoords);

		if(drawing){
			drawCurrentItem();
		}
		else if(erasing){
			eraseCurrentTile();
		}
	}

	private void openEditorMenu(boolean b){

	}

	@Override
	public void mouseClicked(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			drawing = true;
			drawCurrentItem();
		}
		else if(e.getButton() == MouseEvent.BUTTON3){
			erasing = true;
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			drawing = false;
			saveLevel("testCopy");
		}
		else if(e.getButton() == MouseEvent.BUTTON3){
			erasing = false;
			saveLevel("testCopy");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (Configs.isLeftKey(key)){
			move_left = true;
		}
		else if(Configs.isRightKey(key)){
			move_right = true;
		}
		else if(Configs.isJumpKey(key)){
			move_up = true;
		}
		else if(Configs.isCrouchKey(key)){
			move_down = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (Configs.isLeftKey(key))
		{
			move_left = false;
		}
		else if(Configs.isRightKey(key)){
			move_right = false;
		}
		if(Configs.isJumpKey(key)){
			move_up = false;
		}
		else if(Configs.isCrouchKey(key)){
			move_down = false;
		}

		if(Configs.isMenuKey(key)){
			if(!menuOpen)
				openEditorMenu(true);
			else
				openEditorMenu(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void mouseDragged(MouseEvent e) {
		moveGhost();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		moveGhost();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		currentMaterial = (e.getWheelRotation() + currentMaterial) % wallMaterials.length;
		while(currentMaterial < 0){
			currentMaterial += wallMaterials.length;
		}
		moveGhost();
	}
}
