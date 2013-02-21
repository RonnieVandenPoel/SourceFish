package gui;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.json.JSONException;

public class SynchronisatieThread extends Thread {
	User user;
	ArrayList<Project> projects;
	JFrame frame;
	
	public SynchronisatieThread(User user, ArrayList<Project> projects, JFrame frame) {
		this.projects = projects;
		this.user = user;
		this.frame = frame;
		System.out.println("Synchronisatie : instantie");
	}
	
	
	@Override
	public void run() {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (Project p : projects) {
			System.out.println("Synchronisatie : project  " + p.id);
			try {
				entries = Synchronisatie.getUnsyncEntries(p.id, user.username, user);
				for (Entry e : entries) {
					System.out.println("Synchronisatie : entry  " + e.description);
					JFrame[] frames = {frame};
					e.createManual(frames);
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			Synchronisatie.wipeSyncData(user.username, p.id);
			
		}
		
		
		
	}

}
