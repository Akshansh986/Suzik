package com.blackMonster.suzik.musicstore.timeline;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.UserActivity;

class TableUserActivityQueue {

	private static final String TAG = "TableUserActivityQueue";
	private static final String C_LOCAL_ID = "localId";
	private static final String C_SERVER_ID = "serverId";

	
	private static final String C_ACTION = "action";

	private static final String TABLE = "TableUserActivityQueue";

	static void createTable(SQLiteDatabase db) {
		String sql = String.format("create table %s"
				+ "(%s INTEGER AUTOINCREMENT,%s INTEGER, %s INTEGER)", TABLE, C_LOCAL_ID, C_SERVER_ID, C_ACTION);
		db.execSQL(sql);
	}

	static List<UserActivity> getAllActivity(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

		List<UserActivity> allActivity = null;
		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {
			allActivity = new ArrayList<UserActivity>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				allActivity.add(new UserActivity(cursor.getLong(cursor.getColumnIndex(C_LOCAL_ID)), cursor.getLong(cursor
						.getColumnIndex(C_SERVER_ID)), cursor.getInt(cursor
						.getColumnIndex(C_ACTION))));
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

		values.put(C_SERVER_ID, data.getServerId());
		values.put(C_ACTION, data.getAction());
	
		return db.insert(TABLE, null, values) > -1;

	}

	static boolean remove(long localId, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		int res = db.delete(TABLE, C_LOCAL_ID + "='" + localId + "'", null);
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
