import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class HealthBar extends JPanel{
	private static final long serialVersionUID = 1L;

	int health, maxHealth;
	private final static int ICON_WIDTH = 20, ICON_HEIGHT = 20;

	ArrayList<JLabel> icons = new ArrayList<JLabel>();
	
	String img_path = "/img/health.png";


	public HealthBar(){
		this.setBounds(10, 10, ClientWindow.WIDTH, ICON_HEIGHT);
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(null);
		
		maxHealth = 12;
		health = maxHealth;

		//changeHealth(-4);
		System.out.println("Health bar created");
		changeMaxHealth(maxHealth);
	}

	//Change health value and update graphical representation
	public void changeHealth(int d){
		health += d;
		if (health < 0){
			health = 0;
		}
		else if (health > maxHealth){
			health = maxHealth;
		}
		updateAppearance();
	}

	//Increase or decrease maximum health and update graphical representation
	public void changeMaxHealth(int h){
		int damageTaken = h - maxHealth;
		maxHealth = h;
		if(health > maxHealth)
			changeHealth(damageTaken);
		System.out.println("Max health updated");
		updateAppearance();
	}

	private void updateAppearance(){
		this.removeAll();
		icons.clear();
		
		int iconsToAdd = maxHealth/4;
		int iconhealth = health;
		int nextPosition = 0;
		//System.out.println("Health: " + iconhealth);
		
		while(iconsToAdd > 0){
			if(iconhealth >= 4){
				//System.out.println("  Health of icon: " + iconhealth);
				loadImage(4, nextPosition);
				iconhealth -= 4;
			}
			else if(iconhealth >= 0){
				//System.out.println("  Health of icon: " + iconhealth);
				loadImage(iconhealth, nextPosition);
				iconhealth = 0;
			}
			nextPosition += 1;
			
			iconsToAdd--;
			
			this.revalidate();
			this.repaint();
		}

	}

	//Image Handling
	private void loadImage(int h, int pos){
		JLabel newicon;
		
		try {
			BufferedImage sprite = ImageIO.read(getClass().getResource(img_path)).getSubimage(h * ICON_WIDTH, 0, ICON_WIDTH, ICON_HEIGHT);
			newicon = new JLabel( new ImageIcon(sprite.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH) ) );
			newicon.setSize(ICON_WIDTH, ICON_HEIGHT);
			newicon.setLocation(pos * ICON_WIDTH + pos*5, 0);
			icons.add(newicon);
			this.add(newicon);
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + img_path); }
		
		return;
	}
}
