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
	private final String WALL_CODE = "wa",
		BACKGROUND_CODE = "bg",
		STONE_CODE = "st",
		DIRT_CODE = "dr",
		WOOD_CODE = "wd",
		EYE_CODE = "ey",
		PLATFORM_CODE = "pt",
		PLAYER_CODE = "pl",
		ENTITY_CODE = "en",
		SPIKE_CODE = "sp";

	private Wall[][] wall;
	private Wall[][] background;
	private ArrayList<Entity> entity = new ArrayList<Entity>();
	private Player player;
	private HealthBar healthbar;
	private GameMenu gamemenu;

	public static final int BACKGROUND_DEPTH = -20000, BGWALL_DEPTH = -10000, WALL_DEPTH = 0, ENTITY_DEPTH = 0, PARTICLE_DEPTH = 0, MENU_DEPTH = 1000;

	public Level(){
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(ClientWindow.WIDTH,ClientWindow.HEIGHT));
		this.setLayout(null);
		this.setVisible(true);
	}

	//Load a level
	public void loadLevel(String path){
		this.removeAll();
		
		try {
			InputStream istream = getClass().getResourceAsStream("/level/"+path+".lvl");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(istream));
			String readline = null;
			String[] splitline;
			
			int x, y, t;
			
			Entity newEntity = null;
			
			wall = new Wall[ClientWindow.WIDTH/Wall.TILE_WIDTH][ClientWindow.HEIGHT/Wall.TILE_HEIGHT];
			background = new Wall[ClientWindow.WIDTH/Wall.TILE_WIDTH][ClientWindow.HEIGHT/Wall.TILE_HEIGHT];

			System.out.println("Loading Level...");
			while((readline = br.readLine()) != null){
				splitline = readline.split(" ");
				
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
					else if(splitline[1].equals(PLATFORM_CODE)){
						t = Wall.PLATFORM;
					}
					wall[x][y] = new Wall(x, y, t);
					this.add(wall[x][y], WALL_DEPTH);
				}
				if(splitline[0].equals(BACKGROUND_CODE)){
					System.out.println("read a background");
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
				}
				else if(splitline[0].equals(PLAYER_CODE)){
					System.out.println("Adding Player...");
					x = Integer.parseInt(splitline[1])*Wall.TILE_WIDTH + (Wall.TILE_WIDTH-Player.NORMALWIDTH)/2;
					y = Integer.parseInt(splitline[2])*Wall.TILE_HEIGHT;
					
					player = new Player(new Point(x, y), this);
					this.add(player, ENTITY_DEPTH, 0);
					System.out.println("Complete.");
				}
				else if(splitline[0].equals(ENTITY_CODE)){
					System.out.println("Adding entity: " + splitline[1]);
					
					if(splitline[1].equals(SPIKE_CODE)){
						newEntity = new SpikeTrap( Integer.parseInt(splitline[2]), Integer.parseInt(splitline[3]), Integer.parseInt(splitline[4]) );
						entity.add(newEntity);
						this.add(newEntity, ENTITY_DEPTH, 1 );
					}
				}
			}
			
			
			System.out.println("Drawing background...");
			drawBackground();
			System.out.println("Complete.");
			
			System.out.println("Drawing Textures...");
			blendWalls();
			System.out.println("Complete.");
			
			System.out.println("Level Loaded.");
			
		} 
		catch (UnsupportedEncodingException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }

		System.out.println("Creating Pause Menu...");
		gamemenu = new GameMenu();
		this.add(gamemenu, MENU_DEPTH);
		System.out.println("Complete.");

		System.out.println("Adding Background Color...");
		JPanel bgColor = new JPanel();
		bgColor.setBackground(new Color(200, 220, 255));
		bgColor.setSize(ClientWindow.WIDTH, ClientWindow.HEIGHT);
		this.add(bgColor, BACKGROUND_DEPTH);
		System.out.println("Complete.");

		//saveLevel(path);
		healthbar  = new HealthBar();
		this.add(healthbar, MENU_DEPTH, 0);
		
		player.linkHealthBar(healthbar);

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
				System.out.println(printline);

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

				if(i - 1 >= 0 && wall[i-1][j] != null){
					if((w.type != Wall.DIRT && wall[i-1][j].type != Wall.DIRT) || wall[i-1][j].type == w.type)
						if((w.type != Wall.PLATFORM && wall[i-1][j].type != Wall.PLATFORM) || wall[i-1][j].type == w.type)
							left = true;
				}
				if(i + 1 < wall.length && wall[i+1][j] != null && (w.type != Wall.DIRT || wall[i+1][j].type == w.type)){
					if((w.type != Wall.DIRT && wall[i+1][j].type != Wall.DIRT) || wall[i+1][j].type == w.type)
						if((w.type != Wall.PLATFORM && wall[i+1][j].type != Wall.PLATFORM) || wall[i+1][j].type == w.type)
							right = true;
				}
				if(j - 1 >= 0 && wall[i][j-1] != null && ( w.type != Wall.DIRT || wall[i][j-1].type == w.type)){
					if(w.type != Wall.EYE || wall[i][j-1].type == Wall.EYE)
						up = true;
				}
				if(j + 1 < wall[i].length && wall[i][j+1] != null){
					if(w.type != Wall.EYE || wall[i][j+1].type == Wall.EYE)
						down = true;
				}

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

				if(left && right){
					x = Wall.MID;
				}
				else if(left){
					x = Wall.RIGHT;
				}
				else if(right){
					x = Wall.LEFT;
				}


				w.loadTileBackground(x, y);
			}
		}
	}
	//Draw the background
	private void drawBackground(){
		boolean up = false, down = false, left = false, right = false;
		int x = Wall.COLUMN, y = Wall.ROW;
		int xpos, ypos;

		for(int i = 0; i < background.length; i++){
			for(int j = 0; j < background[i].length; j++){
				Wall w = background[i][j];
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
				System.out.println(printline);

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

				//check background walls
				if(i - 1 >= 0 && background[i-1][j] != null){
					if((w.type != Wall.DIRT && background[i-1][j].type != Wall.DIRT) || background[i-1][j].type == w.type)
						if((w.type != Wall.PLATFORM && background[i-1][j].type != Wall.PLATFORM) || background[i-1][j].type == w.type)
							left = true;
				}
				if(i + 1 < background.length && background[i+1][j] != null && (w.type != Wall.DIRT || background[i+1][j].type == w.type)){
					if((w.type != Wall.DIRT && background[i+1][j].type != Wall.DIRT) || background[i+1][j].type == w.type)
						if((w.type != Wall.PLATFORM && background[i+1][j].type != Wall.PLATFORM) || background[i+1][j].type == w.type)
							right = true;
				}
				if(j - 1 >= 0 && background[i][j-1] != null && ( w.type != Wall.DIRT || background[i][j-1].type == w.type)){
					if(w.type != Wall.EYE || background[i][j-1].type == Wall.EYE)
						up = true;
				}
				if(j + 1 < background[i].length && background[i][j+1] != null){
					if(w.type != Wall.EYE || background[i][j+1].type == Wall.EYE)
						down = true;
				}
				
				//check normal walls
				if(i - 1 >= 0 && wall[i-1][j] != null){
					if((w.type != Wall.DIRT && wall[i-1][j].type != Wall.DIRT) || wall[i-1][j].type == w.type)
						if((w.type != Wall.PLATFORM && wall[i-1][j].type != Wall.PLATFORM) || wall[i-1][j].type == w.type)
							left = true;
				}
				if(i + 1 < wall.length && wall[i+1][j] != null && (w.type != Wall.DIRT || wall[i+1][j].type == w.type)){
					if((w.type != Wall.DIRT && wall[i+1][j].type != Wall.DIRT) || wall[i+1][j].type == w.type)
						if((w.type != Wall.PLATFORM && wall[i+1][j].type != Wall.PLATFORM) || wall[i+1][j].type == w.type)
							right = true;
				}
				if(j - 1 >= 0 && wall[i][j-1] != null && ( w.type != Wall.DIRT || wall[i][j-1].type == w.type)){
					if(w.type != Wall.EYE || wall[i][j-1].type == Wall.EYE)
						up = true;
				}
				if(j + 1 < wall[i].length && wall[i][j+1] != null){
					if(w.type != Wall.EYE || wall[i][j+1].type == Wall.EYE)
						down = true;
				}

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

				if(left && right){
					x = Wall.MID;
				}
				else if(left){
					x = Wall.RIGHT;
				}
				else if(right){
					x = Wall.LEFT;
				}


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

		//System.out.println("up: " + bounds[0] + " down: " + bounds[1] + " left: " + bounds[2] + " right:  " + bounds[3]);
		return bounds;
	}

	//Save all tiles of the level
	@SuppressWarnings("unused")
	private void saveLevel(String path){
		try {
			File file = new File("res/level/" + path + ".lvl");
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
			
			writeline = PLAYER_CODE + " " + ((player.getLocation().x - player.getLocation().x%Wall.TILE_WIDTH)/Wall.TILE_WIDTH) + " " + ((player.getLocation().y - player.getLocation().y%Wall.TILE_HEIGHT)/Wall.TILE_HEIGHT);
			bw.write(writeline);
			
			
			bw.flush();
			bw.close();
		} catch (IOException e) { e.printStackTrace(); }

	}
}
