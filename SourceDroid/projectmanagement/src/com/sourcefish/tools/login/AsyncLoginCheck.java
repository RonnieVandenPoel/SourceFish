package com.sourcefish.tools.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.sourcefish.tools.SourceFishConfig;
import com.sourcefish.tools.SourceFishHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncLoginCheck extends AsyncTask<Void, Void, Boolean>{

	private String username;
	private String password;
	private Context ctx;
	
	public AsyncLoginCheck(String username, String password, Context ctx) {
		this.username = username;
		this.password = password;
		this.ctx = ctx;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean test = false;
		SharedPreferences prefs = ctx.getSharedPreferences(SourceFishConfig.PREFFILE, 0);
		SharedPreferences.Editor editor = prefs.edit();
		try
		{
			DefaultHttpClient client= SourceFishHttpClient.getClient(username, password);
			
			//Do not use localhost, because that would be the localhost of your phone. Use your IP!
			HttpGet httpget = new HttpGet("http://projecten3.eu5.org/webservice/tryLogin");
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
