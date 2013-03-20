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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncRegisterUser extends AsyncTask<Object, Integer, Boolean> {

	private Context context;
	private String username;
	private String password;
	
	
	@Override
	protected Boolean doInBackground(Object... params) {
		context=(Context) params[0];
		username=params[1].toString();
		password=params[2].toString();
		
		HttpClient client=new DefaultHttpClient();
		HttpPost post=new HttpPost("http://projecten3.eu5.org/register/registerUser");
		try {
			StringEntity entity=new StringEntity("{\"username\":\""+username+"\",password\":\""+password+"\"}");
			entity.setContentType("application/json");
			post.setEntity(entity);
			
			HttpResponse resp=client.execute(post);
			BufferedReader br=new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String resultString="";
			String line = "";
		    while ((line = br.readLine()) != null) {
		        resultString+=line;
		    }
		    
		    Log.i("Resultstring",resultString);
		
		} catch (Exception e) {
			Log.e("errorz",e.getStackTrace().toString());
		}
		
		
		return null;
	}

	
}
