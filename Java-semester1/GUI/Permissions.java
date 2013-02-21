package gui;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class Permissions {

	private DefaultListModel<User> listModellinks;
	private DefaultListModel<User> listModelrechts;
	private JFrame frame;
	private User logged;
	private User target;
	private static List<User> uList = new ArrayList<User>();
	private List<User> projUserList;
	private Project project;
	private JFrame teSluitenFrame;
	private JButton promoteBtn;
	private boolean promote = true;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final Project project, final JFrame teSluitenFrame) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Permissions window = new Permissions(user,project, teSluitenFrame);
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
	public Permissions(User user,Project project, JFrame teSluitenFrame) {
		logged = user;
		this.project = project;
		this.teSluitenFrame = teSluitenFrame;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("static-access")
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 509, 466);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		listModelrechts = new DefaultListModel<User>();
		listModellinks = new DefaultListModel<User>();
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0,frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame, false);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		bgPanel.setLayout(null);
		
		final JList<User> projUsers = new JList<User>(listModelrechts);
		projUsers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				target = (User) projUsers.getSelectedValue();
				if(target.getRechten() == 2){
					promoteBtn.setText("Demote");
					promote = false;
					promoteBtn.setEnabled(true);
				}else if(target.getRechten() == 3){
					promoteBtn.setText("Promote");
					promote = true;
					promoteBtn.setEnabled(true);
				}else{
					promoteBtn.setEnabled(false);
				}
			}
		});
		projUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		projUsers.setLayoutOrientation(JList.VERTICAL);
		projUsers.setBounds(289, 44, 203, 349);
		projUsers.setBorder(BorderFactory.createLoweredBevelBorder());
		
		JScrollPane scroll = new JScrollPane(projUsers);
		scroll.setBounds(283, 45, 203, 349);
		bgPanel.add(scroll);
		final JList<User> usersList = new JList<User>(listModellinks);
		usersList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		usersList.setLayoutOrientation(JList.VERTICAL);
		usersList.setBounds(10, 44, 184, 349);
		usersList.setBorder(BorderFactory.createLoweredBevelBorder());
		
		JScrollPane scroll2 = new JScrollPane(usersList);
		scroll2.setBounds(12, 45, 184, 349);
		bgPanel.add(scroll2);
		
		promoteBtn = new JButton("Promote");
		promoteBtn.setBounds(195, 224, 88, 25);
		bgPanel.add(promoteBtn);
		promoteBtn.setEnabled(false);
		
		JButton exitBtn = new JButton("<");
		exitBtn.setBounds(204, 159, 67, 25);
		bgPanel.add(exitBtn);
		
		JButton enterBtn = new JButton(">");
		enterBtn.setBounds(204, 103, 67, 25);
		bgPanel.add(enterBtn);
		
		JLabel lblAvailableUsers = new JLabel("Available users:");
		lblAvailableUsers.setBounds(12, 13, 103, 16);
		bgPanel.add(lblAvailableUsers);
		
		JLabel lblUsersInProject = new JLabel("Users in Project:");
		lblUsersInProject.setBounds(283, 16, 116, 16);
		bgPanel.add(lblUsersInProject);
		enterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				target = (User) usersList.getSelectedValue();
				project.adduser(logged,target , frame);
				project.users.clear();
				project.setUsersInProject(frame, logged);
				Permissions.main(null, logged, project, frame);
				frame.dispose();
			}
		});
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				target =  (User) projUsers.getSelectedValue();
				project.removeUser(logged, target, frame);
				project.users.clear();
				project.setUsersInProject(frame, logged);
				Permissions.main(null, logged, project, frame);
				frame.dispose();
			}
		});
		promoteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (projUsers.getSelectedIndex() == -1) {
					Methodes.message("Please select a project user!");
				}
				else {
					target =  (User) projUsers.getSelectedValue();
					if(promote){
						logged.promoteUser(project, target, frame);
					}else{
						logged.demoteUser(project, target, frame);
					}
					project.users.clear();
					project.setUsersInProject(frame, logged);
					Permissions.main(null, logged, project, frame);
					frame.dispose();
				}				
			}
		});
		ServerMethodes.getUnenrolledUsersFromDB(frame, teSluitenFrame, logged, project);
		fillProjUserList(listModelrechts);
		fillUserList(listModellinks);
	}
	
		
	public void fillUserList(DefaultListModel<User> listModel){
		for (User u : getuList()) {			
			listModel.addElement(u);
		}
	}
	
	public void fillProjUserList(DefaultListModel<User> listModel){
		projUserList = project.getUsers();
		for (User u : projUserList) {			
			listModel.addElement(u);
		}
	}

	public static List<User> getuList() {
		return uList;
	}

	public static void setuList(List<User> uList) {
		Permissions.uList = uList;
	}
}
