import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GameMenu extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private JButton resume_btn;
	private JButton exit_btn;
	private JButton level_editor_btn;



	public GameMenu()
	{
		this.setBounds(ClientWindow.WIDTH/2-150, ClientWindow.HEIGHT/2-200, 300, 500);
		this.setLayout(new GridLayout(3, 1));
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setBackground(Color.WHITE);

		exit_btn = new JButton("Exit");
		resume_btn = new JButton("Resume");
		level_editor_btn = new JButton("Level Editor");

		exit_btn.addActionListener(this);
		resume_btn.addActionListener(this);
		level_editor_btn.addActionListener(this);

		this.add(resume_btn);
		this.add(exit_btn);
		this.add(level_editor_btn);

		this.setVisible(false);
	}



	public void open(boolean b) { this.setVisible(b); }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JButton button_clicked = (JButton) e.getSource();

		if(button_clicked == exit_btn)
		{
			/* TODO: What kind of cleanup needs to be done before exiting? */
			System.exit(1);
		}
		
		if(button_clicked == resume_btn)
		{
			/* TODO: This takes a while... figure out why */			
			ClientWindow cw = (ClientWindow) SwingUtilities.getAncestorOfClass(ClientWindow.class, this);
			cw.level.openGameMenu(false);
		}
		
		if(button_clicked == level_editor_btn)
		{
			/* TODO: Why exactly is this a menu option? Do we want to be able to edit the current level? */
			System.out.println("Level Editor functionality coming soon");
		}		
	}
}
