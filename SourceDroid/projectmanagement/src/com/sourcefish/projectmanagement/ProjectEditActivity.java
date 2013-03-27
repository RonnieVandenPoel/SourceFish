package com.sourcefish.projectmanagement;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import org.apache.http.conn.params.ConnConnectionParamBean;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.MenuItem;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.AsyncTestConnectie;
import com.sourcefish.tools.ConnectionManager;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.Tasks;
import com.sourcefish.tools.io.AsyncDataLoad;
import com.sourcefish.tools.io.AsyncLoadServerJSON;
import com.sourcefish.tools.io.JSONConversion;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ProjectEditActivity extends NormalLayoutActivity implements ServerListenerInterface{
	private Project project;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		if(menuItem.getItemId() == android.R.id.home)
		{
			onBackPressed();
		}
		else
		{
			super.onOptionsItemSelected(menuItem);
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {		
		super.onBackPressed();
		Intent i = new Intent(getApplicationContext(), ProjectActivity.class);		
		startActivity(i);
		finish();
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_edit);	
		
		
		//delicious gestolen ronnie code
		project = (Project) getIntent().getSerializableExtra("project");
		
		EditText cust = (EditText) findViewById(R.id.editKlantView);
		EditText desc = (EditText) findViewById(R.id.editDescView);
		EditText name = (EditText) findViewById(R.id.editProjectView);
		
		
		cust.setText(project.customer);
		desc.setText(project.description);
		name.setText(project.name);
	}	

	@Override
	public void getServerResponse(String s) {
		// TODO Auto-generated method stub
		
	}
	
	public void SendButton(View v) {
		EditText cust = (EditText) findViewById(R.id.editKlantView);
		EditText desc = (EditText) findViewById(R.id.editDescView);
		EditText name = (EditText) findViewById(R.id.editProjectView);
		
		if (name.getText().toString().equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Fill in project name.", Toast.LENGTH_LONG);
			toast.show();			
		}
		else {
			String json = "{\"projectname\":\"" + name.getText().toString() + "\",\"client\":\"" + cust.getText().toString() + "\",\"summary\":\"" + desc.getText().toString() + "\",\"pid\":\"" + project.id + "\"}";
			
			AsyncTestConnectie test = new AsyncTestConnectie();
			test.execute("");
			/*boolean server = false;
			try {
				server = test.get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (server) {*/
				AsyncServerPosts task = new AsyncServerPosts(getApplicationContext(), Tasks.EDITPROJECT, this);
				
				StringEntity entity;
				try {
					entity = new StringEntity(json);
					task.execute(entity);				
					JSONObject result = new JSONObject(task.get());				
					if (result.has("error")) {
						SourceFishConfig.alert(getApplicationContext(), "Error when editing project");
					}
					else {
						SourceFishConfig.alert(getApplicationContext(), "Project edit succesful");
					}
					/*AccountManager am = AccountManager.get(getApplicationContext());
					Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
					String user = accounts[0].name;
					String pass = am.getPassword(accounts[0]);
					AsyncDataLoad task2 = new AsyncDataLoad(user,pass,getApplicationContext());
					task2.execute("");
					task2.get(); $*/
					AsyncLoadServerJSON.reloadData(getApplicationContext());
					Intent i = new Intent(getApplicationContext(), ProjectActivity.class);				
					startActivity(i);
					finish();
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
			/*	
			}
			else {
				JSONConversion.addEditProjectToSyncList(json, getApplicationContext());
				//Project
			}*/
			
			
		}
	}

}
