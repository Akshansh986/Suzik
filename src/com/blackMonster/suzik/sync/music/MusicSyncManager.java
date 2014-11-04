package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AllSongsTable.AllSongData;

public class MusicSyncManager {
	public static Pair<Long, Song> getSong(Song song, Context context) {
		AllSongData asd = AllSongsTable.search(song, context);
		if (asd == null)
			return null;
		return new Pair<Long, Song>(asd.getId(), asd.getSong());
	}

	public static long getServerId(long id, Context context) {
		return AllSongsTable.getServerId(id, context);
	}
}
