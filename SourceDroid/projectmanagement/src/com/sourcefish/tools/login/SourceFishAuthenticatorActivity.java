package com.sourcefish.tools.login;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sourcefish.projectmanagement.R;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SourceFishAuthenticatorActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_AUTHTOKEN_TYPE = "com.sourcefish.authenticator";
	public static final String PARAM_CREATE = "create";

	public static final int REQ_CODE_CREATE = 1;

	public static final int REQ_CODE_UPDATE = 2;

	public static final String EXTRA_REQUEST_CODE = "req.code";

	public static final int RESP_CODE_SUCCESS = 0;

	public static final int RESP_CODE_ERROR = 1;

	public static final int RESP_CODE_CANCEL = 2;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		this.setContentView(R.layout.user_credentials);
	}

	public void onCancelClick(View v) {
		this.finish();
	}

	public void onSaveClick(View v) {
		TextView tvUsername;
		TextView tvPassword;
		String username;
		String password;

		boolean hasErrors = false;

		tvUsername = (TextView) this.findViewById(R.id.uc_txt_username);
		tvPassword = (TextView) this.findViewById(R.id.uc_txt_password);

		tvUsername.setBackgroundColor(Color.WHITE);
		tvPassword.setBackgroundColor(Color.WHITE);
	
		username = tvUsername.getText().toString();
		password = tvPassword.getText().toString();

		try {
			new AsyncLoginCheck(username, password, getApplicationContext(), this).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		// finished

		String accountType = this.getIntent().getStringExtra(PARAM_AUTHTOKEN_TYPE);
		if (accountType == null)
		{ 
			accountType = "com.sourcefish.authenticator";
		}

		AccountManager accMgr = AccountManager.get(this);

		if (hasErrors) {
			// handel errors
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
			this.finish();
		}
		
	}
}
