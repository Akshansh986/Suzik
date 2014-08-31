package com.blackMonster.suzik.musicstore;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.musicstore.FetchFriendsMusic.MusicInfo;

public class Database {

	public static class TableNOSongID {
		public static final String TABLE = "TableNOSongID";
		public static final String C_UID = "UID";
		public static final String C_SID = "SID";

		public static void createTable(SQLiteDatabase db) {
			String sql = String.format("create table %s"
					+ "(%s text, %s text, PRIMARY KEY (%s, %s) )", TABLE,
					C_UID, C_SID, C_UID, C_SID);

			db.execSQL(sql);
		}

		public static void insert(String uid, String sid, SQLiteDatabase db) {

			ContentValues values = new ContentValues();

			values.put(C_SID, sid);
			values.put(C_UID, uid);

			db.insert(TABLE, null, values);

		}

		public static void insert(List<MusicInfo> listMusicInfo, Context context) {

			SQLiteDatabase db = DbHelper.getInstance(context)
					.getWritableDatabase();

			for (MusicInfo mi : listMusicInfo) {
				insert(mi.phoneNo, mi.sid, db);
			}

		}

		public static List<String> getAllPhoneNo(Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context)
					.getReadableDatabase();
			List<String> list = new ArrayList<String>();
			Cursor cursor = db.rawQuery("select DISTINCT " + C_UID + " from "
					+ TABLE, null);

			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() == 0)
					return list;
				list.add(cursor.getString(cursor.getColumnIndex(C_UID)));
				while (cursor.moveToNext()) {
					list.add(cursor.getString(cursor.getColumnIndex(C_UID)));

				}

				cursor.close();
			}
			return list;
		}

		public static List<String> getSIDFromUID(String uid, Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context)
					.getReadableDatabase();
			List<String> list = new ArrayList<String>();

			Cursor cursor = db.query(TABLE, null, C_UID + "='" + uid + "'",
					null, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() == 0)
					return list;

				list.add(cursor.getString(cursor.getColumnIndex(C_SID)));
				while (cursor.moveToNext()) {
					list.add(cursor.getString(cursor.getColumnIndex(C_SID)));

				}

				cursor.close();
			}
			return list;
		}

	}

	public static class TableSongsInfo {
		public static final String TABLE = "TableSongsInfo";
		public static final String C_SID = "SID";
		public static final String C_TITLE = "TITLE";
		public static final String C_ARTIST = "ARTIST";
		public static final String C_DURATION = "DURATION";

		public static void createTable(SQLiteDatabase db) {
			String sql = String
					.format("create table %s"
							+ "(%s text, %s text, %s text, %s text, PRIMARY KEY (%s) )",
							TABLE, C_SID, C_TITLE, C_ARTIST, C_DURATION, C_SID);

			db.execSQL(sql);
		}

		public static void insert(MusicInfo song, SQLiteDatabase db) {

			ContentValues values = new ContentValues();

			values.put(C_SID, song.sid);
			values.put(C_TITLE, song.title);
			values.put(C_ARTIST, song.artist);
			values.put(C_DURATION, song.durartion);

			db.insert(TABLE, null, values);

		}

		public static void insert(List<MusicInfo> listMusicInfo, Context context) {

			SQLiteDatabase db = DbHelper.getInstance(context)
					.getWritableDatabase();

			for (MusicInfo mi : listMusicInfo) {
				insert(mi, db);
			}

		}

		public static MusicInfo getSongInfo(String sid, Context context) {
			SQLiteDatabase db = DbHelper.getInstance(context)
					.getReadableDatabase();

			Cursor cursor = db.query(TABLE, null, C_SID + "='" + sid + "'",
					null, null, null, null);
			MusicInfo mi = new MusicInfo();
			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() == 0)
					return mi;

				mi.title = cursor.getString(cursor.getColumnIndex(C_TITLE));
				mi.artist = cursor.getString(cursor.getColumnIndex(C_ARTIST));
				mi.durartion = cursor.getString(cursor
						.getColumnIndex(C_DURATION));

				mi.sid = sid;
			}
			cursor.close();
			return mi;
		}

	}
}
