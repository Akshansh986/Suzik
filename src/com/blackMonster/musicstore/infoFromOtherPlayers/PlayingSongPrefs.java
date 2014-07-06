package com.blackMonster.musicstore.infoFromOtherPlayers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PlayingSongPrefs {

	public static final String PREFS_NAME = "playingSongPrefs";
	public static final String ID = "ID";
	public static final String TRACK = "TRACK";
	public static final String ARTIST = "ARTIST";
	public static final String DURATION = "DURATION";
	public static final String STREAMING = "STREAMING";
	public static final String START_TS = "START_TS";
	public static final String PAST_PLAYED = "PAST_PLAYED";

	private static SharedPreferences prefs = null;

	private static void initPrefInstance(Context context) {
		if (prefs == null)
			prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}

	public static Song getSong(Context context) {
		initPrefInstance(context);
		return new Song(prefs.getLong(ID, -1), prefs.getString(TRACK, "NA"),
				prefs.getString(ARTIST, "NA"), prefs.getLong(DURATION, -1),
				prefs.getLong(STREAMING, -1));
	}

	public static Long getId(Context context) {
		initPrefInstance(context);
		return prefs.getLong(ID, -1);
	}

	public static String getTrack(Context context) {
		initPrefInstance(context);
		return prefs.getString(TRACK, "NA");
	}

	public static String getArtist(Context context) {
		initPrefInstance(context);
		return prefs.getString(ARTIST, "NA");
	}

	public static Long getDuration(Context context) {
		initPrefInstance(context);
		return prefs.getLong(DURATION, -1);
	}

	public static Long getStreaming(Context context) {
		initPrefInstance(context);
		return prefs.getLong(STREAMING, -1);
	}

	public static Long getStartTS(Context context) {
		initPrefInstance(context);
		return prefs.getLong(START_TS, -1);
	}

	public static Long getPastPlayed(Context context) {
		initPrefInstance(context);
		return prefs.getLong(PAST_PLAYED, -1);
	}

	public static void setID(long id, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(ID, id).commit();
	}

	public static void setTrack(String track, Context context) {
		initPrefInstance(context);
		prefs.edit().putString(TRACK, track).commit();
	}

	public static void setArtist(String artist, Context context) {
		initPrefInstance(context);
		prefs.edit().putString(ARTIST, artist).commit();
	}

	public static void setDuration(long duration, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(DURATION, duration).commit();
	}

	public static void setStreaming(long streaming, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(STREAMING, streaming).commit();
	}

	public static void setStartTS(long startTs, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(START_TS, startTs).commit();
	}

	public static void setPastPlayed(long pastPlayed, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(PAST_PLAYED, pastPlayed).commit();
	}

	public static void setAll(Song song, long pastPlayed, long startTS,
			Context context) {
		initPrefInstance(context);
		Editor editor = prefs.edit();
		editor.putLong(ID, song.id);
		editor.putString(TRACK, song.track);
		editor.putString(ARTIST, song.artist);
		editor.putLong(DURATION, song.duration);
		editor.putLong(STREAMING, song.streaming);
		editor.putLong(PAST_PLAYED, pastPlayed);
		editor.putLong(START_TS, startTS);
		editor.commit();

	}

	public static void close() {
		prefs = null;
	}

}
