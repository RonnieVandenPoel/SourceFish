package com.sourcefish.projectmanagement;


import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.SourceFishConfig;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class MainActivity extends SherlockActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.actionbarsherlock.R.style.Sherlock___Theme); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//TODO read json file from tha webz
		if(isOnline())
		{
			// SHIT IS GOING DOWN!!!!!!!!
		}
		
		// check for First time use
		SharedPreferences prefs = getSharedPreferences(SourceFishConfig.PREFFILE, 0);
		if(prefs.getBoolean("my_first_time", true))
		{
			firstRun();
			prefs.edit().putBoolean("my_first_time", false);
		}
	}
	
	private boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
	}
	
	private void firstRun()
	{
		Intent i = new Intent();
	}
}
