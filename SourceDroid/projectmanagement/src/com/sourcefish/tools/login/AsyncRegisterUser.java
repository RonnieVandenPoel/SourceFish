package com.sourcefish.tools.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.sourcefish.tools.SourceFishConfig;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncRegisterUser extends AsyncTask<Object, Integer, Boolean> {

	private Activity context;
	private String username;
	private String password;
	
	
	@Override
	protected Boolean doInBackground(Object... params) {
		context=(Activity) params[0];
		username=params[1].toString();
		password=params[2].toString();
		
		HttpClient client=new DefaultHttpClient();
		HttpPost post=new HttpPost(SourceFishConfig.getBaseURL()+"/register/registerUser");
		try {
			StringEntity entity=new StringEntity("{\"username\":\""+username+"\",\"password\":\""+password+"\"}");
			Log.i("jsoninput","{\"username\":\""+username+"\",\"password\":\""+password+"\"}");
			entity.setContentType("/application/json");
			post.setEntity(entity);
			
			HttpResponse resp=client.execute(post);
			BufferedReader br=new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String resultString="";
			String line = "";
		    while ((line = br.readLine()) != null) {
		        resultString+=line;
		    }
		    
		    Log.i("Resultstring",resultString);
		    JSONObject obj=new JSONObject(resultString);
		    if(obj.getString("OK") != null)
		    {
		    	return true;
		    }
		
		} catch (Exception e) {
			Log.e("errorz",e.getStackTrace().toString());
		}
		
		
		return false;
	}

	protected void onPostExecute(Boolean result)
	{
		Toast toast;
		if(result)
		{
			toast=Toast.makeText(context, "Registering succesful.", Toast.LENGTH_LONG);
			Intent i=new Intent(context,SourceFishAuthenticatorActivity.class);
			i.putExtra("user", username);
			i.putExtra("pass", password);
			context.startActivity(i);
			
			context.finish();
		}
		else
		{
			toast=Toast.makeText(context, "Registering not succesful.", Toast.LENGTH_LONG);
		}
		
		toast.show();
	}
}
