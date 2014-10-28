package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.SongSentTable;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing.TableAllPlayed;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.MusicSyncManager;

public abstract class MusicBroadcastManager extends BroadcastReceiver {
	private static final String TAG = "MusicBroadcastManager";
	private static final String P_TRACK = "track";
	private static final String P_ARTIST = "artist";
	private static final String P_ALBUM = "album";
	
	private static final String P_PLAYING = "playing";
	private static final String P_ID = "id";
	private static final String P_DURATION = "duration";

	private String track, artist, album;
	private long id, duration;
	private boolean streaming, playing;
	
	private static DuplicateBroadcastFilter duplicateFilter = new DuplicateBroadcastFilter();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e(TAG, action + "  " + "started  " + intent.getBooleanExtra(P_PLAYING,false) );

		try {
			fixBroadcastParameters(intent);
			if ( !duplicateFilter.isDuplicate(getSong(), action,playing, System.currentTimeMillis()) ){
				TableAllPlayed.insert(getSong(), System.currentTimeMillis(), context);
				run(context);
			}
		} catch (ExceptionUnknownBroadcast e) {
			e.printStackTrace();
		}
		Log.e(TAG, action + "  " + "completed");

	}

	public abstract void run(Context context);

	private void fixBroadcastParameters(Intent intent, Context context) throws ExceptionUnknownBroadcast {
		Log.i(TAG, "fixBroadcastParameters");
		BroadcastMediaStoreChanged.printBundle(intent.getExtras(), TAG);
		track = intent.getStringExtra(P_TRACK);
		artist = intent.getStringExtra(P_ARTIST);
		album = 	intent.getStringExtra(P_ALBUM);
		duration = intent.getLongExtra(P_DURATION, 0);
		playing = intent.getBooleanExtra(P_PLAYING, false);
		Song song = new Song(track, artist, album, duration);
		if (track == null || artist == null)
			throw new ExceptionUnknownBroadcast();

		Log.d(TAG, track + "   " + artist);
		Pair<Long, Song> song = MusicSyncManager.getSong(song, context);

		if (song == null) {
			Bundle bundle = intent.getExtras();
			id = getFromBundle(bundle,P_ID);
			duration = getFromBundle(bundle, P_DURATION);
			streaming = true;
			Log.d(TAG, "song not found in database");
		} else {
			id = song.first;
			duration = song.second.getDuration();
			streaming = false;
			Log.d(TAG, "song found in database");

		}

	}

	private long getFromBundle(Bundle bundle, String key) throws ExceptionUnknownBroadcast {
		Object value = bundle.get(key);
		if (value==null) throw new ExceptionUnknownBroadcast();
		return Long.parseLong(value.toString());
	}

	public Song getSong() {
		return new Song(getID(), getTrack(), getArtist(), getDuration(),isStreaming());
	}

	public long getID() {
		return id;
	}

	public String getTrack() {
		return track;
	}

	public String getArtist() {
		return artist;
	}

	public long getDuration() {
		return duration;
	}

	public int isStreaming() {
		if (streaming == false)
			return 0;
		else
			return 1;

	}

	public boolean isPlaying() {
		return playing;
	}
	
	
		
		
	
	

}
