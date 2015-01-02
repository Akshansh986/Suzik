package com.blackMonster.suzik.sync.music;

import com.blackMonster.suzik.musicstore.module.Song;

public class SongAndFileName {

	private Song song;
	private String fileName;

	

	public SongAndFileName(Song song, String fileName) {
		super();
		this.song = song;
		this.fileName = fileName;
	}
	

	Song getSong() {
		return song;
	}


	String getFileName() {
		return fileName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SongAndFileName))
			return false;
		SongAndFileName other = (SongAndFileName) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SongAndFileName [song=" + song + ", fileName=" + fileName + "]";
	}
	
	

}