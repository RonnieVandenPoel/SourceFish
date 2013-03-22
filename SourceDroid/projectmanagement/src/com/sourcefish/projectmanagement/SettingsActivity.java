package com.sourcefish.projectmanagement;


import com.actionbarsherlock.view.MenuItem;
import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.io.AsyncChangePicture;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class SettingsActivity extends NormalLayoutActivity {

	private static final int SELECT_PICTURE = 1;
	private static boolean loading=false;
	
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
		ImageLoader loader=new ImageLoader(this);
		
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");
		
		if(!loading)
		{
			loader.DisplayImage(accounts[0].name, iv);
		}
		
		//AsyncServerPosts get=new AsyncServerPosts(this, Tasks.GETUSERDATA);
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
		
	}


}
