
public class Slime extends Entity{
	private static final long serialVersionUID = 1L;

	private int anim_frame = 0;
	private int last_frame = 2;

	public Slime(int x, int y){
		super();
		img_path = "/img/blobbeh.png";
		this.w = 32;
		this.h = 32;
		this.setBounds(x*Wall.TILE_WIDTH + Wall.TILE_WIDTH/2 - w/2, y*Wall.TILE_HEIGHT + (Wall.TILE_HEIGHT-h), w, h);
		this.loadImage(img_path, w, h, 0, 0);
		this.damage = -1;
		entity_code = Level.SLIME_CODE;
	}

	@Override
	protected void animate(){
		boolean kill = false;
		while(!kill){
			if(!pause){
				boundary = ClientWindow.level.findGroundBoundaries(this);

				//get destination
				if(destination == 0){
					int random = (int)(Math.random()*100); //generate random integer between 0 and 100
					if(random < 10 && this.getLocation().x - Wall.TILE_WIDTH > boundary[LEFT].value){
						destination = -1 * Wall.TILE_WIDTH; //move left
						facingRight = false;
					}
					else if (random < 20 && this.getLocation().x + w + Wall.TILE_WIDTH < boundary[RIGHT].value){
						destination = Wall.TILE_WIDTH; //move right
						facingRight = true;
					}
				}

				if(destination < 0){
					destination += 2;
					this.setLocation(this.getLocation().x - 2,this.getLocation().y);
				}
				else if(destination > 0){
					destination -= 2;
					this.setLocation(this.getLocation().x + 2,this.getLocation().y);
				}
				anim_frame = (anim_frame+1)%last_frame;
				this.loadImage(img_path, w, h, w*anim_frame, 0);
			}


			try {
				Thread.sleep(800);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}
