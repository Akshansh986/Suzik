package com.blackMonster.suzik.musicstore.userActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class QueueUserActivity {

	private static final String TAG = "TableUserActivityQueue";
	private static final String C_ID = "id";
	private static final String C_SONG_ID = "songId";
	private static final String C_ACTION = "action";
	private static final String C_COMPLETED_TS = "COMPLETED_TS";
	
	private static final String C_TITLE = "title";
	private static final String C_ARTIST = "artist";
	private	static final String C_ALBUM = "album";
	private static final String C_DURATION = "duration";
	private static final String C_FRIENDS = "friends";


	private static final String TABLE = "TableUserActivityQueue";

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER PRIMARY KEY AUTOINCREMENT,%s INTEGER, %s INTEGER, %s INTEGER, %s text, %s text, %s text, %s INTEGER, %s text)",
						TABLE, C_ID, C_SONG_ID, C_ACTION,
						C_COMPLETED_TS, C_TITLE, C_ARTIST, C_ALBUM, C_DURATION, C_FRIENDS);
		db.execSQL(sql);
	}

	static List<UserActivity> getAllData(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

		List<UserActivity> allActivity = null;
		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			allActivity = new ArrayList<UserActivity>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				
				Song song = new Song(cursor.getString(cursor
						.getColumnIndex(C_TITLE)), cursor.getString(cursor
						.getColumnIndex(C_ARTIST)), cursor.getString(cursor
						.getColumnIndex(C_ALBUM)), cursor.getLong(cursor
						.getColumnIndex(C_DURATION)));
				
				allActivity.add(new UserActivity(song, cursor.getLong(cursor
						.getColumnIndex(C_ID)), cursor.getLong(cursor
						.getColumnIndex(C_SONG_ID)), cursor.getInt(cursor
						.getColumnIndex(C_ACTION)), cursor.getLong(cursor
						.getColumnIndex(C_COMPLETED_TS)),
						fromCommaSeperatedNumbers(cursor.getString(cursor.getColumnIndex(C_FRIENDS)))));
				cursor.moveToNext();
			}
			cursor.close();
		}

		return allActivity;
	}

	static int getRowCount(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			return cursor.getCount();
		} else
			return 0;
	}

	static boolean insert(UserActivity data, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_SONG_ID, data.songId());
		values.put(C_ACTION, data.action());
		values.put(C_COMPLETED_TS, data.completedTS());
		
		values.put(C_TITLE,data.song().getTitle());
		values.put(C_ARTIST, data.song().getArtist());
		values.put(C_ALBUM, data.song().getAlbum());
		values.put(C_DURATION, data.song().getDuration());
		values.put(C_FRIENDS, toCommaSeperatedNumbers(data.getFriends()));

		LOGD(TAG,toCommaSeperatedNumbers(data.getFriends()));

		return db.insert(TABLE, null, values) > -1;

	}

	private static String toCommaSeperatedNumbers(List<String> numbers) {
		if (numbers == null) return "";
		String ans = "";
		for (String number : numbers) {
			ans = ans + "," + number;
		}

		if (ans.length() > 0) ans = ans.substring(1);
		return  ans;

	}

	private static List<String> fromCommaSeperatedNumbers(String str){
		if (str == null || str.equals("")) return new ArrayList<>();
		String[] a =str.split(",");
		return new ArrayList( Arrays.asList(a));
	}

	static boolean remove(long id, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		int res = db.delete(TABLE, C_ID + "='" + id + "'", null);
		return res > 0;
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

}
