package com.sourcefish.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sourcefish.projectmanagement.ServerListenerInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncServerPosts extends AsyncTask<StringEntity, Void, String>{
	
	private Context ctx = null;
	private Tasks task;
	private ProgressDialog dialog;
	private ServerListenerInterface parent;
	
	public AsyncServerPosts(Context c, Tasks t, Activity parent) {
		ctx = c;
		task = t;
		this.parent = (ServerListenerInterface) parent;
		dialog = new ProgressDialog(parent);
	}
	

	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("working...");
        this.dialog.show();
    }
	
	protected void onPostExecute(String s)
	{
		if(dialog.isShowing())
			dialog.dismiss();
		
		parent.getServerResponse(s);
	}

	@Override
	protected String doInBackground(StringEntity... params) {
		StringEntity json = params[0];
		String serverResponse = "";
		DefaultHttpClient client = SourceFishHttpClient.getClient(SourceFishConfig.getUserName(ctx), SourceFishConfig.getUserPassword(ctx));
		HttpPost post = null;
		switch (task) {
		case NEWENTRY:
			post = new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/newEntry");
			break;
		case MANUALENTRY:
			post = new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/manualEntry");
			break;
		case NEWPROJECT:
			post = new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/addProject");
			break;
		case STOPENTRY:
			post = new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/closeEntry");
			break;
		case DELETEENTRY:
			post = new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/deleteEntry");
			break;
		case UPDATEUSER:
			post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/updateUser");
			break;
		case ADDUSERTOPROJECT:
			post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/addProjectUser");
			break;
		case DELETEPROJECT:
			post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/deleteProject");
			break;
		case EDITPROJECT:
				post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/changeProject");
				break;
		case LEAVEPROJECT:
			post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/leaveProject");
			break;
		case REMOVEUSERFROMPROJECT:
			post=new HttpPost(SourceFishConfig.getBaseURL()+"/webservice/removeProjectUser");
			break;
		
		default:
			break;
		}
		
		try
		{
			if(post != null && json != null)
			{
				post.setEntity(json);
			}
		    HttpResponse response = client.execute(post);
		    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	// TODO delete this
		    	Log.i("serverresponselogging", line);
		    	serverResponse += line;
		    }
		}
		catch(Exception e)
		{
			Log.i("ERROR", e.getMessage());
		}
		finally
		{
			client.getConnectionManager().shutdown();
		}
		return serverResponse;
	}
	
	
}
