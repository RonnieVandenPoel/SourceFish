package com.sourcefish.projectmanagement;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.sourcefish.tools.Entry;
import com.sourcefish.tools.Project;
import com.sourcefish.tools.User;

public class EntryActivity extends NormalLayoutActivity implements ActionBar.TabListener {

	private ArrayAdapter adapter = null;
	private Project p = null;
	private Entry openEntry = null;
	private ListView list = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openentrylayout);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 
		//to pass :
		//   intent.putExtra("MyClass", obj);  

		// to retrieve object in second Activity
		p = (Project) getIntent().getSerializableExtra("project");
		
		if(p == null)
		{
			// Geen project meegegeven met de intent... Error.
			Toast t = Toast.makeText(getApplicationContext(), "Geen project kunnen laden.", Toast.LENGTH_LONG);
			t.show();
		
			finish();
		}
		
		//view tab
		 ActionBar.Tab tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);		 
		 tab.setText("Open Entry");
		 tab.setTag(0);
		 getSupportActionBar().addTab(tab);
		 
		 //new tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("Closed Entries");
		 tab.setTag(1);
		 getSupportActionBar().addTab(tab);
		 
		 //edit tab
		 tab = getSupportActionBar().newTab();
		 tab.setTabListener(this);
		 tab.setText("New Entry");
		 tab.setTag(2);
		 getSupportActionBar().addTab(tab);
		 
		 getOpenEntry();
		 setDescription();
	}
	
	private void getOpenEntry()
	{
		if(p != null)
		{
			boolean found = false;
			int i = 0;
			while(!found && i < p.entries.size())
			{
				if(p.entries.get(i).isOpen())
				{
					openEntry = p.entries.get(i);
					found = true;
					getSupportActionBar().setTitle("Entry: " + openEntry.entryid);
				}
				i++;
			}
		}
	}
	
	private void fillEntryAdapter()
	{
		if(p != null)
		{
			ArrayList<String> entryTitles = new ArrayList<String>();
			for(Entry e : p.entries)
			{
				if(! e.isOpen())
					entryTitles.add(e.toString());
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entryTitles);
		}
		list = (ListView) findViewById(R.id.entryList);
		if(adapter != null)
		{
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int itemId, long arg3) {
					Intent i = new Intent(getApplicationContext(), SpecificEntryActivity.class);
					Entry e = p.entries.get(itemId);
					i.putExtra("entry", e);
					startActivity(i);
				}
			});
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int tabInt = (Integer) tab.getTag();		
		
		switch (tabInt) {
		case 0:	
			setContentView(R.layout.openentrylayout);
			if(openEntry != null)
			{
				getSupportActionBar().setTitle("Entry: " + openEntry.entryid);
				setDescription();
			}
			else
			{
				getSupportActionBar().setTitle("No open entry");
			}
			break;
		case 1:
			setContentView(R.layout.entrylayout);
			getSupportActionBar().setTitle("All Entries");
			fillEntryAdapter();
			break;
		case 2:
			setContentView(R.layout.entrylayout);
			break;
		}
		
	}
	
	private void setDescription()
	{
		if(openEntry != null)
		{
			TextView tv = (TextView) findViewById(R.id.open_entry_description);
			String description = "Description: \n";
			description += openEntry.description;
			description += "\n\nOwner: ";
			description += openEntry.u.username;
			description += "\n\nStart: ";
			Timestamp ts = openEntry.start;
			description += ts.toString();
			tv.setText(description);
		}
	}
	
	public void stopEntry(View v)
	{
		Timestamp end = new Timestamp(System.currentTimeMillis());
		closeEntry(end);
		Entry newEntry = openEntry;
		newEntry.end = end;
		p.entries.set(p.entries.indexOf(openEntry), newEntry);
		openEntry = null;
		Toast t = Toast.makeText(getApplicationContext(), "Entry stopped", Toast.LENGTH_LONG);
		t.show();
		getSupportActionBar().getTabAt(1).select();
	}
	
	public StringEntity closeEntry(Timestamp end)
	{
		StringEntity create = null;
		try {
			create = new StringEntity("{\"eind\":\"" + end + "\",\"pid\":\"" + openEntry.entryid + "\",\"notities\":\""+ openEntry.description +"\"}");
	    	create.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return create;
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
