package gui;

import java.awt.EventQueue;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.KeyStroke;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class Options {

	private JFrame frame;
	private JTextField ipveld;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Options window = new Options();
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
	public Options() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 285, 204);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		Preferences prefs = Preferences.userNodeForPackage(Options.class);
		final String PREF_NAME = "IP";
		// Get the value of the preference;
		// default value is returned if the preference does not exist
		String defaultValue = "projecten3.eu5.org";
		String propertyValue = prefs.get(PREF_NAME, defaultValue); // "a string"
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel inputPanel = Methodes.inputPanel();
		inputPanel.setBounds(27, 13, 220, 97);
		layeredPane.add(inputPanel);
		inputPanel.setLayout(null);
		
		ipveld = new JTextField();
		ipveld.setBounds(74, 57, 126, 22);
		inputPanel.add(ipveld);
		ipveld.setColumns(10);
		ipveld.setText(propertyValue);
		
		JLabel lblNewLabel = new JLabel("Language");
		lblNewLabel.setBounds(12, 22, 25, 16);
		inputPanel.add(lblNewLabel);
		
		JLabel lblIpdns = new JLabel("IP/DNS");
		lblIpdns.setBounds(12, 60, 50, 16);
		inputPanel.add(lblIpdns);
		
		JComboBox<?> comboBox = new JComboBox<Object>();
		comboBox.setBounds(74, 19, 126, 22);
		inputPanel.add(comboBox);
		comboBox.setEnabled(false);
		
		JPanel buttonPanel = new JPanel();
		layeredPane.setLayer(buttonPanel, 2);
		buttonPanel.setBounds(12, 123, 247, 40);
		layeredPane.add(buttonPanel);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnTestVerbinding = new JButton("Test connection");
		buttonPanel.add(btnTestVerbinding);
		
		JButton btnSaveSettings = new JButton("Save Settings");
		buttonPanel.add(btnSaveSettings);
		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}
		});
		
		btnSaveSettings.registerKeyboardAction(btnSaveSettings.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnSaveSettings.registerKeyboardAction(btnSaveSettings.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		btnTestVerbinding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Methodes.testConnectie(ipveld.getText().trim())) {
					Methodes.message("Connection OK");
				}
				else {
					Methodes.message("Connection failed");
				}				
			}
		});
		
		btnTestVerbinding.registerKeyboardAction(btnTestVerbinding.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
		btnTestVerbinding.registerKeyboardAction(btnTestVerbinding.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame,false);
		layeredPane.add(bgPanel);
		
		frame.getRootPane().setDefaultButton(btnSaveSettings);
		
	}
	
	public void saveSettings() {
		
		// Retrieve the user preference node for the package com.mycompany
		Preferences prefs = Preferences.userNodeForPackage(Options.class);

		// Preference key name
		final String PREF_NAME = "IP";

		// Set the value of the preference
		String newValue = ipveld.getText().trim();
		prefs.put(PREF_NAME, newValue);
		frame.dispose();
		
	
	}
}
