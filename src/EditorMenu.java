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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class EditorMenu extends JTabbedPane implements ActionListener {
	private static final long serialVersionUID = 1L;
	LevelEditor container;
	
	public EditorMenu(LevelEditor l){
		container = l;
		
		this.setSize(new Dimension(300, 400));
		this.setBackground(Color.gray);
		this.addTab("Properties", new PropertiesTab(this));
		this.addTab("Walls", new WallTab(this));
		this.addTab("Backgrounds", new BackgroundTab(this));
		this.addTab("Entities", new EntityTab(this));
		this.addTab("Fluids", new FluidsTab(this));
		this.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] split = e.getActionCommand().split(" ");
		if(split[0].equals("save")){
			container.saveLevel(container.savename);
		}
		else if(split[0].equals("wall"))
		{
			container.currentType = LevelEditor.WALL_CODE;
			container.currentMaterial = Integer.parseInt(split[1]);
		}
		else if(split[0].equals("background"))
		{
			container.currentType = LevelEditor.BACKGROUND_CODE;
			container.currentMaterial = Integer.parseInt(split[1]);
		}
	}
}

class Tab extends JPanel{
	private static final long serialVersionUID = 1L;
	EditorMenu container;
	
	public Tab(EditorMenu em){
		container = em;
	}
	
	protected ImageIcon loadIcon(String path){
		this.removeAll();
		
		ImageIcon img = null;
		
		java.net.URL imgURL = getClass().getResource("img/" + path + ".png");
		
		if(imgURL == null){
			System.err.println("Error: image could not be found: " + path);
			return null;
		}

		try {
			BufferedImage bi = ImageIO.read(imgURL);
			img = new ImageIcon(bi.getScaledInstance(Wall.TILE_WIDTH, Wall.TILE_HEIGHT, Image.SCALE_SMOOTH));
		} catch (IOException e) { System.err.println("The specified image could not be loaded: " + path); }
		
		return img;
	}
	
}

class PropertiesTab extends Tab{
	private static final long serialVersionUID = 1L;
	
	public PropertiesTab(EditorMenu em){
		super(em);
		JButton toAdd = new JButton("Save");
		toAdd.setActionCommand("save");
		this.add(toAdd);
		toAdd.addActionListener(container);
	}
}

class WallTab extends Tab{
	private static final long serialVersionUID = 1L;
	
	public WallTab(EditorMenu em){
		super(em);
		
		JButton dirtButton = new JButton(loadIcon("dirtIcon"));
		dirtButton.setActionCommand("wall " + 0);
		this.add(dirtButton);
		dirtButton.addActionListener(container);
		
		JButton woodButton = new JButton(loadIcon("woodIcon"));
		woodButton.setActionCommand("wall " + 1);
		this.add(woodButton);
		woodButton.addActionListener(container);
		
		JButton stoneButton = new JButton(loadIcon("stoneIcon"));
		stoneButton.setActionCommand("wall " + 2);
		this.add(stoneButton);
		stoneButton.addActionListener(container);
		
		JButton eyeButton = new JButton(loadIcon("lefteyeIcon"));
		eyeButton.setActionCommand("wall " + 3);
		this.add(eyeButton);
		eyeButton.addActionListener(container);
		
		JButton platformButton = new JButton(loadIcon("platformIcon"));
		platformButton.setActionCommand("wall " + 4);
		this.add(platformButton);
		platformButton.addActionListener(container);
		
		
	}
	
}

class BackgroundTab extends Tab{
	private static final long serialVersionUID = 1L;
	
	public BackgroundTab(EditorMenu em){
		super(em);
		
		JButton dirtButton = new JButton(loadIcon("bgdirtIcon"));
		dirtButton.setActionCommand("background " + 0);
		this.add(dirtButton);
		dirtButton.addActionListener(container);
		
		JButton woodButton = new JButton(loadIcon("bgwoodIcon"));
		woodButton.setActionCommand("background " + 1);
		this.add(woodButton);
		woodButton.addActionListener(container);
		
		JButton stoneButton = new JButton(loadIcon("bgstoneIcon"));
		stoneButton.setActionCommand("background " + 2);
		this.add(stoneButton);
		stoneButton.addActionListener(container);
		
		JButton eyeButton = new JButton(loadIcon("bgeyeIcon"));
		eyeButton.setActionCommand("background " + 3);
		this.add(eyeButton);
		eyeButton.addActionListener(container);
		
	}
}

class EntityTab extends Tab{
	private static final long serialVersionUID = 1L;
	
	public EntityTab(EditorMenu em){
		super(em);
		
	}
}

class FluidsTab extends Tab{
	private static final long serialVersionUID = 1L;
	
	public FluidsTab(EditorMenu em){
		super(em);
		
	}
	
}
