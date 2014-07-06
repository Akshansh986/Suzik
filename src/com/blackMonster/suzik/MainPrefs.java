package com.blackMonster.suzik;

import android.content.Context;
import android.content.SharedPreferences;

public class MainPrefs {
	public static final String PREFS_NAME = "MainPrefs";
	public static final String MY_NO = "MY_NO";


	
	private static SharedPreferences prefs=null;

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}
	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}
	
	
	
	
	public static void setMyNo(String number,Context context) {
		initPrefInstance(context);
		prefs.edit().putString(MY_NO, number).commit();
	}
	

	public static String getMyNo(Context context) {
		initPrefInstance(context);
		return prefs.getString(MY_NO, "123");
	}
	
	
	public static void close() {
		prefs = null;
	}

}
