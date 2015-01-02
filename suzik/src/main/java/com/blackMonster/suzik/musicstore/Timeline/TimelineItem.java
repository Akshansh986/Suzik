package com.blackMonster.suzik.musicstore.Timeline;

import com.blackMonster.suzik.musicstore.module.Song;

public class TimelineItem {
	Song song;
	long id;
	String albumArtUrl, songUrl;

	public TimelineItem(Song song, long id, String albumArtUrl, String songUrl) {
		super();
		this.song = song;
		this.id = id;
		this.albumArtUrl = albumArtUrl;
		this.songUrl = songUrl;
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
