package com.blackMonster.suzik.sync.music;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;
import com.blackMonster.suzik.util.ServerUtils;

public class JsonHelper {
	private static final String TAG = "music.JsonHelper";
	
	static class DeletedSong {
	private static final String P_DELETED_SONGS = "songs";
	private static final String P_MODULE_DELETED_SONGS = "deletedSongs";

	
	static JSONObject toJson(List<Long> ids)
			throws JSONException {
		
		JSONObject root = new JSONObject();
					
		root.put(P_DELETED_SONGS, ids);
		ServerUtils.addEssentialParamToJson(root,P_MODULE_DELETED_SONGS);

		Log.d(TAG, root.toString());
		return root;
	}
	
	}
	
	
	
	
	
	static class AddedSong {
		
		private static final String P_SONGS_ARRAY = "songData";
		private static final String P_MODULE = "music";
		private static final String P_CMD = "cmd";
		private static final String P_CMD_VALUE = "add";

		

		private static final String P_FINGERPRINT = "fp";
		private static final String P_FILENAME = "fileName";
		private static final String P_ALBUM = "album";
		private static final String P_TITLE = "title";
		private static final String P_ARTIST = "artist";
		private static final String P_DURATION = "duration";

	
		
		static JSONObject toJson(List<AndroidData> songs)
				throws JSONException {
			JSONObject root = new JSONObject();
			JSONArray songArray = new JSONArray();

			for (AndroidData song : songs) {
				songArray.put(getSingleObject(song));
			}
			
			root.put(P_SONGS_ARRAY, songArray);
			root.put(P_CMD, P_CMD_VALUE);
			ServerUtils.addEssentialParamToJson(root, P_MODULE);
			Log.d(TAG, root.toString());
			return root;
		}
		
		private static JSONObject getSingleObject(AndroidData song)
				throws JSONException {


			JSONObject obj = new JSONObject();
			obj.put(P_FINGERPRINT, song.getfPrint());
			obj.put(P_FILENAME, song.getFileName());
			obj.put(P_ALBUM, song.getSong().getAlbum());
			obj.put(P_TITLE, song.getSong().getTitle());
			obj.put(P_ARTIST, song.getSong().getArtist());
			obj.put(P_DURATION, song.getSong().getDuration());
			return obj;

		}
				
	}
	
	
}
