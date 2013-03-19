package com.sourcefish.tools.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class AsyncLoadServerJSON extends AsyncTask<String, Integer, String>{
	public static final String PREFS_NAME = "data";
	public Context context;
	
	
	public AsyncLoadServerJSON(Context context) {
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String json;
		
		
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		// then you use
		return prefs.getString("json", ""); //gebrbuik .get om dit resultaat te gebruiken
	    
	}

}
