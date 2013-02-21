package gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class ManueleEntry {

	private JFrame frame;
	private JFrame teSluitenFrame;
	private Project project;
	private User user;
	private JTextArea textDesc;
	private Projectgui gui;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args, final JFrame teSluitenFrame, final Project project, final User user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManueleEntry window = new ManueleEntry(teSluitenFrame, project, user);
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
	public ManueleEntry(JFrame teSluitenFrame,Project project,User user) {
		initialize();
		this.user = user;		
		this.project = project;
		this.teSluitenFrame = teSluitenFrame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		Methodes.setIcon(frame);
		frame.setBounds(100, 100, 339, 246);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		MaskFormatter dateMask;
		
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.getContentPane().add(layeredPane);
		
		JPanel panel = Methodes.inputPanel();
		panel.setBounds(12, 13, 310, 172);
		layeredPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblDescriptie = new JLabel("Description:");
		lblDescriptie.setBounds(12, 80, 73, 16);
		panel.add(lblDescriptie);
		
		
		JLabel lblStartDatum = new JLabel("Start date:");
		lblStartDatum.setBounds(12, 13, 73, 14);
		panel.add(lblStartDatum);
		
		final JFormattedTextField textStart = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		textStart.setBounds(97, 10, 84, 20);
		panel.add(textStart);
		try {
			dateMask = new MaskFormatter("##/##/####");
			dateMask.install(textStart);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		final JFormattedTextField textStartTijd = new JFormattedTextField(new SimpleDateFormat("HH:mm:ss"));
		textStartTijd.setBounds(193, 10, 84, 20);
		panel.add(textStartTijd);
		try {
			dateMask = new MaskFormatter("##:##:##");
			dateMask.install(textStartTijd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JLabel lblEindDatum = new JLabel("End date:");
		lblEindDatum.setBounds(12, 40, 73, 14);
		panel.add(lblEindDatum);
		
		final JFormattedTextField textEnd = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		textEnd.setBounds(97, 37, 84, 20);
		panel.add(textEnd);
		try {
			dateMask = new MaskFormatter("##/##/####");
			dateMask.install(textEnd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		final JFormattedTextField textEndTijd = new JFormattedTextField(new SimpleDateFormat("HH:mm:ss"));
		textEndTijd.setBounds(193, 37, 84, 20);
		panel.add(textEndTijd);		
		try {
			dateMask = new MaskFormatter("##:##:##");
			dateMask.install(textEndTijd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		textDesc = new JTextArea();
		textDesc.setRows(3);
		textDesc.setBounds(12, 96, 286, 63);
		panel.add(textDesc);
		textDesc.setColumns(20);
		
		JPanel bgPanel = Methodes.setBackGroundPane(frame, false);
		bgPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		layeredPane.add(bgPanel);
		
		JButton btnEntry = new JButton("Save entry");
		layeredPane.setLayer(btnEntry, 1);
		btnEntry.setBounds(98, 188, 145, 23);
		layeredPane.add(btnEntry);
		btnEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testVelden(textStart.getText().trim(), textEnd.getText().trim(), textStartTijd.getText().trim(), textEndTijd.getText().trim());
			}
		});
		
		frame.getRootPane().setDefaultButton(btnEntry);
	}
	
	public boolean testVelden(String startDate, String endDate, String startTijd, String endTijd) {
		boolean result = false;	
		String desc = textDesc.getText().trim();
		Date start;
		Date end;
		
		if (desc.equals("")) {
			Methodes.message("Please add a description."); 
			return result;
		}
		String stringDate = startDate + " " + startTijd;
		
		try {
			start = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(stringDate);
		} catch (ParseException e) {
			Methodes.message("Incorrect date format");			
			e.printStackTrace();
			return result;
		}
		
		stringDate = endDate + " " + endTijd;
		
		try {
			end = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(stringDate);
		} catch (ParseException e) {
			Methodes.message("Incorrect date format");			
			e.printStackTrace();
			return result;
		}
		
		Entry entry = new Entry(start,end,desc,this.user,this.project.id);
		JFrame[] frames = {this.frame, this.teSluitenFrame};
		if (entry.createManual(frames)) {
			project.entries.clear();			
			teSluitenFrame.dispose();	
			project.entries.clear();
			Projectgui.main(null, user, project);
			frame.dispose();
			return true;
		}
		return result;
	}
}
