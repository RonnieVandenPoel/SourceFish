package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Rapportgui {

	private JFrame frame;
	private User user;
	private Project project;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private DefaultListModel<User> users;
	private JList<User> list;
	private JRadioButton rdbtnSortByName;
	private JRadioButton rdbtnSortByTime;
	/**
	 * Launch the application.
	 */
	public static void main(final Project pro, final User user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Rapportgui window = new Rapportgui(user,pro);
					window.frame.setLocationRelativeTo(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the application.
	 */
	public Rapportgui(User user, Project project) {
		this.setUser(user);
		this.setProject(project);
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "static-access" })
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 454, 397);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		project.setUsersInProject(frame, user);
		users = fillList();
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		layeredPane.setLayer(panel, 1);
		panel.setBounds(258, 88, 135, 115);
		layeredPane.add(panel);
		panel.setLayout(null);
		
		rdbtnSortByName = new JRadioButton("sort by name");
		rdbtnSortByName.setBounds(10, 9, 103, 25);
		panel.add(rdbtnSortByName);
		rdbtnSortByName.setSelected(true);
		buttonGroup.add(rdbtnSortByName);
		
		rdbtnSortByTime = new JRadioButton("sort by time");
		rdbtnSortByTime.setBounds(10, 39, 97, 25);
		panel.add(rdbtnSortByTime);
		buttonGroup.add(rdbtnSortByTime);
		
		JButton btnRapport = new JButton("Report");
		btnRapport.setBounds(20, 73, 77, 25);
		panel.add(btnRapport);
		btnRapport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String modus;
				if( rdbtnSortByName.isSelected())
					modus = "name";
				else
					modus = "chron";
				List<User> userlist= list.getSelectedValuesList();				
				if(userlist.isEmpty()){
					userlist = project.getUsers();
				}
				String[] uid = new String[userlist.size()];
				int i = 0;
				for(User u : userlist){
					uid[i]= ""+u.id;
					System.out.println(u);
					i++;
				}
				ServerMethodes.createRapport(uid, project, modus, user);
			}
		});
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,true);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		
		
		
		list = new JList<User>(users);
		layeredPane.setLayer(list, 2);
		list.setBounds(36, 40, 211, 220);
		layeredPane.add(list);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnLogin = new JMenu("Account");
		menuBar.add(mnLogin);
		
		JMenuItem mntmAfmelden = new JMenuItem("Log out");
		mntmAfmelden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame[] frames = {frame};
				Methodes.Disconnect(frames, "You have logged out succesfully.");
			}
		});
		
		JMenuItem mntmBack = new JMenuItem("Return to projects");
		mntmBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				user.rechten = 0;
				Home.main(null, user);
				frame.dispose();
			}
		});
		mnLogin.add(mntmBack);
		mnLogin.add(mntmAfmelden);
	
	}

			
			
			
			


			public void setProject(Project project){
				this.project = project;
			}
			
			public User getUser() {
				return user;
			}

			public void setUser(User user) {
				this.user = user;
			}
			public DefaultListModel<User> fillList(){
				ArrayList<User >userlist = project.getUsers();
				DefaultListModel<User> users = new DefaultListModel<User>() ;
				for(User u : userlist)
				{
					System.out.println(u);
					users.addElement(u);
				}
				return users;
				
					
			}
}
	
