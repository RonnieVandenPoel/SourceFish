package com.sourcefish.tools.io;



import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.sourcefish.tools.SourceFishHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncDataLoad extends AsyncTask<String, Integer, Boolean> {
	private String username, password;
	private Context context;
	
	public AsyncDataLoad(String username, String password, Context context) {
		this.username = username;
		this.password = password;
		this.context = context;
	}
	@Override
	protected Boolean doInBackground(String... params) {
		DefaultHttpClient client = SourceFishHttpClient.getClient(username, password);
		
		HttpGet getRequest = new HttpGet(
				"http://projecten3.eu5.org/webservice/getData");
		try{
		HttpResponse resp = client.execute(getRequest);
		
		
		BufferedReader br  = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		
		String output="";
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		
			while ((output = br.readLine()) != null) {	
				Log.i("jsoin", output);
				System.out.println(output);
				AsyncSaveServerJSON saving = new AsyncSaveServerJSON(context);
				saving.execute(output);
				if (saving.get()) {
					Toast meg = Toast.makeText(context, "yes saving ok", Toast.LENGTH_LONG);
					meg.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		    }
		
		
		return null;
	}

}
