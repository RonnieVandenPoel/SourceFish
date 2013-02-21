package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Date;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class NieuwAutoEntry {

	private JFrame frame;
	private JTextArea txtDesc;
	private JFrame teSluitenFrame;
	private Project project;
	private User user;
	private Entry entry;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final JFrame teSluitenFrame, final Project project, final User user,final Entry entry) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NieuwAutoEntry window;
					if(entry == null)
						window = new NieuwAutoEntry(teSluitenFrame, project, user);
					else
						window = new NieuwAutoEntry(teSluitenFrame,project,user,entry);
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
	public NieuwAutoEntry(JFrame teSluitenFrame, Project project, User user) {
		this.user = user;
		this.project = project;
		this.teSluitenFrame = teSluitenFrame;
		initialize();
	}

	public NieuwAutoEntry(JFrame teSluitenFrame, Project project, User user,Entry entry) {
		this.entry = entry;
		this.user = user;
		this.project = project;
		this.teSluitenFrame = teSluitenFrame;
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 335, 213);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel bgPne = Methodes.setBackGroundPane(frame, false);
		bgPne.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPne);
		bgPne.setLayout(null);
		
		JButton btnStart =null;
		if(entry == null)
			btnStart = new JButton("Start");
		else
			btnStart = new JButton("stop");
		btnStart.setBounds(114, 137, 89, 23);
		bgPne.add(btnStart);
		
		if(entry == null)
			txtDesc = new JTextArea();
		else
			txtDesc = new JTextArea(entry.getDescription());
		txtDesc.setRows(3);
		txtDesc.setBounds(12, 35, 206, 63);
		txtDesc.setColumns(20);
		
		JPanel panel = Methodes.inputPanel();
		panel.setBounds(42, 13, 234, 113);
		layeredPane.add(panel);
		layeredPane.setLayer(panel, 1);
		panel.setLayout(null);
		
		JLabel lblDescriptie = new JLabel("Description:");
		lblDescriptie.setBounds(12, 11, 100, 16);
		panel.add(lblDescriptie);
		panel.add(txtDesc);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(txtDesc.getText().trim().equals("")) {
					Methodes.message("Please add a description.");
				}
				else {
					if(entry == null)
					addEntry();
					else{
						JFrame[] frames = {frame, teSluitenFrame};
						entry.setDescription(txtDesc.getText().trim());
						entry.closeEntry(user, new Date(),frames, project.id);
					}
					teSluitenFrame.dispose();
					project.entries.clear();
					Projectgui.main(null, user, project);
					frame.dispose();
				}
			}
		});
		
		frame.getRootPane().setDefaultButton(btnStart);
	}
	
	private void addEntry() {
		JFrame[] frames = new JFrame[2];
		frames[0] = frame;
		frames[1] = teSluitenFrame;
		Entry entry;
		entry = new Entry(new Date(),txtDesc.getText().trim());
		entry.id = project.id;
		entry.create(user, frames);
	}
}
