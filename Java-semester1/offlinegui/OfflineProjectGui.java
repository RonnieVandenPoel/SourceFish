package offlinegui;

import gui.Entry;
import gui.Methodes;
import gui.Project;
import gui.Synchronisatie;
import gui.User;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OfflineProjectGui {

	private JFrame frame;
	private Date start;
	private Date end;
	private Project project;
	private User user;	
	private JList entryJlist;
	private JTextArea entrydiscfield;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final Project project) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OfflineProjectGui window = new OfflineProjectGui(user,project);
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
	public OfflineProjectGui(User user, Project project) {
		
		this.setUser(user);
		this.setProject(project);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 437, 669);
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
				
				OfflineHome.main(null, user);
				frame.dispose();
			}
		});
		mnLogin.add(mntmBack);
		mnLogin.add(mntmAfmelden);
		
		entrydiscfield = new JTextArea();
		
		entrydiscfield.setBounds(12, 438, 406, 127);
		entrydiscfield.setEditable(false);
		frame.getContentPane().add(entrydiscfield);
		entrydiscfield.setColumns(10);
		
		final DefaultListModel listModel = new DefaultListModel();
		vulEntriesInListBox(listModel);
		vulUnsyncEntriesInListBox(listModel);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		panel.setBounds(12, 13, 406, 183);
		layeredPane.add(panel,1);
		panel.setLayout(null);
		
		JLabel lblNaam = new JLabel(project.projectName);
		lblNaam.setBounds(10, 13, 250, 16);
		panel.add(lblNaam);
		
		
		
		JLabel lblDescriptie = new JLabel("Description:");
		lblDescriptie.setBounds(10, 48, 85, 14);
		panel.add(lblDescriptie);
		
		JLabel lblDescription = new JLabel(project.getDescription());
		lblDescription.setBounds(10, 66, 406, 117);
		panel.add(lblDescription);
		
		
		
		
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
			}
		});
		entryJlist.setVisibleRowCount(-1);
		entryJlist.setBounds(12, 203, 406, 199);
		
		JScrollPane scroll = new JScrollPane(entryJlist);
		layeredPane.setLayer(scroll, 1);
		scroll.setBounds(12, 206, 406, 199);
		layeredPane.add(scroll);
		
		final JButton btnSluitEntry = new JButton("Close entry");
		layeredPane.setLayer(btnSluitEntry, 1);
		btnSluitEntry.setBounds(154, 573, 121, 23);
		btnSluitEntry.enable(false);
		layeredPane.add(btnSluitEntry);
		btnSluitEntry.setEnabled(false);
		btnSluitEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//closeEntry(btnSluitEntry); 
			}
		});
		
		JButton btnNieuweEntry = new JButton("New entry");
		layeredPane.setLayer(btnNieuweEntry, 1);
		btnNieuweEntry.setBounds(23, 573, 119, 23);
		layeredPane.add(btnNieuweEntry);
		btnNieuweEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//NieuwAutoEntry.main(null, frame, project, user);
			}
		});
		btnNieuweEntry.enable(false);
		btnNieuweEntry.setEnabled(false);
		vulProjectData(lblNaam,lblDescriptie, btnSluitEntry, btnNieuweEntry);
		
		JButton btnManueleEntry = new JButton("Manual entry");
		layeredPane.setLayer(btnManueleEntry, 1);
		btnManueleEntry.setBounds(287, 573, 110, 23);
		layeredPane.add(btnManueleEntry);
		
		JLabel lblEntryData = new JLabel("Entry data:");
		layeredPane.setLayer(lblEntryData, 1);
		
		lblEntryData.setBounds(12, 413, 76, 14);
		layeredPane.add(lblEntryData);
		btnManueleEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//ManueleEntry.main(null, frame, project, user);
				OfflineManueleEntry.main(null, frame, project, user);
			}
		});
		
		
		
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
	
	public void vulProjectData(JLabel naam, JLabel desc, JButton sluitenKnop, JButton nieuweKnop) {
		naam.setText("Projectnaam: " + project.getProjectName());
		desc.setText(project.getDescription());
		/*JFrame[] frames = new JFrame[1];
		frames[0] = frame;
		if (!(Entry.checkOpenEntry(this.project.id, this.user,frames) == -1)) {
			sluitenKnop.setEnabled(true);
			nieuweKnop.setEnabled(false);
		}		*/
	}
	
	public void showEntryData(DefaultListModel listModel, JTextArea area,int index) {
		Entry a = (Entry)listModel.elementAt(index);
		area.setText(a.toDisplay());
	}
	
	public void vulUnsyncEntriesInListBox(DefaultListModel listModel) {		
		ArrayList<Entry> entries = new ArrayList<Entry>();
		
		
		try {
			user.setFirstname(this.user.getUsername());
			user.setName("");
			entries = Synchronisatie.getUnsyncEntries(this.project.id, this.user.getUsername(), user);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Entry a : entries) {			
			listModel.addElement(a);
		}
		
	}
	
	public void vulEntriesInListBox(DefaultListModel listModel) {		
		ArrayList<Entry> entries = new ArrayList<Entry>();
		try {
			entries = Synchronisatie.getEntriesInProject(this.project.id, this.user);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Entry a : entries) {			
			listModel.addElement(a);
		}
	}
	
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
