package com.sourcefish.projectmanagement;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.ConnectionManager;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.Tasks;
import com.sourcefish.tools.io.AsyncLoadServerJSON;
import com.sourcefish.tools.io.JSONConversion;



import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
	@SuppressWarnings("rawtypes")
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
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		// Log.i("json", json);
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
		//Log.i("positie", "" + project);    	
		
		//strings van project data opslaan
		try {
			chosenProject.name = project.getString("projectname");
			chosenProject.description = project.getString("description");	    				
			chosenProject.customer = project.getString("client");	
			chosenProject.offlineId = project.getInt("online");
			Log.i("chosen project id", "" +chosenProject.offlineId);
			chosenProject.listId = elementId;
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
	
	public void deleteOfflineProject(final int elementId) {
		//final ProjectActivity act = this;
		
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	try {
						int id = projects.get(elementId).getInt("online");
						JSONConversion.deleteProject(getApplicationContext(), id);		
						AsyncLoadServerJSON.reloadData(getApplicationContext());
		    			updateList();
					} catch (JSONException e) {
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
		    			String msg = task.get();
		    			Log.i("server delet repsons",msg);
		    			JSONObject respons = new JSONObject(msg);
		    			if (!(respons.has("error"))) {
		    				SourceFishConfig.alert(getApplicationContext(), "Project deleted succesfully");
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
		    if(!(ConnectionManager.getInstance(getApplicationContext()).isOnline()) && rechten == 1)
	    	{
		    	rechten = 2;
	    	}
			    switch(rechten) {
			    case 0: //offline projecten
			    	String[] opties0 = {"Open","Edit","Delete"};
				    builder.setItems(opties0, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int which) {
				               switch (which) {
				               case 0:
				            	   openProject(elementId);
				            	   break;
				               case 1:
				            	   editProject(elementId);
				            	   break;
				               case 2:
				            	   AlertDialog alert = (AlertDialog) onOfflineDeleteCreateDialog(elementId);
				            	   alert.show();
				            	   break;
				               }	               
				           }
				    });
			    	break;
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
	
public Dialog onOfflineDeleteCreateDialog(final int elementId) {	
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Project");
	    
	   
	    	String[] opties = {"Delete","Cancel"};
		    builder.setItems(opties, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int which) {
		               switch (which) {
		               case 0:
		            	   deleteOfflineProject(elementId);
		            	   break;
		               case 1:		            	   
		            	   break;		               
		               }	               
		           }
		    });
		    return builder.create();
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
		Project chosenProject;
		Log.i("positie", "" + elementId);
		JSONObject project = projects.get(elementId);
		//Log.i("positie", "" + project);
		
		chosenProject = JSONConversion.getFilledProject(project);
		
		//start intent
		Intent i = new Intent(getApplicationContext(), EntryActivity.class);
		i.putExtra("project", chosenProject);
		startActivity(i);
		finish();
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
			
			if(ConnectionManager.getInstance(getApplicationContext()).isOnline())
	    	{
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
						/*AccountManager am = AccountManager.get(getApplicationContext());
						Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
						String user = accounts[0].name;
						String pass = am.getPassword(accounts[0]);
						AsyncDataLoad task2 = new AsyncDataLoad(user,pass,getApplicationContext());
						task2.execute("");
						task2.get();*/
						AsyncLoadServerJSON.reloadData(getApplicationContext());
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
			else {
				String saved;
				SharedPreferences prefs = getApplicationContext().getSharedPreferences("data", 0);
				// then you use				
				saved = prefs.getString("json", "[]");
				ArrayList<JSONObject> projs = JSONConversion.getOfflineProjects(saved);					
				JSONObject offlineobject = new JSONObject();
				
				try {
					offlineobject.put("projectname", name.getText().toString());
					offlineobject.put("description", desc.getText().toString());
					offlineobject.put("client", cust.getText().toString());
					offlineobject.put("online", projs.size());
					JSONArray array = new JSONArray();
					offlineobject.put("entries", array);
					offlineobject.put("users",array);
					offlineobject.put("rid", 0);
					JSONConversion.addProject(getApplicationContext(), offlineobject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.name = "";
				this.desc = "";
				this.cust = "";
				Log.i("debug","lol1");
				setNewValues();
				Log.i("debug","lol2");
				AsyncLoadServerJSON.reloadData(getApplicationContext());
				/*AsyncReloadData taak = new AsyncReloadData();
				taak.execute(getApplicationContext());
				try {
					taak.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				Log.i("debug","lol3");
				getSupportActionBar().selectTab(getSupportActionBar().getTabAt(0));
			}
		}
	}

	@Override
	public void getServerResponse(String s) {
		// TODO Auto-generated method stub
		
	}
}
