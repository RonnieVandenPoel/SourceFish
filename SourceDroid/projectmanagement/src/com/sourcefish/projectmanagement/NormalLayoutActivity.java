package com.sourcefish.projectmanagement;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.Tasks;

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
				AlertDialog alert = (AlertDialog) onCreateDialog(SourceFishConfig.THEMEDIALOG);
            	alert.show();
			break;
		}
		return true;
	}
	
	public Dialog onCreateDialog(final int elementId) {
		String[] opties = {"Dark theme", "Light theme"};
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Theme");
	    builder.setItems(opties, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which)
				{
				case 0:
					SourceFishConfig.MAINTHEME = com.actionbarsherlock.R.style.Sherlock___Theme;
					break;
				case 1:	
					SourceFishConfig.MAINTHEME = com.actionbarsherlock.R.style.Sherlock___Theme_Light;
				}
				finish();
				startActivity(getIntent());
			}
		});
	    return builder.create();
	}
}
