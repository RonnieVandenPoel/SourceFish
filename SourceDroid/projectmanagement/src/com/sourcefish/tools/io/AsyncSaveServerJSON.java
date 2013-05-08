package com.sourcefish.tools.io;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sourcefish.tools.Project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


public class AsyncSaveServerJSON extends AsyncTask<String, Integer, Boolean> {
	public static final String PREFS_NAME = "data";
	public Context context;
	
	
	public AsyncSaveServerJSON (Context context) {
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		String saved;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		// then you use
		/*
		saved = prefs.getString("json", "[]");
		Log.i("lokale json", saved);
		ArrayList<JSONObject> savedproj = JSONConversion.getOfflineProjects(saved);	*/
		
		//fdffff
		String json = params[0];
		JSONArray array = new JSONArray();
		try {
			array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				obj.put("online", -1);
			}
			/*for (JSONObject projecten : savedproj) {
				array.put(projecten);
			}*/
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.i("online data", array.toString());
		// We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putString("json", array.toString());
	    //editor.remove(json);
	      // Commit the edits! 
	    editor.commit();
		
		return true;
	}

}
