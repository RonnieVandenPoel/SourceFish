package com.sourcefish.projectmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.sourcefish.tools.SourceFishConfig;

public abstract class NormalLayoutActivity extends SherlockActivity  {
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		SubMenu toolsMenu=menu.addSubMenu("Settings");
		MenuItem toolsMenuItem=toolsMenu.getItem();
		toolsMenuItem.setIcon(R.drawable.settings);
		toolsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		toolsMenu.add(0, 1, 0, "Settings");
		toolsMenu.add(0,2,0,"Set theme");
		
		
		
		menu.addSubMenu(1,1,0,"Settings");
		menu.addSubMenu(1,2,0,"Set theme");

		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		switch(menuItem.getItemId())
		{
			case 1:
				Intent i=new Intent(this,SettingsActivity.class);
				startActivity(i);

			break;
			
			case 2:
				//set theme
			break;
		}
		return true;
	}
}
