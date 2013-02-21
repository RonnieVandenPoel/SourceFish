package gui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Synchronisatie {	
	
	static void wipeUser(String username, String pass) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		String PREF_NAME = "u: " + username;
		prefs.remove(PREF_NAME);
		PREF_NAME = "u: " + pass;
		prefs.remove(PREF_NAME);		
	}
	
	static void wipeSyncData(String username, int pid) {
		final String PREF_NAME = "u: " + username + " " + pid;
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		prefs.remove(PREF_NAME);
	}
	
	static void wipeProjectData(String username) {
		final String PREF_NAME = "p: " + username;
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		prefs.remove(PREF_NAME);
	}
	
	static void wipeEntryData(String username) {
		final String PREF_NAME = "e: " + username;
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		prefs.remove(PREF_NAME);
	}
	
	public static ArrayList<Entry> getUnsyncEntries(int pid, String username, User user) throws JSONException {		
		ArrayList<Entry> entries = new ArrayList<Entry>();
		final String PREF_NAME = "u: " + username + " "+pid;
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		String json = prefs.get(PREF_NAME, "Geen entries");
		
		
		JSONObject project = new JSONObject(json);
		
		JSONArray entryArray = project.optJSONArray("entries");
		
		for(int i = 0; i< entryArray.length(); i++){
			JSONObject entryJson = entryArray.getJSONObject(i);		
			
			String desc= entryJson.getString("notities");		
			String begin = entryJson.getString("begin");
			String eind = entryJson.getString("eind");
		
			System.out.println(eind);
		
			Date start =Timestamp.valueOf(begin);
			Date end;
			if (eind.equals("0000-00-00 00:00:00")) {
				end = null;
			}
			else {
				end =Timestamp.valueOf(eind);
			}
			
			Entry entry = new Entry(start,end,desc,user,pid);
			entry.entryid = "-1";
			entries.add(entry);
		}
		
		return entries;
	}
		
	public static void setUnsyncdEntries(ArrayList<Entry> entries, int pid, String username) throws JSONException {
		JSONArray jsonEntries = new JSONArray();
		for (Entry e : entries) {
			JSONObject o = new JSONObject();
			String notities = e.description;
			String begin = e.start.toString();
			String eind = e.end.toString();
			o.put("notities", notities);
			o.put("begin", begin);
			o.put("eind", eind);
			jsonEntries.put(o);
		}
		JSONObject project = new JSONObject();
		project.put("entries", jsonEntries);		
		final String PREF_NAME = "u: " + username +" "+ pid;
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		prefs.put(PREF_NAME, project.toString());
	}
	
	static String getLatestUser() {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "USER";	
		String defaultWaarde = "NULL";
		String waarde = prefs.get(PREF_NAME, defaultWaarde);
		return waarde;
	}
	
	static void setLatestUser(String username) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "USER";		
		prefs.put(PREF_NAME, username);		
	}
	
	static User getUser(String user) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "u: " + user;
		String defaultWaarde = "NULL";		
		String waarde = prefs.get(PREF_NAME, defaultWaarde);
		final String PREF_NAME2 = "up: " + user;
		String waarde2 = prefs.get(PREF_NAME2, defaultWaarde);
		User u;
		if (waarde.equals("NULL")){
			u = null;
		}
		else {
			u = new User(waarde,waarde2);
		}		
		return u;
	}
	
	static void setUser(String username, String pass) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "u: " + username;
		prefs.put(PREF_NAME, username);
		
		final String PREF_NAME2 = "up: " + username;
		prefs.put(PREF_NAME2, pass);
	}
	
	static void setOfflineProjects(String json,String username) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "p: " + username;
		prefs.put(PREF_NAME, json);
	}
	
	public static ArrayList<Project> getOfflineProjects(String username) throws JSONException {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "p: " + username;
		String defaultWaarde = "Geen Projecten";		
		String json = prefs.get(PREF_NAME, defaultWaarde);
		
		ArrayList<Project> projects = new ArrayList<Project>();	
		if (!(json.equals("Geen Projecten"))) {
			JSONArray array = new JSONArray(json);
				for (int i = 0; i < array.length(); i++) {						
					JSONObject project = new JSONObject(array.get(i).toString());
					System.out.println(project.getString("pid"));
					String pid = project.getString("pid");
					String projectnaam = project.getString("projectnaam");
					String opdrachtgever = project.getString("opdrachtgever");
					String begindatum = project.getString("begindatum");						
					String omschrijving = project.getString("omschrijving");						
					Project projectentry = new Project(projectnaam,opdrachtgever,begindatum,omschrijving,pid);
					projects.add(projectentry);
				}
		}
		
		return projects;
	}
	
	static void setOfflineEntries(int pid, String json, String username) {
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "e: " + pid + " " + username;
		prefs.put(PREF_NAME, json);		
	}
	
	public static ArrayList<Entry> getEntriesInProject(int pid, User u) throws JSONException {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		
		Preferences prefs = Preferences.userNodeForPackage(Synchronisatie.class);
		final String PREF_NAME = "e: " + pid + " " + u.username;
		String defaultWaarde = "-1";		
		String json = prefs.get(PREF_NAME, defaultWaarde);
		
		if (!(json.equals("-1"))) {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray array = jsonObject.optJSONArray("entries");
			for(int i = 0; i< array.length(); i++){
				JSONObject entry = (JSONObject)array.get(i);
				String description= entry.getString("notities");
				String name = entry.getString("achternaam");
				String firstname = entry.getString("voornaam");
			
				String begin = entry.getString("begin");
				String eind = entry.getString("eind");
			
				System.out.println(eind);
			
				Date start =Timestamp.valueOf(begin);
				Date end;
				if (eind.equals("0000-00-00 00:00:00")) {
					end = null;
				}
				else {
					end =Timestamp.valueOf(eind);
				}
				int uid = entry.getInt("uid");
				String trid = entry.getString("trid");
			
				entries.add(new Entry(start, end, description, u,pid,trid));
			}
		}
		
		return entries;
	}
}
