package com.blackMonster.suzik.musicstore.Timeline;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.InAapSongTable;

public class TimelineItem {
	Song song;
	long id;
	String albumArtUrl, songUrl;
    InAapSongTable.InAppSongData inAppSongMirror;

	public TimelineItem(Song song, long id, String albumArtUrl, String songUrl, Context context) {
		super();
		this.song = song;
		this.id = id;
		this.albumArtUrl = albumArtUrl;
		this.songUrl = songUrl;
        inAppSongMirror = InAapSongTable.getDataFromServerId(id,context);
	}

    public boolean isInAppMirrorAvailable() {
        return inAppSongMirror != null;
    }

	public Song getSong() {
		return song;
	}

	public long getId() {
		return id;
	}
	
	public String getLowAlbumArt() {
		if (albumArtUrl == null) return null;
		return albumArtUrl.replace("1200x1200", "100x100");
	}

	public String getMediumAlbumArt() {
		if (albumArtUrl == null) return null;
		return albumArtUrl.replace("1200x1200", "400x400");
	}
	
	public String getHighAlbumArt() {
		return albumArtUrl;
	}

	public String getSongUrl() {
		return songUrl;
	}

	@Override
	public String toString() {
		return "TimelineItem [song=" + song + ", id=" + id + ", albumArt="
				+ albumArtUrl + ", audio=" + songUrl + "]";
	}

}
