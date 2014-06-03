import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MainMenu extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	JLabel title;
	
	public MainMenu(){
		this.setPreferredSize(new Dimension(ClientWindow.WIDTH,ClientWindow.HEIGHT));
		this.setLayout(null);
		this.setVisible(true);
		this.setBackground(Color.black);
		
		JButton toAdd = new JButton("Load test level");
		toAdd.setActionCommand("testlevel");
		toAdd.setBounds(ClientWindow.WIDTH/2 - 150, ClientWindow.HEIGHT/2 - 50, 300, 40);
		toAdd.setContentAreaFilled(false);
		toAdd.setForeground(Color.white);
		toAdd.addActionListener(this);
		this.add(toAdd);
		
		toAdd = new JButton("Level Editor");
		toAdd.setActionCommand("leveleditor");
		toAdd.setBounds(ClientWindow.WIDTH/2 - 150, ClientWindow.HEIGHT/2, 300, 40);
		toAdd.setContentAreaFilled(false);
		toAdd.setForeground(Color.white);
		toAdd.addActionListener(this);
		this.add(toAdd);
		
		toAdd = new JButton("Level Editor - TestCopy");
		toAdd.setActionCommand("leveleditortestcopy");
		toAdd.setBounds(ClientWindow.WIDTH/2 - 150, ClientWindow.HEIGHT/2 + 50, 300, 40);
		toAdd.setContentAreaFilled(false);
		toAdd.setForeground(Color.white);
		toAdd.addActionListener(this);
		this.add(toAdd);
		
		this.revalidate();
		this.repaint();
		
		(new Thread(new Runnable(){ @Override public void run(){ 
				loadTitleImage();
				Sound.music("Sad_March"); 
			} } )).start();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("testlevel")){
			ClientWindow.activeWindow.loadLevel("testCopy");
		}
		
		if(command.equals("leveleditor")){
			ClientWindow.activeWindow.loadLevelEditor("test");
		}
		if(command.equals("leveleditortestcopy")){
			ClientWindow.activeWindow.loadLevelEditor("testCopy");
		}
		
	}

}
