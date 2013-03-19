package com.sourcefish.tools.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;


public class AsyncSaveServerJSON extends AsyncTask<String, Integer, Boolean> {
	public static final String PREFS_NAME = "data";
	public Context context;
	
	
	public AsyncSaveServerJSON (Context context) {
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		String json = params[0];
		  
		// We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("json", json);

	      // Commit the edits! 
	      editor.commit();
		
		return true;
	}

}
