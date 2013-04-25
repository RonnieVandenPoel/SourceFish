package com.sourcefish.projectmanagement;


import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.MenuItem;
import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.tools.AsyncGet;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.Tasks;
import com.sourcefish.tools.io.AsyncChangePicture;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


public class SettingsActivity extends NormalLayoutActivity implements ServerListenerInterface {

	private static final int SELECT_PICTURE = 1;
	private static boolean loading=false;
	private ImageLoader loader;
	private String username;
	
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		if(menuItem.getItemId() == android.R.id.home)
		{
			onBackPressed();
		}
		else
		{
			super.onOptionsItemSelected(menuItem);
		}
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ImageView iv=(ImageView) findViewById(R.id.imageViewUserPicture);
		loader=new ImageLoader(this);
		
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
		username=accounts[0].name;
		if(!loading)
		{
			loader.DisplayImage(username, iv);
		}
		
		AsyncGet get=new AsyncGet(getApplicationContext());
		try {
			JSONObject json=new JSONObject(get.execute("http://projecten3.eu5.org/webservice/getUser/0").get());
			
			EditText etFirst=(EditText) findViewById(R.id.editTextSetFirstName);
			EditText etLast=(EditText) findViewById(R.id.editTextSetLastName);
			
			etFirst.setText(json.getString("voornaam"));
			etLast.setText(json.getString("achternaam"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changePicture(View view)
	{
		Intent i=new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);
		Intent chooser=Intent.createChooser(i, "Pick a profile picture:");
		startActivityForResult(chooser, SELECT_PICTURE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode == SELECT_PICTURE && data != null && data.getData() != null){
	        Uri _uri = data.getData();

	        if (_uri != null) {
	            //User had pick an image.
	            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
	            cursor.moveToFirst();

	            //Link to the image
	            final String imageFilePath = cursor.getString(0);
	            cursor.close();
	            Log.i("imagepath",imageFilePath);
	            loading=true;
	            new AsyncChangePicture(this).execute(imageFilePath);
	            
	        }
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void updateName(View view)
	{
		EditText etFirst=(EditText) findViewById(R.id.editTextSetFirstName);
		EditText etLast=(EditText) findViewById(R.id.editTextSetLastName);
		
		if(etFirst.getText().toString()!="" && etLast.getText().toString()!="")
		{
			AsyncServerPosts post=new AsyncServerPosts(getApplicationContext(), Tasks.UPDATEUSER, this);
			try {
				Log.i("debug","{'firstname':'" + etFirst.getText().toString() + "','lastname':'"
						+ etLast.getText().toString() + "'}");
				post.execute(new StringEntity("{\"firstname\":\"" + etFirst.getText().toString() + "\",\"lastname\":\""
						+ etLast.getText().toString() + "\"}"));
				Log.i("response",post.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void getServerResponse(String s) {
		// TODO Auto-generated method stub
		ImageView iv=(ImageView) findViewById(R.id.imageViewUserPicture);
		loader.DisplayImage(username, iv);
		
	}


}
