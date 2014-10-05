package com.blackMonster.suzik.sync.music;

import java.util.HashMap;
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

		private static final String P_R_MAIN_TAG = "songs";
		private static final String P_R_ID = "id";
		private static final String P_R_STATUS = "status";


		



		static JSONObject toJson(List<Long> ids) throws JSONException {

			JSONObject root = new JSONObject();

			root.put(P_DELETED_SONGS, ids);
			ServerUtils.addEssentialParamToJson(root, P_MODULE_DELETED_SONGS);

			Log.d(TAG, root.toString());
			return root;
		}
		
		//Hash-Map feature is not use, use different data structure to optimize it.
		public static HashMap<Long, Integer> parseResponse(JSONObject response) throws JSONException {
			   
			HashMap<Long, Integer> idStatus = new HashMap<Long, Integer>();
			JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);
			   
	            for (int i = 0; i < responseArray.length(); i++) {
	                JSONObject responseObj = (JSONObject) responseArray.get(i);
	 
	                idStatus.put(responseObj.getLong(P_R_ID), responseObj.getInt(P_R_STATUS));
	            }
	            
	            
	            return idStatus;
			
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

		static JSONObject toJson(List<AndroidData> songs) throws JSONException {
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
	
	
	static class AddedSongsQueue {
		private static final String P_MAIN_TAG = "fingerPrints";
		private static final String P_MODULE_ADDED_SONG_STATUS = "addedSongsStatus";
		
		private static final String P_R_MAIN_TAG= "response";
		private static final String P_R_FPRINT= "fPrint";
		private static final String P_R_ID= "id";




		public static JSONObject toJson(List<String> fPrints) throws JSONException {
			JSONObject root = new JSONObject();

			root.put(P_MAIN_TAG, fPrints);
			ServerUtils.addEssentialParamToJson(root, P_MODULE_ADDED_SONG_STATUS);

			Log.d(TAG, root.toString());
			return root;

		}

		public static HashMap<String, Long> parseResponse(JSONObject response) throws JSONException {
			   
			HashMap<String, Long> result = new HashMap<String, Long>();
			JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);
			   
	            for (int i = 0; i < responseArray.length(); i++) {
	                JSONObject responseObj = (JSONObject) responseArray.get(i);
	 
	                result.put(responseObj.getString(P_R_FPRINT), responseObj.getLong(P_R_ID));
	            }
	            
	            
	            return result;
			
		}

	
	
	
	
	}
	


}
