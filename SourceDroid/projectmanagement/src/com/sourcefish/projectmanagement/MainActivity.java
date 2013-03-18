package com.sourcefish.projectmanagement;


import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.ConnectionManager;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends SherlockActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.actionbarsherlock.R.style.Sherlock___Theme); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		
		
	}

}
