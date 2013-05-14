package com.sourcefish.projectmanagement;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.sourcefish.tools.SourceFishConfig;

public abstract class NormalLayoutActivity extends SherlockActivity  {
	
	private SharedPreferences prefs;
	private Editor e;
	
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
	
	@Override
	protected void onResume()
	{
		//test
				AccountManager am = AccountManager.get(getApplicationContext());
				Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
				if(accounts==null||accounts.length==0||accounts[0]==null)
				{
					Intent i=new Intent(getApplicationContext(),MainActivity.class);
					startActivity(i);
					this.finish();
				}
				super.onResume();
	}
	
	public Dialog onCreateDialog(final int elementId) {
		String[] opties = {"Dark theme", "Light theme"};
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Theme");
	    builder.setItems(opties, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean changed = false;
				prefs = getSharedPreferences("projectmanagement", Activity.MODE_PRIVATE);
				e = prefs.edit();
				switch(which)
				{
				case 0:
					if(SourceFishConfig.MAINTHEME != com.actionbarsherlock.R.style.Sherlock___Theme)
					{
						SourceFishConfig.MAINTHEME = com.actionbarsherlock.R.style.Sherlock___Theme;
						e.putInt("curTheme", 0);
						changed = true;
					}
					break;
				case 1:	
					if(SourceFishConfig.MAINTHEME != com.actionbarsherlock.R.style.Sherlock___Theme_Light)
					{
						SourceFishConfig.MAINTHEME = com.actionbarsherlock.R.style.Sherlock___Theme_Light;
						e.putInt("curTheme", 1);
						changed = true;
					}
					break;
				}
				if(changed)
				{
					e.commit();
					finish();
					startActivity(getIntent());
				}
			}
		});
	    return builder.create();
	}
}
