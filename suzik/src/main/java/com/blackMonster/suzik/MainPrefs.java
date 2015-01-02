package com.blackMonster.suzik;

import android.content.Context;
import android.content.SharedPreferences;

public class MainPrefs {
	public static final String PREFS_NAME = "MainPrefs";
	public static final String MY_NO = "MY_NO";
	public static final String FIRST_TIME_MUSIC_SYNC = "firstMusicSync";
	private static final String LOGIN_DONE = "loginDone";


	
	private static SharedPreferences prefs=null;

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
	}
	
	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}

    public static void clearAll(Context context) {
        initPrefInstance(context);
        prefs.edit().clear().commit();
    }
	
	
	public static void setCallOnNetAvailable(String caller, boolean value, Context context){
		initPrefInstance(context);
		prefs.edit().putBoolean(caller, value).commit();
	}
	
	public static boolean shouldCallOnNetAvailable(String caller,Context context){
		initPrefInstance(context);
		return prefs.getBoolean(caller, false);
	}
	
	public static void setSyncFailureCount(String caller, int value, Context context){
		initPrefInstance(context);
		prefs.edit().putInt(caller, value).commit();
	}
	
	public static int getSyncFailureCount(String caller,Context context){
		initPrefInstance(context);
		return prefs.getInt(caller, 0);
	}

	
	
	public static void setMyNo(String number,Context context) {
		initPrefInstance(context);
		prefs.edit().putString(MY_NO, number).commit();
	}
	
	public static void setFirstTimeMusicSyncDone(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(FIRST_TIME_MUSIC_SYNC, true).commit();
	}
	
	public static boolean isFirstTimeMusicSyncDone(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(FIRST_TIME_MUSIC_SYNC, false);
	}
	
	
	public static void setLoginDone(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(LOGIN_DONE, true).commit();
	}
	
	public static boolean isLoginDone(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(LOGIN_DONE, false);
	}
	

	public static String getMyNo(Context context) {
		initPrefInstance(context);
		return prefs.getString(MY_NO, "123");
	}
	
	
	
	public static void close() {
		prefs = null;
	}

}
