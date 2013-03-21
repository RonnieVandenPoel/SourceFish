package com.sourcefish.tools.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.sourcefish.tools.SourceFishHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncChangePicture extends AsyncTask<String, Integer, Boolean> {

	Context context;
	
	public AsyncChangePicture(Context context)
	{
		this.context=context;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		String imagedata="";
		String imagelocation=arg0[0];
		
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
		
		DefaultHttpClient client = SourceFishHttpClient.getClient(accounts[0].name, am.getPassword(accounts[0]));
		HttpPost post=new HttpPost("http://projecten3.eu5.org/webservice/setProfilePicture");
		//MultipartEntity entity= new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		//File image=new File(imagelocation);
		//entity.addPart("image", new FileBody(image));
		
		//post.setEntity(entity);

		Log.i("Info","About to post!"+imagedata);
		ByteArrayEntity entity;
		try {
			entity = new ByteArrayEntity(FileUtils.readFileToByteArray(new File(imagelocation)));
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/octetstream"));
			post.setEntity(entity);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			HttpResponse resp=client.execute(post);
			Log.i("Info","Post done");
			BufferedReader br = new BufferedReader( new InputStreamReader((resp.getEntity().getContent())));
			String output = "";
			String serverResult="";
			while ((output = br.readLine()) != null) {
				serverResult+=output;
			};
			
			Log.i("Result:",""+serverResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
