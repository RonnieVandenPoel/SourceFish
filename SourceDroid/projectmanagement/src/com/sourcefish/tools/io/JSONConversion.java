package com.sourcefish.tools.io;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.Entry;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.Tasks;
import com.sourcefish.tools.User;

public class JSONConversion {
	static public void addProject(Context context,JSONObject json) throws JSONException {
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		
		array.put(json);
		SharedPreferences settings = context.getSharedPreferences("data", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putString("json", array.toString());
	   
	      // Commit the edits! 
	    editor.commit();
	}
	
	static public ArrayList<JSONObject> getOfflineProjects(String saved) {
		 ArrayList<JSONObject> savedproj = new ArrayList<JSONObject>();
		JSONArray temparray;
		try {
			temparray = new JSONArray(saved);
			for (int i = 0; i < temparray.length(); i++) {
				JSONObject obj = temparray.getJSONObject(i);
				if (obj.getInt("online")>=0) {
					savedproj.add(obj);
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return savedproj;
	}
	
	
	
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
			chosenProject.offlineId = project.getInt("online");

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
	
	/*static public void addOfflineProject(String json) {
		
	}
/*
	static public void addManualEntryToSyncList(String json, Context context) {
		add("manualentry", json, context);
	}

	static public void addDeleteEntryToSyncList(String json, Context context) {
		add("deleteentry", json, context);
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




	static public void addEditProjectToSyncList(String json, Context context) {
		add("editproject", json, context);
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

	static public ArrayList<String> getEditProjectSyncList(Context context) {
		ArrayList<String> f = get("editproject", context);
		remove("editproject", context);
		return f;
	}

	static public ArrayList<String> getManualEntrySyncList(Context context) {
		ArrayList<String> f = get("manualproject", context);
		remove("manualproject", context);
		return f;
	}	


	static public ArrayList<String> getDeleteEntrySyncList(Context context) {
		ArrayList<String> f = get("deleteentry", context);
		remove("deleteentry", context);
		return f;
	}

	public static void supermegaAwesomeSync(Activity activity)
	{
		syncSomething(activity, getManualEntrySyncList(activity), Tasks.MANUALENTRY);
		syncSomething(activity, getDeleteEntrySyncList(activity), Tasks.DELETEENTRY);
		syncSomething(activity, getEditProjectSyncList(activity), Tasks.EDITPROJECT);

	}

	private static void syncSomething(Activity a,ArrayList<String> syncList,Tasks task)
	{
		for(String syncEntry : syncList)
		{
			try
			{
				AsyncServerPosts apost=new AsyncServerPosts(a, task, a);
				apost.execute(new StringEntity(syncEntry));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
/*
	static private void remove(String prefnaam, Context context) {
		final String PREFS_NAME = "data";	
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		 SharedPreferences.Editor editor = settings.edit();
		 editor.remove(prefnaam);
		 editor.commit();
	}
*/

}