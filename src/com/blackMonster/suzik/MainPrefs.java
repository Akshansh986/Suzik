package com.blackMonster.suzik;

import android.content.Context;
import android.content.SharedPreferences;

public class MainPrefs {
	public static final String PREFS_NAME = "MainPrefs";
	public static final String MY_NO = "MY_NO";
	public static final String UNIDENTIFIABLE_SONG_ID_MAX = "UNIDENTIFIABLE_SONG_ID_MAX";

	
	private static SharedPreferences prefs=null;

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}
	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}
	
	
	public static void setCallOnNetAvailable(String caller, boolean value, Context context){
		initPrefInstance(context);
		prefs.edit().putBoolean(caller, value).commit();
	}
	
	public static boolean shouldCallOnNetAvailable(String caller,Context context){
		initPrefInstance(context);
		return prefs.getBoolean(caller, false);
	}

	
	
	public static void setMyNo(String number,Context context) {
		initPrefInstance(context);
		prefs.edit().putString(MY_NO, number).commit();
	}
	

	public static String getMyNo(Context context) {
		initPrefInstance(context);
		return prefs.getString(MY_NO, "123");
	}
	
	public static void setUnIndetifiableSongId(int id,Context context) {
		initPrefInstance(context);
		prefs.edit().putInt(UNIDENTIFIABLE_SONG_ID_MAX, id).commit();
	}
	

	public static int getUnIdentifiableSongId(Context context) {
		initPrefInstance(context);
		return prefs.getInt(UNIDENTIFIABLE_SONG_ID_MAX, 0);
	}
	
	
	public static void close() {
		prefs = null;
	}

}
