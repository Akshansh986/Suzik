package com.blackMonster.suzik;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackMonster.suzik.musicstore.Flag.FlagTable;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.TableCompletedSongs;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.TablePausedSongs;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing.TableAllPlayed;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing.TableUserSelectedCompleted;
import com.blackMonster.suzik.musicstore.userActivity.QueueUserActivity;
import com.blackMonster.suzik.sync.contacts.SyncContactsCreateTable;
import com.blackMonster.suzik.sync.music.SyncMusicCreateTable;
import com.blackMonster.suzik.ui.ContactsFilterErrorTable;


public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";
	public static final String DB_NAME = "suzik.db";
	public static final int DB_VERSION = 2;

	private static DbHelper dInstance = null;

	public static DbHelper getInstance(Context cont) {
		if (dInstance == null) {
			dInstance = new DbHelper(cont.getApplicationContext());
			// LOGD(TAG, "getWritebledataase");
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// LOGD(TAG, "constructor start");
		// LOGD(TAG, "DbHelper");
		// LOGD(TAG, "constructor end");

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TablePausedSongs.createTable(db);
		TableCompletedSongs.createTable(db);
		
		TableAllPlayed.createTable(db);
		TableUserSelectedCompleted.createTable(db);
		
		QueueUserActivity.createTable(db);
        FlagTable.createTable(db);
		
		SyncMusicCreateTable.createAll(db);
		SyncContactsCreateTable.createAll(db);
        ContactsFilterErrorTable.createTable(db);

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