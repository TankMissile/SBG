import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Preloader extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private int NUM_STEPS = 6;
	
	private JLabel title; //Image of the game's title
	private JLabel overview; //Message displaying general status of loading ("loading level", "drawing tiles", etc.)
	private JLabel currentStatus; //Message displaying what specifically it's working on ("Drawing tile: x, y" etc.)
	private JLabel pBar;
	private int curStep = 1;
	
	public Preloader(){
		this.setPreferredSize(new Dimension(ClientWindow.WIDTH,ClientWindow.HEIGHT));
		this.setLayout(null);
		this.setVisible(true);
		this.setBackground(Color.BLACK);
		
		//Create the title image
		title = new JLabel();
		title.setOpaque(false);
		loadTitleImage();
		
		//Create the overview
		overview = new JLabel("Setting up preloader", JLabel.CENTER);
		overview.setOpaque(false);
		overview.setForeground(new Color(255,255,255));
		overview.setBounds(0, ClientWindow.HEIGHT/2 - 130, ClientWindow.WIDTH, 100);
		overview.setVisible(true);
		this.add(overview);
		
		//Create the progress bar
		pBar = new JLabel("" + curStep + "/" + NUM_STEPS, JLabel.CENTER);
		pBar.setBackground(Color.WHITE);
		pBar.setOpaque(true);
		pBar.setBounds(ClientWindow.WIDTH/4, ClientWindow.HEIGHT/2 - 10, 1, 20);
		//pBar.setBounds(ClientWindow.WIDTH/4, ClientWindow.HEIGHT/2 - 10, ClientWindow.WIDTH/2, 20);
		pBar.setVisible(true);
		this.add(pBar);
		
		//Create current status message
		currentStatus = new JLabel("Setting up preloader...", JLabel.CENTER);
		currentStatus.setOpaque(false);
		currentStatus.setVisible(true);
		currentStatus.setForeground(Color.WHITE);
		currentStatus.setBounds(0, ClientWindow.HEIGHT/2 + 30, ClientWindow.WIDTH, 100);
		currentStatus.setVisible(true);
		this.add(currentStatus);
	}
	
	public void updateCurrentStatus(String msg){
		currentStatus.setText(msg);
	}
	
	public void updateOverview(String msg){
		curStep++;
		pBar.setSize(curStep * (ClientWindow.WIDTH/2)/NUM_STEPS, 20);
		pBar.setText("" + curStep + "/" + NUM_STEPS);
		overview.setText(msg);
		currentStatus.setText(msg + "...");
	}
	
	protected void loadTitleImage(){
		String path = "/img/title.png";
		java.net.URL imgURL = getClass().getResource(path);

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			title = new JLabel(new ImageIcon(bi.getScaledInstance(518, 100, Image.SCALE_SMOOTH)));
			title.setBounds(ClientWindow.WIDTH/2 - 259, ClientWindow.HEIGHT/2 - 200, 518, 100);
			this.add(title);
			this.revalidate();
			this.repaint();
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
	}

}
