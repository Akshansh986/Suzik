package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.util.DbUtils;

public class TablePausedSongs {
	private static final String TABLE = "pausedSongs";
	private static final String C_ID = "ID";
	private static final String C_TITLE = "TRACK";
	private static final String C_ARTIST = "ARTIST";
	private static final String C_ALBUM = "ALBUM";
	private static final String C_DURATION = "DURATION";
	private static final String C_STREAMING = "STREAMING";
	private static final String C_PAST_PLAYED = "PAST_PLAYED";
	private static final String C_PAUSE_TS = "STOP_TS";

	private BroadcastSong song;
	private long pastPlayed, pauseTS;

	public TablePausedSongs(BroadcastSong song, long pastPlayed, long pauseTS) {
		this.song = song;
		this.pastPlayed = pastPlayed;
		this.pauseTS = pauseTS;
	}

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER, %s text,%s text,%s text, %s integer, %s integer, %s integer, %s integer, PRIMARY KEY (%s, %s))",
						TABLE, C_ID, C_TITLE, C_ARTIST, C_ALBUM, C_DURATION,
						C_STREAMING, C_PAST_PLAYED, C_PAUSE_TS, C_TITLE,
						C_PAUSE_TS);
		db.execSQL(sql);
	}

	public static void insert(TablePausedSongs tPsong, Context context) {
		Log.d(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_ID, tPsong.song.getId());
		values.put(C_TITLE, tPsong.song.getTitle());
		values.put(C_ARTIST, tPsong.song.getArtist());
		values.put(C_ALBUM, tPsong.song.getAlbum());
		values.put(C_DURATION, tPsong.song.getDuration());
		values.put(C_STREAMING, tPsong.song.isStreaming());
		values.put(C_PAST_PLAYED, tPsong.pastPlayed);
		values.put(C_PAUSE_TS, tPsong.pauseTS);

		db.insert(TABLE, null, values);
	}

	
	
	
	public static TablePausedSongs search(Song song,
			Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		TablePausedSongs ans = null;
		
		
		
		if (song.getTitle() == null || song.getArtist() == null) return null;
		
		Pair<String, String[]> args = DbUtils.songToWhereArgs(song, C_TITLE, C_ARTIST, C_ALBUM, C_DURATION);
		
				
		Cursor cursor = db.query(TABLE, null,args.first , args.second, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				ans = new TablePausedSongs(new BroadcastSong(
						cursor.getLong(cursor.getColumnIndex(C_ID)),
						cursor.getString(cursor.getColumnIndex(C_TITLE)),
						cursor.getString(cursor.getColumnIndex(C_ARTIST)),
						cursor.getString(cursor.getColumnIndex(C_ALBUM)),
						cursor.getLong(cursor.getColumnIndex(C_DURATION)),
						cursor.getInt(cursor.getColumnIndex(C_STREAMING))),
						cursor.getLong(cursor.getColumnIndex(C_PAST_PLAYED)),
						cursor.getLong(cursor.getColumnIndex(C_PAUSE_TS)));
			}
			cursor.close();
		}

		return ans;
	}

	public static boolean remove(BroadcastSong song, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		Pair<String, String[]> args = DbUtils.songToWhereArgs(song, C_TITLE, C_ARTIST, C_ALBUM, C_DURATION);

		return db.delete(TABLE, args.first,args.second) > 0;
	}

	public static Cursor getAllRows(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, null, null);
	}

	public static int clearTable(Context context) {
		return DbHelper.getInstance(context).getWritableDatabase()
				.delete(TABLE, null, null);
	}

	public BroadcastSong getSong() {
		return song;
	}

	public long getPastPlayed() {
		return pastPlayed;
	}

	public long getPauseTS() {
		return pauseTS;
	}

}
