package com.sourcefish.tools.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
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

import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.projectmanagement.R;
import com.sourcefish.tools.SourceFishHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class AsyncChangePicture extends AsyncTask<String, Integer, Boolean> {

	Activity activity;
	
	public AsyncChangePicture(Activity activity)
	{
		this.activity=activity;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		String imagedata="";
		String imagelocation=arg0[0];
		
		AccountManager am = AccountManager.get(activity);
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
			ByteArrayOutputStream stream=new ByteArrayOutputStream();
			/*BitmapFactory.decodeFile(imagelocation).compress(Bitmap.CompressFormat.PNG,
					100, stream);*/
			BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(imagelocation), null, o);
			
			
			   //The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=4;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        BitmapFactory.decodeStream(new FileInputStream(imagelocation), null, o2).compress(Bitmap.CompressFormat.PNG,
					100, stream);
			
	        
			entity = new ByteArrayEntity(stream.toByteArray());
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
			
			Log.i("Result:",""+serverResult);return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected void onPostExecute(Boolean ok)
	{
		if(ok)
		{
			ImageView iv=(ImageView) activity.findViewById(R.id.imageViewUserPicture);
			ImageLoader loader=new ImageLoader(activity);
			
			AccountManager am = AccountManager.get(activity);
			Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
			
			loader.DisplayImage(accounts[0].name, iv);
		}
	}
	
	
}
