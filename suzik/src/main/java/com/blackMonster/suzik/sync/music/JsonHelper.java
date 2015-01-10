package com.blackMonster.suzik.sync.music;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper.AndroidData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.blackMonster.suzik.util.ServerUtils;

 class JsonHelper {
	private static final String TAG = "music.JsonHelper";

	 static class DeletedSong {
		private static final String P_DELETED_SONGS = "ids";
		private static final String P_MODULE_DELETED_SONGS = "music";
		private static final String P_CMD_VALUE = "delete";


		private static final String P_R_MAIN_TAG = "songData";
		private static final String P_R_SERVER_ID = "id";
		private static final String P_R_STATUS = "status";
		
		public static final int RESPONSE_OK = 1;
		public static final int RESPONSE_SQL_ERROR = 0;
		public static final int RESPONSE_NOT_IN_DB = 2;



		 static JSONObject toJson(List<Long> ids) throws JSONException {

			JSONObject root = new JSONObject();
			JSONArray array = new JSONArray();

			for (long id : ids) {
				//root.put(P_DELETED_SONGS, id);
				array.put(id);
				}
			root.put(P_DELETED_SONGS, array);
			
			
			ServerUtils.addEssentialParamToJson(root, P_MODULE_DELETED_SONGS,P_CMD_VALUE);

			LOGD(TAG, root.toString());
			return root;
		}

		// Hash-Map feature is not use, use different data structure to optimize
		// it.
		 static HashMap<Long, Integer> parseResponse(JSONObject response)
				throws JSONException {

			HashMap<Long, Integer> idStatus = new HashMap<Long, Integer>();
			JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);

			for (int i = 0; i < responseArray.length(); i++) {
				JSONObject responseObj = (JSONObject) responseArray.get(i);

				idStatus.put(responseObj.getLong(P_R_SERVER_ID),
						responseObj.getInt(P_R_STATUS));
			}

			return idStatus;

		}

	}

	static class AddedSong {

		private static final String P_SONGS_ARRAY = "songData";
		private static final String P_MODULE = "music";
		private static final String P_CMD_VALUE = "add";

		private static final String P_FINGERPRINT = "fp";
		private static final String P_FILENAME = "fileName";
		private static final String P_ALBUM = "album";
		private static final String P_TITLE = "title";
		private static final String P_ARTIST = "artist";
		private static final String P_DURATION = "duration";
		
		private static final String P_R_STATUS = "status";
		private static final int STATUS_OK = 1;

		
		

		static JSONObject toJson(List<AndroidData> songs) throws JSONException {
			JSONObject root = new JSONObject();
			JSONArray songArray = new JSONArray();

			for (AndroidData song : songs) {
				songArray.put(getSingleObject(song));
			}

			root.put(P_SONGS_ARRAY, songArray);
			ServerUtils.addEssentialParamToJson(root, P_MODULE, P_CMD_VALUE);
			LOGD(TAG, root.toString());
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
		
		 static boolean parseResponse(JSONObject response)
				throws JSONException {

			int status = response.getInt(P_R_STATUS);
			return status==STATUS_OK;
	

		}

	}

	static class AddedSongsQueue {
		private static final String P_MAIN_TAG = "fingerprints";			//NIK
		private static final String P_MODULE_ADDED_SONG_STATUS = "music";
		private static final String P_CMD = "getSongStatus";


		private static final String P_R_MAIN_TAG = "songsStatus";
		private static final String P_R_FPRINT = "fp";
		private static final String P_R_SERVER_ID = "id";

		static JSONObject toJson(List<String> fPrints)
				throws JSONException {
			JSONObject root = new JSONObject();
			JSONArray array = new JSONArray();
			
			for (String fprint : fPrints) {
				array.put(fprint);
				}
			root.put(P_MAIN_TAG, array);

			//root.put(P_MAIN_TAG, fPrints);
			ServerUtils.addEssentialParamToJson(root,
					P_MODULE_ADDED_SONG_STATUS,P_CMD);

			LOGD(TAG, root.toString());
			return root;

		}

		 static HashMap<String, Long> parseResponse(JSONObject response)
				throws JSONException {

			HashMap<String, Long> result = new HashMap<String, Long>();
			JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);

			int n = responseArray.length();
			for (int i = 0; i < n; i++) {
				JSONObject responseObj = (JSONObject) responseArray.get(i);

				result.put(responseObj.getString(P_R_FPRINT),
						responseObj.getLong(P_R_SERVER_ID));
			}

			return result;

		}

	}

	static class ServerAllSongs {
		private static final String P_MODULE = "music";
		private static final String P_CMD = "getSongsList";

		private static final String P_R_SONG_LIST = "songData";
		private static final String P_R_SERVER_ID = "id";
		private static final String P_R_FPRINT = "fingerprint";
		private static final String P_R_ALUBMART_LINK = "album_art_link";
		private static final String P_R_SONG_LINK = "songLink";
		private static final String P_R_TITLE = "title";
		private static final String P_R_ARTIST = "artist";
		private static final String P_R_ALBUM = "album";
		private static final String P_R_DURATION = "duration";

		static JSONObject getCredentials() throws JSONException {
			JSONObject root = new JSONObject();
			ServerUtils.addEssentialParamToJson(root, P_MODULE, P_CMD);
			LOGD(TAG, root.toString());
			return root;

		}

		static List<InAppSongData> parseResponse(JSONObject response)
				throws JSONException {

			List<InAppSongData> result = new ArrayList<InAapSongTable.InAppSongData>();

			JSONArray responseArray = response.getJSONArray(P_R_SONG_LIST);

			Song song;
			int n = responseArray.length();
			for (int i = 0; i < n ; i++) {
				JSONObject rObj = (JSONObject) responseArray.get(i);

				song = new Song(rObj.getString(P_R_TITLE),
						rObj.getString(P_R_ARTIST), rObj.getString(P_R_ALBUM),
						rObj.getLong(P_R_DURATION));

				result.add(new InAppSongData(null, rObj.getLong(P_R_SERVER_ID),song, rObj
						.getString(P_R_FPRINT), rObj.getString(P_R_ALUBMART_LINK),rObj.getString(P_R_SONG_LINK), null,null));

			}

			return result;

		}

	}

}
