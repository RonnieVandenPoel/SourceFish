package gui;


import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Projectgui {

	private JFrame frame;
	@SuppressWarnings("unused")
	private Date start;
	@SuppressWarnings("unused")
	private Date end;
	private Project project;
	private User user;	
	private JList<Entry> entryJlist;
	private JTextArea entrydiscfield;
	private JButton btnDelete;
	private Entry a;
	private ArrayList<Entry> entries;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final Project project) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Projectgui window = new Projectgui(user, project);
					window.frame.setLocationRelativeTo(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public String getPID() {
		return "" + this.project.id;
	}

	/**
	 * Create the application.
	 */
	public Projectgui(User user, Project project) {
		this.setUser(user);
		this.setProject(project);
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private void initialize() {
		user.getRightsFromDB(frame, project.id, user);
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 488, 669);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
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
		
		JMenuItem mntmBack = new JMenuItem("Return to projects.");
		mntmBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				user.rechten = 0;
				Home.main(null, user);
				frame.dispose();
			}
		});
		mnLogin.add(mntmBack);
		mnLogin.add(mntmAfmelden);
		
		final DefaultListModel listModel = new DefaultListModel();
		vulEntriesInListBox(listModel);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		panel.setBounds(36, 13, 406, 183);
		layeredPane.add(panel,1);
		panel.setLayout(null);
		
		JLabel lblNaam = new JLabel(project.getProjectName());
		lblNaam.setBounds(10, 13, 250, 16);
		panel.add(lblNaam);
		
		JButton btnTeam = new JButton("Team");
		btnTeam.setBounds(297, 13, 97, 25);
		panel.add(btnTeam);
		if(user.getRechten() >= 3){
			btnTeam.setEnabled(false);
		}
		
		JLabel lblDescriptie = new JLabel("Description:");
		lblDescriptie.setBounds(10, 48, 85, 14);
		panel.add(lblDescriptie);
		
		JLabel lblDescription = new JLabel(project.getDescription());
		lblDescription.setBounds(10, 66, 406, 117);
		panel.add(lblDescription);
		btnTeam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Permissions.main(null,user,project,frame);
			}
		});
		
		btnTeam.registerKeyboardAction(btnTeam.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnTeam.registerKeyboardAction(btnTeam.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,false);
		layeredPane.setLayer(bgPanel, 0);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		entryJlist = new JList(listModel);
		entryJlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		entryJlist.setLayoutOrientation(JList.VERTICAL);
		entryJlist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {		
				showEntryData(listModel, entrydiscfield, entryJlist.getSelectedIndex());
				btnDelete.setEnabled(true);
			}
		});
		entryJlist.setVisibleRowCount(-1);
		entryJlist.setBounds(12, 203, 406, 199);
		
		JScrollPane scroll = new JScrollPane(entryJlist);
		layeredPane.setLayer(scroll, 1);
		scroll.setBounds(36, 201, 406, 199);
		layeredPane.add(scroll);
		
		final JButton btnSluitEntry = new JButton("Close entry");
		layeredPane.setLayer(btnSluitEntry, 1);
		btnSluitEntry.setBounds(126, 573, 97, 23);
		layeredPane.add(btnSluitEntry);
		btnSluitEntry.setEnabled(false);
		btnSluitEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				Entry entry = null;
				for(Entry en : entries){
					if(en.end== null)
						entry = en;
				}
				NieuwAutoEntry.main(null, frame, project, user,entry);
			}
		});
		
		JButton btnNieuweEntry = new JButton("New entry");
		layeredPane.setLayer(btnNieuweEntry, 1);
		btnNieuweEntry.setBounds(23, 573, 91, 23);
		layeredPane.add(btnNieuweEntry);
		btnNieuweEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NieuwAutoEntry.main(null, frame, project, user,null);
			}
		});
		vulProjectData(lblNaam,lblDescriptie, btnSluitEntry, btnNieuweEntry);
		
		JButton btnManueleEntry = new JButton("Manual entry");
		layeredPane.setLayer(btnManueleEntry, 1);
		btnManueleEntry.setBounds(235, 573, 107, 23);
		layeredPane.add(btnManueleEntry);
		
		JLabel lblEntryData = new JLabel("Entry data:");
		layeredPane.setLayer(lblEntryData, 1);
		lblEntryData.setBounds(38, 413, 76, 14);
		layeredPane.add(lblEntryData);
		
		btnDelete = new JButton("Delete Entry");
		layeredPane.setLayer(btnDelete, 1);
		btnDelete.setBounds(354, 572, 107, 25);
		layeredPane.add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				JFrame[] frames = {frame};
				a.delete(user, frames);
				project.entries.clear();
				Projectgui.main(null, user, project);
				frame.dispose();
			}
		});
		btnDelete.setEnabled(false);
		
		entrydiscfield = new JTextArea();
		layeredPane.setLayer(entrydiscfield, 1);
		entrydiscfield.setBounds(36, 433, 406, 127);
		layeredPane.add(entrydiscfield);
		entrydiscfield.setColumns(10);
		entrydiscfield.setEditable(false);
		btnManueleEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ManueleEntry.main(null, frame, project, user);
			}
		});
	}
	
	public void emptyList(DefaultListModel<?> listModel) {
		listModel.clear();
	}
	/*
	public boolean closeEntry(JButton knop) {
		boolean result = true;
		Date nu = new Date();
		JFrame[] frames = new JFrame[1];
		frames[0] = frame;
		if (Entry.closeEntry(user, nu, frames, this.project.id)) {
			result = true;
			knop.setEnabled(false);
			project.entries.clear();
			Projectgui.main(null, user, project);
			frame.dispose();
		}
		return result;
	}
	*/
	public void vulProjectData(JLabel naam, JLabel desc, JButton sluitenKnop, JButton nieuweKnop) {
		naam.setText("Projectnaam: " + project.getProjectName());
		desc.setText(project.getDescription());
		JFrame[] frames = new JFrame[1];
		frames[0] = frame;
		if (!(Entry.checkOpenEntry(this.project.id, this.user,frames) == -1)) {
			sluitenKnop.setEnabled(true);
			nieuweKnop.setEnabled(false);
		}		
	}
	
	
	
	public void showEntryData(DefaultListModel<Entry> listModel, JTextArea area,int index) {
		a = (Entry)listModel.elementAt(index);
		area.setText(a.toDisplay());
	}
	
	public void vulEntriesInListBox(DefaultListModel<Entry> listModel) {
		this.project.getEntries(this.user, frame);
		entries = this.project.getAllEntriesInList();
		for (Entry a : entries) {			
			listModel.addElement(a);
		}
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
	
	@SuppressWarnings("unused")
	private String timestamp(boolean type){
		Date datetime = new Date();
		if(type){
			this.start = datetime; 
		}else{
			this.end = datetime;
		}
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM, new Locale ("en", "EN"));
		DateFormat date = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale ("en", "EN"));
		
		return time.format(datetime) + " " + date.format(datetime);
	}
}
