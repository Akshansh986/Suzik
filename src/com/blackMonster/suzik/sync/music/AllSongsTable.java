package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.util.DbUtils;

 class AllSongsTable {

	private static final String TAG = "AllSongsTable";
	 static final String C_ID = "id";
	 static final String C_TITLE = "title";
	 static final String C_ARTIST = "artist";
	 static final String C_ALBUM = "album";
	 static final String C_DURATION = "duration";
	
	 static final String TABLE = "allSongsTable";

	static void createTable(SQLiteDatabase db) {

		String sql = String.format("create table %s" + "(%s INTEGER primary key,%s text, %s text, %s text, %s INTEGER)",
				TABLE, C_ID,C_TITLE,C_ARTIST, C_ALBUM, C_DURATION);
		db.execSQL(sql);
	}
	
	 static Song getSong(long id, Context context) {
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
	 
	
	 
	 static Pair<Long, Song> search(Song song, Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
			Pair<Long, Song> result = null;
			
			if (song.getTitle() == null || song.getArtist() == null) return null;
			
			Pair<String, String[]> args = DbUtils.songToWhereArgs(song, C_TITLE, C_ARTIST, C_ALBUM, C_DURATION);
			
			
			Cursor cursor = db.query(TABLE, null,args.first , args.second, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					Song dbSong = new Song(cursor.getString(cursor
							.getColumnIndex(C_TITLE)), cursor.getString(cursor
							.getColumnIndex(C_ARTIST)), cursor.getString(cursor
							.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
							.getColumnIndex(C_DURATION)));
					result = new Pair<Long, Song>(cursor.getLong(cursor.getColumnIndex(C_ID)), dbSong);
					
				}
				cursor.close();
			}

			return result;
		}
	

	 static boolean insert(long id, Song song, Context context) {
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
	
	 static boolean update(long id, Song newSong, Context context) {
		LOGD(TABLE, "update");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_TITLE, newSong.getTitle());
		values.put(C_ARTIST, newSong.getArtist());
		values.put(C_ALBUM, newSong.getAlbum());
		values.put(C_DURATION, newSong.getDuration());
		return db.update(TABLE, values,  C_ID + "='" + id + "'" ,null) > 0;
		
	}
	


	
	 static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_ID + "='" + id + "'", null) > 0;
	}

	 static boolean isEmpty(Context context) {
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
