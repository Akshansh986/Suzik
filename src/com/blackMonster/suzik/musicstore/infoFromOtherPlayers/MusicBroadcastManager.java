package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import com.blackMonster.suzik.musicstore.SongSentTable;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing.TableAllPlayed;

public abstract class MusicBroadcastManager extends BroadcastReceiver {
	private static final String TAG = "MusicBroadcastManager";
	private static final String P_TRACK = "track";
	private static final String P_ARTIST = "artist";
	private static final String P_PLAYING = "playing";
	private static final String P_ID = "id";
	private static final String P_DURATION = "duration";

	private String track, artist;
	private long id, duration;
	private boolean streaming, playing;
	
	private static DuplicateBroadcastFilter duplicateFilter = new DuplicateBroadcastFilter();
	@Override
	public void onReceive(Context context, Intent intent) {
		Debug.startMethodTracing("sm");
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
		Debug.stopMethodTracing();

	}

	public abstract void run(Context context);

	private void fixBroadcastParameters(Intent intent) throws ExceptionUnknownBroadcast {
		Log.i(TAG, "fixBroadcastParameters");

		track = intent.getStringExtra(P_TRACK);
		artist = intent.getStringExtra(P_ARTIST);
		playing = intent.getBooleanExtra(P_PLAYING, false);
		if (track == null || artist == null)
			throw new ExceptionUnknownBroadcast();

		Log.d(TAG, track + "   " + artist);
		SongSentTable sst = SongSentTable.search(artist, track);

		if (sst == null) {
			Bundle bundle = intent.getExtras();
			id = getFromBundle(bundle,P_ID);
			duration = getFromBundle(bundle, P_DURATION);
			streaming = true;
		} else {
			id = sst.getId();
			duration = sst.getDuration();
			streaming = false;
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
