package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;

public class MusicSyncManager {
	public static  Pair<Long, Song> getSong(String title, String artist, Context context) {
		return AllSongsTable.search(title, artist, context);
	}
}
