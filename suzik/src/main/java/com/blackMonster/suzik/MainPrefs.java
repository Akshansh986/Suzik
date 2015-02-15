package com.blackMonster.suzik;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.google.gson.Gson;

public class MainPrefs {
	public static final String PREFS_NAME = "MainPrefs";
	public static final String MY_NO = "MY_NO";
	public static final String FIRST_TIME_MUSIC_SYNC = "firstMusicSync";
	private static final String LOGIN_DONE = "loginDone";
    private static final String TIMELINE_CACHE = "timelineCache";
    private static final String FIRST_TIME_MUSIC_POST_SERVER_SYNC = "firstPostMusicSync";
    private static final String ADDEDSONGS_RESPONSE_HANDLER_INIT_TIME = "addedsongsResponseHandlerInitTime";
    private static final String FLAG_FIRST_CLICK = "flagFirstClick";


    private static SharedPreferences prefs=null;

    private static final String LAST_PLAYED_SONG = "lastPlayedSong";


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

    public static void storePlayable(Playable playable,Context context) {
        initPrefInstance(context);
        Gson gson = new Gson();
        String json = gson.toJson(playable);
        prefs.edit().putString(LAST_PLAYED_SONG, json).commit();
    }

    public static Playable getPlayable(Context context) {
        initPrefInstance(context);
        Gson gson = new Gson();
        String json =  prefs.getString(LAST_PLAYED_SONG, "");
        if (json.equals("")) return  null;

        Playable obj = gson.fromJson(json, Playable.class);
        return obj;
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

    public static void setAddedsongsResponseHandlerInitTime(long time,Context context) {
        initPrefInstance(context);
        prefs.edit().putLong(ADDEDSONGS_RESPONSE_HANDLER_INIT_TIME, time).commit();
    }

	public static void setFirstTimeMusicSyncDone(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(FIRST_TIME_MUSIC_SYNC, true).commit();
	}
	
	public static boolean isFirstTimeMusicSyncDone(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(FIRST_TIME_MUSIC_SYNC, false);
	}

    public static void setFlagFirstClick(Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(FLAG_FIRST_CLICK, true).commit();
    }

    public static boolean isFlagFirstClicked(Context context) {
        initPrefInstance(context);
        return prefs.getBoolean(FLAG_FIRST_CLICK, false);
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

    public static long getAddedSongsResponseHandlerInitTime(Context context) {
        initPrefInstance(context);
        return prefs.getLong(ADDEDSONGS_RESPONSE_HANDLER_INIT_TIME, 0);
    }

    public static void setTimelineCache(String data, Context context){
        initPrefInstance(context);
        prefs.edit().putString(TIMELINE_CACHE, data).commit();
    }

    public static String getTimelineCache(Context context){
        initPrefInstance(context);
        return prefs.getString(TIMELINE_CACHE, "");
    }
	
	
	
	public static void close() {
		prefs = null;
	}

    public static void setFirstTimeSongPostedToServer( Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(FIRST_TIME_MUSIC_POST_SERVER_SYNC, true).commit();
    }

    public static boolean getFirstTimeSongPostedToServer(Context context){
        initPrefInstance(context);
        return prefs.getBoolean(FIRST_TIME_MUSIC_POST_SERVER_SYNC, false);
    }
}
