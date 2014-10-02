package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;

class CacheTable {

	private static final String TAG = "CacheTable";
	private static final String C_ID = "id";
	private static final String C_FPRINT = "fPrint";
	private static final String C_FILENAME = "fileName";

	private static final String TABLE = "MusicCacheTable";

	static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER primary key,%s text,%s text, FOREIGN KEY (%s) REFERENCES %s (%s) )",
						TABLE, C_ID, C_FPRINT,C_FILENAME, C_ID, AllSongsTable.TABLE,
						AllSongsTable.C_ID);
		db.execSQL(sql);
	}

	static CacheData getData(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		Song song = AllSongsTable.getSong(id, context);
		String fp = null;
		String fileName = null;
		CacheData cacheData = null;
		if (song != null) {
			Cursor cursor = db.query(TABLE, null, C_ID + "='" + id +"'", null, null,
					null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
					fileName = cursor.getString(cursor.getColumnIndex(C_FILENAME));
					
					cacheData = new CacheData(id, song, fp, fileName);
				}
				cursor.close();
			}
		}

		return cacheData;
	}

	static List<CacheData> getAllData(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		List<CacheData> cachedDataSet = new ArrayList<CacheData>();

		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				long id = cursor.getLong(cursor.getColumnIndex(C_ID));
				Song song = AllSongsTable.getSong(id, context);
				String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
				String fileName = cursor.getString(cursor.getColumnIndex(C_FILENAME));

				if (song != null)
					cachedDataSet.add(new CacheData(id, song, fp, fileName));
				else
					LOGE(TAG, "Song no found");
				cursor.moveToNext();

			}

			cursor.close();

		}

		return cachedDataSet;
	}

	static void insert(CacheData cacheData, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		AllSongsTable.insert(cacheData.getId(), cacheData.getSong(), context);

		ContentValues values = new ContentValues();
		values.put(C_ID, cacheData.getId());
		values.put(C_FPRINT, cacheData.getfPrint());
		values.put(C_FILENAME, cacheData.getFileName());
		db.insert(TABLE, null, values);
	}
	
	// This should be improved (bulk entry)
		static void insert(List<CacheData> cacheData, Context context) {
			LOGD(TABLE, "insert bulk");

			for (CacheData cn : cacheData) {
				insert(cn, context);
			}

		}

	static boolean update(CacheData newCacheData, Context context) {
		LOGD(TABLE, "update");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_FPRINT, newCacheData.getfPrint());
		values.put(C_FILENAME, newCacheData.getFileName());
		if (db.update(TABLE, values, C_ID + "='" + newCacheData.getId() + "'", null) > 0) {
			return AllSongsTable.update(newCacheData.getId(),
					newCacheData.getSong(), context);
		} else
			return false;

	}

	static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		AllSongsTable.remove(id, context);
		return db.delete(TABLE, C_ID + "='" + id + "'", null) > 0;
	}
	
	
	static class CacheData {
		private long id;
		private Song song;
		private String fPrint;
		private String fileName;

		CacheData(long id, Song song, String fPrint, String fileName) {
			super();
			this.id = id;
			this.song = song;
			this.fPrint = fPrint;
			this.fileName = fileName;
		}

		long getId() {
			return id;
		}

		void setId(long id) {
			this.id = id;
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
