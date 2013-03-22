package com.sourcefish.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncServerPosts extends AsyncTask<StringEntity, Void, Boolean>{
	
	private Context ctx = null;
	private Tasks task;
	
	public AsyncServerPosts(Context c, Tasks t) {
		ctx = c;
		task = t;
	}

	@Override
	protected Boolean doInBackground(StringEntity... params) {
		StringEntity json = params[0];
		boolean check = false;
		DefaultHttpClient client = SourceFishHttpClient.getClient(SourceFishConfig.getUserName(ctx), SourceFishConfig.getUserPassword(ctx));
		HttpPost post = null;
		if(json != null)
		{
			switch (task) {
			case NEWENTRY:
				post = new HttpPost("http://projecten3.eu5.org/webservice/newEntry");
				break;
			case NEWPROJECT:
				post = new HttpPost("http://projecten3.eu5.org/webservice/closeEntry");
				break;
			case STOPENTRY:
				post = new HttpPost("http://projecten3.eu5.org/webservice/closeEntry");
				break;
			default:
				break;
			}
		}
		try
		{
			if(post != null && json != null)
			{
				post.setEntity(json);
		    	HttpResponse response = client.execute(post);
		    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    	String line;
		    	while ((line = rd.readLine()) != null) {
		    		Log.i("logging", line);
		    		check = true;
		    	}
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
		return check;
	}
	
	
}
