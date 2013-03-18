package com.sourcefish.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManager {
	
	Context context=null;
	static ConnectionManager currentInstance=null;
	
	private ConnectionManager(Context context)
	{
		this.context=context;
	}
	
	public static ConnectionManager getInstance(Context context)
	{
		if(currentInstance==null)
		{
			currentInstance=new ConnectionManager(context);
		}
		
		return currentInstance;
	}
	
	public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
	
	public boolean testConnection() {
		boolean test = false;
		if (!isOnline()) {
			return test;
		}
		else {
			AsyncTestConnectie asynctest = new AsyncTestConnectie();
			try {
				test = asynctest.execute("").get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return test;
	}	
}
