package com.sourcefish.tools.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.SourceFishHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncLoginCheck extends AsyncTask<Void, Void, Boolean>{

	private String username;
	private String password;
	private SourceFishAuthenticatorActivity parent;
	private ProgressDialog dialog;
	
	public AsyncLoginCheck(String username, String password, Context ctx, SourceFishAuthenticatorActivity parent) {
		this.username = username;
		this.password = password;
		this.parent = parent;
		dialog = new ProgressDialog(parent);
	}

	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("Trying to log in...");
        this.dialog.show();
    }
	
	@Override
	protected void onPostExecute(final Boolean succes)
	{
		if(dialog.isShowing())
			dialog.dismiss();
		
		if(! succes)
		{
			parent.setError();
		}
		
		// if succesfull the user will be added, else restart login activity
		parent.logUsername();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean test = false;
		try
		{
			DefaultHttpClient client= SourceFishHttpClient.getClient(username, password);
			
			//Do not use localhost, because that would be the localhost of your phone. Use your IP!
			HttpGet httpget = new HttpGet(SourceFishConfig.getBaseURL()+"/webservice/tryLogin");
			// Execute HTTP Post Request
			HttpResponse response = client.execute(httpget);

			BufferedReader br = new BufferedReader( new InputStreamReader((response.getEntity().getContent())));
			String output = "";
			while ((output = br.readLine()) != null) {
				try {
					JSONObject message = new JSONObject(output);
					String ok = message.getString("username");
					if(ok.toLowerCase().equals(username)) {
						test = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(test)
			{
				Log.i("", "succes");
			}
			else
			{
				Log.i("", "failed");
			}
		}
		catch (ClientProtocolException e)
		{
			Log.i("Auth5000", "error "+e);
		}
		catch (IOException e)
		{
			Log.i("Auth5000", "error "+e);
		}
		catch (Exception e)
		{
			Log.i(username, "" + e);
		}
		return test;
	}
}
