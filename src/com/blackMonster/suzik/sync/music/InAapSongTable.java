package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AllSongsTable.AllSongData;

public class InAapSongTable {

	private static final String TAG = "InAppSongTable";
	private static final String C_ID = "id";
	private static final String C_FPRINT = "fPrint";
	private static final String C_LINK = "link";
	private static final String C_LOCATION = "location";

	
	private static final String TABLE = "InAppSongTable";

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER primary key,%s text,%s text, %s text, FOREIGN KEY (%s) REFERENCES %s (%s) )",
						TABLE, C_ID, C_FPRINT,C_LINK,C_LOCATION, C_ID, AllSongsTable.TABLE,
						AllSongsTable.C_LOCAL_ID);
		db.execSQL(sql);
	}

	public static InAppSongData getData(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		AllSongData asd = AllSongsTable.getSong(id, context);
		InAppSongData inAppSongData = null;
		if (asd != null) {
			Cursor cursor = db.query(TABLE, null, C_ID + "='" + id + "'", null, null,
					null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
					String link = cursor.getString(cursor.getColumnIndex(C_LINK));
					String location = cursor.getString(cursor.getColumnIndex(C_LOCATION));
					inAppSongData = new InAppSongData(id, asd.getServerId(), asd.getSong(), fp, link, location);
				}
				cursor.close();
			}
		}

		return inAppSongData;
	}
	
	public static InAppSongData getData(String fingerPrint, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		
		
		InAppSongData inAppSongData = null;
		
		Cursor cursor = db.query(TABLE, null, C_FPRINT + "=?", new String[]{fingerPrint}, null,
					null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
					String link = cursor.getString(cursor.getColumnIndex(C_LINK));
					String location = cursor.getString(cursor.getColumnIndex(C_LOCATION));
					long id = cursor.getLong(cursor.getColumnIndex(C_ID));
					
					AllSongData asd = AllSongsTable.getSong(id, context);
					if (asd != null)
					inAppSongData = new InAppSongData(id, asd.getServerId(), asd.getSong(), fp, link, location);
				
				}
				cursor.close();
			}
		

		return inAppSongData;
	}

	public static HashSet<InAppSongData> getAllData(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		HashSet<InAppSongData> inAppSongDataSet = null;

		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			inAppSongDataSet = new HashSet<InAppSongData>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				long id = cursor.getLong(cursor.getColumnIndex(C_ID));
				String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
				String link = cursor.getString(cursor.getColumnIndex(C_LINK));
				String location = cursor.getString(cursor.getColumnIndex(C_LOCATION));
				
				AllSongData asd = AllSongsTable.getSong(id, context);
				if (asd != null)
					inAppSongDataSet.add(new InAppSongData(id, asd.getServerId(), asd.getSong(), fp, link, location));
				else
					LOGE(TAG, "Song no found");
				
				
				cursor.moveToNext();

			}

			cursor.close();

		}

		return inAppSongDataSet;
	}

	public static void insert(InAppSongData inAppSongData, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		long localId = AllSongsTable.insert(new AllSongData(null, inAppSongData.getServerId(), inAppSongData.getSong()), context);

		ContentValues values = new ContentValues();
		values.put(C_ID, localId);;
		values.put(C_FPRINT, inAppSongData.getfPrint());
		values.put(C_LINK, inAppSongData.getLink());
		values.put(C_LOCATION, inAppSongData.getLocation());
		
		db.insert(TABLE, null, values);
	}
	
	public static void insert(List<InAppSongData> inAppSongData, Context context) {
		LOGD(TABLE, "Bulk insert");
		
		for (InAppSongData data : inAppSongData) {
			insert(data, context);
		}
	}
/*
	public static boolean update(InAppSongData newInAppSongData, Context context) {
		LOGD(TABLE, "update");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_FPRINT, newInAppSongData.getfPrint());
		values.put(C_LINK, newInAppSongData.getLink());
		values.put(C_LOCATION, newInAppSongData.getLocation());
		
		if (db.update(TABLE, values, C_ID + "='" + newInAppSongData.getId() + "'", null) > 0) {
			return AllSongsTable.update(newInAppSongData.getId(),
					newInAppSongData.getSong(), context);
		} else
			return false;

	}
*/
	static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		boolean rr = AllSongsTable.remove(id, context);
		boolean r = db.delete(TABLE, C_ID + "='" + id + "'", null) >0;
		return r && rr ;
	}
	
	
	public static class InAppSongData {
		private Long id;
		private long serverId;
		private Song song;
		private String fPrint, link , location;
	
		public InAppSongData(Long id,long serverId, Song song, String fPrint, String link,
				String location) {
			super();
			this.id = id;
			this.serverId = serverId;
			this.song = song;
			this.fPrint = fPrint;
			this.link = link;
			this.location = location;
		}
		
		/*public InAppSongData(long id, Song song, String fPrint) {
			super();
			this.id = id;
			this.song = song;
			this.fPrint = fPrint;
		}  */

		public Long getId() {
			return id;
		}
		public long getServerId() {
			return serverId;
		}

		public Song getSong() {
			return song;
		}

		public String getfPrint() {
			return fPrint;
		}

		public String getLink() {
			return link;
		}

		public String getLocation() {
			return location;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		@Override
		public String toString() {
			return "InAppSongData [id=" + serverId + ", song=" + song + ", fPrint="
					+ fPrint + ", link=" + link + ", location=" + location
					+ "]";
		}
		
		
		
		
	}
}
