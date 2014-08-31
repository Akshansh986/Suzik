package com.blackMonster.suzik.sync.contacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blackMonster.suzik.BuildConfig;
import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.Song;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.TablePausedSongs;

class TableLocalContacts {
	
	private static final String C_PHONE_NO = "PHONE NO";
	private static final String TABLE = "LocalContacts";

	static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s text primary key)",
						TABLE, C_PHONE_NO);
		db.execSQL(sql);
	}

	public static void insert(String phNumber, Context context) {
		Log.d(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(C_PHONE_NO, phNumber);
		
		db.insert(TABLE, null, values);
		
		
		List<String> l1 = new ArrayList<String>();
		List<String>	 l2 = new ArrayList<String>();
		
		l1.retainAll(l2);
		
		
	}
	
	
	//This should be improved (bulk entry)
	public static void insert(List<String> phNumbers, Context context) {
		Log.d(TABLE, "insert");
		
		for (String num : phNumbers) {
			insert(num, context);
		}
			
	}
	

	

}
