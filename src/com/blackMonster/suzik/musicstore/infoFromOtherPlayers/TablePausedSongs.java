package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import com.blackMonster.suzik.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TablePausedSongs {
	private static final String TABLE = "pausedSongs";
	private static final String C_ID = "ID";
	private static final String C_TRACK = "TRACK";
	private static final String C_ARTIST = "ARTIST";
	private static final String C_DURATION = "DURATION";
	private static final String C_STREAMING = "STREAMING";
	private static final String C_PAST_PLAYED = "PAST_PLAYED";
	private static final String C_PAUSE_TS = "STOP_TS";

	private Song song;
	private long pastPlayed, pauseTS;

	public TablePausedSongs(Song song, long pastPlayed, long pauseTS) {
		this.song = song;
		this.pastPlayed = pastPlayed;
		this.pauseTS = pauseTS;
	}

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER, %s text,%s text, %s integer, %s integer, %s integer, %s integer, PRIMARY KEY (%s, %s))",
						TABLE, C_ID, C_TRACK, C_ARTIST, C_DURATION,
						C_STREAMING, C_PAST_PLAYED, C_PAUSE_TS, C_TRACK,
						C_PAUSE_TS);
		db.execSQL(sql);
	}

	public static void insert(TablePausedSongs tPsong, Context context) {
		Log.d(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_ID, tPsong.song.id);
		values.put(C_TRACK, tPsong.song.track);
		values.put(C_ARTIST, tPsong.song.artist);
		values.put(C_DURATION, tPsong.song.duration);
		values.put(C_STREAMING, tPsong.song.streaming);
		values.put(C_PAST_PLAYED, tPsong.pastPlayed);
		values.put(C_PAUSE_TS, tPsong.pauseTS);

		db.insert(TABLE, null, values);
	}

	public static TablePausedSongs search(String track, String artist,
			Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		TablePausedSongs ans = null;
		Cursor cursor = db.query(TABLE, null, C_TRACK + "='" + track + "' AND "
				+ C_ARTIST + "='" + artist + "'", null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				ans = new TablePausedSongs(new Song(cursor.getLong(cursor
						.getColumnIndex(C_ID)), cursor.getString(cursor
						.getColumnIndex(C_TRACK)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)), cursor.getLong(cursor
						.getColumnIndex(C_STREAMING))), cursor.getLong(cursor
						.getColumnIndex(C_PAST_PLAYED)), cursor.getLong(cursor
						.getColumnIndex(C_PAUSE_TS)));
			}
			cursor.close();
		}

		return ans;
	}

	public static boolean remove(String track, String artist, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_TRACK + "='" + track + "' AND " + C_ARTIST
				+ "='" + artist + "'", null) > 0;
	}

	public static Cursor getAllRows(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, null, null);
	}

	public static int clearTable(Context context) {
		return DbHelper.getInstance(context).getWritableDatabase()
				.delete(TABLE, null, null);
	}

	public Song getSong() {
		return song;
	}

	public long getPastPlayed() {
		return pastPlayed;
	}

	public long getPauseTS() {
		return pauseTS;
	}

}
