package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

public class Song {
	public long id;
	public String track;
	public String artist;
	public long duration;
	public long streaming;

	public Song(long id, String track, String artist, long duration,
			long streaming) {
		this.id = id;
		this.track = track;
		this.artist = artist;
		this.duration = duration;
		this.streaming = streaming;
	}

	@Override
	public boolean equals(Object o) {
		Song newSong = (Song) o;
		return (id == newSong.id && track.equals(newSong.track)
				&& artist.equals(newSong.artist)
				&& duration == newSong.duration && streaming == newSong.streaming);
	}
}
