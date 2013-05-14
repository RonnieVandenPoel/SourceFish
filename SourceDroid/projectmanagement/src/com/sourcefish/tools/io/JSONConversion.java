package com.sourcefish.tools.io;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
	static public void addOfflineEntry(Context context,String notities,int offlineId,Timestamp now, Timestamp end) throws JSONException {
		JSONObject entry = new JSONObject();
		entry.put("notes", notities);
		entry.put("edit", 1);
		entry.put("start",now.toString());
		entry.put("start",end.toString());
		
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		
	}
	static public void addOnlineEntry(Context context,String notities,int projectId,Timestamp now, Timestamp end) {
		
	}
	
	static private void pushToServer(Context context, Activity activity) throws JSONException, UnsupportedEncodingException, InterruptedException, ExecutionException {
		//eerste nieuwe projecten, daarna edits, daarna entries .get checks voor async voor elke functie, vergeet niet de juiste pid op te slaan
		
		
		ArrayList<String> newEntryStrings = new ArrayList<String>();
		ArrayList<String> editStrings = new ArrayList<String>();
		
		//String json = "{\"projectname\":\"" + name.getText().toString() + "\",\"client\":\"" + cust.getText().toString() + "\",\"summary\":\"" + desc.getText().toString() + "\"}";
		String json;
		
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		for (int i =0; i < array.length(); i++) {		
			JSONObject object = array.getJSONObject(i);
			if (object.getInt("online")>=0) {
				json = "{\"projectname\":\"" + object.getString("projectname") + "\",\"client\":\"" + object.getString("client") + "\",\"summary\":\"" + object.getString("description") + "\"}";
				AsyncServerPosts task = new AsyncServerPosts(context, Tasks.NEWPROJECT, activity);
				StringEntity entity = new StringEntity(json);
				task.execute(entity);
				JSONObject result = new JSONObject(task.get());				
				if (result.has("pid") && !(result.isNull("pid"))) {
					// maak string entity met entries en slaag op in array
				}
				else {
					Log.i("syncerror", "error bij maken nieuw project");
					return;
				}
			} else if (object.has("edit")) {
				json = "{\"projectname\":\"" + object.getString("projectname") + "\",\"client\":\"" + object.getString("client") + "\",\"summary\":\"" + object.getString("description") + "\"}";
				StringEntity entity = new StringEntity(json);
				AsyncServerPosts task = new AsyncServerPosts(context, Tasks.EDITPROJECT, activity);
				task.execute(entity);
				JSONObject result = new JSONObject(task.get());	
				if (result.has("error")) {
					Log.i("errorsynceditis",result.getString("error"));
					return;
				}
			}
			
			//entries pushen die in neiuwe offline projecten zaten
		}
		
		deleteDataNaSync(context);
	}
	
	static private void deleteDataNaSync(Context context) {
		SharedPreferences settings = context.getSharedPreferences("data", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove("json");	    
	    editor.commit();
	    AsyncLoadServerJSON.reloadData(context);
	}
	
	static public boolean checkSync(Context context) throws JSONException { //DEZE FUNCTIE MOET NOG WORDEN GE UPDATE
		boolean allesIsGescynt = true;
		
		//check of er nieuwe projecten zijn
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		for (int i =0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			if (object.getInt("online") >= 0) {
				allesIsGescynt = false;
				return allesIsGescynt;
			}
			else if (object.has("edit")) {
				allesIsGescynt = false;
				return allesIsGescynt;
			}		
			/*else if () { ENTRY CHECK CODE VOOR ONLINE PROJECTEN
				
			}*/
		}
		return allesIsGescynt;
	}
	
	static public void editProject(Context context, int id, String naam, String klant, String desc) throws JSONException {
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		JSONObject obj = array.getJSONObject(id);
		obj.remove("projectname");
		obj.remove("client");
		obj.remove("description");
		
		obj.put("projectname", naam);
		obj.put("client", klant);
		obj.put("description", desc);
		
		if (obj.getInt("online")==-1) {
			obj.put("edit", 1);
		}
		
		ArrayList<JSONObject> objecten = new ArrayList<JSONObject>();
		for (int i =0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			if (i != id) {
				objecten.add(object);
			}
		}
		
		JSONArray removedArray = new JSONArray();
		for (JSONObject o : objecten) {
			removedArray.put(o);
		}
		removedArray.put(obj);
		SharedPreferences settings = context.getSharedPreferences("data", 0);
		 SharedPreferences.Editor editor = settings.edit();
		    
		    editor.putString("json", removedArray.toString());
		    //editor.remove(json);
		      // Commit the edits! 
		    editor.commit();
	}
	
	static public void deleteProject(Context context, int id) throws JSONException {
		SharedPreferences prefs = context.getSharedPreferences("data", 0);
		JSONArray array = new JSONArray(prefs.getString("json", "[]"));
		ArrayList<JSONObject> objecten = new ArrayList<JSONObject>();
		for (int i =0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.getInt("online")!=id) {				
				if (obj.getInt("online")>id) {
					int temp = obj.getInt("online")-1;
					obj.remove("online");
					obj.put("online", temp);
				}
				objecten.add(obj);
			}
		}
		JSONArray removedArray = new JSONArray();
		for (JSONObject obj : objecten) {
			removedArray.put(obj);
		}
		 SharedPreferences settings = context.getSharedPreferences("data", 0);
		 SharedPreferences.Editor editor = settings.edit();
		    
		    editor.putString("json", removedArray.toString());
		    //editor.remove(json);
		      // Commit the edits! 
		    editor.commit();
	}
	
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