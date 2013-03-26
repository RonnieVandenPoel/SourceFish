package com.sourcefish.projectmanagement;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.Entry;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.SourceFishHttpClient;
import com.sourcefish.tools.Tasks;
import com.sourcefish.tools.User;
import com.sourcefish.tools.io.AsyncDataLoad;
import com.sourcefish.tools.io.AsyncLoadServerJSON;


import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectActivity extends NormalLayoutActivity implements ActionBar.TabListener, ServerListenerInterface {
	private ArrayList<String> listItems;
	private ArrayAdapter adapter;
	private ListView list;
	private JSONArray projectArray;
	private ArrayList<JSONObject> projects;
	private String desc,name,cust;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		
		desc = "";
		cust = "";
		name= "";
		
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
		/* tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("Edit");
		 tab.setTag(2);
		 getSupportActionBar().addTab(tab);	 */
		
		 
		 try {
			updateList();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if ((Integer)getSupportActionBar().getSelectedTab().getTag() == 0) {
			try {
				updateList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 
	
	protected void onPause() {
        super.onPause();
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
			openProject(elementId);
			
			
		}
		  });
		 list.setOnItemLongClickListener(new OnItemLongClickListener() {

	            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
	                    int elementId, long id) {
	            	AlertDialog alert = (AlertDialog) onCreateDialog(elementId);
	            	alert.show();
	    			return true;
	            }
	            
	        }); 
		 
	}
	
	public void editProject(int elementId) {
		Project chosenProject = new Project();
		Log.i("positie", "" + elementId);
		JSONObject project = projects.get(elementId);
		Log.i("positie", "" + project);    	
		
		//strings van project data opslaan
		try {
			chosenProject.name = project.getString("projectname");
			chosenProject.description = project.getString("description");	    				
			chosenProject.customer = project.getString("client");	
			chosenProject.id = project.getInt("pid");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent i = new Intent(getApplicationContext(), ProjectEditActivity.class);
		i.putExtra("project", chosenProject);
		startActivity(i);
		finish();
		
	}	
	
	
	public void deleteProject(final int elementId) {
		final ProjectActivity act = this;
		
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	try {
		    			int pid = projects.get(elementId).getInt("pid");
		    			String json = "{\"pid\":\"" + pid + "\"}";
		    			StringEntity entity = new StringEntity(json);
		    			Log.i("delete test", "starten met deleten");
		    			AsyncServerPosts task = new AsyncServerPosts(getApplicationContext(), Tasks.DELETEPROJECT, act);			
		    			task.execute(entity);			
		    			Log.i("server delet repsons",task.get());
		    			AsyncLoadServerJSON.reloadData(getApplicationContext());
		    			updateList();
		    			
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (UnsupportedEncodingException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (InterruptedException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (ExecutionException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
		
		
	}
	
	public Dialog onCreateDialog(final int elementId) {
		if(elementId == SourceFishConfig.THEMEDIALOG)
		{
			return super.onCreateDialog(elementId);
		}
		else
		{
			int rechten = 3;
			try {
				rechten = projects.get(elementId).getInt("rid");
				Log.i("rechten","" +rechten);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Project");
	    
			    switch(rechten) {
			    case 1:
			    	String[] opties1 = {"Open","Edit","Delete"};
				    builder.setItems(opties1, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int which) {
				               switch (which) {
				               case 0:
				            	   openProject(elementId);
				            	   break;
				               case 1:
				            	   editProject(elementId);
				            	   break;
				               case 2:
				            	   AlertDialog alert = (AlertDialog) onDeleteCreateDialog(elementId);
				            	   alert.show();
				            	   break;
				               }	               
				           }
				    });
			    	break;
			    case 2:
			    	String[] opties2 = {"Open","Edit"};
				    builder.setItems(opties2, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int which) {
				               switch (which) {
				               case 0:
				            	   openProject(elementId);
				            	   break;
				               case 1:
				            	   editProject(elementId);
				            	   break;		              
				               }	               
				           }
				    });
			    	break;
			    case 3:
			    	String[] opties3 = {"Open"};
				    builder.setItems(opties3, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int which) {
				               switch (which) {
				               case 0:
				            	   openProject(elementId);
				            	   break;		               
				               }	               
				           }
				    });
			    	break;
			    }
			return builder.create();
		}
	}
	
	public Dialog onDeleteCreateDialog(final int elementId) {	
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Project");
	    
	   
	    	String[] opties = {"Delete","Remove From Project","Cancel"};
		    builder.setItems(opties, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int which) {
		               switch (which) {
		               case 0:
		            	   deleteProject(elementId);
		            	   break;
		               case 1:
		            	   removeProject(elementId);
		            	   break;
		               case 2:
		            	   
		            	   break;
		               }	               
		           }
		    });
		    return builder.create();
		}
	
	private void removeProject (final int elementId) {
		final ProjectActivity act = this;
		
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:	
		        	try {
		    			int pid = projects.get(elementId).getInt("pid");
		    			String json = "{\"pid\":\"" + pid + "\"}";
		    			StringEntity entity = new StringEntity(json);
		    			AsyncServerPosts task = new AsyncServerPosts(getApplicationContext(), Tasks.LEAVEPROJECT, act);
		    			task.execute(entity);			
		    			Log.i("server remove repsons",task.get());
		    			JSONObject respons = new JSONObject(task.get());
		    			if (respons.has("error")) {		    				
		    				SourceFishConfig.alert(getApplicationContext(),"You are the creator of this project,  pass the project first before leaving!");
		    			}
		    			else {
		    				SourceFishConfig.alert(getApplicationContext(), "Unsubscribed from project");
		    			}
		    			AsyncLoadServerJSON.reloadData(getApplicationContext());
		    			updateList();
		    			
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (UnsupportedEncodingException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (InterruptedException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (ExecutionException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
		
		
		
		
	}
	
	private void openProject(int elementId) {
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
			chosenProject.rechtenId = project.getInt("rid");
			
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
	
	/*private void stuff() {
		Project chosenProject = new Project();
		Log.i("positie", "" + elementId);
		JSONObject project = projects.get(elementId);
		Log.i("positie", "" + project);    	
		
		//strings van project data opslaan
		try {
			chosenProject.name = project.getString("projectname");
			chosenProject.description = project.getString("description");	    				
			chosenProject.customer = project.getString("client");		    				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start intent
		Intent i = new Intent(getApplicationContext(), EntryActivity.class);
		i.putExtra("project", chosenProject);
		startActivity(i);
	}
	*/
	
	
	public void setNewValues() {
		TextView cust = (TextView) findViewById(R.id.customerView);
		TextView desc = (TextView) findViewById(R.id.descriptionView);
		TextView name = (TextView) findViewById(R.id.projectnameView);
		
		cust.setText(this.cust);
		desc.setText(this.desc);
		name.setText(this.name);
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
			setNewValues();
			break;
		}
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
int tabInt = (Integer) tab.getTag();		
		
		switch (tabInt) {
		case 0:				
			break;
		case 1:
			EditText cust = (EditText) findViewById(R.id.customerView);
			EditText desc = (EditText) findViewById(R.id.descriptionView);
			EditText name = (EditText) findViewById(R.id.projectnameView);
			
			this.cust = cust.getText().toString();
			this.desc = desc.getText().toString();
			this.name = name.getText().toString();
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(desc.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(cust.getWindowToken(), 0);
			break;
		}
		
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	public void SendButton(View v) {
		EditText cust = (EditText) findViewById(R.id.customerView);
		EditText desc = (EditText) findViewById(R.id.descriptionView);
		EditText name = (EditText) findViewById(R.id.projectnameView);
		
		if (name.getText().toString().equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Fill in project name.", Toast.LENGTH_LONG);
			toast.show();			
		}
		else {
			String json = "{\"projectname\":\"" + name.getText().toString() + "\",\"client\":\"" + cust.getText().toString() + "\",\"summary\":\"" + desc.getText().toString() + "\"}";
			
			AsyncServerPosts task = new AsyncServerPosts(getApplicationContext(), Tasks.NEWPROJECT, this);
			
			StringEntity entity;
			try {
				entity = new StringEntity(json);
				task.execute(entity);				
				JSONObject result = new JSONObject(task.get());				
				if (result.has("pid") && !(result.isNull("pid"))) {
					this.name = "";
					this.desc = "";
					this.cust = "";
					setNewValues();
					AccountManager am = AccountManager.get(getApplicationContext());
					Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
					String user = accounts[0].name;
					String pass = am.getPassword(accounts[0]);
					AsyncDataLoad task2 = new AsyncDataLoad(user,pass,getApplicationContext());
					task2.execute("");
					task2.get();
					getSupportActionBar().selectTab(getSupportActionBar().getTabAt(0));
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void getServerResponse(String s) {
		// TODO Auto-generated method stub
		
	}
}
