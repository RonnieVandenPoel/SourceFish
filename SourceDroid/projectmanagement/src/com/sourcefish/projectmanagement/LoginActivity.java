package com.sourcefish.projectmanagement;

import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.SourceFishConfig;

import android.os.Bundle;
import android.view.View;

public class LoginActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void onLogInAttempt(View v)
	{
		
	}
}
