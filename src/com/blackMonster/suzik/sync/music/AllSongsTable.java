package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;

public class AllSongsTable {

	private static final String TAG = "AllSongsTable";
	public static final String C_ID = "id";
	public static final String C_TITLE = "title";
	public static final String C_ARTIST = "artist";
	public static final String C_ALBUM = "album";
	public static final String C_DURATION = "duration";
	
	public static final String TABLE = "allSongsTable";

	public static void createTable(SQLiteDatabase db) {

		String sql = String.format("create table %s" + "(%s INTEGER ,%s text, %s text, %s text, %s INTEGER, PRIMARY KEY(%s,%s,%s,%s,%s) )",
				TABLE, C_ID,C_TITLE,C_ARTIST, C_ALBUM, C_DURATION,C_ID,C_TITLE,C_ARTIST,C_ALBUM,C_DURATION);
		db.execSQL(sql);
	}
	
	public static Song getSong(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		Song ans = null;

		Cursor cursor = db.query(TABLE, null, C_ID + "='" + id + "'", null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				ans = new Song(cursor.getString(cursor
						.getColumnIndex(C_TITLE)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getString(cursor
						.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)));
			}
			cursor.close();
		}

		return ans;
	}

	

	public static boolean insert(long id, Song song, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_ID, id);
		values.put(C_TITLE, song.getTitle());
		values.put(C_ARTIST, song.getArtist());
		values.put(C_ALBUM, song.getAlbum());
		values.put(C_DURATION, song.getDuration());
		return db.insert(TABLE, null, values) > -1;
		
	}
	
	public static boolean update(long id, Song newSong, Context context) {
		LOGD(TABLE, "update");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_TITLE, newSong.getTitle());
		values.put(C_ARTIST, newSong.getArtist());
		values.put(C_ALBUM, newSong.getAlbum());
		values.put(C_DURATION, newSong.getDuration());
		return db.update(TABLE, values,  C_ID + "='" + id + "'" ,null) > 0;
		
	}
	


	
	public static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_ID + "='" + id + "'", null) > 0;
	}

	public static boolean isEmpty(Context context) {
		LOGD(TAG, "in isEmpty");
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		boolean result;
		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			if (cursor.getCount() == 0)
				result = true;
			else
				result = false;
			cursor.close();
		} else {
			LOGD(TAG, "cursor is null");
			result = false;
		}

		return result;
	}

}
