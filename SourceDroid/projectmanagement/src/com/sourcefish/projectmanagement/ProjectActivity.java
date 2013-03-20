package com.sourcefish.projectmanagement;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.SourceFishConfig;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class ProjectActivity extends SherlockActivity implements ActionBar.TabListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		 getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 
		 //view tab
		 ActionBar.Tab tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("View");
		 getSupportActionBar().addTab(tab);
		 
		 //new tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("New");
		 getSupportActionBar().addTab(tab);
		 
		 //edit tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("Edit");
		 getSupportActionBar().addTab(tab);
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
