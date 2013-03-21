package com.sourcefish.projectmanagement;

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
		toolsMenu.add(0, 0, 0, "Settings");
		
		
		
		SubMenu toolsMenuOld=menu.addSubMenu("Settings");
		MenuItem toolsMenuOldItem=toolsMenuOld.getItem();
		toolsMenuOldItem.setIcon(R.drawable.settings);
		toolsMenuOldItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		toolsMenuOld.add(1,0,0,"Settings");
		
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
			case 0:
				Toast toast=Toast.makeText(getApplicationContext(), "Settings clicked", Toast.LENGTH_LONG);
				toast.show();
				
			break;
		}
		return true;
	}
}
