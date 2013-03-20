package com.sourcefish.tools.login;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.projectmanagement.R;
import com.sourcefish.tools.SourceFishConfig;

public class RegisterActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.activity_register);
		setTheme(SourceFishConfig.MAINTHEME);
	}
	
	public void Register()
	{
		TextView emailField=(TextView) findViewById(R.id.uc_txt_registerUsername);
		TextView passwordField=(TextView) findViewById(R.id.uc_txt_registerPassword);
		TextView passwordRepeatField=(TextView) findViewById(R.id.uc_txt_registerPasswordrepeat);
		
		if(passwordField.getText().equals(passwordRepeatField.getText()))
		{
			//register
		}
		else
		{
			Toast toast=Toast.makeText(getApplicationContext(), "Passwords not matching", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	
}
