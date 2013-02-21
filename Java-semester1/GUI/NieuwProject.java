package gui;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class NieuwProject {

	private JFrame frame;
	private JTextField naamField;
	private JTextField klantField;
	private JTextArea omschrijvingPane;
	private JFrame teSluitenFrame;
	private User user;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final JFrame teSluitenFrame) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NieuwProject window = new NieuwProject(user, teSluitenFrame);
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
	public NieuwProject(User user, JFrame teSluitenframe) {
		setUser(user);
		setTeSluitenFrame(teSluitenframe);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 252, 272);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.getContentPane().add(layeredPane);
		layeredPane.setLayout(null);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame, false);
		bgPanel.setBounds(0, 0, 260, 289);
		layeredPane.add(bgPanel);
		bgPanel.setLayout(null);
		
		JButton btnCreate = new JButton("Add");
		btnCreate.setBounds(71, 209, 97, 25);
		bgPanel.add(btnCreate);
		
		JPanel panel = Methodes.inputPanel();
		panel.setBounds(12, 13, 223, 183);
		bgPanel.add(panel);
		layeredPane.setLayer(panel, 1);
		panel.setLayout(null);
		
		JLabel lblProjectnaam = new JLabel("Project name:");
		lblProjectnaam.setBounds(12, 13, 81, 16);
		panel.add(lblProjectnaam);
		
		naamField = new JTextField();
		naamField.setBounds(95, 10, 116, 22);
		panel.add(naamField);
		naamField.setColumns(10);
		
		JLabel lblKlant = new JLabel("Client:");
		lblKlant.setBounds(12, 38, 56, 16);
		panel.add(lblKlant);
		
		klantField = new JTextField();
		klantField.setBounds(95, 35, 116, 22);
		panel.add(klantField);
		klantField.setColumns(10);
		
		JLabel lblOmschrijving = new JLabel("Description:");
		lblOmschrijving.setBounds(12, 67, 95, 16);
		panel.add(lblOmschrijving);
		
		omschrijvingPane = new JTextArea();
		omschrijvingPane.setBounds(12, 96, 199, 74);
		panel.add(omschrijvingPane);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(inputValidatie()){
					Project project = new Project(naamField.getText().trim(),klantField.getText().trim(), omschrijvingPane.getText().trim(),user);
					project.create(user,frame);
					teSluitenFrame.dispose();
					Home.main(null, user);
					frame.dispose();
					
				}else{
					JOptionPane.showMessageDialog(null, "Not all fields have been filled out.");
					
				}
			}
		});
		
		btnCreate.registerKeyboardAction(btnCreate.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnCreate.registerKeyboardAction(btnCreate.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		
		frame.getRootPane().setDefaultButton(btnCreate);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JFrame getTeSluitenFrame() {
		return teSluitenFrame;
	}

	public void setTeSluitenFrame(JFrame teSluitenFrame) {
		this.teSluitenFrame = teSluitenFrame;
	}
	
	public boolean inputValidatie(){
		return naamField.getText().toLowerCase().trim().equals("");
	}
}
