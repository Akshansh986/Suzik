package com.blackMonster.suzik.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;

public class DbUtils {

	public static Pair<String, String[]> songToWhereArgs(Song song,String C_TITLE, String C_ARTIST, String C_ALBUM, String C_DURATION) {
		List<String> whereParams = new ArrayList<String>();
		

		String whereArgs = C_TITLE +"=? AND " + C_ARTIST + "=?"; 
		whereParams.add(song.getTitle());
		whereParams.add(song.getArtist());
	 
		if (song.getAlbum() != null) {
			whereArgs+= " AND " +  C_ALBUM + "=?"; 
			whereParams.add(song.getAlbum());
			
		}
		
		if (song.getDuration() != 0) {
			whereArgs+= " AND " +  C_DURATION + "=?";
			whereParams.add(song.getDuration()+"");
		}
		
		return new Pair<String, String[]>(whereArgs, whereParams.toArray(new String[whereParams.size()]));
	}
}
