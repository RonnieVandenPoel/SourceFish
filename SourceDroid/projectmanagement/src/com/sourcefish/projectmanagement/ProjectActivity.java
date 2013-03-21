package com.sourcefish.projectmanagement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.Entry;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.User;
import com.sourcefish.tools.io.AsyncLoadServerJSON;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
		 projects = new ArrayList<JSONObject>();
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
			Project chosenProject = new Project();
			Log.i("positie", "" + elementId);
			JSONObject project = projects.get(elementId);
			Log.i("positie", "" + project);
			
			//users toevoegen aan project
			ArrayList<User> users = new ArrayList<User>();
			JSONArray userarray;
			try {
				userarray = project.getJSONArray("users");
				for (int j = 0; j < userarray.length(); j++) {
					JSONObject user = userarray.getJSONObject(j);
					users.add(new User(user.getString("username"),user.getInt("rid")));
				}
				chosenProject.users = users;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//entries toevoegen
			ArrayList<Entry> entries = new ArrayList<Entry>();
			JSONArray entryarray;
			try {
				entryarray = project.getJSONArray("entries");
				for (int j = 0; j < entryarray.length(); j++) {
					JSONObject entry = entryarray.getJSONObject(j);
					User u = new User();
					u.username = entry.getString("entryowner");
										
					Timestamp start = Timestamp.valueOf(entry.getString("start"));
					
					Entry e = new Entry(start,entry.getString("notes"),u,entry.getString("trid"));
					
					if (!(entry.isNull("end"))) {						
						Timestamp end = Timestamp.valueOf(entry.getString("end"));
						e.end = end;
					}	
					entries.add(e);
				}
				chosenProject.entries = entries;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//strings van project data opslaan
			try {
				chosenProject.name = project.getString("projectname");
				chosenProject.description = project.getString("description");
				chosenProject.id = project.getInt("pid");
				chosenProject.customer = project.getString("client");	
				chosenProject.owner = project.getString("projectowner");
				
				if (!(project.isNull("end"))) {
					Timestamp projectEnd = Timestamp.valueOf(project.getString("enddate"));
					chosenProject.endDate = projectEnd;
				}				
				Timestamp projectStart = Timestamp.valueOf(project.getString("startdate"));
				chosenProject.startDate = projectStart;
				Log.i("project", chosenProject.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//start intent
			Intent i = new Intent(getApplicationContext(), EntryActivity.class);
			i.putExtra("project", chosenProject);
			startActivity(i);
			
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
