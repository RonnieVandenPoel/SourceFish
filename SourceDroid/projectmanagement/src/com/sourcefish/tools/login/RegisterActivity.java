package com.sourcefish.tools.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.sourcefish.projectmanagement.NormalLayoutActivity;
import com.sourcefish.projectmanagement.R;
import com.sourcefish.tools.SourceFishConfig;

public class RegisterActivity extends NormalLayoutActivity {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.activity_register);
		setTheme(SourceFishConfig.MAINTHEME);
	}
	
	public void Register(View view)
	{
		TextView emailField=(TextView) findViewById(R.id.uc_txt_registerUsername);
		TextView passwordField=(TextView) findViewById(R.id.uc_txt_registerPassword);
		TextView passwordRepeatField=(TextView) findViewById(R.id.uc_txt_registerPasswordrepeat);
		
		Log.i("Pw1:",passwordField.getText().toString());
		Log.i("Pw2:",passwordRepeatField.getText().toString());
		
		if(passwordField.getText().toString().equals(passwordRepeatField.getText().toString()))
		{
			//register
			AsyncRegisterUser asynRegister=new AsyncRegisterUser();
			asynRegister.execute(this,emailField.getText(),passwordField.getText());
		}
		else
		{
			Toast toast=Toast.makeText(getApplicationContext(), "Passwords not matching", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
}

