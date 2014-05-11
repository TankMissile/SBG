import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class GameMenu extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private JButton resume_btn;
	private JButton exit_btn;
	private JButton level_editor_btn;
	


	public GameMenu(){
		//this.setSize(new Dimension(300,500));
		//this.setla
		this.setBounds(ClientWindow.WIDTH/2-150, ClientWindow.HEIGHT/2-200, 300, 500);
		
		this.setBackground(Color.WHITE);
		exit_btn = new JButton("Exit");
		resume_btn = new JButton("Resume");
		level_editor_btn = new JButton("Level Editor");
		
		this.add(resume_btn);
		this.add(exit_btn);
		this.add(level_editor_btn);
		
		this.setVisible(false);
	}
	
	public void open(boolean b){
		if(b)
		{
			this.setVisible(true);
		}
		else
		{
			this.setVisible(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().getClass().equals("JButton"));
		
		
	}
}
