package com.blackMonster.suzik.contacts.io;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.sync.contacts.model.Contact;

public class FrndsOnAppTable {
	
	private static final String TAG = "FrndsOnAppTable";
	private static final String C_PHONE_NO = "PHONE_NO";
	private static final String C_AVAILABLE_TO_ME = "C_AVAILABLE_TO_ME";
	private static final String C_IN_MY_TIMELINE = "C_IN_MY_TIMELINE";

	
	private static final String TABLE = "FrndsOnAppTable";

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

}
