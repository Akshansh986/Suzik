package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

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
	private boolean  playing;
	private int streaming;
	
	private static DuplicateBroadcastFilter duplicateFilter = new DuplicateBroadcastFilter();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e(TAG, action + "  " + "started  " + intent.getBooleanExtra(P_PLAYING,false) );

		try {
			fixBroadcastParameters(intent,context);
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
		if (track==null) throw new ExceptionUnknownBroadcast();
		if (track.equals("")) track = "<unknown>";

		artist = intent.getStringExtra(P_ARTIST);
		if (artist==null) throw new ExceptionUnknownBroadcast();
		if (artist.equals("")) artist = "<unknown>";

		album = 	intent.getStringExtra(P_ALBUM);
		if (album!=null && album.equals("")) album = "<unknown>";

		duration = getFromBundle(intent.getExtras(), P_DURATION);
		playing = intent.getBooleanExtra(P_PLAYING, false);
		Song tempSong = new Song(track, artist, album, duration);
		if (track == null || artist == null)
			throw new ExceptionUnknownBroadcast();

		Log.d(TAG, track + "   " + artist);
		Pair<Long, Song> song = MusicSyncManager.getSong(tempSong, context);

		if (song == null) {
			Bundle bundle = intent.getExtras();
			id = getFromBundle(bundle,P_ID);   //Using getFromBundle instead of getExtraLong because getFromBundle can throw exception.
			duration = getFromBundle(bundle, P_DURATION);
			streaming = 1;
			Log.d(TAG, "song not found in database");
		} else {
			id = song.first;
			duration = song.second.getDuration();
			streaming = 0;
			Log.d(TAG, "song found in database");

		}

	}

	private long getFromBundle(Bundle bundle, String key) throws ExceptionUnknownBroadcast {
		Object value = bundle.get(key);
		if (value==null) throw new ExceptionUnknownBroadcast();
		return Long.parseLong(value.toString());
	}

	public BroadcastSong getSong() {
		return new BroadcastSong(getID(), getTrack(), getArtist(), getAlbum(), getDuration(), isStreaming());
	}

	public long getID() {
		return id;
	}

	public String getTrack() {
		return track;
	}

	public String getAlbum() {
		return album;
	}
	
	
	public String getArtist() {
		return artist;
	}

	public long getDuration() {
		return duration;
	}

	public int isStreaming() {
		return streaming;

	}

	public boolean isPlaying() {
		return playing;
	}
	
	
		
		
	
	

}
