package com.sourcefish.tools;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.Toast;

public class SourceFishConfig {
	public static int MAINTHEME = com.actionbarsherlock.R.style.Sherlock___Theme;
	public static String PREFFILE = "SourceFishPrefs";
	public static int THEMEDIALOG = 13234588;
	private static String BASE_URL="http://projecten3.eu5.org";
	
	
	
	static public void alert(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void setTheme(int theme)
	{
		MAINTHEME = theme;
	}
	
	/**
	 * 
	 * @param c
	 * @return username or null if no username found
	 */
	public static String getUserName(Context c)
	{
		try
		{
			AccountManager am = AccountManager.get(c);
			Account[] aca = am.getAccountsByType("com.sourcefish.authenticator");
			if (aca.length > 0)
			{
				return aca[0].name;
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return null;
	}
	
	public static String getUserPassword(Context c)
	{
		try
		{
			AccountManager am = AccountManager.get(c);
			Account[] aca = am.getAccountsByType("com.sourcefish.authenticator");
			if (aca.length > 0)
			{
				return am.getPassword(aca[0]);
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return null;
	}
	
	public static String getBaseURL()
	{
		return BASE_URL;
	}
	
	
}
