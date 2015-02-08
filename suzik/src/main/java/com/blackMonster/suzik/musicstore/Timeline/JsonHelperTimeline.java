package com.blackMonster.suzik.musicstore.Timeline;

import android.content.Context;

import com.blackMonster.suzik.musicstore.Flag.Flag;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.util.ServerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class JsonHelperTimeline {
    private static final String TAG = "JsonHelperTimeline";

    private static final String P_MODULE = "generateTimeline";

    private static final String P_R_FULL_SONG_DATA = "response";
    //private static final String P_R_NEXT_IDS = "nextIds";

    private static final String P_R_ID = "songId";
    private static final String P_R_SONG_URL = "songLink";

    private static final String P_R_ALBUM_URL = "albumArtLink";

    private static final String P_R_TITLE = "title";
    private static final String P_R_ARTIST = "artist";
    private static final String P_R_ALBUM = "album";
    private static final String P_R_DURATION = "duration";

    private static final String P_R_FLAG_DISLPAY = "displayFlag";
    private static final String P_R_FLAG_BAD_SONG = "badSongFlag";



    //private static final String P_R_NUMBER = "num";

    public static JSONObject getCredentials() throws JSONException {
        JSONObject root = new JSONObject();
        ServerUtils.addEssentialParamToJson(root, P_MODULE);
        LOGD(TAG, root.toString());
        return root;
    }


    public static List<TimelineItem> parseTimelineItems(JSONObject response, Context context) throws JSONException {

        List<TimelineItem> result = new ArrayList<TimelineItem>();

        JSONArray responseArray = response.getJSONArray(P_R_FULL_SONG_DATA);

        Song song;
        int n = responseArray.length();
        for (int i = 0; i < n; i++) {
            JSONObject o = (JSONObject) responseArray.get(i);

            if (o.getString(P_R_SONG_URL).equals("")) continue;

//            if (!NetworkUtils.isValidUrl(o.getString(P_R_SONG_URL))) continue;

            song = new Song(o.getString(P_R_TITLE),
                    o.isNull(P_R_ARTIST) ? null : o.getString(P_R_ARTIST),
                    o.isNull(P_R_ALBUM) ? null : o.getString(P_R_ALBUM), o.isNull(P_R_DURATION) ? 0 : o.getLong(P_R_DURATION));

            Flag flag = new Flag(o.getLong(P_R_ID),o.getInt(P_R_FLAG_DISLPAY) == 1, o.getInt(P_R_FLAG_BAD_SONG) == 1, context);

            LOGD(TAG, song.toString());
            result.add(new TimelineItem(song, o.getLong(P_R_ID), o.isNull(P_R_ALBUM_URL) ? null : o.getString(P_R_ALBUM_URL),
                    o.getString(P_R_SONG_URL),flag, context));
        }

        return result;

    }


    public static class FlagJosnHelper {

        private static final String P_SONG_IDS = "songIds";

        private static final String P_MODULE = "storeTimelineData";

        private static final String P_R_MAIN_TAG = "result";
        private static final String P_R_SONG_ID = "songId";
        private static final String P_R_STATUS = "status";

        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = 0;


       public  static JSONObject toJson(long id)
                throws JSONException {
            JSONObject root = new JSONObject();
            JSONArray uaArray = new JSONArray();

            uaArray.put(id);

            root.put(P_SONG_IDS, uaArray);
            LOGD(TAG, root.toString());
            return root;
        }



        public static boolean isSuccessfull(JSONObject response)
                throws JSONException {

            JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);
            JSONObject responseObj = (JSONObject) responseArray.get(0);
            return responseObj.getInt(P_R_STATUS) == 1;
        }

    }


















    /*
	public static List<Long> parseNextIds(JSONObject response) throws JSONException {

		List<Long> result = new ArrayList<Long>();

		JSONArray responseArray = response.getJSONArray(P_R_NEXT_IDS);

		int n = responseArray.length();
		for (int i = 0; i < n; i++) {
			result.add((Long) responseArray.get(i));
		}

		return result;

	}
	
	
	
	*/
    public static class ServerAllSongs {
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

        public static JSONObject getCredentials() throws JSONException {
            JSONObject root = new JSONObject();
            ServerUtils.addEssentialParamToJson(root, P_MODULE, P_CMD);
            LOGD(TAG, root.toString());
            return root;

        }


        public static List<TimelineItem> parseTimelineItems(JSONObject response, Context context) throws JSONException {

            List<TimelineItem> result = new ArrayList<TimelineItem>();

            JSONArray responseArray = response.getJSONArray(P_R_SONG_LIST);

            Song song;
            int n = responseArray.length();
            for (int i = 0; i < n; i++) {
                JSONObject o = (JSONObject) responseArray.get(i);
                song = new Song(o.getString(P_R_TITLE),
                        o.isNull(P_R_ARTIST) ? null : o.getString(P_R_ARTIST),
                        o.isNull(P_R_ALBUM) ? null : o.getString(P_R_ALBUM), o.getLong(P_R_DURATION));

                Flag flag = new Flag(o.getLong(P_R_ID),false, false, context);

                result.add(new TimelineItem(song, o.getLong(P_R_SERVER_ID), o.isNull(P_R_ALUBMART_LINK) ? null : o.getString(P_R_ALUBMART_LINK),
                        o.getString(P_R_SONG_LINK),flag,context));
            }

            LOGD(TAG, "size " + result.size());

            return result;

        }

    }


}
