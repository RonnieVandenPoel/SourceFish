package gui;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import offlinegui.OfflineHome;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;


public class Login {	
	private JFrame frame;
	private JTextField txtNaam;
	private JPasswordField txtPass;
	
	/**
	 * Launch the application.
	 */
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
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
	public Login() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	
	private void initialize() {
		try {
		    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Exception e) {
		    e.printStackTrace();
		}	
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 514, 358);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		layeredPane.setLayer(panel, 1);
		panel.setBounds(134, 74, 258, 109);
		layeredPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNaam = new JLabel("Name:");
		lblNaam.setBounds(12, 25, 50, 16);
		panel.add(lblNaam);
		
		txtNaam = new JTextField();
		txtNaam.setBounds(121, 22, 116, 22);
		panel.add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblPasswoord = new JLabel("Password:");
		lblPasswoord.setBounds(12, 65, 93, 16);
		panel.add(lblPasswoord);
		
		txtPass = new JPasswordField();
		txtPass.setBounds(121, 62, 116, 22);
		panel.add(txtPass);
		
		JPanel buttonPanel = new JPanel();
		layeredPane.setLayer(buttonPanel, 2);
		buttonPanel.setBounds(69, 265, 353, 35);
		layeredPane.add(buttonPanel);
		
		
		JButton btnLogin = new JButton("Login");
		buttonPanel.add(btnLogin);
		
		JButton btnRegistreer = new JButton("Register");
		buttonPanel.add(btnRegistreer);
		
		JButton btnOpties = new JButton("Options");
		buttonPanel.add(btnOpties);
		
		JButton btnOffline = new JButton("Offline Mode");
		btnOffline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				offlineLogin();
			}
		});
		buttonPanel.add(btnOffline);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,true);
		layeredPane.add(bgPanel);
		btnOpties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openOpties();
			}
		});
		btnOpties.registerKeyboardAction(btnOpties.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnOpties.registerKeyboardAction(btnOpties.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnRegistreer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Methodes.testConnectie()) {
					Registreer.main(null);
				}				
			}
		});
		btnRegistreer.registerKeyboardAction(btnRegistreer.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnRegistreer.registerKeyboardAction(btnRegistreer.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(true){
						login();					
				}
			}
		});
		btnLogin.registerKeyboardAction(btnLogin.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnLogin.registerKeyboardAction(btnLogin.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		frame.getRootPane().setDefaultButton(btnLogin);
		
	}
	public void openOpties() {		
		Options.main(null);
		
	}
	@SuppressWarnings("deprecation")
	public void login() {
		String naam = txtNaam.getText().toLowerCase().trim();
		String passwoord = txtPass.getText().trim();
		
		if (naam.equals("")) {
			JOptionPane.showMessageDialog(null, "Please enter a Username.");
		return;
		}
		if (passwoord.equals("")) {
			JOptionPane.showMessageDialog(null, "Please enter a Password.");
			return;
		}
		
			if (Methodes.testConnectie()) {
				if (ServerMethodes.testLogin(naam,passwoord)) {
					User user = new User(naam,passwoord);
					Home.main(null, user);
					frame.dispose();
				}	
				else {					
					JOptionPane.showMessageDialog(null, "Username and/or password could not be found.");
					return;
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Unable to connect to server");
			}
	}	
	
	@SuppressWarnings("deprecation")
	public void offlineLogin() {
		String naam = txtNaam.getText().toLowerCase().trim();
		String passwoord = txtPass.getText().trim();
		
		if (naam.equals("")) {
			JOptionPane.showMessageDialog(null, "Please enter a Username.");
		return;
		}
		if (passwoord.equals("")) {
			JOptionPane.showMessageDialog(null, "Please enter a Password.");
			return;
		}
				if (testLoginOffline(naam,passwoord)) {
					User user = new User(naam,passwoord);
					OfflineHome.main(null, user);
					frame.dispose();
				}	
				else {					
					JOptionPane.showMessageDialog(null, "Username and/or password could not be found.");
					return;
				}
	}	
	
	private boolean testLoginOffline(String naam, String passwoord) {
		boolean check = false;
		
		User offlineUser = Synchronisatie.getUser(naam);
		if (offlineUser == null)
		{
			check = false;
			return check;
		}
		if (naam.equals(offlineUser.username) && passwoord.equals(offlineUser.password)) {
			check = true;
		}
		return check;
	}
	
	
}
