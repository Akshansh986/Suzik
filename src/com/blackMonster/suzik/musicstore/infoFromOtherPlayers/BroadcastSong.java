package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import com.blackMonster.suzik.musicstore.module.Song;

public class BroadcastSong extends Song{
	private long id;
	private int streaming;

	public BroadcastSong(long id, String track, String artist,String album, long duration,
			int streaming) {
		super(track, artist, album, duration);
		this.id = id;
		this.streaming = streaming;
	}

	public long getId() {
		return id;
	}

	public int isStreaming() {
		return streaming;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (streaming ^ (streaming >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BroadcastSong other = (BroadcastSong) obj;
		if (id != other.id)
			return false;
		if (streaming != other.streaming)
			return false;
		return true;
	}

	
}
