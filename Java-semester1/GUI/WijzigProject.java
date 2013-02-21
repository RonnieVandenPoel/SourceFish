package gui;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class WijzigProject {

	private JFrame frame;
	private JTextField txtNaam;
	private JTextField txtOpdrachtgever;
	private JTextField txtDesc;
	private Project project;
	private JFrame teSluitenFrame;
	private User user;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final JFrame teSluitenFrame, final Project project) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WijzigProject window = new WijzigProject(user, teSluitenFrame, project);
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
	public WijzigProject( User user,  JFrame teSluitenFrame,  Project project) {
		this.user = user;
		this.teSluitenFrame = teSluitenFrame;
		this.project = project;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 295, 211);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel bgPanel =Methodes.setBackGroundPane(frame,false);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		
		JPanel panel = Methodes.inputPanel();
		layeredPane.setLayer(panel, 1);
		panel.setBounds(12, 13, 267, 132);
		layeredPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblProjectnaam = new JLabel("Project name:");
		lblProjectnaam.setBounds(12, 13, 109, 14);
		panel.add(lblProjectnaam);
		
		txtNaam = new JTextField();
		txtNaam.setBounds(133, 10, 124, 20);
		panel.add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblOpdrachtgever = new JLabel("Client:");
		lblOpdrachtgever.setBounds(12, 55, 109, 14);
		panel.add(lblOpdrachtgever);
		
		txtOpdrachtgever = new JTextField();
		txtOpdrachtgever.setBounds(133, 52, 124, 20);
		panel.add(txtOpdrachtgever);
		txtOpdrachtgever.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Description:");
		lblNewLabel.setBounds(12, 105, 109, 14);
		panel.add(lblNewLabel);
		
		txtDesc = new JTextField();
		txtDesc.setBounds(133, 102, 124, 20);
		panel.add(txtDesc);
		txtDesc.setColumns(10);
		
		JButton btnWijzig = new JButton("Update");
		layeredPane.setLayer(btnWijzig, 1);
		btnWijzig.setBounds(79, 157, 107, 23);
		layeredPane.add(btnWijzig);
		btnWijzig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testGegevens()) {
					stuurGegevens();
					teSluitenFrame.dispose();
					Home.main(null, user);
					frame.dispose();
				}				
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
		
		vulEntries();
		frame.getRootPane().setDefaultButton(btnWijzig);
	}
	
	public void stuurGegevens() {
		String json = "{\"pid\":\"" + project.id + "\",\"projectname\":\"" + txtNaam.getText().trim() + "\",\"client\":\"" + txtOpdrachtgever.getText().trim() + "\",\"summary\":\"" + txtDesc.getText().trim() + "\"}";
		System.out.println(json);
		if (ServerMethodes.updateProject(json, frame, teSluitenFrame, user)) {
			JFrame[] frames = {frame, teSluitenFrame};
			Methodes.Disconnect(frames, "Something went wrong, returning to login (code:WijzigProject1)");
		}
	}
	public boolean testGegevens() {
		boolean test = true;
		String klant = txtOpdrachtgever.getText().trim();		
		String naam = txtNaam.getText().trim();
		String desc = txtDesc.getText().trim();
		
		if (naam.equals("")) {
			Methodes.message("Gelieve een projectnaam in te geven.");
			return false;
		}
		if (klant.equals("")) {
			Methodes.message("Gelieve een projectnaam in te geven.");
			return false;
		}
		if (desc.equals("")) {
			Methodes.message("Gelieve een projectnaam in te geven.");
			return false;
		}
		
		
		return test;
	}
	
	private void vulEntries() {
		txtNaam.setText(project.projectName);
		txtDesc.setText(project.description);
		txtOpdrachtgever.setText(project.customer);
		
		
	}
}
