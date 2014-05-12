import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Player extends JPanel implements KeyListener {
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

	public static final int NORMALHEIGHT = 32;
	public static final int NORMALWIDTH = 32;
	private final boolean RESIZE_CROUCH = true; //Controls whether to rescale the image when crouching

	//Boundaries for movement
	private Boundary boundary[] = new Boundary[4]; //0 up 1 down 2 left 3 right
	private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

	//Bounds of character
	private int x = 0;
	private int y = 0;
	public int w = NORMALWIDTH;
	public int h = NORMALHEIGHT;

	//Action variables
	private boolean moveright = false;
	private boolean moveleft = false;
	private int vert_velocity = 0;
	private int horiz_velocity = 0;
	private boolean doublejump = false;
	private boolean airborne = true;
	public boolean crouching = false;
	private boolean uncrouch = false;
	public boolean airdrop = true;
	private boolean rightwallgrab, leftwallgrab = false;
	private int lastgrabbed = 0; //store which wall was last grabbed, -1 left 1 right
	private boolean pause = true;

	//Counters for various timers
	private int frames_to_particle = 0;
	public int frames_to_dropthrough = -1;

	Thread thread;


	//Constructor
	public Player(Point p, Level l){
		container = l;

		boundary[UP] = new Boundary(0);
		boundary[DOWN] = new Boundary(460);
		boundary[LEFT] = new Boundary(0);
		boundary[RIGHT] = new Boundary(640);

		this.setPreferredSize(new Dimension(w, h));
		this.setVisible(true);
		loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT);
		this.addKeyListener(this);
		this.setSize(w, h);
		this.setLocation(new Point(x = p.x, y = p.y));
		this.setOpaque(false);
		this.setLayout(null);

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
				//if(vert_velocity != 0 || horiz_velocity != 0 || moveright || moveleft )
				getBoundaries();

				if(uncrouch)
					updateCrouching(false);

				//Reset position and size variables
				setLocationVars();

				if( y+h < boundary[DOWN].value) //if not on the ground
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

				if(frames_to_particle > 0)
					frames_to_particle--;

				if( frames_to_dropthrough != -1){
					if(frames_to_dropthrough > 0){
						frames_to_dropthrough--;
					}
				} 
				else if( frames_to_dropthrough != -1 ) {
					frames_to_dropthrough = -1;
				}

				//System.out.println(this.getLocation());
			}
		}
	}

	//Pause running (or resume, if false)
	public void pause(boolean b){
		pause = b;
	}

	//Image Handling
	private void loadImage(String path, int w, int h){
		this.removeAll();
		java.net.URL imgURL = getClass().getResource(path);

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			JLabel image = new JLabel(new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
			image.setSize(w, h);
			this.add(image);
			this.revalidate();
			this.repaint();
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
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
			this.removeAll();
			h = NORMALHEIGHT/2;
			this.setLocation(new Point(x,y+NORMALHEIGHT/2));
			this.setSize(w,h);
			if(RESIZE_CROUCH) loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT/2);
			crouching = true;
			frames_to_dropthrough = 10;
		}
		else{
			if(y-NORMALHEIGHT/2 < boundary[UP].value)
				return;

			this.removeAll();
			h = NORMALHEIGHT;
			this.setLocation(new Point(x, y-NORMALHEIGHT/2));
			this.setSize(w,h);
			if(RESIZE_CROUCH) loadImage("/img/gameicon.png", NORMALWIDTH, NORMALHEIGHT);
			crouching = false;
			uncrouch = false;
			if(!airdrop) frames_to_dropthrough = -1;
		}
	}
	private void performJump(){
		//Check superjump
		if(crouching && !airborne){
			if(frames_to_particle == 0){
				container.addParticle(Particle.JUMP_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN].value - Particle.TILE_HEIGHT);
			}

			vert_velocity = (int)(JUMPSPEED * 1.5);
			doublejump = true;
			updateCrouching(false);
		}
		//Check air drop
		else if(crouching && airborne){
			vert_velocity = -1 * (int)(VCAP * 6);
			horiz_velocity = 0;
			airdrop = true;
			frames_to_dropthrough = 0;
		}
		//otherwise
		else if(!airborne || !doublejump){
			vert_velocity = JUMPSPEED;

			//Check air status
			if(!airborne){
				if(horiz_velocity < SLIDECAP && frames_to_particle == 0){
					container.addParticle(Particle.JUMP_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN].value - Particle.TILE_HEIGHT);
				}
				airborne = true;
			}
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
		if(x + w + horiz_velocity/10 > boundary[RIGHT].value){ //right wall
			horiz_velocity = 0;

			if(boundary[RIGHT].slidable && airborne && !rightwallgrab){
				rightwallgrab = true;
				if(lastgrabbed <=0){
					lastgrabbed = 1;
					doublejump = false;

					//If moving upwards, grant wall run
					if(vert_velocity > 0 ){
						vert_velocity = (int)(JUMPSPEED * .7);
					}
				}
			}
			else if(!boundary[RIGHT].slidable){
				rightwallgrab = false;
			}



			this.setLocation(new Point(boundary[RIGHT].value - w, this.getLocation().y));
		}
		else if(x + horiz_velocity/10 < boundary[LEFT].value){ //left wall
			horiz_velocity = 0;

			if(boundary[LEFT].slidable && airborne && !leftwallgrab){
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
			else if(!boundary[LEFT].slidable){
				leftwallgrab = false;
			}

			this.setLocation(new Point(boundary[LEFT].value, this.getLocation().y));
		}

		//Grant Wall Climb
		else if(rightwallgrab && x + w < boundary[RIGHT].value || leftwallgrab && x > boundary[LEFT].value){
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
		if(this.getLocation().y + h - vert_velocity/10 > boundary[DOWN].value){ //hit floor
			if(vert_velocity < -1 * SLIDECAP - VDECEL){
				container.addParticle(Particle.DUST_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN].value - Particle.TILE_HEIGHT);
				//frames_to_particle = 5;
			}
			vert_velocity = 0;
			doublejump = false;
			airborne = false;
			airdrop = false;
			leftwallgrab = rightwallgrab = false;
			lastgrabbed = 0;
			frames_to_dropthrough = -1;

			this.setLocation(new Point(this.getX(), boundary[DOWN].value - h));
		}
		else if(this.getLocation().y - vert_velocity/10 < boundary[UP].value){ //hit ceiling
			vert_velocity = 0;
			horiz_velocity /= 2;
			this.setLocation(new Point(this.getX(), boundary[UP].value));
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
