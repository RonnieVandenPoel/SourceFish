package com.sourcefish.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncGet extends AsyncTask<String, Integer, String> {

	Context context;
	
	public AsyncGet(Context context)
	{
		this.context=context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
		
		DefaultHttpClient client = SourceFishHttpClient.getClient(accounts[0].name, am.getPassword(accounts[0]));
		HttpGet get=new HttpGet(params[0]);
		String serverResponse="";
		
		try {
			HttpResponse response = client.execute(get);
		    BufferedReader rd;
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			 
			 String line;
			    while ((line = rd.readLine()) != null) {
			    	// TODO delete this
			    	Log.i("serverresponselogging", line);
			    	serverResponse += line;
			    }
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		
		return serverResponse;
	}

}
