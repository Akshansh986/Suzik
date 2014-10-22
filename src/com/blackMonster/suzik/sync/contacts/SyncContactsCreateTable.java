package com.blackMonster.suzik.sync.contacts;

import android.database.sqlite.SQLiteDatabase;

public class SyncContactsCreateTable {

	public static void createAll(SQLiteDatabase db) {
		CacheTable.createTable(db);
	}
}
