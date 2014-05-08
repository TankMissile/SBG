import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;


public class Platform extends JPanel {
	private static final long serialVersionUID = 1L;

	public Platform(int x, int y, int w, int h, Color c){
		this.setPreferredSize(new Dimension(w,h));
		this.setVisible(true);
		this.setLocation(new Point(x,y));
		this.setBackground(c);
		this.repaint();
	}
	
	public Platform(int x, int y){
		this.setPreferredSize(new Dimension(100, 30));
		this.setVisible(true);
		this.setLocation(new Point(x,y));
		this.repaint();
		this.setBackground(new Color(0,0,0));
	}
	
}
