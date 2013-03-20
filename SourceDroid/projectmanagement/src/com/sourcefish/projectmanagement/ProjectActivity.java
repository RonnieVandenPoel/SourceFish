package com.sourcefish.projectmanagement;

import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.tools.SourceFishConfig;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ProjectActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SourceFishConfig.MAINTHEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
	}
}
