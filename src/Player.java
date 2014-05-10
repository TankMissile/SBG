import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class Player extends JButton implements KeyListener {
	private static final long serialVersionUID = 1L;

	//The parent window
	private Level container;

	//Movement caps
	private final int VCAP = 120; //max fall speed
	private final int SLIDECAP = 17; //max wallslide fall speed
	private final int HCAP = 40; //max horizontal speed
	private final int ACCEL = 3; //Rate of horizontal acceleration on ground
	private final int AIR_ACCEL = 2; //Rate of horizontal acceleration while airborne
	private final int DECEL = 3; //horizontal deceleration
	private final int AIR_DECEL = 0; //Rate of horizontal deceleration while airborne
	private final int VDECEL = 2; //vertical acceleration due to gravity
	private final int JUMPSPEED = 65; //Initial vertical velocity while jumping

	private final int NORMALHEIGHT = 32;
	private final int NORMALWIDTH = 32;
	private final boolean RESIZE_CROUCH = true; //Controls whether to rescale the image when crouching

	//Boundaries for movement
	private int boundary[] = new int[4]; //0 up 1 down 2 left 3 right
	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

	//Bounds of character
	private int x = 0;
	private int y = 0;
	private int w = NORMALWIDTH;
	private int h = NORMALHEIGHT;

	//Action variables
	private boolean moveright = false;
	private boolean moveleft = false;
	private int vert_velocity = 0;
	private int horiz_velocity = 0;
	private boolean doublejump = false;
	private boolean airborne = true;
	private boolean crouching = false;
	private boolean uncrouch = false;
	private boolean airdrop = true;
	private boolean rightwallgrab, leftwallgrab = false;
	private int lastgrabbed = 0; //store which wall was last grabbed, -1 left 1 right
	private boolean pause = true;

	Thread thread;


	//Constructor
	public Player(Point p, Level l){
		container = l;

		boundary[UP] = 0;
		boundary[DOWN] = 460;
		boundary[LEFT] = 0;
		boundary[RIGHT] = 640;

		this.setPreferredSize(new Dimension(w, h));
		this.setBorderPainted(false);
		this.setVisible(true);
		this.setIcon(loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT));
		//this.setIcon(new ImageIcon("img/gameicon.png"));
		this.addKeyListener(this);
		this.setSize(w, h);
		this.setLocation(new Point(x = p.x, y = p.y));

		thread = new Thread(new Runnable(){ @Override public void run(){ animate(); } });
		thread.start();
	}


	//Thread
	private void animate(){
		boolean kill = false;
		while(!kill){
			if(!pause)
			{
				//Make sure the character is the focused object (for keylistener)
				if(!this.isFocusOwner())
					this.requestFocusInWindow();
				
				//Find boundaries (if in motion)
				if(vert_velocity != 0 || horiz_velocity != 0 || moveright || moveleft )
					getBoundaries();

				if(uncrouch)
					updateCrouching(false);

				//Reset position and size variables
				setLocationVars();

				if( y+h < boundary[DOWN]) //if not on the ground
					airborne = true;

				//Increase velocity
				if(!airborne){
					if(moveright)
						horiz_velocity += ACCEL;
					if(moveleft)
						horiz_velocity -= ACCEL;
				}
				else {
					if(moveright){
						if(leftwallgrab) leftwallgrab = false; //let go of the wall
						horiz_velocity += AIR_ACCEL;
					}
					else if(moveleft){
						if(rightwallgrab) rightwallgrab = false; //let go of the wall
						horiz_velocity -= AIR_ACCEL;
					}
				}

				//Determine acceleration (both horizontal and vertical)
				getAcceleration();

				//check for collisions
				checkHorizontalCollisions();
				checkVerticalCollisions();

				//Reset position and size variables (may have changed above)
				setLocationVars();

				//Move the player character
				this.setBounds(x + horiz_velocity/10, this.getLocation().y - vert_velocity/10, w, h);

				try {
					Thread.sleep(1000/60); //60 fps (temp)
				} catch (InterruptedException e) { }

				//System.out.println(this.getLocation());
			}
		}
	}

	//Pause running (or resume, if false)
	public void pause(boolean b){
		pause = b;
	}

	//Image Handling
	private ImageIcon loadImage(String path, int w, int h){

		java.net.URL imgURL = getClass().getResource(path);

		if (imgURL != null) {
			ImageIcon img = new ImageIcon(imgURL);
			Image resize = img.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
			img = new ImageIcon(resize);
			return img;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	//Position and size functions
	private void setLocationVars(){
		x = this.getLocation().x;
		y = this.getLocation().y;
		w = this.getWidth();
		h = this.getHeight();
	}
	private void updateCrouching(boolean c){
		if(c){
			this.setLocation(new Point(x,y+NORMALHEIGHT/2));
			this.setSize(w,NORMALHEIGHT/2);
			if(RESIZE_CROUCH) this.setIcon(loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT/2));
			crouching = true;
		}
		else{
			if(y-NORMALHEIGHT/2 < boundary[UP])
				return;
			this.setLocation(new Point(x, y-NORMALHEIGHT/2));
			this.setSize(w,NORMALHEIGHT);
			if(RESIZE_CROUCH) this.setIcon(loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT));
			crouching = false;
			uncrouch = false;
		}
	}
	private void performJump(){
		//Check superjump
		if(crouching && !airborne){
			vert_velocity = (int)(JUMPSPEED * 1.5);
			doublejump = true;
			updateCrouching(false);
		}
		//Check air drop
		else if(crouching && airborne){
			vert_velocity = -1 * (int)(VCAP * 6);
			horiz_velocity = 0;
			airdrop = true;
		}
		//otherwise
		else if(!airborne || !doublejump){
			vert_velocity = JUMPSPEED;

			//Check air status
			if(!airborne)
				airborne = true;
			else{
				doublejump = true;
				if(leftwallgrab){
					horiz_velocity = HCAP;
					leftwallgrab = false;
				}
				else if(rightwallgrab){
					horiz_velocity = -1 * HCAP;
					rightwallgrab = false;
				}
				else
					if(moveright && !moveleft)
						horiz_velocity = HCAP;
					else if(moveleft)
						horiz_velocity = -1 * HCAP;
			}
		}
	}
	private void getAcceleration(){
		//Cap velocity / decelerate
		if(!airborne) //Ground Acceleration
		{
			int temphcap = HCAP;
			if(crouching) temphcap /= 2;
			
			if(horiz_velocity > 0){ //Moving right
				if(horiz_velocity > temphcap)
					horiz_velocity = temphcap;
				else if (!moveright)
					horiz_velocity -= DECEL;
			}
			else if(horiz_velocity < 0){ //Moving left
				if(horiz_velocity < -1 * temphcap)
					horiz_velocity = -1 * temphcap;
				else if(!moveleft)
					horiz_velocity += DECEL;
			}
		}
		else //Air acceleration
		{
			if(horiz_velocity > HCAP){ //Moving right
				horiz_velocity -= DECEL;
			}
			else if(horiz_velocity > 0)
			{
				horiz_velocity -= AIR_DECEL;
			}
			else if(horiz_velocity < -1 * HCAP){ //Moving left
				horiz_velocity += DECEL;
			}
			else if(horiz_velocity < 0)
			{
				horiz_velocity += AIR_DECEL;
			}
		}

		//Cap fall velocity
		if(airborne){
			int tempvcap = VCAP;
			int tempvdecel = VDECEL;
			if(leftwallgrab || rightwallgrab){
				tempvdecel /= 2;
				if(!crouching)
					tempvcap = SLIDECAP;
				else
					tempvcap = SLIDECAP*2;
			}
			if(airdrop)
				tempvcap = VCAP * 6;

			if(vert_velocity > -1 * tempvcap){ //If not falling faster than terminal velocity, accelerate downward
				vert_velocity -= tempvdecel;
			}
			else if(vert_velocity < (-1 * tempvcap)){ //If falling faster than terminal velocity, slow down
				vert_velocity += tempvdecel;
			}
		}
	}


	//Collision Checkers
	private void checkHorizontalCollisions(){
		if(x + w + horiz_velocity/10 > boundary[RIGHT]){ //right wall
			horiz_velocity = 0;

			if(airborne && !rightwallgrab){
				rightwallgrab = true;
				if(lastgrabbed <=0){
					lastgrabbed = 1;
					doublejump = false;

					//If moving upwards, grant wall run
					if(vert_velocity > 0){
						vert_velocity = (int)(JUMPSPEED * .7);
					}
				}
			}



			this.setLocation(new Point(boundary[RIGHT] - w, this.getLocation().y));
		}
		else if(x + horiz_velocity/10 < boundary[LEFT]){ //left wall
			horiz_velocity = 0;

			if(airborne && !leftwallgrab){
				leftwallgrab = true;
				if(lastgrabbed >= 0 ){
					lastgrabbed = -1;
					doublejump = false;

					//If moving upwards, grant wall run
					if(vert_velocity > 0){
						vert_velocity  = (int)(JUMPSPEED * .7);
					}
				}
			}

			this.setLocation(new Point(boundary[LEFT], this.getLocation().y));
		}
		
		//Grant Wall climb
		else if(rightwallgrab && x + w < boundary[RIGHT] || leftwallgrab && x > boundary[LEFT]){
			if(vert_velocity > 0) //if moving upwards
				if(rightwallgrab && !moveleft){
					vert_velocity = 2 * VDECEL;
					horiz_velocity = 20;
				}
				else if(leftwallgrab && !moveright){
					vert_velocity = 2 * VDECEL;
					horiz_velocity = -20;
				}

			rightwallgrab = false;
			leftwallgrab = false;
			lastgrabbed = 0;
		}
	}
	private void checkVerticalCollisions(){
		if(this.getLocation().y + h - vert_velocity/10 > boundary[DOWN]){ //hit floor
			if(vert_velocity < -1 * SLIDECAP - VDECEL)
				container.addParticle(Particle.DUST_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN] - Particle.TILE_HEIGHT);
			vert_velocity = 0;
			doublejump = false;
			airborne = false;
			airdrop = false;
			leftwallgrab = rightwallgrab = false;
			lastgrabbed = 0;

			this.setLocation(new Point(this.getX(), boundary[DOWN] - h));
		}
		else if(this.getLocation().y - vert_velocity/10 < boundary[UP]){ //hit ceiling
			vert_velocity = 0;
			horiz_velocity /= 2;
			this.setLocation(new Point(this.getX(), boundary[UP]));
		}
	}
	private void getBoundaries(){
		boundary = container.findBoundaries(this);
	}


	//Listeners
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if(!pause){
			if(Configs.isLeftKey(key))
				moveleft = true;

			if(Configs.isRightKey(key))
				moveright = true;

			//Check for jumps
			if(Configs.isJumpKey(key)){
				performJump();
			}
			
			if(Configs.isCrouchKey(key) && !crouching){
				updateCrouching(true);
			}
		}

		if(Configs.isMenuKey(key)){
			if(!pause){
				container.openGameMenu(true);
			}
			else{
				container.openGameMenu(false);
			}
		}

	}
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(!pause){
			if(Configs.isLeftKey(key))
				moveleft = false;

			if(Configs.isRightKey(key))
				moveright = false;

			if(Configs.isCrouchKey(key) && crouching){
				uncrouch = true;
			}
		}
	}
	@Override 
	public void keyTyped(KeyEvent e) { }
}
