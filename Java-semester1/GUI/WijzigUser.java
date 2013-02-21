package gui;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class WijzigUser {
	private User user;
	private JFrame frame;
	private JTextField txtVoor;
	private JTextField txtNaam;
	private JTextField txtMail;
	private JPasswordField txtHuidigPass;
	private JPasswordField txtNieuwPass;
	private JPasswordField txtHerhaalNieuwPass;
	private JFrame teSluitenFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final User user, final JFrame teSluitenFrame) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WijzigUser window = new WijzigUser(user, teSluitenFrame);
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
	public WijzigUser(User user, JFrame teSluitenFrame) {
		this.setUser(user);
		this.teSluitenFrame = teSluitenFrame;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 370, 328);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		layeredPane.setLayer(panel, 1);
		panel.setBounds(12, 13, 346, 237);
		layeredPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNaam = new JLabel("Surname:");
		lblNaam.setBounds(12, 46, 46, 14);
		panel.add(lblNaam);
		layeredPane.setLayer(lblNaam, 1);
		
		
		
		JLabel lblVoornaam = new JLabel("Given Name:");
		lblVoornaam.setBounds(12, 13, 82, 14);
		panel.add(lblVoornaam);
		layeredPane.setLayer(lblVoornaam, 1);
		
		JLabel lblHerhaalNieuwPaswoord = new JLabel("re-type new Password:");
		lblHerhaalNieuwPaswoord.setBounds(12, 210, 155, 14);
		panel.add(lblHerhaalNieuwPaswoord);
		layeredPane.setLayer(lblHerhaalNieuwPaswoord, 1);
		
		JLabel lblNieuwPaswoord = new JLabel("New password:");
		lblNieuwPaswoord.setBounds(12, 165, 125, 14);
		panel.add(lblNieuwPaswoord);
		layeredPane.setLayer(lblNieuwPaswoord, 1);
		
		JLabel lblHuidigPaswoord = new JLabel("Current password:");
		lblHuidigPaswoord.setBounds(12, 124, 125, 14);
		panel.add(lblHuidigPaswoord);
		layeredPane.setLayer(lblHuidigPaswoord, 1);
		
		JLabel lblEmail = new JLabel("E-mail:");
		lblEmail.setBounds(12, 83, 46, 14);
		panel.add(lblEmail);
		layeredPane.setLayer(lblEmail, 1);
		
		txtVoor = new JTextField();
		txtVoor.setBounds(168, 10, 166, 20);
		panel.add(txtVoor);
		txtVoor.setColumns(10);
		
		txtNaam = new JTextField();
		txtNaam.setBounds(168, 43, 166, 20);
		panel.add(txtNaam);
		txtNaam.setColumns(10);
		
		txtMail = new JTextField();
		txtMail.setBounds(168, 80, 166, 20);
		panel.add(txtMail);
		txtMail.setColumns(10);
		
		txtHuidigPass = new JPasswordField();
		txtHuidigPass.setBounds(168, 121, 166, 20);
		panel.add(txtHuidigPass);
		txtHuidigPass.setColumns(10);
		
		txtNieuwPass = new JPasswordField();
		txtNieuwPass.setBounds(168, 162, 166, 20);
		panel.add(txtNieuwPass);
		txtNieuwPass.setColumns(10);
		
		txtHerhaalNieuwPass = new JPasswordField();
		txtHerhaalNieuwPass.setBounds(168, 207, 166, 20);
		panel.add(txtHerhaalNieuwPass);
		txtHerhaalNieuwPass.setColumns(10);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,false);
		bgPanel.setBounds(0, 0, frame.getWidth(),frame.getHeight());
		layeredPane.add(bgPanel);
		
		JButton btnWijzigGegevens = new JButton("Save changes");
		layeredPane.setLayer(btnWijzigGegevens, 1);
		btnWijzigGegevens.setBounds(104, 258, 136, 23);
		layeredPane.add(btnWijzigGegevens);
		btnWijzigGegevens.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if (testGegevens()) {
					if (txtNieuwPass.getText().toLowerCase().trim().equals("")) {
						updateOldPass();
						getUser().name = txtNaam.getText();
						getUser().firstname = txtVoor.getText();
						getUser().email = txtMail.getText();					
						getUser().password = txtHuidigPass.getText();
					}
					else {
						updateNewPass();
						getUser().name = txtNaam.getText();
						getUser().firstname = txtVoor.getText();
						getUser().email = txtMail.getText();					
						getUser().password = txtNieuwPass.getText();
					}
					teSluitenFrame.dispose();
					Home.main(null, user);
					frame.dispose();
				}
			}			
		});
		
		btnWijzigGegevens.registerKeyboardAction(btnWijzigGegevens.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnWijzigGegevens.registerKeyboardAction(btnWijzigGegevens.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		vulEntries();
		
		frame.getRootPane().setDefaultButton(btnWijzigGegevens);
	}
	
	
	
	private void updateOldPass() {
		String naam = txtNaam.getText();
		String voornaam = txtVoor.getText();
		String email = txtMail.getText();
		String pass= this.user.password;
		
		String json = "{\"password\":\"" + pass + "\",\"email\":\"" + email + "\",\"firstname\":\"" + voornaam + "\",\"lastname\":\"" + naam + "\"}";
		ServerMethodes.updateUser(json,user,frame,teSluitenFrame);
		
	}
	
	@SuppressWarnings("deprecation")
	private void updateNewPass() {
		
		String naam = txtNaam.getText();
		String voornaam = txtVoor.getText();
		String email = txtMail.getText();		
		String pass = txtNieuwPass.getText();
		this.user.password = txtNieuwPass.getText();
		
		String json = "{\"password\":\"" + pass + "\",\"email\":\"" + email + "\",\"firstname\":\"" + voornaam + "\",\"lastname\":\"" + naam + "\"}";
		ServerMethodes.updateUser(json,user,frame,teSluitenFrame);
		Synchronisatie.setUser(user.username, user.password);
	}
	
	
	
	@SuppressWarnings("deprecation")
	private boolean testGegevens() {
		boolean test = true;
		String naam = txtNaam.getText();
		String voornaam = txtVoor.getText();
		String email = txtMail.getText();
		String huidigPass= txtHuidigPass.getText();
		String nieuwPass = txtNieuwPass.getText();
		String herhaalNieuwPass = txtHerhaalNieuwPass.getText();
		
		//Inhoud check
		if (voornaam.equals("")) {
			JOptionPane.showMessageDialog(null, "Gelieve een voornaam in te geven.");
			return false;
		}
		if (naam.toLowerCase().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Gelieve een naam in te geven.");
			return false;
		}
		if (email.toLowerCase().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Gelieve een e-mail adres op te geven.");
			return false;
		}
		if (huidigPass.toLowerCase().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Gelieve uw huidig passwoord in te geven.");
			txtNieuwPass.setText("");
			txtHerhaalNieuwPass.setText("");
			txtHuidigPass.setText("");
			return false;
		}
		
		if (!(txtNieuwPass.getText().toLowerCase().trim().equals(""))) {
			if (nieuwPass.toLowerCase().trim().length() < 6) {
			JOptionPane.showMessageDialog(null, "Uw nieuw wachtwoord moet minstens 6 tekens bevatten.");
			txtNieuwPass.setText("");
			txtHerhaalNieuwPass.setText("");
			txtHuidigPass.setText("");
			return false;
			}
		}
		
		
		if (!nieuwPass.toLowerCase().trim().equals("")) {
			if (herhaalNieuwPass.toLowerCase().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Gelieve uw nieuw passwoord te herhalen.");
			txtNieuwPass.setText("");
			txtHerhaalNieuwPass.setText("");
			txtHuidigPass.setText("");
			return false;
			}
			else {
				//dubbele pass checker
				if (!nieuwPass.toLowerCase().trim().equals(herhaalNieuwPass.toLowerCase().trim())) {
					JOptionPane.showMessageDialog(null, "Nieuwe passwoorden zijn ongelijk, controleer beide velden");
					txtNieuwPass.setText("");
					txtHerhaalNieuwPass.setText("");
					txtHuidigPass.setText("");
					return false;
				}
			}
		}
		
		
		//speciale karakters check 1: nummers
		if (voornaam.trim().toLowerCase().matches(".*\\d.*")) {
			JOptionPane.showMessageDialog(null, "Ongeldige voornaam. (bevat nummers?)");
			return false;
		}
		if (naam.trim().toLowerCase().matches(".*\\d.*")) {
			JOptionPane.showMessageDialog(null, "Ongeldige achternaam. (bevat nummers?)");
			return false;
		}
		
		//email checker
		if (!matcher(email.toLowerCase().trim())) {
			JOptionPane.showMessageDialog(null, "Ongeldig E-mail adres");
			return false;
		}
		
		
		
		
		if (!huidigPass.toLowerCase().trim().equals(this.user.password)) {
			JOptionPane.showMessageDialog(null, "Huidig passwoord incorrect.");
			txtNieuwPass.setText("");
			txtHerhaalNieuwPass.setText("");
			txtHuidigPass.setText("");;
			return false;
		}
		
		
		return test;
	}
	
	public boolean matcher(String email){
	    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher m = p.matcher(email);
	    boolean matchFound = m.matches();
	    return matchFound;
	}
	

	private void vulEntries() {
		txtNaam.setText(user.name);
		txtVoor.setText(user.firstname);
		txtMail.setText(user.email);
		
		
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
