package com.sourcefish.projectmanagement;

import org.json.JSONException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class EntryActivity extends NormalLayoutActivity implements ActionBar.TabListener {

	private ArrayAdapter adapter = null;
	private Project p = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		//to pass :
		//   intent.putExtra("MyClass", obj);  

		// to retrieve object in second Activity
		try
		{
			Project p = (Project) getIntent().getSerializableExtra("Project");
		}
		catch (Exception e)
		{
			// Geen project meegegeven met de intent... Error.
			
		}
		
		//view tab
		 ActionBar.Tab tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);		 
		 tab.setText("View");
		 tab.setTag(0);
		 getSupportActionBar().addTab(tab);
		 
		 //new tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("New");
		 tab.setTag(1);
		 getSupportActionBar().addTab(tab);
		 
		 //edit tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("Edit");
		 tab.setTag(2);
		 getSupportActionBar().addTab(tab);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int tabInt = (Integer) tab.getTag();		
		
		switch (tabInt) {
		case 0:	
			setContentView(R.layout.entrylayout);
			break;
		case 1:
			setContentView(R.layout.entrylayout);
			break;
		case 2:
			setContentView(R.layout.entrylayout);
			break;
		}
		
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
