package offlinegui;

import gui.Methodes;
import gui.NieuwProject;
import gui.Project;
import gui.Rapportgui;
import gui.Synchronisatie;
import gui.User;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OfflineHome {
	private User user;	
	private JTextArea txtDescriptie;
	private ArrayList<Project> projecten = new ArrayList<Project>();
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args,final User user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OfflineHome window = new OfflineHome(user);
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
	public OfflineHome(User user) {
		this.setUser(user);
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 385, 473);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnLogin = new JMenu("Account");
		menuBar.add(mnLogin);
		
		JMenuItem mntmAfmelden = new JMenuItem("Afmelden");
		mntmAfmelden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame[] frames = {frame};
				Methodes.Disconnect(frames, "U bent succesvol uitgelogt");
			}
		});
		mnLogin.add(mntmAfmelden);
		
		
		frame.getContentPane().setLayout(null);
		
		final DefaultListModel listModel = new DefaultListModel();  
		
		
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
	
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,false);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		
		JPanel inputPanel = Methodes.inputPanel();
		layeredPane.setLayer(inputPanel, 1);
		inputPanel.setBounds(31, 76, 321, 203);
		layeredPane.add(inputPanel);
		inputPanel.setLayout(null);
		final JList listProjecten = new JList(listModel);
		listProjecten.setBounds(195, 200, -183, -183);
	
		
		JButton btnNew = new JButton("New");
		btnNew.enable(false);
		btnNew.setBounds(220, 11, 89, 25);
		inputPanel.add(btnNew);
		btnNew.setEnabled(false);
		
		final JButton btnOpenKeuze = new JButton("Open");
		btnOpenKeuze.setBounds(220, 49, 89, 25);
		inputPanel.add(btnOpenKeuze);
		btnOpenKeuze.setEnabled(false);
		
		
		final JButton btnWijzig = new JButton("Wijzig");
		btnWijzig.setBounds(220, 87, 89, 25);
		inputPanel.add(btnWijzig);
		btnWijzig.setEnabled(false);
		
		final JButton btnVerwijder = new JButton("Verwijder");
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
		
		final JButton btnRapport = new JButton("Rapport");
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
				//btnRapport.setEnabled(true);
				btnOpenKeuze.setEnabled(true);
				//btnWijzig.setEnabled(true);	
				//btnVerwijder.setEnabled(true);
				frame.getRootPane().setDefaultButton(btnOpenKeuze);
				}
		});
		
		JScrollPane scroll = new JScrollPane(listProjecten);
		scroll.setBounds(12, 13, 196, 175); 
		inputPanel.add(scroll);
		
		btnOpenKeuze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Project pro = (Project)listModel.getElementAt(listProjecten.getSelectedIndex());
				openProject(pro, user);
				frame.dispose();
			}
		});
		
		
		
		txtDescriptie = new JTextArea();
		layeredPane.setLayer(txtDescriptie, 1);
		txtDescriptie.setBounds(31, 292, 321, 99);
		layeredPane.add(txtDescriptie);
		txtDescriptie.setEditable(false);
		txtDescriptie.setColumns(10);
		txtDescriptie.setBorder(BorderFactory.createLoweredBevelBorder());
		
		setProjecten();
		vullistbox(listModel);
	}
	
	public void showProjectData(int id) {
		txtDescriptie.setText("");
		Project entry = projecten.get(id);
		String opdrachtgever = entry.getCustomer();
		String desc = entry.getDescription();
		String datum = entry.getStartDate().toString();
		String naam = entry.getProjectName();
		
		String antwoord;
		
		antwoord = "Naam: " + naam + " Startdatum: " + datum + "\n";
		antwoord += "Omschrijving: " + desc + "\n";
		antwoord += "Opdrachtgever: " + opdrachtgever;
		
		txtDescriptie.setText(antwoord);
	}
	
	public void vullistbox(DefaultListModel listProjecten ) {
		for (Project entry : projecten) {
			listProjecten.addElement(entry);			
		}
	}
	
	private void openProject(Project pro, User user) {
		Project project = pro;
		project.setUser(user);
		//Projectgui.main(null, user, project);
		OfflineProjectGui.main(null, user, project);
		
		
	}
	
	public void setProjecten() {		
		try {
			projecten = Synchronisatie.getOfflineProjects(this.user.getUsername());
		} catch (Exception e) {
			JFrame[] frames = {this.frame};
			Methodes.Disconnect(frames, "Er zijn geen offline projecten gevonden voor deze gebruiker!");
			e.printStackTrace();
		}
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
