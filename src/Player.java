import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Player extends Entity implements KeyListener {
	private static final long serialVersionUID = 1L;

	//The parent window
	private Level container;

	//The health bar
	private HealthBar healthbar;

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
	private final int WALL_JUMPSPEED = 60;

	public static final int NORMALHEIGHT = 32;
	public static final int NORMALWIDTH = 32;
	private final boolean RESIZE_CROUCH = true; //Controls whether to rescale the image when crouching

	//Action variables
	private boolean moveright = false;
	private boolean moveleft = false;
	private boolean doublejump = false;
	public boolean crouching = false;
	private boolean uncrouch = false;
	public boolean airdrop = true;
	private boolean rightwallgrab, leftwallgrab = false;
	private int lastgrabbed = 0; //store which wall was last grabbed, -1 left 1 right

	//Counters for various timers
	private int frames_to_particle = 0;
	public int frames_to_dropthrough = -1;

	Thread thread;

	//Constructor
	public Player(Point p, Level l){
		super();

		container = l;

		img_path = "/img/player.png";
		loadImage(img_path, NORMALWIDTH, NORMALHEIGHT);

		w = NORMALWIDTH;
		h = NORMALHEIGHT;

		boundary[UP] = new Boundary(0);
		boundary[DOWN] = new Boundary(460);
		boundary[LEFT] = new Boundary(0);
		boundary[RIGHT] = new Boundary(640);

		this.addKeyListener(this);
		this.setSize(w, h);
		this.setLocation(new Point(x = p.x, y = p.y));

		thread = new Thread(new Runnable(){ @Override public void run(){ animate(); } });
		thread.start();
	}


	//Thread
	@Override
	public void animate(){
		boolean kill = false;
		Entity collidedWith = null;
		while(!kill){
			if(!pause)
			{
				//Make sure the character is the focused object (for keylistener)
				if(!this.isFocusOwner())
					this.requestFocusInWindow();

				//Find boundaries (if in motion)
				//if(vert_velocity != 0 || horiz_velocity != 0 || moveright || moveleft )
				getBoundaries();

				//Check if the player is in contact with any entities, and if so, take whatever damage they deal
				collidedWith = container.checkCollision(this);
				if(invincibility == 0){
					if(collidedWith != null && collidedWith.damage < 0){
						System.out.println("SBG: \"Ow!\"");
						healthbar.changeHealth(collidedWith.damage);
						invincibility = 60;
						horiz_velocity *= -1;
						vert_velocity = JUMPSPEED/2;
						hurt = true;
						flash_timer = FLASH_DELAY;
					}
				}
				else if(invincibility > 0){
					invincibility--;
					if(invincibility == 0 && hurt){
						hurt = false;
						if(healthbar.health > 2)
							img_path = "/img/player.png";
						else
							img_path = "/img/player_sad.png";
						loadImage(img_path, w, h);
					}
				}

				//Update image while in pain
				if(hurt){
					if(flash_timer >= FLASH_DELAY && img_path != "/img/player_hurt.png"){
						img_path = "/img/player_hurt.png";
						flash_timer = 0;
						loadImage(img_path, w, h);
					}
					else if(flash_timer >= FLASH_DELAY && img_path != "/img/player_sad.png"){
						img_path = "/img/player_sad.png";
						flash_timer = 0;
						loadImage(img_path, w, h);
					}
					else {
						flash_timer++;
					}
				}

				//Check for fluids
				collidedWith = container.checkFluidCollision(this);
				if(collidedWith != null){
					if(collidedWith.entity_code == Level.WATER_CODE){
						this.speed_modifier = collidedWith.speed_modifier;
						this.speed_cap_modifier = collidedWith.speed_cap_modifier;
					}
					this.in_fluid = true;
					this.doublejump = false;
					leftwallgrab = rightwallgrab = false;
				}
				else {
					this.in_fluid = false;
					this.speed_modifier = 1;
					this.speed_cap_modifier = 1;
				}

				//If the user has released the crouch button, check if there's enough room to do so
				if(uncrouch)
					updateCrouching(false);

				//Reset position and size variables
				setLocationVars();

				if( y+h < boundary[DOWN].value) //if not on the ground
					airborne = true;

				//Increase velocity
				if(!airborne){
					if(moveright)
						horiz_velocity += ACCEL * speed_modifier;
					if(moveleft)
						horiz_velocity -= ACCEL * speed_modifier;
				}
				else {
					if(moveright){
						if(leftwallgrab) leftwallgrab = false; //let go of the wall
						horiz_velocity += AIR_ACCEL * speed_modifier;
					}
					else if(moveleft){
						if(rightwallgrab) rightwallgrab = false; //let go of the wall
						horiz_velocity -= AIR_ACCEL * speed_modifier;
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

	//Link the player to his health bar
	public void linkHealthBar(HealthBar h){
		healthbar = h;
	}

	//Position and size functions
	private void setLocationVars(){
		x = this.getLocation().x;
		y = this.getLocation().y;
		w = this.getWidth();
		h = this.getHeight();
	}

	//Crouch if true is passed, otherwise check if there's room to uncrouch and perform appropriate action
	private void updateCrouching(boolean c){
		if(c){
			this.removeAll(); //Remove the character's image

			h = NORMALHEIGHT/2; //keep track of how tall the player is
			this.setSize(w,h); //resize the player
			this.setLocation(new Point(x,y+NORMALHEIGHT/2)); //move the player's head down

			if(RESIZE_CROUCH) loadImage(img_path, w, h); //reskin the player

			crouching = true; //yep, he's crouching now
			frames_to_dropthrough = 10; //in 10 frames, activate dropthrough status

			this.repaint(); //force refresh the panel
		}
		else{
			if(y-NORMALHEIGHT/2 < boundary[UP].value) //If uncrouching would make the player bigger than the space he has available, cancel the action
				return;

			this.removeAll(); //Remove the character's image
			this.setLocation(new Point(x, y-NORMALHEIGHT/2)); //move the player up to allow his resize

			h = NORMALHEIGHT; //keep track of player's height
			this.setSize(w,h); //resize player

			if(RESIZE_CROUCH) loadImage(img_path, w, h); //reskin the player

			crouching = false; //not crouching
			uncrouch = false; //no need to uncrouch anymore
			if(!airdrop) frames_to_dropthrough = -1; //Don't drop through things anymore

			this.repaint(); //force refresh the panel
		}
	}
	private void performJump(){
		int temphcap = (int) (HCAP * speed_cap_modifier);

		//Check superjump
		if(crouching && !airborne){
			if(frames_to_particle == 0){
				container.addParticle(Particle.JUMP_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN].value - Particle.TILE_HEIGHT);
			}

			vert_velocity = (int)(JUMPSPEED * 1.5 * speed_modifier);
			if(!in_fluid) doublejump = true;
			updateCrouching(false);
		}
		//Check air drop
		else if(crouching && airborne){
			vert_velocity = -1 * (int)(VCAP * 6);
			horiz_velocity = 0;
			airdrop = true;
			frames_to_dropthrough = 0;
			return;
		}
		//otherwise
		else if(!airborne || !doublejump){
			if(!rightwallgrab && !leftwallgrab){
				vert_velocity = (int) (JUMPSPEED * speed_modifier);
			}
			else if (rightwallgrab || leftwallgrab){
				vert_velocity = (int) (WALL_JUMPSPEED * speed_modifier);
			}

			//Check air status
			if(!airborne){
				//add particle effect
				if(horiz_velocity < SLIDECAP && frames_to_particle == 0){
					container.addParticle(Particle.JUMP_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, boundary[DOWN].value - Particle.TILE_HEIGHT);
				}
				airborne = true;
			}
			else{
				doublejump = true;
				if(leftwallgrab){
					horiz_velocity = temphcap;
					leftwallgrab = false;
				}
				else if(rightwallgrab){
					horiz_velocity = -1 * temphcap;
					rightwallgrab = false;
				}
				else
					if(moveright && !moveleft)
						horiz_velocity = temphcap;
					else if(moveleft)
						horiz_velocity = -1 * temphcap;

				//Add particle effect
				if(frames_to_particle == 0){
					container.addParticle(Particle.DUST_POOF, this.getX() + this.getWidth()/2 - Particle.TILE_WIDTH/2, this.y + this.h - Particle.TILE_HEIGHT);
				}
			}
		}
		else return;

		Sound.sound("bounce");
	}
	//Cap velocity / decelerate
	private void getAcceleration(){
		int temphcap = (int) (HCAP * speed_cap_modifier);
		int tempvcap = (int) (VCAP * speed_cap_modifier);
		if(in_fluid){
			tempvcap = temphcap;
		}

		int tempvdecel = (int) (VDECEL * vert_accel_modifier);
		if(!airborne) //Ground Acceleration
		{
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

			if(horiz_velocity > temphcap){ //Moving right
				horiz_velocity -= DECEL;
			}
			else if(horiz_velocity > 0)
			{
				horiz_velocity -= AIR_DECEL;
			}
			else if(horiz_velocity < -1 * temphcap){ //Moving left
				horiz_velocity += DECEL;
			}
			else if(horiz_velocity < 0)
			{
				horiz_velocity += AIR_DECEL;
			}
		}

		//Cap fall velocity
		if(airborne){
			if(leftwallgrab || rightwallgrab){
				tempvdecel /= 2;
				if(!crouching){
					tempvcap = (int) (SLIDECAP * speed_cap_modifier);
					if(tempvcap == 0) tempvcap = 2;
				}
				else{
					tempvcap = (int) (SLIDECAP * 2 * speed_cap_modifier);
					if (tempvcap == 0) tempvcap = 1;
				}
			}
			if(airdrop)
				if(!in_fluid) tempvcap = VCAP * 6;
				else {
					airdrop = false;
				}

			if(vert_velocity > -1 * tempvcap){ //If not falling faster than terminal velocity, accelerate downward
				vert_velocity -= tempvdecel;
			}
			else if(vert_velocity < (-1 * tempvcap)){ //If falling faster than terminal velocity, slow down
				vert_velocity = -1 * tempvcap;
			}
		}
	}


	//Collision Checkers
	private void checkHorizontalCollisions(){
		if(x + w + horiz_velocity/10 > boundary[RIGHT].value){ //right wall
			horiz_velocity = 0;

			if(boundary[RIGHT].slidable && airborne && !rightwallgrab && !in_fluid){
				rightwallgrab = true;
				if(lastgrabbed <=0){
					lastgrabbed = 1;
					doublejump = false;

					//If moving upwards, grant wall run
					if(vert_velocity > 0 ){
						vert_velocity = (int)(WALL_JUMPSPEED * .7 * speed_modifier);
					}
				}
			}
			else if(!boundary[RIGHT].slidable || in_fluid){
				rightwallgrab = false;
			}



			this.setLocation(new Point(boundary[RIGHT].value - w, this.getLocation().y));
		}
		else if(x + horiz_velocity/10 < boundary[LEFT].value){ //left wall
			horiz_velocity = 0;

			if(boundary[LEFT].slidable && airborne && !leftwallgrab && !in_fluid){
				leftwallgrab = true;
				if(lastgrabbed >= 0 ){
					lastgrabbed = -1;
					doublejump = false;

					//If moving upwards, grant wall run
					if(vert_velocity > 0){
						vert_velocity  = (int)(JUMPSPEED * .7 * speed_modifier);
					}
				}
			}
			else if(!boundary[LEFT].slidable || in_fluid){
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
