import javax.swing.JPanel;



public class Level extends JPanel{
	private static final long serialVersionUID = 1L;

	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3; //Define directional array numbers

	private Wall[] wall;



	public Level(){
		super();
	}

	public void loadLevel(String path){
		wall = new Wall[6];
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
		for(Wall p : wall)
			this.add(p);
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
}
