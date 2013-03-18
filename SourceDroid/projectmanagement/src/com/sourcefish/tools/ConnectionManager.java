package com.sourcefish.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		
		
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
				"http://projecten3.eu5.org/register/tryConnect");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();	
			return false;
		}
		//System.out.println(resp);
		
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		try {
			while ((output = br.readLine()) != null) {
				try {
					JSONObject message = new JSONObject(output);
					String ok = message.getString("msg");
					if(ok.equals("OK")) {
						test = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(output);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		   }
		return test;
	}
}
