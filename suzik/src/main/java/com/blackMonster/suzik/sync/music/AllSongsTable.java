package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
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
	 static final String C_LOCAL_ID = "localId";
	 static final String C_SERVER_ID = "serverId";
	 static final String C_TITLE = "title";
	 static final String C_ARTIST = "artist";
	 static final String C_ALBUM = "album";
	 static final String C_DURATION = "duration";
	
	 static final String TABLE = "allSongsTable";

	static void createTable(SQLiteDatabase db) {

		String sql = String.format("create table %s" + "(%s INTEGER primary key AUTOINCREMENT,%s text, %s text, %s text, %s INTEGER, %s INTEGER)",
				TABLE, C_LOCAL_ID,C_TITLE,C_ARTIST, C_ALBUM, C_DURATION, C_SERVER_ID);
		db.execSQL(sql);
	}
	
	 static AllSongData getSong(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		AllSongData ans = null;

		Cursor cursor = db.query(TABLE, null, C_LOCAL_ID + "='" + id + "'", null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				Song song = new Song(cursor.getString(cursor
						.getColumnIndex(C_TITLE)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getString(cursor
						.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)));
				long serverId = cursor.getLong(cursor.getColumnIndex(C_SERVER_ID));
				ans = new AllSongData(id, serverId, song);
			}
			cursor.close();
		}

		return ans;
	}
	 
	 
	 static Long getServerId(long localId, Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
			Long ans = null;

			Cursor cursor = db.query(TABLE, null, C_LOCAL_ID + "='" + localId + "'", null, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ans = cursor.getLong(cursor.getColumnIndex(C_SERVER_ID));
				}
				cursor.close();
			}

			return ans;
		}
	 
	
	 
	 static AllSongData search(Song song, Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
			AllSongData result = null;
			
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
					long id = cursor.getLong(cursor.getColumnIndex(C_LOCAL_ID));
					long serverId = cursor.getLong(cursor.getColumnIndex(C_SERVER_ID));
					result = new AllSongData(id, serverId, dbSong);
					
				}
				cursor.close();
			}

			return result;
		}
	

	 static long insert(AllSongData data, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_SERVER_ID, data.getServerId());
		values.put(C_TITLE,data.getSong().getTitle());
		values.put(C_ARTIST, data.getSong().getArtist());
		values.put(C_ALBUM, data.getSong().getAlbum());
		values.put(C_DURATION, data.getSong().getDuration());
		return db.insert(TABLE, null, values);
		
	}
	/*
	 static boolean update(long id, Song newSong, Context context) {
		LOGD(TABLE, "update");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_TITLE, newSong.getTitle());
		values.put(C_ARTIST, newSong.getArtist());
		values.put(C_ALBUM, newSong.getAlbum());
		values.put(C_DURATION, newSong.getDuration());
		return db.update(TABLE, values,  C_LOCAL_ID + "='" + id + "'" ,null) > 0;
		
	}
	*/


	
	 static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_LOCAL_ID + "='" + id + "'", null) > 0;
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
	 
	 
	static class AllSongData {
		 
		 private Song song;
		 private Long id;
		 private long serverId;
		 
		 	 
		 
		public AllSongData(Long id, long serverId,Song song) {
			super();
			this.song = song;
			this.id = id;
			this.serverId = serverId;
		}
		
		
		Song getSong() {
			return song;
		}


		Long getId() {
			return id;
		}


		long getServerId() {
			return serverId;
		}
	 }
	 

}
