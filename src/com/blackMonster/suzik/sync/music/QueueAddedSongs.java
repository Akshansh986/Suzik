package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;

class QueueAddedSongs {

	private static final String TAG = "QueueAddedSongs";

	private static final String C_TITLE = "title";
	private static final String C_ARTIST = "artist";
	private static final String C_ALBUM = "album";
	private static final String C_DURATION = "duration";
	private static final String C_FPRINT = "fprint";
	
	private static final String TABLE = "QueueAddedSongs";

	static void createTable(SQLiteDatabase db) {
		String sql = String.format("create table %s" + "(%s text, %s text, %s text, %s INTEGER, %s text)",
				TABLE, C_TITLE,C_ARTIST, C_ALBUM, C_DURATION, C_FPRINT);
		db.execSQL(sql);
	}
	
	static Song search(String fPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		Song ans = null;

		Cursor cursor = db.query(TABLE, null, C_FPRINT + "='" + fPrint + "'" , null, null, null, null);

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

	

	static boolean insert(Song song,String fPrint, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_TITLE, song.getTitle());
		values.put(C_ARTIST, song.getArtist());
		values.put(C_ALBUM, song.getAlbum());
		values.put(C_DURATION, song.getDuration());
		values.put(C_FPRINT, fPrint);
		return db.insert(TABLE, null, values) > -1;
		
	}
	
	static boolean remove(String fPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_FPRINT + "='" + fPrint + "'", null) > 0;
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
