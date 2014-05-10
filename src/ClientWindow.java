import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;


public class ClientWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private Level level;

	private final int WIDTH = 1080;
	private final int HEIGHT = 720;

	public ClientWindow(){
		this.setResizable(false);
		this.setTitle("QVTN");
		this.setLocationByPlatform(true);
		this.setIconImage((new ImageIcon(getClass().getResource("/img/gameicon.png"))).getImage());
		this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		Configs.init();

		level = new Level();
		level.setBackground(Color.white);
		level.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		level.setLayout(null);
		level.setVisible(true);
		this.setContentPane(level);

		loadLevel("test");

		this.revalidate();
		this.pack();
	}

	//Load a game level
	public void loadLevel(String str){
		level.loadLevel(str);

		return;
	}

	public static void main(String[] args) {
		new ClientWindow();
	}

}
