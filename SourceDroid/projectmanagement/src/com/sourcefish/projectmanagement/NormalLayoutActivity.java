package com.sourcefish.projectmanagement;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sourcefish.tools.SourceFishConfig;

public abstract class NormalLayoutActivity extends SherlockActivity  {
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add("Settings")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
