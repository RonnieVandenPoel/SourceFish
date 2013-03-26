package com.sourcefish.tools.io;

import java.util.concurrent.ExecutionException;

import android.accounts.Account;
import android.accounts.AccountManager;
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
	
	static public void reloadData(Context context) {
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
		String user = accounts[0].name;
		String pass = am.getPassword(accounts[0]);
		AsyncDataLoad task2 = new AsyncDataLoad(user,pass,context);
		task2.execute("");
		try {
			task2.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
