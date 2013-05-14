package com.sourcefish.tools.io;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.sourcefish.tools.SourceFishConfig;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class AsyncLoadServerJSON extends AsyncTask<String, Integer, String>{
	public static final String PREFS_NAME = "data";
	public Context context;
	
	
	public AsyncLoadServerJSON(Context context) {
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String json;
		
		/*
		SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove("json");
	    editor.commit();*/
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		// then you use
		Log.i("json", prefs.getString("json", "[]"));
		return prefs.getString("json", "[]"); //gebrbuik .get om dit resultaat te gebruiken
	    
	}
	
	static public void reloadData(Context context) {		
		AccountManager am = AccountManager.get(context);		
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");		
		String user = accounts[0].name;
		Log.i("dbug","0");
		String pass = am.getPassword(accounts[0]);
		Log.i("dbug","1");
		AsyncDataLoad task2 = new AsyncDataLoad(user,pass,context);
		task2.execute("");
		Log.i("dbug","2");
		try {			
			if (task2.get()) {
				Log.i("dbug","3");
				SourceFishConfig.alert(context, "Data from server loaded succesfully!");
			}
			else {
				SourceFishConfig.alert(context, "Error when retrieving data from server.");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
