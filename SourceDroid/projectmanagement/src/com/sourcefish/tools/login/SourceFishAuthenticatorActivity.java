package com.sourcefish.tools.login;

import com.sourcefish.projectmanagement.R;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SourceFishAuthenticatorActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_AUTHTOKEN_TYPE = "com.sourcefish.authenticator";
	public static final String PARAM_CREATE = "create";

	public static final int REQ_CODE_CREATE = 1;

	public static final int REQ_CODE_UPDATE = 2;

	public static final String EXTRA_REQUEST_CODE = "req.code";

	public static final int RESP_CODE_SUCCESS = 0;

	public static final int RESP_CODE_ERROR = 1;

	public static final int RESP_CODE_CANCEL = 2;

	boolean hasErrors = false;
	String username = "";
	String password = "";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.user_credentials);
	}
	
	public void setError()
	{
		hasErrors = true;
	}

	public void onCancelClick(View v) {
		Intent i = new Intent(this, RegisterActivity.class);
		startActivity(i);
		finish();
	}

	public void onSaveClick(View v) {
		TextView tvUsername;
		TextView tvPassword;

		tvUsername = (TextView) this.findViewById(R.id.uc_txt_username);
		tvPassword = (TextView) this.findViewById(R.id.uc_txt_password);

		tvUsername.setBackgroundColor(Color.WHITE);
		tvPassword.setBackgroundColor(Color.WHITE);
	
		username = tvUsername.getText().toString();
		password = tvPassword.getText().toString();

		new AsyncLoginCheck(username, password, getApplicationContext(), this).execute();
		
		// finished		
	}
	
	public void logUsername()
	{
		String accountType = this.getIntent().getStringExtra(PARAM_AUTHTOKEN_TYPE);
		if (accountType == null)
		{ 
			accountType = "com.sourcefish.authenticator";
		}

		AccountManager accMgr = AccountManager.get(this);

		if (hasErrors) {
			TextView tv = (TextView) findViewById(R.id.uc_lbl_username);
			tv.setText("");
			TextView tv2 = (TextView) findViewById(R.id.uc_lbl_password);
			tv2.setText("");
			Toast toast = Toast.makeText(getApplicationContext(), "wrong password or username", Toast.LENGTH_LONG);
			toast.show();
		}
		else
		{
			// This is the magic that addes the account to the Android Account Manager
			final Account account = new Account(username, accountType);
			accMgr.addAccountExplicitly(account, password, null);

			// Now we tell our caller, could be the Andreoid Account Manager
			// or even our own application
			// that the process was successful

			final Intent intent = new Intent();
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
			intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
			this.setAccountAuthenticatorResult(intent.getExtras());
			this.setResult(RESULT_OK, intent);
		}
	}
	
}
