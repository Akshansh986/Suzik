package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AllSongsTable.AllSongData;

public class MusicSyncManager {
	public static  Pair<Long, Song> getSong(Song song, Context context) {
		AllSongData asd =  AllSongsTable.search(song, context);
		return new Pair<Long, Song>(asd.getId(), asd.getSong());
	}
}
