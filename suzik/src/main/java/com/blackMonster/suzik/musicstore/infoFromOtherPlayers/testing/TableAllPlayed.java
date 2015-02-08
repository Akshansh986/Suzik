package com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.BroadcastSong;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class TableAllPlayed {
	public static final String TABLE = "allPlayed";
	public static final String C_ID = "ID";
	public static final String C_TRACK = "TRACK";
	public static final String C_ARTIST = "ARTIST";
	public static final String C_ALBUM = "ALBUM";
	public static final String C_DURATION = "DURATION";
	public static final String C_STREAMING = "STREAMING";
	public static final String C_COMPLETED_TS = "COMPLETED_TS";
	BroadcastSong song;

	

	public static void createTable(SQLiteDatabase db) {
		String sql = String.format("create table %s"
				+ "(%s INTEGER, %s text,%s text,%s text, %s integer, %s integer, %s integer)",
				TABLE, C_ID, C_TRACK, C_ARTIST, C_ALBUM, C_DURATION, C_STREAMING, C_COMPLETED_TS);
		db.execSQL(sql);
	}

	public static void insert(BroadcastSong song,long completedTS, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_ID, song.getId());
		values.put(C_TRACK, song.getTitle());
		values.put(C_ARTIST, song.getArtist());
		values.put(C_ALBUM, song.getAlbum());
		values.put(C_DURATION, song.getDuration());
		values.put(C_STREAMING, song.isStreaming());
		values.put(C_COMPLETED_TS, completedTS);


		db.insert(TABLE, null, values);
		
	}

	public static BroadcastSong search(String track, String artist,
			Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		BroadcastSong ans = null;

		Cursor cursor = db.query(TABLE, null, C_TRACK + "='" + track + "' AND "
				+ C_ARTIST + "='" + artist + "'", null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				ans = new BroadcastSong(cursor.getLong(cursor
						.getColumnIndex(C_ID)), cursor.getString(cursor
						.getColumnIndex(C_TRACK)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getString(cursor
						.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)), cursor.getInt(cursor
						.getColumnIndex(C_STREAMING)));
			}
			cursor.close();
		}

		return ans;
	}

	public static int getCount(String track, String artist, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		int ans = 0;

		Cursor cursor = db.query(TABLE, null, C_TRACK + "='" + track + "' AND "
				+ C_ARTIST + "='" + artist + "'", null, null, null, null);

		if (cursor != null) {
			ans = cursor.getCount();
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
		return db.rawQuery("select rowid _id,* from " + TABLE
				+ " ORDER BY " + "_id" + " DESC", null);	}

	public static int clearTable(Context context) {
		return DbHelper.getInstance(context).getWritableDatabase()
				.delete(TABLE, null, null);
	}

}
