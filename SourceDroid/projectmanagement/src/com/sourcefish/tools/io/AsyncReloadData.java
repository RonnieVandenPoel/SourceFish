package com.sourcefish.tools.io;

import java.util.concurrent.ExecutionException;

import com.sourcefish.tools.SourceFishConfig;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncReloadData extends AsyncTask<Context, Integer, String>{

	@Override
	protected String doInBackground(Context... arg0) {
		AccountManager am = AccountManager.get(arg0[0]);		
		Account[] accounts = am.getAccountsByType("com.sourcefish.authenticator");		
		String user = accounts[0].name;
		Log.i("dbug","0");
		String pass = am.getPassword(accounts[0]);
		Log.i("dbug","1");
		AsyncDataLoad task2 = new AsyncDataLoad(user,pass,arg0[0]);
		task2.execute("");
		Log.i("dbug","2");
		try {			
			if (task2.get()) {
				Log.i("dbug","3");
				SourceFishConfig.alert(arg0[0], "Data from server loaded succesfully!");
			}
			else {
				SourceFishConfig.alert(arg0[0], "Error when retrieving data from server.");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
