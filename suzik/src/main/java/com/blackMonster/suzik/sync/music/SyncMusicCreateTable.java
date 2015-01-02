package com.blackMonster.suzik.sync.music;

import android.database.sqlite.SQLiteDatabase;

public class SyncMusicCreateTable {

	public static void createAll(SQLiteDatabase db) {
		AllSongsTable.createTable(db);
		CacheTable.createTable(db);
		InAapSongTable.createTable(db);
		QueueAddedSongs.createTable(db);
	}
}
