import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;


public class MainMenu extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
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
		
		this.revalidate();
		this.repaint();
		
		Sound.music("The_Invention_of_Color");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("testlevel")){
			ClientWindow.activeWindow.loadLevel("test");
		}
		
	}

}
