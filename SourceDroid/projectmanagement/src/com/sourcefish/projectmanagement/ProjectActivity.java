package com.sourcefish.projectmanagement;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.io.AsyncLoadServerJSON;


import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProjectActivity extends NormalLayoutActivity implements ActionBar.TabListener {
	private ArrayList<String> listItems;
	private ArrayAdapter adapter;
	private ListView list;
	private JSONArray projectArray;
	private ArrayList<JSONObject> projects;
	
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
		 
		 projects = new ArrayList<JSONObject>();
		 
		 try {
			updateList();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	private void updateList() throws JSONException {
		 AsyncLoadServerJSON task = new AsyncLoadServerJSON(getApplicationContext());
		 task.execute("");
		 String json = "";
		 listItems=new ArrayList<String>();	
		 try {
			json = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 Log.i("json", json);
		 projectArray = new JSONArray(json);
		
		 for (int i = 0; i < projectArray.length(); i++) {
			 JSONObject project = projectArray.getJSONObject(i);
			 listItems.add(project.getString("projectname"));	
			 projects.add(project);
			 } 
		 	 
		 list = (ListView)findViewById(R.id.projectListView);
		 adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
		 list.setAdapter(adapter);
		 list.setOnItemClickListener(new OnItemClickListener()
		 {		 
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int elementId,
				long arg3) {
			Log.i("positie", "" + elementId);
			JSONObject project = projects.get(elementId);
			Log.i("positie", "" + project);
		}
		  });
	}
	

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {		
		int tabInt = (Integer) tab.getTag();		
		
		switch (tabInt) {
		case 0:	
			setContentView(R.layout.projectview);
			try {
				updateList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			setContentView(R.layout.projectnew);
			break;
		case 2:
			setContentView(R.layout.projectedit);
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
