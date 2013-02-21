package gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Home {
	private User user;
	private JFrame frame;
	private JTextArea txtDescriptie;
	private ArrayList<Project> projecten = new ArrayList<Project>();
	private static String latestpid;
	private SynchronisatieThread sync;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user) {			
		EventQueue.invokeLater(new Runnable() {			
			public void run() {
				try {					
					Home window = new Home(user);
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
	public Home(User user) {
		this.setUser(user);
		
		initialize();
		sync = new SynchronisatieThread(user, projecten, frame);
		sync.start();
		try {
			sync.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setUserSyncData();
		
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 385, 473);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnLogin = new JMenu("Account");
		menuBar.add(mnLogin);
		
		JMenuItem mntmAfmelden = new JMenuItem("Log Off");
		mntmAfmelden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame[] frames = {frame};
				Methodes.Disconnect(frames, "You have logged out succesfully.");
			}
		});
		mnLogin.add(mntmAfmelden);
		
		JMenuItem mntmWijzigGegevens = new JMenuItem("Change Data");
		mntmWijzigGegevens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WijzigUser.main(null, user,frame);				
			}
		});
		mnLogin.add(mntmWijzigGegevens);
		frame.getContentPane().setLayout(null);
		
		final DefaultListModel<Project> listModel = new DefaultListModel<Project>();  
		
		ServerMethodes.setProjecten(user, frame, projecten);
		vullistbox(listModel);
		setAllUserData();
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel lastprojPanel = Methodes.inputPanel();
		lastprojPanel.setBounds(32, 13, 321, 47);
		layeredPane.add(lastprojPanel);
		lastprojPanel.setLayout(null);
		
		JLabel lblLaatsteProject = new JLabel("No project found");
		lblLaatsteProject.setBounds(24, 14, 186, 16);
		lastprojPanel.add(lblLaatsteProject);
		
		JButton btnOpenLaatste = new JButton("Open");
		btnOpenLaatste.setBounds(222, 10, 93, 25);
		lastprojPanel.add(btnOpenLaatste);
		btnOpenLaatste.setEnabled(false);
		btnOpenLaatste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openProject(latestpid, user);
				frame.dispose();
			}
		});
		
		btnOpenLaatste.registerKeyboardAction(btnOpenLaatste.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnOpenLaatste.registerKeyboardAction(btnOpenLaatste.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		ServerMethodes.setLatestProject(lblLaatsteProject,btnOpenLaatste, user, frame, projecten);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,false);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		
		JPanel inputPanel = Methodes.inputPanel();
		layeredPane.setLayer(inputPanel, 1);
		inputPanel.setBounds(31, 76, 321, 203);
		layeredPane.add(inputPanel);
		inputPanel.setLayout(null);
		final JList<Project> listProjecten = new JList<Project>(listModel);
		listProjecten.setBounds(195, 200, -183, -183);
	
		
		JButton btnNew = new JButton("New");
		btnNew.setBounds(220, 11, 89, 25);
		inputPanel.add(btnNew);
		
		final JButton btnOpenKeuze = new JButton("Open");
		btnOpenKeuze.setBounds(220, 49, 89, 25);
		inputPanel.add(btnOpenKeuze);
		btnOpenKeuze.setEnabled(false);
		
		
		final JButton btnWijzig = new JButton("Update");
		btnWijzig.setBounds(220, 87, 89, 25);
		inputPanel.add(btnWijzig);
		btnWijzig.setEnabled(false);
		
		final JButton btnVerwijder = new JButton("Delete");
		btnVerwijder.setBounds(220, 125, 89, 25);
		inputPanel.add(btnVerwijder);
		btnVerwijder.setEnabled(false);
		btnVerwijder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Project pro = (Project)listModel.getElementAt(listProjecten.getSelectedIndex());
				pro.deleteProject(user, frame);
			}
		});
		
		btnVerwijder.registerKeyboardAction(btnVerwijder.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnVerwijder.registerKeyboardAction(btnVerwijder.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnWijzig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wijzigProject(listModel, listProjecten.getSelectedIndex());
			}
		});
		
		btnWijzig.registerKeyboardAction(btnWijzig.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnWijzig.registerKeyboardAction(btnWijzig.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnOpenKeuze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Project pro = (Project)listModel.getElementAt(listProjecten.getSelectedIndex());
				openProject(pro.getID(), user);
				frame.dispose();
			}
		});
		
		btnOpenKeuze.registerKeyboardAction(btnOpenKeuze.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnOpenKeuze.registerKeyboardAction(btnOpenKeuze.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NieuwProject.main(null,user,frame);
			}
		});
		
		btnNew.registerKeyboardAction(btnNew.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnNew.registerKeyboardAction(btnNew.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		final JButton btnRapport = new JButton("Report");
		btnRapport.setBounds(220, 161, 89, 25);
		inputPanel.add(btnRapport);
		btnRapport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Rapportgui.main((Project)listProjecten.getSelectedValue(), user);
				frame.dispose();
			}
		});
		btnRapport.setEnabled(false);
		
		btnRapport.registerKeyboardAction(btnRapport.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnRapport.registerKeyboardAction(btnRapport.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		
		listProjecten.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {				
				showProjectData(listProjecten.getSelectedIndex());
				btnRapport.setEnabled(true);
				btnOpenKeuze.setEnabled(true);
				btnWijzig.setEnabled(true);	
				btnVerwijder.setEnabled(true);
				frame.getRootPane().setDefaultButton(btnOpenKeuze);
				}
		});
		
		JScrollPane scroll = new JScrollPane(listProjecten);
		scroll.setBounds(12, 13, 196, 175); 
		inputPanel.add(scroll);
		
		
		
		txtDescriptie = new JTextArea();
		layeredPane.setLayer(txtDescriptie, 1);
		txtDescriptie.setBounds(31, 292, 321, 99);
		layeredPane.add(txtDescriptie);
		txtDescriptie.setEditable(false);
		txtDescriptie.setColumns(10);
		txtDescriptie.setBorder(BorderFactory.createLoweredBevelBorder());
		
		
		
		
		
	}
	
	private void setUserSyncData() {
		Synchronisatie.setLatestUser(user.username);
		Synchronisatie.setUser(user.username, user.password);
	}
	
	private void wijzigProject(DefaultListModel<Project> listModel, int index) {
		Project proj = (Project) listModel.getElementAt(index);
		WijzigProject.main(null, user, frame, proj);		
	}
	
	private void openProject(String id, User user) {
		Project project = new Project(Integer.parseInt(id), user,frame);		
		Projectgui.main(null, user, project);
		
	}
	
	
	
	
	
	private void setAllUserData() {	
		this.user.fillUserData(frame);		
	}

	public void vullistbox(DefaultListModel<Project> listProjecten ) {
		for (Project entry : projecten) {
			listProjecten.addElement(entry);			
		}
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	public void showProjectData(int id) {
		txtDescriptie.setText("");
		Project entry = projecten.get(id);
		String opdrachtgever = entry.getCustomer();
		String desc = entry.getDescription();
		String datum = entry.getStartDate().toString();
		String naam = entry.getProjectName();
		
		String antwoord;
		
		antwoord = "Name: " + naam + " Start date: " + datum + "\n";
		antwoord += "Description: " + desc + "\n";
		antwoord += "Client: " + opdrachtgever;
		
		txtDescriptie.setText(antwoord);
	}

	public static void setLatestpid(String pid) {
		// TODO Auto-generated method stub
		latestpid = pid;
	}
}
