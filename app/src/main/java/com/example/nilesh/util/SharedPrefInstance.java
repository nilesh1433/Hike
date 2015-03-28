package com.example.nilesh.util;

import android.content.Context;

public class SharedPrefInstance {

	private SharedPrefInstance(){
	}
	
	public static Boolean getBoolean(Context context,String sharedPrefName, String keyName )
	{
		return context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).getBoolean(keyName, false);
	}
	
	public static String getString(Context context,String sharedPrefName, String keyName )
	{
		return context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).getString(keyName, null);
	}
	
	public static void setBoolean(Context context,String sharedPrefName, String keyName, boolean value )
	{
		context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit().putBoolean(keyName, value).commit();
	}
	
	public static void setString(Context context, String sharedPrefName, String keyName, String value )
	{
		context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit().putString(keyName, value).commit();
	}
}
