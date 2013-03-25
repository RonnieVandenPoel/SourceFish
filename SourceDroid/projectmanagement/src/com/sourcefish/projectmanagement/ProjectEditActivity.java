package com.sourcefish.projectmanagement;

import com.sourcefish.tools.Project;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class ProjectEditActivity extends NormalLayoutActivity implements ServerListenerInterface{
	private Project project;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_edit);
		
		//delicious gestolen ronnie code
		project = (Project) getIntent().getSerializableExtra("project");
		
		EditText cust = (EditText) findViewById(R.id.editKlantView);
		EditText desc = (EditText) findViewById(R.id.editDescView);
		EditText name = (EditText) findViewById(R.id.editProjectView);
		
		
		cust.setText(project.customer);
		desc.setText(project.description);
		name.setText(project.name);
	}	

	@Override
	public void getServerResponse(String s) {
		// TODO Auto-generated method stub
		
	}
	
	public void SendButton(View v) {
		
	}

}
