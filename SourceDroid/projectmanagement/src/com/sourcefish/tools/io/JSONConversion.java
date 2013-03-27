package com.sourcefish.tools.io;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sourcefish.tools.Entry;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.User;

public class JSONConversion {
	static public Project getFilledProject(JSONObject project) {
		Project chosenProject = new Project();		
		
		//users toevoegen aan project
		ArrayList<User> users = new ArrayList<User>();
		JSONArray userarray;
		try {
			userarray = project.getJSONArray("users");
			for (int j = 0; j < userarray.length(); j++) {
				JSONObject user = userarray.getJSONObject(j);
				users.add(new User(user.getString("username"),user.getInt("rid")));
			}
			chosenProject.users = users;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//entries toevoegen
		ArrayList<Entry> entries = new ArrayList<Entry>();
		JSONArray entryarray;
		try {
			entryarray = project.getJSONArray("entries");
			for (int j = 0; j < entryarray.length(); j++) {
				JSONObject entry = entryarray.getJSONObject(j);
				User u = new User();
				u.username = entry.getString("entryowner");
									
				Timestamp start = Timestamp.valueOf(entry.getString("start"));
				
				Entry e = new Entry(start,entry.getString("notes"),u,entry.getString("trid"));
				
				if (!(entry.isNull("end"))) {						
					Timestamp end = Timestamp.valueOf(entry.getString("end"));
					e.end = end;
				}	
				entries.add(e);
			}
			chosenProject.entries = entries;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//strings van project data opslaan
		try {
			chosenProject.name = project.getString("projectname");
			chosenProject.description = project.getString("description");
			chosenProject.id = project.getInt("pid");
			chosenProject.customer = project.getString("client");	
			chosenProject.owner = project.getString("projectowner");
			chosenProject.rechtenId = project.getInt("rid");
			
			if (!(project.isNull("end"))) {
				Timestamp projectEnd = Timestamp.valueOf(project.getString("enddate"));
				chosenProject.endDate = projectEnd;
			}				
			Timestamp projectStart = Timestamp.valueOf(project.getString("startdate"));
			chosenProject.startDate = projectStart;
			Log.i("project", chosenProject.toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chosenProject;
	}
	
	static public void addManualEntryToSyncList(String json, Context context) {
		add("manualentry", json, context);
	}
	
	static public void addDeleteEntryToSyncList(String json, Context context) {
		add("deleteentry", json, context);
	}
	
	static public void addCloseEntryToSyncList(String json, Context context) {
		add("closeentry", json, context);
	}
	
	static private void add(String prefnaam, String json, Context context) {
		final String PREFS_NAME = "data";
		JSONObject result = new JSONObject();
		ArrayList<String> entries = new ArrayList<String>();
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);		
		String load = prefs.getString(prefnaam, "{\"entries\":[]}"); //gebrbuik .get om dit resultaat te gebruiken
		try {
			JSONObject opgeslagenData = new JSONObject(load);
			JSONArray array = opgeslagenData.getJSONArray("entries");
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				entries.add(object.getString("entry"));				
			}
			entries.add(json);
			
			JSONArray newSync = new JSONArray();
			for (String e : entries) {
				JSONObject obj = new JSONObject();
				obj.put("entry", e);
				newSync.put(obj);
			}			
			result.put("entries", newSync);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		 SharedPreferences.Editor editor = settings.edit();
		    
		 editor.putString(prefnaam, result.toString());
		 //editor.remove("entriessync");

		 // Commit the edits! 
		 Log.i(prefnaam,result.toString());
		 editor.commit();
	}
	
	static public void addStartEntryToSyncList(String json, Context context) {
		add("startentry", json, context);
	}
	
	static public void addNewProjectToSyncList(String json, Context context) {
		add("newproject", json, context);
	}
	
	static public void addEditProjectToSyncList(String json, Context context) {
		add("editproject", json, context);
	}
	
	static public void addDeleteProjectToSyncList(String json, Context context) {
		add("deleteproject", json, context);
	}
	
	static private ArrayList<String> get(String prefnaam,Context context) {
		ArrayList<String> lijst = new ArrayList<String>();		
		final String PREFS_NAME = "data";			
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);		
		String load = prefs.getString(prefnaam, "{\"entries\":[]}"); //gebrbuik .get om dit resultaat te gebruiken
		JSONObject opgeslagenData;
			try {
				opgeslagenData = new JSONObject(load);
				JSONArray array = opgeslagenData.getJSONArray("entries");
				
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					lijst.add(object.getString("entry"));				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return lijst;
	}
	
	static public ArrayList<String> getDeleteProjectSyncList(String json, Context context) {
		return get("deleteproject", context);
	}
	
	static public ArrayList<String> getNewProjectSyncList(String json, Context context) {
		return get("newproject", context);
	}
	
	static public ArrayList<String> getEditProjectSyncList(String json, Context context) {
		return get("editproject", context);
	}
	
	static public ArrayList<String> getManualEntrySyncList(String json, Context context) {
		return get("manualentry", context);
	}	
	
	
	static public ArrayList<String> getStartEntrySyncList(String json, Context context) {
		return get("startentry", context);
	}
	
	static public ArrayList<String> getCloseEntrySyncList(String json, Context context) {
		return get("closeentry", context);
	}
	static public ArrayList<String> getDeleteEntrySyncList(String json, Context context) {
		return get("deleteentry", context);
	}
}
