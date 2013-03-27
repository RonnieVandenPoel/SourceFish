package com.sourcefish.tools.io;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
