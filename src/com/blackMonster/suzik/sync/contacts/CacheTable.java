package com.blackMonster.suzik.sync.contacts;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.sync.contacts.model.Contact;

class CacheTable {
	private static final String TAG = "CacheTable";
	private static final String C_PHONE_NO = "PHONE_NO";
	private static final String TABLE = "CacheTable";

	static void createTable(SQLiteDatabase db) {
		String sql = String.format("create table %s" + "(%s text primary key)",
				TABLE, C_PHONE_NO);
		db.execSQL(sql);
	}

	static void insert(Contact contact, Context context) {
		LOGD(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_PHONE_NO, contact.getNumber());

		db.insert(TABLE, null, values);

		List<String> l1 = new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();

		l1.retainAll(l2);

	}

	// This should be improved (bulk entry)
	static void insert(HashSet<Contact> contacts, Context context) {
		LOGD(TABLE, "insert bulk");

		for (Contact cn : contacts) {
			insert(cn, context);
		}

	}
	
	static HashSet<Contact> getMyContacts(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		HashSet<Contact> result = new HashSet<Contact>();
		Cursor cursor = db.query(TABLE, null,null, null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				result.add(new Contact(cursor.getString(cursor
						.getColumnIndex(C_PHONE_NO))));
			}
			cursor.close();
		}else {
			LOGD(TAG, "cursor is null");
		}
		return result;
	}
	
	public static boolean remove(Contact contact, Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		return db.delete(TABLE, C_PHONE_NO + "='" + contact.getNumber() + "'", null) > 0;
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
