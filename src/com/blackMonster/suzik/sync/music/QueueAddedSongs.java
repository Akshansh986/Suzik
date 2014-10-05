package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.List;

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
	private static final String C_FILENAME = "fileName";

	
	private static final String TABLE = "QueueAddedSongs";

	static void createTable(SQLiteDatabase db) {
		String sql = String.format("create table %s" + "(%s text, %s text, %s text, %s text, %s INTEGER, %s text)",
				TABLE,C_FILENAME, C_TITLE,C_ARTIST, C_ALBUM, C_DURATION, C_FPRINT);
		db.execSQL(sql);
	}
	
	static QueueData search(String fPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		QueueData queueData = null;
		Cursor cursor = db.query(TABLE, null, C_FPRINT + "='" + fPrint + "'" , null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				Song song = new Song(cursor.getString(cursor
						.getColumnIndex(C_TITLE)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getString(cursor
						.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)));
			queueData = new QueueData(song, fPrint, cursor.getString(cursor.getColumnIndex(C_FILENAME)));
				
			}
			cursor.close();
		}

		return queueData;
	}
	
	static List<String> getAllFprints(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

		List<String> allFp = null;
		Cursor cursor = db.query(TABLE, null,null, null, null, null, null);
		
		if (cursor != null) {
			allFp = new ArrayList<String>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				allFp.add(cursor.getString(cursor.getColumnIndex(C_FPRINT)));
 				cursor.moveToNext();
			}
			cursor.close();
		}

		return allFp;
	}
	
	static int getRowCount(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

		Cursor cursor = db.query(TABLE, null,null, null, null, null, null);
		
		if (cursor!=null) {
			return cursor.getCount();
		}
		else
			return 0;
	}
	
	

	

	static boolean insert(QueueData data, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_TITLE, data.getSong().getTitle());
		values.put(C_ARTIST, data.getSong().getArtist());
		values.put(C_ALBUM, data.getSong().getAlbum());
		values.put(C_DURATION, data.getSong().getDuration());
		values.put(C_FPRINT, data.getfPrint());
		values.put(C_FILENAME, data.getFileName());
		return db.insert(TABLE, null, values) > -1;
		
	}
	
	static boolean remove(String fPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_FPRINT + "='" + fPrint + "'", null) > 0;
	}
	
	static boolean clearAll(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, null, null) > 0;
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
	
	static class QueueData {
		private Song song;
		private String fPrint;
		private String fileName;

		QueueData(Song song, String fPrint, String fileName) {
			super();
			this.song = song;
			this.fPrint = fPrint;
			this.fileName = fileName;
		}

		Song getSong() {
			return song;
		}
		
		String getFileName() {
			return fileName;
		}

		void setSong(Song song) {
			this.song = song;
		}

		String getfPrint() {
			return fPrint;
		}

		void setfPrint(String fPrint) {
			this.fPrint = fPrint;
		}

	}

}
