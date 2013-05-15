package com.sourcefish.projectmanagement;

import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;

public class SpecificEntryActivity extends NormalLayoutActivity {
	
	//private Entry e = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_entry);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		if(menuItem.getItemId() == android.R.id.home)
		{
			finish();
		}
		else
		{
			super.onOptionsItemSelected(menuItem);
		}
		return false;
	}

}
