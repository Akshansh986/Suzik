package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;

public class MusicSyncManager {
	public static  Pair<Long, Song> getSong(Song song, Context context) {
		return AllSongsTable.search(song, context);
	}
}
