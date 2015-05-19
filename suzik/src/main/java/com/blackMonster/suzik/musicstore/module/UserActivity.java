package com.blackMonster.suzik.musicstore.module;

import java.util.ArrayList;
import java.util.List;

public class UserActivity {
    //Make sure to read every function of this class, if your are planning to add new ACTION  to this class.

    public static final int ACTION_IN_APP_DOWNLOAD = 7;
    public static final int ACTION_OUT_APP_DOWNLOAD = 6;

    public static final int ACTION_IN_APP_PLAYED_LOCAL = 5;
    public static final int ACTION_IN_APP_PLAYED_ONLINE = 4;
    public static final int ACTION_IN_APP_PLAYED_CACHED = 3;
    public static final int ACTION_OUT_APP_PLAYED_LOCAL = 2;
    public static final int ACTION_OUT_APP_PLAYED_ONLINE = 1;

    private static final String ACTION_IN_APP_DOWNLOAD_STRING = "inAppDownload";
    private static final String ACTION_OUT_APP_DOWNLOAD_STRING = "outAppDownload";
    private static final String ACTION_IN_APP_PLAYED_CACHED_STRING = "inAppPlayCached";
    private static final String ACTION_IN_APP_PLAYED_LOCAL_STRING = "inAppPlayLocal";
    private static final String ACTION_IN_APP_PLAYED_ONLINE_STRING = "inAppPlayOnline";
    private static final String ACTION_OUT_APP_PLAYED_LOCAL_STRING = "outAppPlayLocal";
    private static final String ACTION_OUT_APP_PLAYED_ONLINE_STRING = "outAppPlayOnline";

    public static final int STREAMING_TRUE = 1;
    public static final int STREAMING_FALSE = 0;

    private Song song;
    private Long id;
    private long songId, completedTS;
    private int action;
    List<String> friends;


    /**
     * @param song        Song whose activity has to be sended.
     * @param id          pass null, it is automatically assigned by system.
     * @param songId      Local id of song i.e id used to refer song in this app.
     * @param action      Action InappDownlaod | outappDownload.....
     * @param completedTS Completed timestamp of activity. (System.currentTimeMillis() can be used)
     */


    public UserActivity(Song song, Long id, long songId, int action,
                        long completedTS, List<String> friends) {
        super();
        this.song = song;
        this.id = id;
        this.songId = songId;
        this.action = action;
        this.completedTS = completedTS;

        if (friends == null) this.friends = new ArrayList<>();
        else this.friends = friends;
    }

    public List<String> getFriends() {
        return friends;
    }

    public boolean isOnlinePlayedSong(){
        return action == ACTION_IN_APP_PLAYED_ONLINE || action == ACTION_OUT_APP_PLAYED_ONLINE ;
    }

    public Long id() {
        return id;
    }

    public Song song() {
        return song;
    }

    public long songId() {
        return songId;
    }

    public int action() {
        return action;
    }



    public long completedTS() {
        return completedTS;
    }

    public String getActionString() {
        switch (action) {
            case ACTION_OUT_APP_PLAYED_LOCAL:
                return ACTION_OUT_APP_PLAYED_LOCAL_STRING;

            case ACTION_OUT_APP_PLAYED_ONLINE:
                return ACTION_OUT_APP_PLAYED_ONLINE_STRING;

            case ACTION_IN_APP_PLAYED_LOCAL:
                return ACTION_IN_APP_PLAYED_LOCAL_STRING;


            case ACTION_IN_APP_PLAYED_ONLINE:
                return ACTION_IN_APP_PLAYED_ONLINE_STRING;

            case ACTION_IN_APP_PLAYED_CACHED:
                return ACTION_IN_APP_PLAYED_CACHED_STRING;

            case ACTION_IN_APP_DOWNLOAD:
                return ACTION_IN_APP_DOWNLOAD_STRING;

            case ACTION_OUT_APP_DOWNLOAD:
                return ACTION_OUT_APP_DOWNLOAD_STRING;
        }
        return null;
    }


    //TODO both these functions are jugad, actually inappPlay and OutappPlay must store playing information in terms of action defined in user activity, not in terms of offline, chached or streaming. Therefore there should not be need of these two functions.
    public static int getInappPlayAction(boolean isOffline, boolean isCached) {

        if (!isOffline) return  UserActivity.ACTION_IN_APP_PLAYED_ONLINE;
        if (isCached) return UserActivity.ACTION_IN_APP_PLAYED_CACHED;
        return  UserActivity.ACTION_IN_APP_PLAYED_LOCAL;
    }

    public static int getOutappPlayAction(int isStreaming) {

        if (isStreaming == 1) return ACTION_OUT_APP_PLAYED_ONLINE;
        else return ACTION_OUT_APP_PLAYED_LOCAL;

    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "song=" + song +
                ", id=" + id +
                ", songId=" + songId +
                ", completedTS=" + completedTS +
                ", action=" + action +
                ", friends=" + friends +
                '}';
    }
}
