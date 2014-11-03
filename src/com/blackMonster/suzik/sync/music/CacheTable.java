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
import com.blackMonster.suzik.sync.music.AllSongsTable.AllSongData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

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
						AllSongsTable.C_LOCAL_ID);
		db.execSQL(sql);
	}

	static CacheData getData(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		AllSongData asd = AllSongsTable.getSong(id, context);
		String fp = null;
		String fileName = null;
		CacheData cacheData = null;
		if (asd != null) {
			Cursor cursor = db.query(TABLE, null, C_ID + "='" + id +"'", null, null,
					null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
					fileName = cursor.getString(cursor.getColumnIndex(C_FILENAME));
					cacheData = new CacheData(id, asd.getServerId(), asd.getSong(), fp, fileName);
				}
				cursor.close();
			}
		}

		return cacheData;
	}
	
	static Integer noOfCopies(String fPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		Integer result=null;
		Cursor cursor = db.query(TABLE, null, C_FPRINT + "='" + fPrint + "'" , null, null, null, null);

		if (cursor != null) {
			result = cursor.getCount();
			cursor.close();
		}

		return result;
	}

	static List<CacheData> getAllData(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		List<CacheData> cachedDataSet = null;

		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			cachedDataSet =  new ArrayList<CacheData>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				long id = cursor.getLong(cursor.getColumnIndex(C_ID));
				AllSongData asd = AllSongsTable.getSong(id, context);
				String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
				String fileName = cursor.getString(cursor.getColumnIndex(C_FILENAME));

				if (asd != null)
					cachedDataSet.add(new CacheData(id, asd.getServerId(), asd.getSong(), fp, fileName));
				else
					LOGE(TAG, "Song no found");
				cursor.moveToNext();

			}

			cursor.close();

		}
		

		return cachedDataSet;
	}

	static long insert(CacheData cacheData, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		long id = AllSongsTable.insert(new AllSongData(null, cacheData.getServerId(), cacheData.getSong()), context);

		ContentValues values = new ContentValues();
		values.put(C_ID, id);
		values.put(C_FPRINT, cacheData.getfPrint());
		values.put(C_FILENAME, cacheData.getFileName());
		db.insert(TABLE, null, values);
		return id;
	}
	
	// This should be improved (bulk entry)
		static void insert(List<CacheData> cacheData, Context context) {
			LOGD(TABLE, "insert bulk");

			for (CacheData cn : cacheData) {
				insert(cn, context);
			}

		}
/*
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
*/
	static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		AllSongsTable.remove(id, context);
		return db.delete(TABLE, C_ID + "='" + id + "'", null) > 0;
	}
	
	
	static class CacheData extends SongAndFileName {
		private Long id;
		private String fPrint;
		private long serverId;
		CacheData(Long id,long serverId, Song song, String fPrint, String fileName) {
			super(song,fileName);
			this.id = id;
			this.serverId = serverId;
			this.fPrint = fPrint;
		}

		Long getId() {
			return id;
		}
		
		long getServerId() {
			return serverId;
		}

		void setId(long id) {
			this.id = id;
		}

	

		

		String getfPrint() {
			return fPrint;
		}

		void setfPrint(String fPrint) {
			this.fPrint = fPrint;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((fPrint == null) ? 0 : fPrint.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + (int) (serverId ^ (serverId >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (!(obj instanceof CacheData))
				return false;
			CacheData other = (CacheData) obj;
			if (fPrint == null) {
				if (other.fPrint != null)
					return false;
			} else if (!fPrint.equals(other.fPrint))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (serverId != other.serverId)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CacheData [id=" + id + ", fPrint=" + fPrint + ", serverId="
					+ serverId + "]";
		}

		
		
		
		
		

	}

}
