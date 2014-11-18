package com.blackMonster.suzik.musicstore.userActivity;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.UserActivity;

public class QueueUserActivity {

	private static final String TAG = "TableUserActivityQueue";
	private static final String C_ID = "id";
	private static final String C_SONG_ID = "songId";
	private static final String C_ACTION = "action";
	private static final String C_STREAMING = "STREAMING";
	private static final String C_COMPLETED_TS = "COMPLETED_TS";

	private static final String TABLE = "TableUserActivityQueue";

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s INTEGER PRIMARY KEY AUTOINCREMENT,%s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
						TABLE, C_ID, C_SONG_ID, C_ACTION, C_STREAMING,
						C_COMPLETED_TS);
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
				allActivity.add(new UserActivity(cursor.getLong(cursor
						.getColumnIndex(C_ID)), cursor.getLong(cursor
						.getColumnIndex(C_SONG_ID)), cursor.getInt(cursor
						.getColumnIndex(C_ACTION)), cursor.getInt(cursor
						.getColumnIndex(C_STREAMING)), cursor.getLong(cursor
						.getColumnIndex(C_COMPLETED_TS))));
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
		values.put(C_STREAMING, data.streaming());
		values.put(C_COMPLETED_TS, data.completedTS());

		return db.insert(TABLE, null, values) > -1;

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
