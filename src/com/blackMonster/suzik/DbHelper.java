package com.blackMonster.suzik;

import com.blackMonster.musicstore.infoFromOtherPlayers.TableCompletedSongs;
import com.blackMonster.musicstore.infoFromOtherPlayers.TablePausedSongs;
import com.blackMonster.musicstore.infoFromOtherPlayers.testing.TableAllPlayed;
import com.blackMonster.musicstore.infoFromOtherPlayers.testing.TableUserSelectedCompleted;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";
	public static final String DB_NAME = "suzik.db";
	public static final int DB_VERSION = 1;

	private static DbHelper dInstance = null;

	public static DbHelper getInstance(Context cont) {
		if (dInstance == null) {
			dInstance = new DbHelper(cont.getApplicationContext());
			// Log.d(TAG, "getWritebledataase");
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// Log.d(TAG, "constructor start");
		// Log.d(TAG, "DbHelper");
		// Log.d(TAG, "constructor end");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TablePausedSongs.createTable(db);
		TableCompletedSongs.createTable(db);
		
		TableAllPlayed.createTable(db);
		TableUserSelectedCompleted.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

	@Override
	public synchronized void close() {
		super.close();
		dInstance = null;
	}

	public static void shutDown() {
		if (dInstance != null)
			dInstance.close();
		dInstance = null;
	}

}