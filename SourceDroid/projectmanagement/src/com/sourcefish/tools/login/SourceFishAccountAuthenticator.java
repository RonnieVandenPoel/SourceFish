package com.sourcefish.tools.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sourcefish.tools.SourceFishHttpClient;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SourceFishAccountAuthenticator extends AbstractAccountAuthenticator {

	private Context mContext;

	public SourceFishAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		
		final Bundle result;
		final Intent intent;
		//check if there already is a smartcity account. If so, return, else this


		AccountManager am = AccountManager.get(mContext);
		Account[] aca = am.getAccountsByType("com.sourcefish.authenticator");
		Log.i("Auth5000", "So viele:"+aca.length);
		if(aca.length==0)
		{
			intent = new Intent(this.mContext, SourceFishAuthenticatorActivity.class);
			intent.putExtra("com.sourcefish.authenticator", authTokenType);
			intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			result = new Bundle();
			result.putParcelable(AccountManager.KEY_INTENT, intent);
		}
		else
		{
			result = new Bundle();
		}
		return result;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {

		final Bundle result;
		result = new Bundle();


		AccountManager am = AccountManager.get(mContext);
		String username = account.name;
		String password = am.getPassword(account);
		Log.i("Auth5000", password);
		
		DefaultHttpClient httpclient = SourceFishHttpClient.getClient(username, password);
		
		//Do not use localhost, because that would be the localhost of your phone. Use your IP!
		HttpGet httpget = new HttpGet("http://projecten3.eu5.org/webservice/tryLogin");
		try
		{
			// Execute HTTP Post Request
			HttpResponse htr = httpclient.execute(httpget);
			Log.i("Auth5000", ""+htr);
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(htr.getEntity().getContent()));
			String line = "";
			while ((line = in.readLine()) != null)
			{
				Log.i("Auth5000", ">><<<<<<<<<<<<<<<<<<<<<<"+line);
				Intent i = new Intent();
				i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
				i.putExtra("user", username);
				i.putExtra("authToken", line);
				result.putParcelable(AccountManager.KEY_INTENT, i);
			}
			in.close();
		}
		catch (ClientProtocolException e)
		{
		Log.i("Auth5000", "error "+e);
		}
		catch (IOException e)
		{
		Log.i("Auth5000", "error "+e);
		}
		return result;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
