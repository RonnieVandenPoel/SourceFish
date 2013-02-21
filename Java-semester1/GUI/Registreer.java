package gui;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Registreer {

	private JFrame frame;
	private JTextField usertxt;
	private JTextField givenNametxt;
	private JTextField surNametxt;
	private JTextField mailtxt;
	private JPasswordField pwdtxt;
	private JPasswordField checkertxt;
	private JLayeredPane layeredPane;
	private JPanel bgPanel;
	private JPanel inputPanel;
	private JPanel buttonPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Registreer window = new Registreer();
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
	public Registreer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 307, 280);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		bgPanel = Methodes.setBackGroundPane(frame,false);
		layeredPane.add(bgPanel);
		
		inputPanel = Methodes.inputPanel();
		layeredPane.setLayer(inputPanel, 1);
		inputPanel.setBounds(29, 13, 254, 178);
		layeredPane.add(inputPanel);
		inputPanel.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(8, 8, 63, 16);
		inputPanel.add(lblUsername);
		
		usertxt = new JTextField();
		usertxt.setBounds(125, 5, 116, 22);
		inputPanel.add(usertxt);
		usertxt.setColumns(10);
		
		JLabel lblVoornm = new JLabel("Given name:");
		lblVoornm.setBounds(8, 35, 75, 16);
		inputPanel.add(lblVoornm);
		
		givenNametxt = new JTextField();
		givenNametxt.setBounds(125, 32, 116, 22);
		inputPanel.add(givenNametxt);
		givenNametxt.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Surname:");
		lblNewLabel.setBounds(8, 62, 74, 16);
		inputPanel.add(lblNewLabel);
		
		surNametxt = new JTextField();
		surNametxt.setBounds(125, 59, 116, 22);
		inputPanel.add(surNametxt);
		surNametxt.setColumns(10);
		
		JLabel lblEmail = new JLabel("e-mail:");
		lblEmail.setBounds(8, 91, 41, 16);
		inputPanel.add(lblEmail);
		
		mailtxt = new JTextField();
		mailtxt.setBounds(125, 88, 116, 22);
		inputPanel.add(mailtxt);
		mailtxt.setColumns(10);
		
		JLabel lblPasswoord = new JLabel("Password:");
		lblPasswoord.setBounds(8, 120, 105, 16);
		inputPanel.add(lblPasswoord);
		
		pwdtxt = new JPasswordField();
		pwdtxt.setBounds(125, 115, 116, 22);
		pwdtxt.setColumns(10);
		inputPanel.add(pwdtxt);
		
		JLabel lblRetypePasswoord = new JLabel("re-type Password:");
		lblRetypePasswoord.setBounds(8, 147, 116, 16);
		inputPanel.add(lblRetypePasswoord);
		
		checkertxt = new JPasswordField();
		checkertxt.setBounds(125, 144, 116, 22);
		inputPanel.add(checkertxt);
		
		buttonPanel = new JPanel();
		layeredPane.setLayer(buttonPanel, 1);
		buttonPanel.setBounds(92, 200, 107, 41);
		layeredPane.add(buttonPanel);
		
		JButton button = new JButton("Register");
		buttonPanel.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean emptyfield = usertxt.getText().toLowerCase().trim().equals(null) || pwdtxt.getText().trim().equals(null) || mailtxt.getText().trim().equals(null) || givenNametxt.getText().trim().equals(null)
						|| surNametxt.getText().trim().equals(null);
				if(!emptyfield){
					if(pwdtxt.getText().equals(checkertxt.getText())){
						if (pwdtxt.getText().trim().length() >= 6) {
							if(matcher(mailtxt.getText())){
								gui.User user = new gui.User(usertxt.getText().trim().toLowerCase(),pwdtxt.getText().trim(),givenNametxt.getText().trim(), surNametxt.getText().trim(),mailtxt.getText().trim());
								if(user.register()){
									frame.dispose();
								}else{
									JOptionPane.showMessageDialog(null, "Your chosen username or e-mail are already in use.");
								}
							}else{
								JOptionPane.showMessageDialog(null, "Your email adress doesnot fit any known standard.");
						} 
						}else {							
							JOptionPane.showMessageDialog(null, "Passwords need to be atleast 6 letters long.");
						}
					}else{
						JOptionPane.showMessageDialog(null, "Passwords do not match.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Not all fields have been filled out.");
				}
			}
		});
		
		button.registerKeyboardAction(button.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		button.registerKeyboardAction(button.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		frame.getRootPane().setDefaultButton(button);
	}

	public boolean matcher(String email){
	    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher m = p.matcher(email);
	    boolean matchFound = m.matches();
	    return matchFound;
	}
}
