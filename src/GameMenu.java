import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;


public class GameMenu extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JButton resume_btn;
	private JButton exit_btn;
	private JButton level_editor_btn;
	
	
	
	public GameMenu()
	{
		this.setBounds(ClientWindow.WIDTH/2-150, ClientWindow.HEIGHT/2-200, 300, 500);		
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setBackground(Color.WHITE);

		exit_btn = new JButton("Exit");
		resume_btn = new JButton("Resume");
		level_editor_btn = new JButton("Level Editor");
		
		this.add(resume_btn);
		this.add(exit_btn);
		this.add(level_editor_btn);
		
		this.setVisible(false);
	}
	
	
	
	public void open(boolean b)
	{
		if(b) this.setVisible(true);
		else  this.setVisible(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().getClass().equals("JButton"));
		{
			JButton button_clicked = (JButton) e.getSource();
			
			if(button_clicked.getText().compareTo("Exit") == 0)
			{
				/* TODO: What kind of cleanup needs to be done before exiting? */
				System.out.println("Exit functionality coming soon");
				System.exit(1);
			}
			else if(button_clicked.getText().compareTo("Resume") == 0)
			{
				/* TODO: How does this menu pause the game? We need to un-pause at this point */
				System.out.println("Resume functionality coming soon");
				this.open(false);
			}
			else if(button_clicked.getText().compareTo("Level Editor") == 0)
			{
				/* TODO: Why exactly is this a menu option? Do we want to be able to edit the current level? */
				System.out.println("Level Editor functionality coming soon");
			}
		}
		
	}
}
