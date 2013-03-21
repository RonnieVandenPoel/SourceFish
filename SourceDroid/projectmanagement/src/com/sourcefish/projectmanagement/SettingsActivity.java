package com.sourcefish.projectmanagement;


import com.sourcefish.tools.io.AsyncChangePicture;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class SettingsActivity extends NormalLayoutActivity {

	private static final int SELECT_PICTURE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
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
	            new AsyncChangePicture(this).execute(imageFilePath);
	        }
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}


}
