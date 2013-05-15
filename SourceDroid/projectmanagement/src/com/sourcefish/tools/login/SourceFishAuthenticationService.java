package com.sourcefish.tools.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SourceFishAuthenticationService extends Service {

	//private static final String TAG = "AccountAuthenticatorService";
	private static SourceFishAccountAuthenticator sAccountAuthenticator = null;

	public SourceFishAuthenticationService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
		{
			ret = new SourceFishAccountAuthenticator(this).getIBinder();
		}
		return ret;
	}

	private SourceFishAccountAuthenticator getAuthenticator()
	{
		if (sAccountAuthenticator == null)
		{
			sAccountAuthenticator = new SourceFishAccountAuthenticator(this);
		}
		return sAccountAuthenticator;
	}
	
}
